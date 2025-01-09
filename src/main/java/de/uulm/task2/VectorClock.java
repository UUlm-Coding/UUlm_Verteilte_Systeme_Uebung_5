package de.uulm.task2;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class VectorClock {

    private long[] times;
    private final int processId;

    public VectorClock(Collection<Long> C, int id) {
        this.times = C.stream().mapToLong(Long::longValue).toArray();
        this.processId = id;
    }

    public VectorClock(int size, int id){
        this.times = new long[size];
        this.processId = id;
    }

    /**
    * Returns all times in the vector
    */
    public long[] getTime() {
        return times.clone();
    }

    /**
    * Also returns incremented time for own processID
    */
    public long increment() {

        return ++times[processId];
    }

    /**
    * Returns time of given id
    */
    public long getTime(int id) {
        if (id < 0 || id >= times.length) {
            throw new IllegalArgumentException("invalid id: " + id);
        }
        return times[id];
    }

    public long merge(VectorClock b) throws IllegalArgumentException {
        if (this.size() != b.size()) {
            throw new IllegalArgumentException("Cannot merge VectorClocks of different size!");
        }

        for (int i = 0; i < this.times.length; i++) {
            this.times[i] = Math.max(this.times[i], b.times[i]);
        }
        // Lamport-Konvention: Nach jedem Merge-Ereignis -> increment() (optional, je nach Definition)
        return this.increment();
    }

    public long size() {
        return this.times.length;
    }

    /**
    * Greater-or-Equals comparison
    * IllegalArgumentException is thrown when vectors are of different size.
    */
    public boolean geq(VectorClock b) throws IllegalArgumentException {
        if (this.times.length != b.times.length) {
            throw new IllegalArgumentException("VectorClocks must have the same size");
        }
        for (int i = 0; i < times.length; i++) {
            if (this.times[i] < b.times[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return Positive if a>b, Negative if a<b, 0 if a==b, empty Optional if not ordered
     * @throws IllegalArgumentException If Vectors are of different size
     */
    public static Optional<Integer> compare(VectorClock a, VectorClock b) throws IllegalArgumentException {
        if (a.times.length != b.times.length) {
            throw new IllegalArgumentException("VectorClocks must have the same size");
        }

        boolean aIsGreater = false;
        boolean bIsGreater = false;

        for (int i = 0; i < a.times.length; i++) {
            if (a.times[i] > b.times[i]) {
                aIsGreater = true;
            } else if (a.times[i] < b.times[i]) {
                bIsGreater = true;
            }
        }

        if (aIsGreater && bIsGreater) {
            return Optional.empty();
        } else if (aIsGreater) {
            return Optional.of(1);
        } else if (bIsGreater) {
            return Optional.of(-1);
        } else {
            return Optional.of(0);
        }
    }

    public boolean equals(VectorClock b) {
        if (this.times.length != b.times.length) {
            return false;
        }
        for (int i = 0; i < times.length; i++) {
            if (this.times[i] != b.times[i]) {
                return false;
            }
        }
        return true;
    }

    public void addProcess() {
        long[] newTimes = new long[times.length + 1];
        System.arraycopy(times, 0, newTimes, 0, times.length);
        newTimes[newTimes.length - 1] = 0;
        this.times = newTimes;
    }
}
