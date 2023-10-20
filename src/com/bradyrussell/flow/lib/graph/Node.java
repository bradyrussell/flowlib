/* (C) Brady Russell 2021 */
package com.bradyrussell.flow.lib.graph;

import com.bradyrussell.flow.lib.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Node {
    @SerializedName("ID")
    @Expose
    private String id;
    @SerializedName("X")
    @Expose
    private Integer x;
    @SerializedName("Y")
    @Expose
    private Integer y;
    @SerializedName("Values")
    @Expose
    private HashMap<String, String> values = new HashMap<>();

    private Flow flow;

    public Node() {
    }

    public Node(Flow flow) {
        this.flow = flow;
    }

    public Flow getFlow() {
        return flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }

    public Node(Flow flow, String type, int index) {
        this.flow = flow;
        setId(type, index);
    }

    public String getId() {
        return id;
    }

    public void setId(String type, int index) {
        this.id = type + Constants.NodeTypeIndexDelimiter + index;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public HashMap<String, String> getValues() {
        return values;
    }

    public void setValues(HashMap<String, String> values) {
        this.values = values;
    }

    public void setValue(String id, String value){
        values.put(id, value);
    }

    public String getType() {
        return getId().split(Constants.NodeTypeIndexDelimiter)[0];
    }

    public String getPinId(String valueId) { // returns Node_0.valueId
        return getId() + Constants.NodeIDValueDelimiter + valueId;
    }

/*    public boolean hasPin(String valueId) {
        return true;//todo
    }*/

/*    public List<String> getInputPins() {
        List<VariableDefinition> inputs = getFlow().getNodeDefinition(getType()).getInputs();
        if(inputs == null) {
            return List.of();
        }
        return inputs.stream().map((variableDefinition -> getPinId(variableDefinition.getId()))).collect(Collectors.toList());
    }

    public List<String> getOutputPins() {
        List<VariableDefinition> outputs = getFlow().getNodeDefinition(getType()).getOutputs();
        if(outputs == null) {
            return List.of();
        }
        return outputs.stream().map((variableDefinition -> getPinId(variableDefinition.getId()))).collect(Collectors.toList());
    }*/

/*    public List<String> getPins() {
        ArrayList<String> pins = new ArrayList<>(getInputPins());
        pins.addAll(getOutputPins());
        return pins;
    }

    public List<String> getInputPins(String type) {
        return List.of(getPinId("todo"));
    }

    public List<String> getOutputPins(String type) {
        return List.of(getPinId("todo"));
    }

    public List<String> getPins(String type) {
        ArrayList<String> pins = new ArrayList<>(getInputPins(type));
        pins.addAll(getOutputPins(type));
        return pins;
    }*/
}