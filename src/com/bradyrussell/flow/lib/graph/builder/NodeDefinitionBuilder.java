package com.bradyrussell.flow.lib.graph.builder;

import com.bradyrussell.flow.lib.graph.NodeDefinition;
import com.bradyrussell.flow.lib.graph.VariableDefinition;

import java.util.ArrayList;
import java.util.List;

public class NodeDefinitionBuilder {
    private final NodeDefinition nodeDefinition;

    public NodeDefinitionBuilder(String id) {
        nodeDefinition = new NodeDefinition();
        nodeDefinition.setId(id);
    }

    public NodeDefinitionBuilder addInput(VariableDefinition variableDefinition) {
        List<VariableDefinition> inputs = nodeDefinition.getInputs();
        if(inputs == null) {
            inputs = new ArrayList<>();
        }
        inputs.add(variableDefinition);
        return this;
    }

    public NodeDefinitionBuilder addOutput(VariableDefinition variableDefinition) {
        List<VariableDefinition> outputs = nodeDefinition.getOutputs();
        if(outputs == null) {
            outputs = new ArrayList<>();
        }
        outputs.add(variableDefinition);
        return this;
    }

    public NodeDefinitionBuilder addFlowInput(String flowInput) {
        List<String> inputs = nodeDefinition.getFlowInputs();
        if(inputs == null) {
            inputs = new ArrayList<>();
        }
        inputs.add(flowInput);
        return this;
    }

    public NodeDefinitionBuilder addFlowOutput(String flowOutput) {
        List<String> outputs = nodeDefinition.getFlowOutputs();
        if(outputs == null) {
            outputs = new ArrayList<>();
        }
        outputs.add(flowOutput);
        return this;
    }

    public NodeDefinition build() {
        return nodeDefinition;
    }
}
