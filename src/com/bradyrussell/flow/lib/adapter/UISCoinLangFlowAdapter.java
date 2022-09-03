package com.bradyrussell.flow.lib.adapter;

import com.bradyrussell.flow.lib.Constants;
import com.bradyrussell.flow.lib.graph.*;
import com.bradyrussell.flow.lib.graph.builder.NodeDefinitionBuilder;
import com.bradyrussell.flow.lib.graph.builder.StructDefinitionBuilder;

import java.util.ArrayList;
import java.util.List;

public class UISCoinLangFlowAdapter implements FlowAdapter<String> {
    @Override
    public String getName() {
        return "UISCoinLangFlowAdapter";
    }

    @Override
    public boolean supportsArrays() {
        return true;
    }

    @Override
    public boolean supportsStructs() {
        return true;
    }

    @Override
    public List<String> getPrimitiveTypes() {
        return List.of("byte", "int32", "int64", "float", "void");
    }

    @Override
    public List<StructDefinition> getNativeStructs() {
        return List.of(new StructDefinitionBuilder("test_struct").setColor("red").addVariable(new VariableDefinition("test", "int32")).build());
    }

    @Override
    public List<NodeDefinition> getNativeNodes() {
        return nativeMethods;
    }

    @Override
    public String visitFlow(Flow flow) {
        StringBuilder structs = new StringBuilder();
        for (StructDefinition struct : flow.getStructs()) {
            structs.append(visitStructDeclaration(flow, struct)).append("\n");
        }
        return "BEGIN\n" + structs + visitNode(flow, flow.getNodeFromPinId(flow.getConnectedPinId("Flow.Begin")));
    }

    @Override
    public String visitVariableDeclaration(Flow flow, VariableDefinition variable) {
        return variable.getType() + (variable.getPointer() ? "@" : "") + " " + variable.getId() + (variable.getArray() ? "[" + variable.getArrayLength() + "]" : "") + ";\n";
    }

    @Override
    public String visitStructDeclaration(Flow flow, StructDefinition struct) {
        StringBuilder sb = new StringBuilder("struct ");
        sb.append(struct.getId()).append(" {\n");
        for (VariableDefinition variable : struct.getVariables()) {
            sb.append(visitVariableDeclaration(flow, variable));
        }
        sb.append("}\n");
        return sb.toString();
    }

    @Override
    public String visitNode(Flow flow, Node node) {
        if (node == null) {
            return "END";
        }
        StringBuilder sb = new StringBuilder();

        switch (node.getType()) {
            case "Print" -> {
                String pinConstantValue = flow.getPinConstantValue(node.getInputPins().get(0));
                if(pinConstantValue != null) {
                    sb.append("orint(\""+ pinConstantValue +"\");");
                } else {
                    List<String> inputPins = node.getInputPins();
                    sb.append("print("+ convertIdentifier(flow.getConnectedPinId(inputPins.get(0))) +");");
                }

                sb.append(visitNode(flow, flow.getNodeFromPinId(flow.getConnectedPinId(node.getPinId("FlowOut")))));
            }
            default -> {
                NodeDefinition nodeDefinition = flow.getNodeDefinition(node.getType());
                if(node.getOutputPins().size() == 1) {
                    sb.append(nodeDefinition.getOutputs().get(0).getType()).append(" ").append(convertIdentifier(node.getOutputPins().get(0))).append(" = ");
                } else if(node.getOutputPins().size() > 1) {
                    sb.append("(");
                    List<String> outputPins = node.getOutputPins();
                    for (int i = 0; i < outputPins.size(); i++) {
                        sb.append(nodeDefinition.getOutputs().get(i).getType());
                        sb.append(" ");
                        sb.append(convertIdentifier(outputPins.get(i)));
                        if(i < outputPins.size()-1) {
                            sb.append(",");
                        }
                    }
                    sb.append(") = ");
                }

                sb.append("_").append(node.getType()).append("(");

                List<String> inputPins = node.getInputPins();
                for (int i = 0; i < inputPins.size(); i++) {
                    String pinConstantValue = flow.getPinConstantValue(inputPins.get(i));
                    if(pinConstantValue != null) {
                        sb.append(pinConstantValue);
                    } else {
                        sb.append(convertIdentifier(inputPins.get(i)));
                    }
                }

                sb.append(");");
                sb.append(visitNode(flow, flow.getNodeFromPinId(flow.getConnectedPinId(node.getPinId("FlowOut")))));
            }
        }

        sb.append("\n");
        return sb.toString();
       // throw new RuntimeException("Node " + node.getType() + " was not implemented!");
    }

    private String convertIdentifier(String input) {
        return input.replace("#","_").replace(".","__");
    }

    @Override
    public boolean isValidTypeLiteral(String type, String literal) {
        switch (type) {
            case "byte" -> {
                try {
                    Byte.parseByte(literal);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case "int32" -> {
                try {
                    Integer.parseInt(literal);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case "int64" -> {
                try {
                    Long.parseLong(literal);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case "float" -> {
                try {
                    Float.parseFloat(literal);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case "void" -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public boolean isAutoCastAllowed(String fromType, String toType) {
        return !fromType.equals(Constants.FlowType) && !toType.equals(Constants.FlowType);
    }

    private static List<NodeDefinition> nativeMethods = List.of(
            new NodeDefinitionBuilder("Print").addInput(new VariableDefinition("Message", "void")).build(),
            new NodeDefinitionBuilder("set").addInput(
                    new VariableDefinition("location", "int32"),
                    new VariableDefinition("position", "int32"),
                    new VariableDefinition("length", "int32"),
                    new VariableDefinition("value", "int32")
            ).build(),
            new NodeDefinitionBuilder("get").addInput(
                    new VariableDefinition("location", "int32"),
                    new VariableDefinition("position", "int32"),
                    new VariableDefinition("length", "int32")
            ).addOutput(
                    new VariableDefinition("value", "int32")
            ).build(),
            new NodeDefinitionBuilder("encrypt").addInput(
                    new VariableDefinition("key", "byte", 0),
                    new VariableDefinition("data", "byte", 0)
            ).addOutput(
                    new VariableDefinition("result", "byte", 0)
            ).build(),
            new NodeDefinitionBuilder("decrypt").addInput(
                    new VariableDefinition("key", "byte", 0),
                    new VariableDefinition("data", "byte", 0)
            ).addOutput(
                    new VariableDefinition("result", "byte", 0)
            ).build(),
            new NodeDefinitionBuilder("alloc").addInput(
                    new VariableDefinition("location", "int32"),
                    new VariableDefinition("size", "int32")
            ).build(),
            new NodeDefinitionBuilder("zip").addInput(
                    new VariableDefinition("data", "byte", 0)
            ).addOutput(
                    new VariableDefinition("result", "byte", 0)
            ).build(),
            new NodeDefinitionBuilder("unzip").addInput(
                    new VariableDefinition("data", "byte", 0)
            ).addOutput(
                    new VariableDefinition("result", "byte", 0)
            ).build(),
            new NodeDefinitionBuilder("sha512").addInput(
                    new VariableDefinition("data", "byte", 0)
            ).addOutput(
                    new VariableDefinition("result", "byte", 0)
            ).build(),
            new NodeDefinitionBuilder("instruction").addOutput(
                    new VariableDefinition("instruction", "int32")
            ).build()
    );
}
