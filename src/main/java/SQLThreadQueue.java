import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SQLThreadQueue {
    private static final Logger log = LoggerFactory.getLogger(SQLThreadQueue.class);
    static final int LICZBA_OBSLUGIWANYCH_WATKOW = 2;
    private static List<Exception> listaBledowPodczasWykonywania;
    private static int liczbaZajetychWatkow = 0;
    private static final Queue<Thread> threadQueue = new LinkedList<>();
    private static final Object lock = new Object();

    public static void dodajWatek(SQLRunnable runnable){
        Thread thread = new Thread(runnable);
        threadQueue.add(thread);
        log.debug("Thread added to queue: {}", thread.getName());
    }

    public static synchronized void rozpocznijWykonywanie() {
        listaBledowPodczasWykonywania= new LinkedList<>();
        PanelSterowania.getPanelSterowania().getProgressBar().przygotujProgressBar();

        Thread thread = new Thread(() -> {
            if (!threadQueue.isEmpty()) {
                log.debug("Thread queue started...");
                liczbaZajetychWatkow = 0;

                while(!threadQueue.isEmpty()) {
                    if(liczbaZajetychWatkow < LICZBA_OBSLUGIWANYCH_WATKOW) {
                        Thread t = threadQueue.poll();
                        if (t != null) {
                            t.start();
                            liczbaZajetychWatkow++;
                        }
                    }
                    while(liczbaZajetychWatkow >= LICZBA_OBSLUGIWANYCH_WATKOW) {
                        log.debug("Queue full, waiting for current threads to resolve. ({} threads left)", liczbaPozostalychWatkow());
                        try {
                            synchronized (lock) {
                                lock.wait();
                            }
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            }

            PanelSterowania.getPanelSterowania().getProgressBar().setValue(PanelSterowania.getPanelSterowania().getProgressBar().getMaximum());
            log.debug("All threads in queue have been executed");

        });


        thread.start();

    }


    public static void zakonczonoWatek() {
        synchronized (lock) {
            liczbaZajetychWatkow--;
            SwingUtilities.invokeLater(() -> {
                PanelSterowania.getPanelSterowania().getProgressBar().zwiekszProgress();
                PanelSterowania.getPanelSterowania().getProgressBar().repaint();
            });
            lock.notifyAll();
        }
    }

    public static int liczbaPozostalychWatkow() {
        return threadQueue.size();
    }

    public static void resetQueue(){
        threadQueue.clear();
        log.debug("Thread queue has been cleared");
    }
    public static void logError(Exception e){
        listaBledowPodczasWykonywania.add(e);
    }
    public static List<Exception> getErrors(){
        return listaBledowPodczasWykonywania;
    }
}