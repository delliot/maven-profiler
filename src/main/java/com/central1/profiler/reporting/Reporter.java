package com.central1.profiler.reporting;

import com.central1.profiler.reporting.template.Data;

public interface Reporter {

    void write(Data data);
}
