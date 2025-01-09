package de.uulm.task1;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author David Mödinger
 *
 */

public class SynchronizedClock implements Clock {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ZContext context;
    private final String host;
    private final int numRequests;
    private long currentTime;

    public SynchronizedClock(ZContext context, String host, int numRequests) {
        this(context, host, numRequests, 0);
    }

    public SynchronizedClock(ZContext context, String host, int numRequests, long start) {
        this.context = context;
        this.host = host;
        this.numRequests = numRequests;
        this.currentTime = start;
        synchronizeTime();
        startSynchronization();
    }

    private void synchronizeTime() {
        try (ZMQ.Socket socket = context.createSocket(SocketType.REQ)) {
            socket.connect(host);

            List<Long> offsets = new ArrayList<>();
            for (int i = 0; i < numRequests; i++) {
                long sendTime = System.currentTimeMillis();
                socket.send("TIME");

                String response = socket.recvStr();
                long receiveTime = System.currentTimeMillis();

                long serverTime = Long.parseLong(response);
                long roundTripTime = receiveTime - sendTime;
                long offset = serverTime + roundTripTime / 2 - receiveTime;

                offsets.add(offset);
                Thread.sleep(10); // debounce
            }

            long medianOffset = calculateMedian(offsets);
            if (medianOffset > 0) {
                this.currentTime += medianOffset;
            }
        } catch (Exception e) {
            System.err.println("Error during synchronization: " + e.getMessage());
        }
    }

    private long calculateMedian(List<Long> offsets) {
        offsets.sort(Long::compareTo);
        int size = offsets.size();
        if (size % 2 == 0) {
            return (offsets.get(size / 2 - 1) + offsets.get(size / 2)) / 2;
        } else {
            return offsets.get(size / 2);
        }
    }

    public void startSynchronization() {
        scheduler.scheduleAtFixedRate(this::synchronizeTime, 1, 1, TimeUnit.MINUTES);
    }

    public long getTime() {
        return System.currentTimeMillis() + currentTime;
    }

    public long getOffset() {
        return currentTime - System.currentTimeMillis();
    }

    public void shutdown() {
        scheduler.shutdown();
        context.close();
    }
}
