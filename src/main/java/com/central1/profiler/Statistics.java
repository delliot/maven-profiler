package com.central1.profiler;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;
import org.slf4j.Logger;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableMap;
import static org.slf4j.LoggerFactory.getLogger;

public class Statistics {

    private static final Logger LOGGER = getLogger(Statistics.class);

    private final Map<MavenProject, Stopwatch> projects = new LinkedHashMap<MavenProject, Stopwatch>();
    private final Map<MavenProject, Map<MojoExecution, Stopwatch>> executions = new LinkedHashMap<MavenProject, Map<MojoExecution, Stopwatch>>();
    private final Map<Artifact, Stopwatch> downloadTimers = new LinkedHashMap<Artifact, Stopwatch>();

    private MavenProject topProject;
    private Set<String> goals = emptySet();
    private Properties properties = new Properties();
    private Date startTime;
    private boolean succeeded = true;

    /**
     *
     * @param topProject
     * @return
     */
    public Statistics setTopProject(MavenProject topProject) {
        this.topProject = topProject;
        return this;
    }

    /**
     *
     * @return
     */
    public MavenProject topProject() {
        return topProject;
    }

    /**
     *
     * @param goals
     * @return
     */
    public Statistics setGoals(Set<String> goals) {
        this.goals = goals;
        return this;
    }

    /**
     *
     * @return
     */
    public Iterable<String> goals() {
        return goals;
    }

    /**
     *
     * @param properties
     * @return
     */
    public Statistics setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    /**
     *
     * @return
     */
    public Properties properties() {
        return properties;
    }

    /**
     *
     * @return
     */
    public Map<MavenProject, Stopwatch> projects() {
        return unmodifiableMap(projects);
    }

    /**
     *
     * @return
     */
    public Table<MavenProject, MojoExecution, Stopwatch> executions() {
        ImmutableTable.Builder<MavenProject, MojoExecution, Stopwatch> builder = ImmutableTable.builder();
        for (Map.Entry<MavenProject, Map<MojoExecution, Stopwatch>> byProject : executions.entrySet()) {
            for (Map.Entry<MojoExecution, Stopwatch> executionTimer : byProject.getValue().entrySet()) {
                builder.put(byProject.getKey(), executionTimer.getKey(), executionTimer.getValue());
            }
        }
        return builder.build();
    }

    /**
     *
     * @return
     */
    public Map<Artifact, Stopwatch> downloads() {
        return unmodifiableMap(downloadTimers);
    }

    /**
     *
     * @param project
     * @return
     */
    public synchronized Statistics startProject(MavenProject project) {
        LOGGER.debug("Starting timer for project: {}", project);
        projects.put(project, new Stopwatch().start());
        return this;
    }


    /**
     *
     * @param project
     * @param execution
     * @return
     */
    public synchronized Statistics startExecution(MavenProject project, MojoExecution execution) {
        LOGGER.debug("Starting timer for mojo [{}] in project [{}].", execution, project);
        Map<MojoExecution, Stopwatch> projectExecutions = executions.get(project);
        if (projectExecutions == null) {
            projectExecutions = new LinkedHashMap<MojoExecution, Stopwatch>();
            executions.put(project, projectExecutions);
        }
        projectExecutions.put(execution, new Stopwatch().start());
        return this;
    }


    /**
     *
     * @param project
     * @param execution
     * @return
     */
    public Statistics stopExecution(MavenProject project, MojoExecution execution) {
        LOGGER.debug("Stopping timer for mojo [{}] in project [{}].", execution, project);
        Map<MojoExecution, Stopwatch> projectExecutions = executions.get(project);
        if (projectExecutions == null) {
            throw new IllegalStateException("Cannot stop a timer execution because project has not been registered");
        }
        Stopwatch stopwatch = projectExecutions.get(execution);
        if (stopwatch == null) {
            throw new IllegalStateException("Cannot stop a timer execution because execution has not been registered");
        }
        stopwatch.stop();
        return this;
    }

    /**
     *
     * @param project
     * @param type
     * @return
     */
    public Statistics stopProject(MavenProject project, ExecutionEvent.Type type) {
        LOGGER.debug("Stopping timer for project: {}", project);

        if(type == ExecutionEvent.Type.ProjectFailed) {
            succeeded = false;
        }

        projects.get(project).stop();
        return this;
    }

    /**
     *
     * @param succeeded
     * @return
     */
    public Statistics setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
        return this;
    }

    /**
     *
     * @return
     */
    public boolean getSucceeded() {
        return this.succeeded;
    }

    /**
     *
     * @param startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     *
     * @return
     */
    public Date getStartTime() {
        return startTime;
    }
}
