package com.central1.profiler.reporting;

import com.central1.profiler.reporting.template.Data;


/**
 * Full credit to jcgay on github for the original maven-profiler
 *
 * Licensed under MIT
 *
 * Modifications by Delan Elliot (delliot@central1.com)
 */
public interface Reporter {

    void write(Data data);
}
