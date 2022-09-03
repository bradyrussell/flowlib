package com.bradyrussell.flow.lib.graph.builder;

import com.bradyrussell.flow.lib.graph.StructDefinition;
import com.bradyrussell.flow.lib.graph.VariableDefinition;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StructDefinitionBuilder {
    private final StructDefinition structDefinition;

    public StructDefinitionBuilder(String id) {
        structDefinition = new StructDefinition();
        structDefinition.setId(id);
    }

    public StructDefinitionBuilder setColor(String color) {
        structDefinition.setColor(color);
        return this;
    }

    public StructDefinitionBuilder addVariable(VariableDefinition... variableDefinition) {
        List<VariableDefinition> vars = structDefinition.getVariables();
        if (vars == null) {
            vars = new ArrayList<>();
        }
        vars.addAll(Arrays.asList(variableDefinition));
        structDefinition.setVariables(vars);
        return this;
    }

    public StructDefinition build() {
        return structDefinition;
    }
}
