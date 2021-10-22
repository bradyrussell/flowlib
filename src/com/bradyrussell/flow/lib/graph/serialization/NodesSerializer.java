/* (C) Brady Russell 2021 */
package com.bradyrussell.flow.lib.graph.serialization;

import com.bradyrussell.flow.lib.graph.Node;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class NodesSerializer implements JsonSerializer<HashMap<String, Node>>, JsonDeserializer<HashMap<String, Node>> {
    private static final Type nodesList = new TypeToken<List<Node>>(){}.getType();

    @Override
    public HashMap<String, Node> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(!json.isJsonArray()) {
            throw new JsonParseException("NodesSerializer expected a json array!");
        }

        HashMap<String, Node> nodes = new HashMap<>();
        List<Node> nodeList = context.deserialize(json, nodesList);
        if(nodeList != null){
            for (Node node : nodeList) {
                nodes.put(node.getId(), node);
            }
            return nodes;
        }
        return null;
    }

    @Override
    public JsonElement serialize(HashMap<String, Node> src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.values());
    }
}
