package com.central1.profiler;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;

/**
 * Full credit to jcgay on github for the original maven-profiler
 *
 * Licensed under MIT
 *
 * Modifications by Delan Elliot (delliot@central1.com)
 */
public class KnownElapsedTimeTicker extends Ticker {

    private final long expectedElapsedTime;
    private boolean firstRead;

    /**
     *
     * @param expectedElapsedTime
     */
    public KnownElapsedTimeTicker(long expectedElapsedTime) {
        this.expectedElapsedTime = expectedElapsedTime;
    }

    /**
     *
     * @param elapsedTime
     * @return
     */
    public static Stopwatch aStopWatchWithElapsedTime(long elapsedTime) {
        return new Stopwatch(new KnownElapsedTimeTicker(elapsedTime)).start().stop();
    }

    /**
     *
     * @return
     */
    @Override
    public long read() {
        firstRead = !firstRead;
        return firstRead ? 0 : expectedElapsedTime;
    }
}
