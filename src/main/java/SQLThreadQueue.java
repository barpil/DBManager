import javax.swing.*;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SQLThreadQueue {
    static final int LICZBA_OBSLUGIWANYCH_WATKOW = 2; //Mozna dodac do config file'a liczbe obslugiwanych watkow
    private static List<Exception> listaBledowPodczasWykonywania;
    private static int liczbaZajetychWatkow = 0;
    private static final Queue<Thread> threadQueue = new LinkedList<>();
    private static final Object lock = new Object();

    public static void dodajWatek(SQLRunnable runnable){
        threadQueue.add(new Thread(runnable));
        System.out.println("Dodano wątek.");
    }

    public static synchronized void rozpocznijWykonywanie() {
        listaBledowPodczasWykonywania= new LinkedList<>();
        PanelSterowania.getPanelSterowania().getProgressBar().przygotujProgressBar();

        Thread thread = new Thread(() -> {
            if (!threadQueue.isEmpty()) {
                System.out.println("Rozpoczeto wykonywanie watkow!");
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
                        System.out.println("Czekanie...");
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
            System.out.println("Zakonczono wątki. Liczba pozostalych watkow: "+liczbaPozostalychWatkow());

            BazaDanych.getBazaDanych().zaktualizujBaze();
            PanelElementow.zaladujTabele();


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
        System.out.println("Kolejka watkow wyczyszczona");
    }
    public static void logError(Exception e){
        listaBledowPodczasWykonywania.add(e);
    }
    public static List<Exception> getErrors(){
        return listaBledowPodczasWykonywania;
    }
}