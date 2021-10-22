package com.bradyrussell.flow.lib.adapter;

import com.bradyrussell.flow.lib.graph.*;

import java.util.List;

public class UISCoinLangFlowAdapter implements FlowAdapter<String>{
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
        return List.of();
    }

    @Override
    public List<NodeDefinition> getNativeNodes() {
        return List.of();
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
        return variable.getType() + (variable.getPointer() ? "@" : "") + " " + variable.getId() + (variable.getArray() ? "["+variable.getArrayLength()+"]" : "")+";\n";
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
        if(node == null) {
            return "END";
        }

        switch (node.getType()) {
            case "Print" -> {
                return visitPrintNode(flow.getPinConstantValue(node.getInputPins().get(0))) + visitNode(flow, flow.getNodeFromPinId(flow.getConnectedPinId(node.getPinId("FlowOut"))));
            }
        }

       return null;
    }

    public String visitPrintNode(String message) {
        return "print(\""+message+"\");\n";
    }
}
