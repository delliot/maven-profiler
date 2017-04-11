package com.central1.profiler.reporting;

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
