/* (C) Brady Russell 2021 */
package com.bradyrussell.flow.lib.graph;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StructDefinition {
    @SerializedName("ID")
    @Expose
    private String id;
    @SerializedName("Color")
    @Expose
    private String color;
    @SerializedName("Variables")
    @Expose
    private List<VariableDefinition> variables = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<VariableDefinition> getVariables() {
        return variables;
    }

    public void setVariables(List<VariableDefinition> variables) {
        this.variables = variables;
    }
}