package de.uulm;

import de.uulm.task1.SynchronizedClock;
import org.zeromq.ZContext;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        startSynchronization(false);
        startSynchronization(true);
    }

    private static void startSynchronization(boolean useCurrentTimeToStart) {
        try (ZContext context = new ZContext()) {
            String serverHost = "tcp://vs.lxd-vs.uni-ulm.de:3322";

            int numRequests = 5;
            SynchronizedClock clock;
            if (useCurrentTimeToStart) {
                 clock = new SynchronizedClock(context, serverHost, numRequests, System.currentTimeMillis());
            } else {
                clock = new SynchronizedClock(context, serverHost, numRequests);
            }
            System.out.println("Synchronisierte Zeit: " + time(clock.getTime()));
            System.out.println("Aktueller Offset zur lokalen Zeit: " + clock.getOffset() + " ms");
            Thread.sleep(5000);
            System.out.println("Synchronisierte Zeit nach 5 Sekunden: " + time(clock.getTime()));
            clock.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");

    public static String time(long time){
        return format.format(new Date(time));
    }

}