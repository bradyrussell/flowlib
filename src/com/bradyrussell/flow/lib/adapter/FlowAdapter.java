/* (C) Brady Russell 2021 */
package com.bradyrussell.flow.lib.adapter;

import com.bradyrussell.flow.lib.graph.*;

import java.util.List;

public interface FlowAdapter<T> {
    String getName();
    boolean supportsArrays();
    boolean supportsStructs();

    List<String> getPrimitiveTypes();
    List<StructDefinition> getNativeStructs();
    List<NodeDefinition> getNativeNodes();
    List<VariableDefinition> getNativeVariables();

    T visitFlow(Flow flow);
    T visitVariableDeclaration(Flow flow, VariableDefinition variable);
    T visitStructDeclaration(Flow flow, StructDefinition struct);
    T visitNode(Flow flow, Node node);

    boolean isValidTypeLiteral(String type, String literal);
    boolean isAutoCastAllowed(String fromType, String toType);
}
