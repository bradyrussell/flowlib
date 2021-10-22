package com.bradyrussell.flow.lib.repository;

import com.bradyrussell.flow.lib.Constants;
import com.bradyrussell.flow.lib.repository.serialization.FlowDependencySerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@JsonAdapter(FlowDependencySerializer.class)
public class FlowDependency {
    public String groupId;
    public String artifactId;
    public String minimumVersion;
    public String maximumVersion;
    public String repository;

    public FlowDependency() {
    }

    public FlowDependency(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.minimumVersion = version;
        this.maximumVersion = version;
    }

    public FlowDependency(String groupId, String artifactId, String version, boolean isMinimum) {
        this(groupId, artifactId, isMinimum ? version : null, isMinimum ? null : version);
    }

    public FlowDependency(String groupId, String artifactId, String minimumVersion, String maximumVersion) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.minimumVersion = minimumVersion;
        this.maximumVersion = maximumVersion;
    }

    public boolean isMatchingArtifact(String fullyQualifiedArtifactId) {
        String[] parsed = FlowArtifact.parseFullyQualifiedId(fullyQualifiedArtifactId);
        if (!groupId.equals(parsed[0])) {
            return false;
        }
        if (!artifactId.equals(parsed[1])) {
            return false;
        }
        if (minimumVersion != null && FlowArtifact.compareVersion(minimumVersion, parsed[2]) > 0) {
            return false;
        }
        return maximumVersion == null || FlowArtifact.compareVersion(maximumVersion, parsed[2]) >= 0;
    }

    public String toDependencyString() {
        String versionString;
        if (minimumVersion == null) {
            versionString = maximumVersion + Constants.VersionRangeLessThanOrEqual;
        } else if (maximumVersion == null) {
            versionString = minimumVersion + Constants.VersionRangeGreaterThanOrEqual;
        } else if (minimumVersion.equals(maximumVersion)) {
            versionString = maximumVersion;
        } else {
            versionString = minimumVersion + Constants.VersionRangeDelimiter + maximumVersion;
        }
        return groupId + Constants.GroupIdArtifactIdDelimiter + artifactId + Constants.ArtifactIdVersionDelimiter + versionString + Constants.DependencyIdRepositoryDelimiter + repository;
    }

    public static FlowDependency fromDependencyString(String dependencyString) {
        FlowDependency flowDependency = new FlowDependency();

        String[] dependencyAndRepository = dependencyString.split(Pattern.quote(Constants.DependencyIdRepositoryDelimiter), 2);
        flowDependency.repository = dependencyAndRepository[1];
        String[] artifactAndVersion = dependencyAndRepository[0].split(Pattern.quote(Constants.ArtifactIdVersionDelimiter), 2);
        String[] groupIdAndArtifactId = artifactAndVersion[0].split(Pattern.quote(Constants.GroupIdArtifactIdDelimiter));
        flowDependency.artifactId = groupIdAndArtifactId[groupIdAndArtifactId.length - 1];
        List<String> groupIdParts = new ArrayList<>(List.of(groupIdAndArtifactId));
        groupIdParts.remove(groupIdParts.size() - 1);
        flowDependency.groupId = String.join(Constants.GroupIdArtifactIdDelimiter, groupIdParts);

        if (artifactAndVersion[1].contains(Constants.VersionRangeDelimiter)) {
            String[] versionRange = artifactAndVersion[1].split(Pattern.quote(Constants.VersionRangeDelimiter), 2);
            flowDependency.minimumVersion = versionRange[0];
            flowDependency.maximumVersion = versionRange[1];

            if (FlowArtifact.compareVersion(flowDependency.minimumVersion, flowDependency.maximumVersion) >= 0) {
                throw new IllegalArgumentException("Minimum version must be less than maximum version! " + flowDependency.minimumVersion + " is not less than " + flowDependency.maximumVersion + ".");
            }
        } else if (artifactAndVersion[1].endsWith(Constants.VersionRangeGreaterThanOrEqual)) {
            flowDependency.minimumVersion = artifactAndVersion[1].substring(0, artifactAndVersion[1].length() - 1);
        } else if (artifactAndVersion[1].endsWith(Constants.VersionRangeLessThanOrEqual)) {
            flowDependency.maximumVersion = artifactAndVersion[1].substring(0, artifactAndVersion[1].length() - 1);
        } else {
            flowDependency.minimumVersion = artifactAndVersion[1];
            flowDependency.maximumVersion = artifactAndVersion[1];
        }
        return flowDependency;
    }
}
