package com.central1.profiler.reporting.template;

import com.google.common.base.Stopwatch;
import org.eclipse.aether.artifact.Artifact;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static com.google.common.base.Objects.firstNonNull;

public class Data {

    private List<Project> projects;
    private List<EntryAndTime<Artifact>> downloads;
    private Stopwatch totalDownloadTime;
    private Stopwatch buildTime;
    private Date date;
    private Properties parameters;
    private String name;
    private String goals;
    private String machineName;
    private String developerName;
    private String operatingSystem;
    private String cpuModel;
    private String cpuFreq;
    private String ramAmt;
    private String ipAddress;
    private String version;
    private String topGroupId;
    private String topArtifactId;
    private String commit;
    private long key;
    private boolean buildSucceeded;

    public List<Project> getProjects() {
        return firstNonNull(projects, Collections.<Project>emptyList());
    }

    public Stopwatch getBuildTime() {
        return buildTime;
    }

    public String getFormattedDate() {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
    }

    public String getName() {
        return name;
    }

    public String getGoals() {
        return goals;
    }

    public long getKey()
    {
        return key;
    }

    public boolean getBuildSucceeded() {
        return buildSucceeded;
    }

    public String getMachineName() {
        return machineName;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public Properties getParameters() {
        return parameters == null ? new Properties() : parameters;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public String getOperatingSystem()
    {
        return operatingSystem;
    }

    public String getVersion() {
        return version;
    }

    public String getTopGroupId() {
        return topGroupId;
    }

    public String getTopArtifactId() {
        return topArtifactId;
    }

    public String getCommit() {
        return commit;
    }


    public String getCpuModel() {
        return cpuModel;
    }

    public String getCpuFreq() {
        return cpuFreq;
    }


    public String getRamAmt() {
        return ramAmt;
    }

    public Data setRamAmt(String ramAmt) {
        this.ramAmt = ramAmt;
        return this;
    }

    public Data setTopArtifactId(String topArtifactId) {
        this.topArtifactId = topArtifactId;
        return this;
    }

    public Data setTopGroupId(String topGroupId) {
        this.topGroupId = topGroupId;
        return this;
    }

    public Data setCpuModel(String cpuModel) {
        this.cpuModel = cpuModel;
        return this;
    }

    public Data setCpuFreq(String cpuFreq) {
        this.cpuFreq = cpuFreq;
        return this;
    }

    public Data setVersion(String version) {
        this.version = version;
        return this;
    }

    public Data setCommit(String commit) {
        this.commit = commit;
        return this;
    }

    public Data setProjects(List<Project> projects) {
        this.projects = projects;
        return this;
    }

    public Data setOperatingSystem() {
        this.operatingSystem = System.getProperty("os.name") + " " + System.getProperty("os.version");
        return this;
    }

    public Data setBuildSucceeded(boolean success) {
        this.buildSucceeded = success;
        return this;
    }
    public Data setDeveloperName(String developerName) {
        this.developerName = developerName;
        return this;
    }

    public Data setKey(long timeSinceEpoch) {
        this.key = (timeSinceEpoch * 1000) + (this.machineName.hashCode() % 1000);
        return this;
    }

    public Data setMachineName(String machineName) {
        this.machineName = machineName;
        return this;
    }

    public Data setDownloads(List<EntryAndTime<Artifact>> downloads) {
        this.downloads = downloads;
        return this;
    }

    public Data setTotalDownloadTime(Stopwatch time) {
        this.totalDownloadTime = time;
        return this;
    }

    public Data setBuildTime(Stopwatch time) {
        this.buildTime = time;
        return this;
    }

    public Data setDate(Date date) {
        this.date = date;
        return this;
    }

    public Data setName(String name) {
        this.name = name;
        return this;
    }

    public Data setGoals(String goals) {
        this.goals = goals;
        return this;
    }

    public Data setParameters(Properties parameters) {
        this.parameters = parameters;
        return this;
    }

    public Data setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }
}
