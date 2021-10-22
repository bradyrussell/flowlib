package com.bradyrussell.flow.lib.repository;

import com.bradyrussell.flow.lib.graph.Flow;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class FlowArtifactBase implements FlowArtifact {
    @SerializedName("GroupID")
    @Expose
    public String groupId;
    @SerializedName("ArtifactID")
    @Expose
    public String artifactId;
    @SerializedName("Version")
    @Expose
    public String version;
    @SerializedName("Dependencies")
    @Expose
    public List<FlowDependency> dependencies;
    @SerializedName("Repository")
    @Expose
    public String repository;

    @Override
    public String getArtifactId() {
        return artifactId;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public List<FlowDependency> getDependencies() {
        return dependencies;
    }

    @Override
    public String getRepository() {
        return repository;
    }

    public FlowRepository getFlowRepository() {
        return RepositoryProtocolRegistry.get(repository);
    }

    @Override
    public CompletableFuture<Flow> getFlow() {
        return getFlowRepository().getFlowByFullyQualifiedId(getFullyQualifiedId());
    }
}
