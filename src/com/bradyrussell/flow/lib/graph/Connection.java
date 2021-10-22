/* (C) Brady Russell 2021 */
package com.bradyrussell.flow.lib.graph;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Connection {
    @SerializedName("Between")
    @Expose
    private List<String> between = null;
    @SerializedName("Reference")
    @Expose
    private Boolean reference;

    public List<String> getBetween() {
        return between;
    }

    public void setBetween(List<String> between) {
        this.between = between;
    }

    public Boolean getReference() {
        return reference;
    }

    public void setReference(Boolean reference) {
        this.reference = reference;
    }
}