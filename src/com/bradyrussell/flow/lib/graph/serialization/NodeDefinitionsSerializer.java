/* (C) Brady Russell 2021 */
package com.bradyrussell.flow.lib.graph.serialization;

import com.bradyrussell.flow.lib.graph.Node;
import com.bradyrussell.flow.lib.graph.NodeDefinition;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class NodeDefinitionsSerializer implements JsonSerializer<HashMap<String, NodeDefinition>>, JsonDeserializer<HashMap<String, NodeDefinition>> {
    private static final Type nodesList = new TypeToken<List<NodeDefinition>>(){}.getType();

    @Override
    public HashMap<String, NodeDefinition> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(!json.isJsonArray()) {
            throw new JsonParseException("NodeDefinitionsSerializer expected a json array!");
        }

        HashMap<String, NodeDefinition> nodes = new HashMap<>();
        List<NodeDefinition> nodeList = context.deserialize(json, nodesList);
        if(nodeList != null){
            for (NodeDefinition node : nodeList) {
                nodes.put(node.getId(), node);
            }
            return nodes;
        }
        return null;
    }

    @Override
    public JsonElement serialize(HashMap<String, NodeDefinition> src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.values());
    }
}
