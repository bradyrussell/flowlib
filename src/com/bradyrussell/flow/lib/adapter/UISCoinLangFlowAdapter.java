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
        return structs + visitNode(flow, flow.getNodeFromPinId(flow.getConnectedPinId("Flow.Begin")));
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

    private String resolveLiteral(String literal) {
        if(isValidTypeLiteral("float", literal)) {
            return literal;
        } else {
            return "\""+literal+"\"";
        }
    }

    private String biOperator(Flow flow, Node node, String operator) {
        StringBuilder sb = new StringBuilder();
        NodeDefinition nodeDefinition = flow.getNodeDefinition(node.getType());
        sb.append(nodeDefinition.getOutputs().get(0).getType()).append(" ").append(convertIdentifier(node.getOutputPins().get(0))).append(" = ");
        List<String> inputPins = node.getInputPins();
        String aPinConstantValue = flow.getPinConstantValue(inputPins.get(0));
        if(aPinConstantValue != null) {
            sb.append(resolveLiteral(aPinConstantValue));
        } else {
            String connectedPinId = flow.getConnectedPinId(inputPins.get(0));
            if(connectedPinId != null) {
                sb.append(convertIdentifier(connectedPinId));
            }
        }

        sb.append(" ").append(operator).append(" ");

        String bPinConstantValue = flow.getPinConstantValue(inputPins.get(1));
        if(bPinConstantValue != null) {
            sb.append(resolveLiteral(bPinConstantValue));
        } else {
            String connectedPinId = flow.getConnectedPinId(inputPins.get(1));
            if(connectedPinId != null) {
                sb.append(convertIdentifier(connectedPinId));
            }
        }
        sb.append(";\n");
        sb.append(visitNode(flow, flow.getNodeFromPinId(flow.getConnectedPinId(node.getPinId("FlowOut")))));
        return sb.toString();
    }

    @Override
    public String visitNode(Flow flow, Node node) {
        if (node == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        switch (node.getType()) {
            case "code" -> {
                List<String> inputPins = node.getInputPins();
                String pinConstantValue = flow.getPinConstantValue(inputPins.get(0));
                sb.append("/* Begin Code Node: ").append(node.getId()).append(" */\n");
                if(pinConstantValue != null) {
                    sb.append(resolveLiteral(pinConstantValue));
                } else {
                    String connectedPinId = flow.getConnectedPinId(inputPins.get(0));
                    if(connectedPinId != null) {
                        sb.append(convertIdentifier(connectedPinId));
                    }
                }
                sb.append("\n/* End Code Node: ").append(node.getId()).append(" */\n");
            }
            case "equals" -> {
                sb.append(biOperator(flow, node, "=="));
            }
            case "notequals" -> {
                sb.append(biOperator(flow, node, "!="));
            }
            case "greaterthan" -> {
                sb.append(biOperator(flow, node, ">"));
            }
            case "lessthan" -> {
                sb.append(biOperator(flow, node, "<"));
            }
            case "greaterthanequals" -> {
                sb.append(biOperator(flow, node, ">="));
            }
            case "lessthanequals" -> {
                sb.append(biOperator(flow, node, "<="));
            }
            case "add" -> {
                sb.append(biOperator(flow, node, "+"));
            }
            case "subtract" -> {
                sb.append(biOperator(flow, node, "-"));
            }
            case "multiply" -> {
                sb.append(biOperator(flow, node, "*"));
            }
            case "divide" -> {
                sb.append(biOperator(flow, node, "/"));
            }
            case "if" -> {
                sb.append("if(");
                List<String> inputPins = node.getInputPins();
                String pinConstantValue = flow.getPinConstantValue(inputPins.get(0));
                if(pinConstantValue != null) {
                    sb.append(resolveLiteral(pinConstantValue));
                } else {
                    String connectedPinId = flow.getConnectedPinId(inputPins.get(0));
                    if(connectedPinId != null) {
                        sb.append(convertIdentifier(connectedPinId));
                    }
                }

                sb.append(") {\n\t")
                        .append(visitNode(flow, flow.getNodeFromPinId(flow.getConnectedPinId(node.getPinId("true")))))
                        .append("}\nelse {\n\t")
                        .append(visitNode(flow, flow.getNodeFromPinId(flow.getConnectedPinId(node.getPinId("false")))))
                        .append("}\n");
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
                        sb.append(resolveLiteral(pinConstantValue));
                    } else {
                        String connectedPinId = flow.getConnectedPinId(inputPins.get(i));
                        if(connectedPinId != null) {
                            sb.append(convertIdentifier(connectedPinId));
                        }
                    }
                    if(i < inputPins.size()-1) {
                        sb.append(",");
                    }
                }

                sb.append(");\n");
                sb.append(visitNode(flow, flow.getNodeFromPinId(flow.getConnectedPinId(node.getPinId("FlowOut")))));
            }
        }

        sb.append("\n");
        return sb.toString();
    }

    private String convertIdentifier(String input) {
        return input.replace("#","").replace(".","_");
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
            new NodeDefinitionBuilder("print").addInput(new VariableDefinition("message", "void")).build(),
            new NodeDefinitionBuilder("code").addInput(new VariableDefinition("code", "void")).build(),
            new NodeDefinitionBuilder("equals").addInput(
                    new VariableDefinition("a", "void"),
                    new VariableDefinition("b", "void")
            ).addOutput(new VariableDefinition("result", "byte")).build(),
            new NodeDefinitionBuilder("notequals").addInput(
                    new VariableDefinition("a", "void"),
                    new VariableDefinition("b", "void")
            ).addOutput(new VariableDefinition("result", "byte")).build(),
            new NodeDefinitionBuilder("greaterthan").addInput(
                    new VariableDefinition("a", "void"),
                    new VariableDefinition("b", "void")
            ).addOutput(new VariableDefinition("result", "byte")).build(),
            new NodeDefinitionBuilder("lessthan").addInput(
                    new VariableDefinition("a", "void"),
                    new VariableDefinition("b", "void")
            ).addOutput(new VariableDefinition("result", "byte")).build(),
            new NodeDefinitionBuilder("greaterthanequals").addInput(
                    new VariableDefinition("a", "void"),
                    new VariableDefinition("b", "void")
            ).addOutput(new VariableDefinition("result", "byte")).build(),
            new NodeDefinitionBuilder("lessthanequals").addInput(
                    new VariableDefinition("a", "void"),
                    new VariableDefinition("b", "void")
            ).addOutput(new VariableDefinition("result", "byte")).build(),
            new NodeDefinitionBuilder("add").addInput(
                    new VariableDefinition("a", "void"),
                    new VariableDefinition("b", "void")
            ).addOutput(new VariableDefinition("result", "float")).build(),
            new NodeDefinitionBuilder("subtract").addInput(
                    new VariableDefinition("a", "void"),
                    new VariableDefinition("b", "void")
            ).addOutput(new VariableDefinition("result", "float")).build(),
            new NodeDefinitionBuilder("multiply").addInput(
                    new VariableDefinition("a", "void"),
                    new VariableDefinition("b", "void")
            ).addOutput(new VariableDefinition("result", "float")).build(),
            new NodeDefinitionBuilder("divide").addInput(
                    new VariableDefinition("a", "void"),
                    new VariableDefinition("b", "void")
            ).addOutput(new VariableDefinition("result", "float")).build(),
            new NodeDefinitionBuilder("if").addInput(
                    new VariableDefinition("condition", "byte")
            ).addFlowOutput("true").addFlowOutput("false").build(),
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
            new NodeDefinitionBuilder("copy").addInput(
                    new VariableDefinition("sourceLocation", "void", true),
                    new VariableDefinition("sourcePosition", "int32"),
                    new VariableDefinition("destinationLocation", "void", true),
                    new VariableDefinition("destinationPosition", "int32"),
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
            new NodeDefinitionBuilder("verifySig").addInput(
                    new VariableDefinition("signature", "byte", 0),
                    new VariableDefinition("publicKey", "byte", 0)
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
            ).build(),
            new NodeDefinitionBuilder("log").addInput(
                    new VariableDefinition("value", "float")
            ).addOutput(
                    new VariableDefinition("result", "float")
            ).build(),
            new NodeDefinitionBuilder("log").addInput(
                    new VariableDefinition("value", "float")
            ).addOutput(
                    new VariableDefinition("result", "float")
            ).build(),
            new NodeDefinitionBuilder("logn").addInput(
                    new VariableDefinition("value", "float"),
                    new VariableDefinition("n", "float")
            ).addOutput(
                    new VariableDefinition("result", "float")
            ).build(),
            new NodeDefinitionBuilder("isinf").addInput(
                    new VariableDefinition("value", "float")
            ).addOutput(
                    new VariableDefinition("result", "byte")
            ).build(),
            new NodeDefinitionBuilder("isnan").addInput(
                    new VariableDefinition("value", "float")
            ).addOutput(
                    new VariableDefinition("result", "byte")
            ).build(),
            new NodeDefinitionBuilder("isfin").addInput(
                    new VariableDefinition("value", "float")
            ).addOutput(
                    new VariableDefinition("result", "byte")
            ).build(),
            new NodeDefinitionBuilder("pow").addInput(
                    new VariableDefinition("value", "float"),
                    new VariableDefinition("power", "float")
            ).addOutput(
                    new VariableDefinition("result", "float")
            ).build(),
            new NodeDefinitionBuilder("root").addInput(
                    new VariableDefinition("value", "float"),
                    new VariableDefinition("root", "float")
            ).addOutput(
                    new VariableDefinition("result", "float")
            ).build(),
            new NodeDefinitionBuilder("abs").addInput(
                    new VariableDefinition("value", "int64")
            ).addOutput(
                    new VariableDefinition("result", "int64")
            ).build(),
            new NodeDefinitionBuilder("fabs").addInput(
                    new VariableDefinition("value", "float")
            ).addOutput(
                    new VariableDefinition("result", "float")
            ).build(),
            new NodeDefinitionBuilder("sin").addInput(
                    new VariableDefinition("value", "float")
            ).addOutput(
                    new VariableDefinition("result", "float")
            ).build(),
            new NodeDefinitionBuilder("cos").addInput(
                    new VariableDefinition("value", "float")
            ).addOutput(
                    new VariableDefinition("result", "float")
            ).build(),
            new NodeDefinitionBuilder("tan").addInput(
                    new VariableDefinition("value", "float")
            ).addOutput(
                    new VariableDefinition("result", "float")
            ).build(),
            new NodeDefinitionBuilder("asin").addInput(
                    new VariableDefinition("value", "float")
            ).addOutput(
                    new VariableDefinition("result", "float")
            ).build(),
            new NodeDefinitionBuilder("acos").addInput(
                    new VariableDefinition("value", "float")
            ).addOutput(
                    new VariableDefinition("result", "float")
            ).build(),
            new NodeDefinitionBuilder("atan").addInput(
                    new VariableDefinition("value", "float")
            ).addOutput(
                    new VariableDefinition("result", "float")
            ).build(),
            new NodeDefinitionBuilder("floor").addInput(
                    new VariableDefinition("value", "float")
            ).addOutput(
                    new VariableDefinition("result", "float")
            ).build(),
            new NodeDefinitionBuilder("ceil").addInput(
                    new VariableDefinition("value", "float")
            ).addOutput(
                    new VariableDefinition("result", "float")
            ).build(),
            new NodeDefinitionBuilder("round").addInput(
                    new VariableDefinition("value", "float")
            ).addOutput(
                    new VariableDefinition("result", "float")
            ).build(),
            new NodeDefinitionBuilder("len").addInput(
                    new VariableDefinition("value", "void")
            ).addOutput(
                    new VariableDefinition("result", "int32")
            ).build(),
            new NodeDefinitionBuilder("sizeof").addInput(
                    new VariableDefinition("value", "void")
            ).addOutput(
                    new VariableDefinition("result", "int32")
            ).build()
    );
}
