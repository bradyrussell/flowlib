package com.bradyrussell.flow.lib.repository;

import com.bradyrussell.flow.lib.graph.Flow;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FlowRepository {
    String getRepositoryString();
    CompletableFuture<Flow> getFlowByFullyQualifiedId(String fullyQualifiedId);
    CompletableFuture<FlowArtifact> getArtifactByFullyQualifiedId(String fullyQualifiedId);
    CompletableFuture<List<FlowArtifact>> getArtifactsByGroupId(String groupId);
    CompletableFuture<List<FlowArtifact>> getArtifactsByArtifactId(String artifactId);
    CompletableFuture<List<FlowArtifact>> searchArtifacts(String keywords);
    CompletableFuture<List<FlowArtifact>> searchArtifacts(String groupId, String artifactId, String minimumVersion, String maximumVersion);
    default CompletableFuture<FlowArtifact> getArtifactByDependency(FlowDependency dependency) {
        return searchArtifacts(dependency.groupId, dependency.artifactId, dependency.minimumVersion, dependency.maximumVersion).thenApply((flowArtifacts -> {
            flowArtifacts.sort((o1, o2) -> FlowArtifact.compareVersion(o1.getVersion(), o2.getVersion()));
            return flowArtifacts.get(flowArtifacts.size() - 1);
        }));
    }
}
