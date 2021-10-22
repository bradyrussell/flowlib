package com.bradyrussell.flow.lib.repository.serialization;

import com.bradyrussell.flow.lib.repository.FlowDependency;
import com.google.gson.*;

import java.lang.reflect.Type;

public class FlowDependencySerializer implements JsonSerializer<FlowDependency>, JsonDeserializer<FlowDependency> {
    @Override
    public FlowDependency deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return FlowDependency.fromDependencyString(json.getAsString());
    }

    @Override
    public JsonElement serialize(FlowDependency src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.toDependencyString());
    }
}
