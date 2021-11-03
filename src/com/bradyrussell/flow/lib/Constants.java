/* (C) Brady Russell 2021 */
package com.bradyrussell.flow.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.function.Supplier;

public class Constants {
    public static final String NodeTypeIndexDelimiter = "#";
    public static final String NodeIDValueDelimiter = ".";
    public static final String GroupIdArtifactIdDelimiter = ".";
    public static final String ArtifactIdVersionDelimiter = ":";
    public static final String VersionPartDelimiter = ".";
    public static final String VersionTagDelimiter = "-";
    public static final String VersionRangeDelimiter = "|";
    public static final String VersionRangeGreaterThanOrEqual = "+";
    public static final String VersionRangeLessThanOrEqual = "-";
    public static final String DependencyIdRepositoryDelimiter = "@";
    public static final String RepositoryProtocolAddressDelimiter = "://";
    public static final String FlowType = "Flow";

    public static final Supplier<Gson> gson = () -> new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
}
