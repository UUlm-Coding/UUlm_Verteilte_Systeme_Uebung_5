package de.uulm.task2;

public class LamportClock implements Comparable<LamportClock>{

    private long time;

    public LamportClock() {
        this.time = 0;
    }

    public LamportClock(long init) {
        this.time = init;
    }

    public long getTime() {
        return this.time;
    }

    /**
    * Also returns incremented time.
    */
    public long increment() {
        return ++this.time;
    }

    public long merge(LamportClock b) {
        this.time = Math.max(this.time, b.getTime()) + 1;
        return this.time;
    }

    public static LamportClock merge(LamportClock a, LamportClock b) {
        return new LamportClock(Math.max(a.getTime(), b.getTime()) + 1);
    }

    public static int compare(LamportClock a, LamportClock b) {
        return Long.compare(a.getTime(), b.getTime());
    }

    public boolean equals(LamportClock b) {
        return this.time == b.getTime();
    }

    @Override
    public int compareTo(LamportClock l) {
        return Long.compare(this.time, l.getTime());
    }
}
