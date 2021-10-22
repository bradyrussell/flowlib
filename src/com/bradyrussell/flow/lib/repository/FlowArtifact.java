package com.bradyrussell.flow.lib.repository;

import com.bradyrussell.flow.lib.Constants;
import com.bradyrussell.flow.lib.graph.Flow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public interface FlowArtifact {
    String getArtifactId(); // MyExampleFlow
    String getGroupId(); // com.bradyrussell.flow
    String getVersion(); // 1.0.0-snapshot
    default String getFullyQualifiedId() { //com.bradyrussell.flow.MyExampleFlow:1.0.0-snapshot
        return getGroupId() + Constants.GroupIdArtifactIdDelimiter + getArtifactId() + Constants.ArtifactIdVersionDelimiter + getVersion();
    }
    String getRepository();

    List<FlowDependency> getDependencies();
    CompletableFuture<Flow> getFlow();

    static String[] parseFullyQualifiedId(String fullyQualifiedId) {
        String[] parsed = new String[3];
        String[] artifactAndVersion = fullyQualifiedId.split(Pattern.quote(Constants.ArtifactIdVersionDelimiter), 2);
        String[] groupIdAndArtifactId = artifactAndVersion[0].split(Pattern.quote(Constants.GroupIdArtifactIdDelimiter));
        parsed[1] = groupIdAndArtifactId[groupIdAndArtifactId.length - 1];
        List<String> groupIdParts = new ArrayList<>(List.of(groupIdAndArtifactId));
        groupIdParts.remove(groupIdParts.size() - 1);
        parsed[0] = String.join(Constants.GroupIdArtifactIdDelimiter, groupIdParts);
        parsed[2] = artifactAndVersion[1];
        return parsed;
    }

    static int compareVersion(String versionA, String versionB) {
        String[] a = versionA.split(Pattern.quote(Constants.VersionTagDelimiter))[0].replaceAll("[^\\d.]", "").split(Pattern.quote(Constants.VersionPartDelimiter));
        String[] b = versionB.split(Pattern.quote(Constants.VersionTagDelimiter))[0].replaceAll("[^\\d.]", "").split(Pattern.quote(Constants.VersionPartDelimiter));
        for (int i = 0; i < Math.max(a.length, b.length); i++) {
            if(a.length-1 < i) {
                return -1;
            }
            if(b.length-1 < i) {
                return 1;
            }
            if(a[i].equals(b[i])) {
                continue;
            }
            return (Integer.parseInt(a[i]) > Integer.parseInt(b[i])) ? 1 : -1;
        }
        return 0;
    }
}
