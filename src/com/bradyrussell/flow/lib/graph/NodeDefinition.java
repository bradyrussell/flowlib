package com.bradyrussell.flow.lib.graph;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NodeDefinition {
    @SerializedName("ID")
    @Expose
    private String id;
    @SerializedName("Inputs")
    @Expose
    private List<VariableDefinition> inputs = null;
    @SerializedName("Outputs")
    @Expose
    private List<VariableDefinition> outputs = null;
    @SerializedName("FlowInputs")
    @Expose
    private List<String> flowInputs = null; // if null assume a single Flow pin called FlowIn
    @SerializedName("FlowOutputs")
    @Expose
    private List<String> flowOutputs = null; // if null assume a single Flow pin called FlowOut

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<VariableDefinition> getInputs() {
        return inputs;
    }

    public void setInputs(List<VariableDefinition> inputs) {
        this.inputs = inputs;
    }

    public List<VariableDefinition> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<VariableDefinition> outputs) {
        this.outputs = outputs;
    }

    public List<String> getFlowInputs() {
        return flowInputs == null ? List.of("FlowIn") : flowInputs;
    }

    public void setFlowInputs(List<String> flowInputs) {
        this.flowInputs = flowInputs;
    }

    public List<String> getFlowOutputs() {
        return flowOutputs == null ? List.of("FlowOut") : flowOutputs;
    }

    public void setFlowOutputs(List<String> flowOutputs) {
        this.flowOutputs = flowOutputs;
    }
}