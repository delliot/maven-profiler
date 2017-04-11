package com.central1.profiler.reporting;

import com.google.common.base.Stopwatch;

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
