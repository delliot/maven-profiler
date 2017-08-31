package com.central1.profiler;

import com.central1.profiler.parser.GitCommitParser;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.base.Supplier;
import com.central1.profiler.reporting.template.Data;
import com.central1.profiler.reporting.template.Project;
import com.central1.profiler.sorting.Sorter;
import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.eventspy.EventSpy;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
//import java.util.Map.Entry;

import static com.central1.profiler.KnownElapsedTimeTicker.aStopWatchWithElapsedTime;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.maven.execution.ExecutionEvent.Type.ProjectDiscoveryStarted;
import static org.apache.maven.execution.ExecutionEvent.Type.SessionStarted;

/**
 * Full credit to jcgay on github for the original maven-profiler
 *
 *
 *
 * Modifications by Delan Elliot (delliot@central1.com)
 */
@Component(role = EventSpy.class, hint = "profiler", description = "Measure times taken by Maven.")
public class ProfilerEventSpy extends AbstractEventSpy {

    private final Statistics statistics;
    private final Configuration configuration;
    private final Supplier<Date> now;

    private static final String VERSION = "1.0.1-SNAPSHOT";

    @Requirement
    private Logger logger;

    /**
     * <p>
     *     Entry point called by maven.
     * </p>
     */
    public ProfilerEventSpy() {
        this.statistics = new Statistics();
        this.configuration = Configuration.read();
        this.now = new Supplier<Date>() {
            @Override
            public Date get() {
                return new Date();
            }
        };
    }

    /**
     * Testing entry point.
     * @param statistics
     * @param configuration
     * @param now
     */
    @VisibleForTesting
    ProfilerEventSpy(Statistics statistics, Configuration configuration, Supplier<Date> now) {
        this.statistics = statistics;
        this.configuration = configuration;
        this.logger = new ConsoleLogger();
        this.now = now;
    }


    /**
     *
     */
    @Override
    public void init(Context context) throws Exception {
        super.init(context);

        if (true) {
            logger.info("Profiling mvn execution...");
        }

    }

    /**
     *
     * @param event
     * @throws Exception
     */
    @Override
    public void onEvent(Object event) throws Exception {
        super.onEvent(event);
        if (configuration.isProfiling()) {
            if (event instanceof DefaultMavenExecutionRequest) {
                DefaultMavenExecutionRequest mavenEvent = (DefaultMavenExecutionRequest) event;
                if(mavenEvent.isOffline()) {
                    configuration.setProfiling(false);
                }
                statistics.setGoals(new LinkedHashSet<String>(mavenEvent.getGoals()));
                statistics.setProperties(mavenEvent.getUserProperties());
            } else if (event instanceof ExecutionEvent) {
                storeExecutionEvent((ExecutionEvent) event);
                trySaveTopProject((ExecutionEvent) event);
                storeStartTime((ExecutionEvent) event);
            }
        }
    }

    /**
     *
     * @param event
     */
    private void storeStartTime(ExecutionEvent event) {
        if (event.getType() == ProjectDiscoveryStarted) {
            statistics.setStartTime(event.getSession().getStartTime());
        }
    }

    /**
     *
     * @return
     */
    private String machineName() {
        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            String hostname = addr.getHostName();
            return hostname;
        } catch (UnknownHostException e) {
            logger.error("could not get machine name", e);
            return null;
        }
    }

    /**
     *
     * @return
     */
    private String developerName() {
        return System.getProperty("user.name");
    }


    /**
     *
     * @return
     */
    private String ipAddress() {
        URL whatismyip = null;
        try {
            whatismyip = new URL("http://checkip.amazonaws.com");
        } catch (MalformedURLException e) {
            logger.debug(e.getMessage());
        }

        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(
                whatismyip.openStream()));
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }

        String ip = null;
        try {
            ip = in.readLine();
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
        return ip;
    }

    /**
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        super.close();
        HardwareInvestigator hi = new HardwareInvestigator();
        hi.buildSysInfo();
        Map<String, String> sysInfo = hi.getSysInfo();

        if (configuration.isProfiling()) {
            Date finishTime = now.get();
            long time = System.currentTimeMillis();
            Data context = new Data()
                .setProjects( sortedProjects() )
                .setDate( finishTime )
                .setName( statistics.topProject().getName() )
                .setTopArtifactId( statistics.topProject().getArtifactId() )
                .setTopGroupId( statistics.topProject().getGroupId() )
                .setCpuModel( sysInfo.get( "cpu_model" ) )
                .setCpuFreq( sysInfo.get( "cpu_freq" ) )
                .setRamAmt( sysInfo.get( "memory" ) )
                .setGoals( Joiner.on(' ').join( statistics.goals() ) )
                .setParameters( statistics.properties() )
                .setMachineName( machineName() )
                .setDeveloperName( developerName() )
                .setIpAddress( ipAddress() )
                .setOperatingSystem()
                .setBuildSucceeded( statistics.getSucceeded() )
                .setKey( time )
                .setCommit( getCommitId() )
                .setVersion( VERSION );

            if (statistics.getStartTime() != null) {
                context.setBuildTime(aStopWatchWithElapsedTime(MILLISECONDS.toNanos(finishTime.getTime() - statistics.getStartTime().getTime())));
            }

            configuration.reporter().write(context);
        }
    }

    /**
     *
     * @param event
     */
    private void trySaveTopProject(ExecutionEvent event) {
        if (event.getType() == SessionStarted) {
            statistics.setTopProject(event.getSession().getTopLevelProject());
        }
    }


    /**
     *
     * @return
     */
    private List<Project> sortedProjects() {
        Sorter sorter = configuration.sorter();

        List<Project> result = new ArrayList<Project>();
        Map<MavenProject, Stopwatch> allProjectsWithTimer = statistics.projects();
        for (MavenProject project : sorter.projects(allProjectsWithTimer)) {
            Project currentProject = new Project(project.getGroupId(), project.getArtifactId(), project.getName(), allProjectsWithTimer.get(project));
            result.add(currentProject);
        }
        return result;
    }


    /**
     *
     * @param event
     */
    private void storeExecutionEvent(ExecutionEvent event) {
        logger.debug(String.format("Received event (%s): %s. Type: %s", event.getClass(), event, event.getType().toString()));

        MavenProject currentProject = event.getSession().getCurrentProject();
        switch (event.getType()) {
            case ProjectStarted:
                statistics.startProject(currentProject);
                break;
            case ProjectSucceeded:
            case ProjectFailed:
                statistics.stopProject(currentProject, event.getType());
                break;
            case MojoStarted:
                statistics.startExecution(currentProject, event.getMojoExecution());
                break;
            case MojoSucceeded:
            case MojoFailed:
                statistics.stopExecution(currentProject, event.getMojoExecution());
                break;
            default:
                break;
        }
    }

    private String getCommitId() {
        try {
            GitCommitParser gitCommitParser = new GitCommitParser();

            String s = gitCommitParser.getHeadId();

            if (s != null )
            {
                return s;
            }

        } catch (IOException ioe) {
            logger.debug("Git dir cannot be found!");
            logger.debug(ioe.getMessage());
        }

        return "";
    }
}
