/* (C) Brady Russell 2021 */
package com.bradyrussell.flow.lib.graph;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public class VariableDefinition {
    @SerializedName("ID")
    @Expose
    private String id;
    @SerializedName("Type")
    @Expose
    private String type;
    @SerializedName("Array")
    @Expose
    private Boolean array; // if null assume not array
    @SerializedName("Pointer")
    @Expose
    private Boolean pointer; // if null assume not pointer
    @SerializedName("ArrayLength")
    @Expose
    private Integer arrayLength; // if null assume not array

    public VariableDefinition() {
    }

    public VariableDefinition(String id, String type) {
        this.id = id;
        this.type = type;
        this.pointer = null;
        this.array = null;
        this.arrayLength = null;
    }

    public VariableDefinition(String id, String type, Integer arrayLength) {
        this.id = id;
        this.type = type;
        this.arrayLength = arrayLength;
        this.array = true;
        this.pointer = false;
    }

    public VariableDefinition(String id, String type, boolean pointer) {
        this.id = id;
        this.type = type;
        this.pointer = pointer ? true : null;
        this.array = null;
        this.arrayLength = null;
    }

    public VariableDefinition(String id, String type, Integer arrayLength, boolean pointer) {
        this.id = id;
        this.type = type;
        this.arrayLength = arrayLength;
        this.array = true;
        this.pointer = pointer ? true : null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getArray() {
        return array != null && array;
    }

    public void setArray(Boolean array) {
        this.array = array;
    }

    public void setArray(Boolean array, int length) {
        this.array = array;
        this.arrayLength = length;
    }

    public Boolean getPointer() {
        return pointer != null && pointer;
    }

    public void setPointer(Boolean pointer) {
        this.pointer = pointer;
    }

    public Integer getArrayLength() {
        return arrayLength;
    }

    public void setArrayLength(Integer arrayLength) {
        this.arrayLength = arrayLength;
    }
}