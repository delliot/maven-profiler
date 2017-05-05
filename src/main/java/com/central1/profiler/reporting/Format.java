package com.central1.profiler.reporting;

import com.google.common.base.Stopwatch;

/**
 * Full credit to jcgay on github for the original maven-profiler
 *
 * Licensed under MIT
 *
 * Modifications by Delan Elliot (delliot@central1.com)
 */
public class Format {

    private Format() {
    }

    public static String ms(Stopwatch time) {
        if (time == null) {
            return null;
        }
        return String.valueOf(time.elapsedMillis());
    }
}
