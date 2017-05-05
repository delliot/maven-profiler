package com.central1.profiler.sorting;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Table;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;

import java.util.List;
import java.util.Map;

/**
 * Full credit to jcgay on github for the original maven-profiler
 *
 * Licensed under MIT
 *
 * Modifications by Delan Elliot (delliot@central1.com)
 */
public interface Sorter {

    List<MavenProject> projects(Map<MavenProject, Stopwatch> projects);

    List<Artifact> downloads(Map<Artifact, Stopwatch> projects);

    List<Map.Entry<MojoExecution, Stopwatch>> mojoExecutionsOf(MavenProject project, Table<MavenProject, MojoExecution, Stopwatch> executions);
}
