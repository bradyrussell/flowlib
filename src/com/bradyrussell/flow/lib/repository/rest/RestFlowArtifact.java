package com.bradyrussell.flow.lib.repository.rest;

import com.bradyrussell.flow.lib.graph.Flow;
import com.bradyrussell.flow.lib.repository.FlowArtifactBase;
import com.bradyrussell.flow.lib.repository.FlowDependency;
import com.bradyrussell.flow.lib.repository.FlowRepository;
import com.bradyrussell.flow.lib.repository.RepositoryProtocolRegistry;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class RestFlowArtifact extends FlowArtifactBase {
    public RestFlowArtifact() {
    }

    public RestFlowArtifact(String groupId, String artifactId, String version, List<String> dependencies, String repository) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
        this.dependencies = dependencies.stream().map(FlowDependency::fromDependencyString).toList();
        this.repository = repository;
    }
}
