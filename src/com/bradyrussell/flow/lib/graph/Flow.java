/* (C) Brady Russell 2021 */
package com.bradyrussell.flow.lib.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.bradyrussell.flow.lib.Constants;
import com.bradyrussell.flow.lib.adapter.FlowAdapter;
import com.bradyrussell.flow.lib.graph.serialization.NodeDefinitionsSerializer;
import com.bradyrussell.flow.lib.graph.serialization.NodesSerializer;
import com.bradyrussell.flow.lib.repository.FlowDependency;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

public class Flow {
    @SerializedName("Meta")
    @Expose
    private HashMap<String, String> meta = new HashMap<>();
    @SerializedName("Structs")
    @Expose
    private List<StructDefinition> structs = new ArrayList<>();
    @SerializedName("Nodes")
    @Expose
    @JsonAdapter(NodesSerializer.class)
    private HashMap<String, Node> nodes = new HashMap<>();
    @SerializedName("Connections")
    @Expose
    private List<Connection> connections = new ArrayList<>();
    @SerializedName("NodeDefinitions")
    @Expose
    @JsonAdapter(NodeDefinitionsSerializer.class)
    private HashMap<String, NodeDefinition> nodeDefinitions = new HashMap<>();
    @SerializedName("Include")
    @Expose
    private List<FlowDependency> dependencies = new ArrayList<>();

    public String getMeta(String attribute) {
        return meta.get(attribute);
    }

    public void setMeta(String attribute, String value) {
        meta.put(attribute, value);
    }

    public HashMap<String, String> getMeta() {
        return meta;
    }

    public void setMeta(HashMap<String, String> meta) {
        this.meta = meta;
    }

    public List<StructDefinition> getStructs() {
        return structs;
    }

    public void setStructs(List<StructDefinition> structs) {
        this.structs = structs;
    }

    public void addStruct(StructDefinition struct) {
        structs.add(struct);
    }

    public Collection<Node> getNodes() {
        return nodes.values();
    }

    public void setNodes(List<Node> nodeList) {
        for (Node node : nodeList) {
            nodes.put(node.getId(), node);
        }
    }

    public boolean addNode(Node node) {
        if (nodes.containsKey(node.getId())) {
            return false;
        }
        nodes.put(node.getId(), node);
        return true;
    }

    public void removeNode(Node node) {
        List<Connection> toRemove = new ArrayList<>();
        for (Connection connection : getConnections()) {
            if(connection.getBetween().contains(node.getId())) {
                toRemove.add(connection);
            }
        }
        connections.removeAll(toRemove);
        nodes.remove(node.getId());
    }

    public Node getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    public void addConnection(Connection connection) {
        connections.add(connection);
    }

    public HashMap<String, NodeDefinition> getNodeDefinitions() {
        return nodeDefinitions;
    }

    public void setNodeDefinitions(List<NodeDefinition> nodeDefinitions) {
        for (NodeDefinition nodeDefinition : nodeDefinitions) {
            this.nodeDefinitions.put(nodeDefinition.getId(), nodeDefinition);
        }
    }

    public List<FlowDependency> getDependencies() {
        return dependencies;
    }

    public void addDependency(FlowDependency dependency) {
        dependencies.add(dependency);
    }

    public boolean hasNodeDefinition(String nodeType) {
        return nodeDefinitions.containsKey(nodeType);
    }

    public boolean addNodeDefinition(NodeDefinition nodeDefinition) {
        if(hasNodeDefinition(nodeDefinition.getId())) {
            return false;
        }
        nodeDefinitions.put(nodeDefinition.getId(), nodeDefinition);
        return true;
    }

    public NodeDefinition getNodeDefinition(String nodeType) {
        return nodeDefinitions.get(nodeType);
    }

    public int getNextNodeIndex(String nodeType) {
        int highestSeen = -1;
        for (String node : nodes.keySet()) {
            String[] nodeIdParts = node.split(Pattern.quote(Constants.NodeTypeIndexDelimiter));
            if(nodeIdParts[0].equals(nodeType)) {
                int index = Integer.parseInt(nodeIdParts[1]);
                if(index > highestSeen) {
                    highestSeen = index;
                }
            }
        }
        return highestSeen + 1;
    }

    public String getConnectedPinId(String pinId) {
        for (Connection connection : connections) {
            if(connection.getBetween().contains(pinId)) {
                for (String connectedPin : connection.getBetween()) {
                    if(!connectedPin.equals(pinId)) {
                        return connectedPin;
                    }
                }
            }
        }
        return null;
    }

    public String getConnectedPinId(String nodeId, String value) {
        return getConnectedPinId(nodeId + Constants.NodeIDValueDelimiter + value);
    }

    public Node getNodeFromPinId(String pinId) {
        return getNode(pinId.split(Pattern.quote(Constants.NodeIDValueDelimiter))[0]);
    }

    public String getPinConstantValue(String pinId) {
        return getNodeFromPinId(pinId).getValues().get(pinId.split(Pattern.quote(Constants.NodeIDValueDelimiter))[1]);
    }

    public void load() {
        for (Node node : nodes.values()) {
            node.setFlow(this);
        }
    }
}