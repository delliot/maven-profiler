package com.central1.profiler;

import com.central1.profiler.reporting.CompositeReporter;
import com.central1.profiler.reporting.json.JsonReporter;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.central1.profiler.reporting.Reporter;
import com.central1.profiler.sorting.Sorter;
import com.central1.profiler.sorting.execution.ByExecutionOrder;

import java.util.List;

import static com.google.common.base.Functions.compose;
import static com.google.common.base.Functions.forMap;
import static com.google.common.collect.Collections2.transform;
import static java.util.Arrays.asList;

public class Configuration {

    private static final String PROFILE = "profile";
    private static final String PROFILE_FORMAT = "profileFormat";
    private static final String POST = "postUrl";

    /**
     *
     */
    private static final Function<String,Reporter> reporters =  compose(forMap(ImmutableMap.<String,Reporter>builder()
    		.put("json", new JsonReporter())
    		.build()), new Function<String,String>(){
				@Override
				public String apply(String it) {
					return it.toLowerCase();
				}});

    private boolean isProfiling;
    private final Reporter reporter;
    private final Sorter sorter;
    private String postUrl;

    /**
     *
     * @param isProfiling
     * @param reporter
     * @param sorter
     */
    public Configuration(boolean isProfiling, Reporter reporter, Sorter sorter, String postUrl) {
        this.isProfiling = true;
        this.reporter = reporter;
        this.sorter = sorter;
        this.postUrl = postUrl;
    }

    /**
     *
     * @return
     */
    public static Configuration read() {
        return new Configuration(isActive(), chooseReporter(), chooseSorter(), chooseUrl());
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
    private static Reporter chooseReporter() {
        List<String> formats = asList(System.getProperty(PROFILE_FORMAT, "json").split(","));
        return new CompositeReporter(transform(formats, reporters));
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
