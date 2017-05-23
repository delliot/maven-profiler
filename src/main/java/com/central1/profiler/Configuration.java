package com.central1.profiler;


import com.central1.profiler.reporting.json.JsonReporter;
import com.central1.profiler.reporting.Reporter;
import com.central1.profiler.sorting.Sorter;
import com.central1.profiler.sorting.execution.ByExecutionOrder;


/**
 * Full credit to jcgay on github for the original maven-profiler
 *
 * Licensed under MIT
 *
 * Modifications by Delan Elliot (delliot@central1.com)
 */
public class Configuration {

    private static final String PROFILE = "profile";
    public static final String POST = "postUrl";

    private boolean isProfiling;
    private final Reporter reporter;
    private final Sorter sorter;


    /**
     *
     * @param isProfiling
     * @param sorter
     */
    public Configuration(boolean isProfiling, Sorter sorter, String postUrl) {
        this.isProfiling = true;
        this.reporter = chooseReporter(postUrl);
        this.sorter = sorter;
    }

    /**
     *
     * @return
     */
    public static Configuration read() {
        return new Configuration(isActive(),  chooseSorter(), chooseUrl());
    }

    /**
     *
     * @return
     */
    public boolean isProfiling() {
        return isProfiling;
    }

    /**
     *
     * @return
     */
    public Reporter reporter() {
        return reporter;
    }

    /**
     *
     * @return
     */
    public Sorter sorter() {
        return sorter;
    }

    /**
     *
     * @return
     */
    private static Sorter chooseSorter() {
        return new ByExecutionOrder();
    }

    private static String chooseUrl() {
        return System.getProperty(POST, "http://httpbin.org/post");
    }

    /**
     *
     * @return
     */
    private static Reporter chooseReporter(String url) {
        return new JsonReporter(url);
    }

    /**
     *
     * @param isProfiling
     */
    public void setProfiling (boolean isProfiling) {
        this.isProfiling = isProfiling;
    }


    /**
     *
     * @return
     */
    private static boolean isActive() {
        String parameter = System.getProperty(PROFILE);
        return parameter != null && !"false".equalsIgnoreCase(parameter);
    }
}
