package com.bradyrussell.flow.lib.repository.rest;

import com.bradyrussell.flow.lib.Constants;
import com.bradyrussell.flow.lib.graph.Flow;
import com.bradyrussell.flow.lib.repository.FlowArtifact;
import com.bradyrussell.flow.lib.repository.FlowArtifactBase;
import com.bradyrussell.flow.lib.repository.FlowRepository;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RestFlowRepository implements FlowRepository {
    private static final int CacheExpirySeconds = 300;

    @SerializedName("RepositoryURL")
    @Expose
    private String baseUrl;

    private final HashMap<String, String> cache = new HashMap<>();
    private final HashMap<String, Long> cachedAt = new HashMap<>();

    public RestFlowRepository() {
    }

    public RestFlowRepository(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void clearCache() {
        cache.clear();
    }

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    @Override
    public String getRepositoryString() {
        return "rest" + Constants.RepositoryProtocolAddressDelimiter + baseUrl;
    }

    @Override
    public CompletableFuture<FlowArtifact> getArtifactByFullyQualifiedId(String fullyQualifiedId) {
        String cacheKey = "artifact#" + fullyQualifiedId;
        if (cache.containsKey(cacheKey) && cachedAt.get(cacheKey) + CacheExpirySeconds > Instant.now().getEpochSecond()) {
            return CompletableFuture.completedFuture(new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(cache.get(cacheKey), RestFlowArtifact.class));
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/artifact/" + fullyQualifiedId + "/"))
                .timeout(Duration.ofMinutes(2))
                .header("Accept", "application/json")
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).thenApplyAsync((json) -> {
                    cache.put(cacheKey, json);
                    cachedAt.put(cacheKey, Instant.now().getEpochSecond());
                    return json;
                }).thenApplyAsync((json) -> new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(json, RestFlowArtifact.class));
    }

    @Override
    public CompletableFuture<List<FlowArtifact>> getArtifactsByGroupId(String groupId) {
        return null;
    }

    @Override
    public CompletableFuture<List<FlowArtifact>> getArtifactsByArtifactId(String artifactId) {
        return null;
    }

    @Override
    public CompletableFuture<List<FlowArtifact>> searchArtifacts(String groupId, String artifactId, String minimumVersion, String maximumVersion) {
        return null;
    }

    @Override
    public CompletableFuture<Flow> getFlowByFullyQualifiedId(String fullyQualifiedId) {
        String cacheKey = "flow#" + fullyQualifiedId;
        if (cache.containsKey(cacheKey) && cachedAt.get(cacheKey) + CacheExpirySeconds > Instant.now().getEpochSecond()) {
            return CompletableFuture.completedFuture(new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(cache.get(cacheKey), Flow.class));
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/artifact/" + fullyQualifiedId + "/flow"))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body).thenApplyAsync((json) -> {
                    cache.put(cacheKey, json);
                    cachedAt.put(cacheKey, Instant.now().getEpochSecond());
                    return json;
                }).thenApplyAsync((json) -> new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(json, Flow.class));
    }
}
