package com.central1.profiler.reporting.template;

import com.google.common.base.Stopwatch;
import org.apache.maven.plugin.MojoExecution;

import java.util.ArrayList;
import java.util.List;

import static com.central1.profiler.reporting.Format.ms;

public class Project {

    private final String name;
    private final Stopwatch time;
    private final String groupId;
    private final String artifactId;
    private final List<EntryAndTime<MojoExecution>> mojosWithTime = new ArrayList<EntryAndTime<MojoExecution>>();

    public Project(String groupId, String artifactId, String name, Stopwatch time) {
        this.name = name;
        this.time = time;
        this.groupId = groupId;
        this.artifactId = artifactId;
    }


    public String getName() {
        return name;
    }


    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public Stopwatch getTime() {
        return time;
    }

    @Override
    public String toString() {
        return String.format("{%s, %s, totalMojos = %d}", name, ms(time), mojosWithTime.size());
    }

}
