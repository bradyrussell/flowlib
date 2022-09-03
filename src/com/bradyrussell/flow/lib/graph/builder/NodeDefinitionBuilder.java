package com.bradyrussell.flow.lib.graph.builder;

import com.bradyrussell.flow.lib.graph.NodeDefinition;
import com.bradyrussell.flow.lib.graph.VariableDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodeDefinitionBuilder {
    private final NodeDefinition nodeDefinition;

    public NodeDefinitionBuilder(String id) {
        nodeDefinition = new NodeDefinition();
        nodeDefinition.setId(id);
    }

    public NodeDefinitionBuilder addInput(VariableDefinition... variableDefinition) {
        List<VariableDefinition> inputs = nodeDefinition.getInputs();
        if(inputs == null) {
            inputs = new ArrayList<>();
        }
        inputs.addAll(Arrays.asList(variableDefinition));
        nodeDefinition.setInputs(inputs);
        return this;
    }

    public NodeDefinitionBuilder addOutput(VariableDefinition... variableDefinition) {
        List<VariableDefinition> outputs = nodeDefinition.getOutputs();
        if(outputs == null) {
            outputs = new ArrayList<>();
        }
        outputs.addAll(Arrays.asList(variableDefinition));
        nodeDefinition.setOutputs(outputs);
        return this;
    }

    public NodeDefinitionBuilder addFlowInput(String... flowInput) {
        List<String> inputs = nodeDefinition.getFlowInputs();
        if(inputs == null) {
            inputs = new ArrayList<>();
        }
        inputs.addAll(Arrays.asList(flowInput));
        nodeDefinition.setFlowInputs(inputs);
        return this;
    }

    public NodeDefinitionBuilder addFlowOutput(String... flowOutput) {
        List<String> outputs = nodeDefinition.getFlowOutputs();
        if(outputs == null) {
            outputs = new ArrayList<>();
        }
        outputs.addAll(Arrays.asList(flowOutput));
        nodeDefinition.setFlowOutputs(outputs);
        return this;
    }

    public NodeDefinition build() {
        return nodeDefinition;
    }
}
