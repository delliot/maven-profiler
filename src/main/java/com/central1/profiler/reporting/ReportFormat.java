package com.central1.profiler.reporting;

/**
 * Full credit to jcgay on github for the original maven-profiler
 *
 * Licensed under MIT
 *
 * Modifications by Delan Elliot (delliot@central1.com)
 */
public enum ReportFormat {
    HTML("html"), JSON("json");

    private final String extension;

    ReportFormat(String extension) {
        this.extension = extension;
    }

    public String extension() {
        return extension;
    }
}
