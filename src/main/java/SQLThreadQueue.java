import javax.swing.*;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class SQLThreadQueue {
    final int LICZBA_OBSLUGIWANYCH_WATKOW = 2; //Mozna dodac do config file'a liczbe obslugiwanych watkow

    private int liczbaZajetychWatkow = 0;
    private final Queue<Thread> threadQueue = new LinkedList<>();
    private final Object lock = new Object();
    private ThreadProgressBar progressBar;

    public void dodajWatek(SQLRunnable runnable){
        threadQueue.add(new Thread(runnable));
        System.out.println("Dodano wątek.");
    }

    public synchronized void rozpocznijWykonywanie() {
        progressBar = PanelSterowania.getPanelSterowania().getProgressBar();
        progressBar.przygotujProgressBar();

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

                try {
                    progressBar.setValue(progressBar.getMaximum());
                    System.out.println("Zakonczono wątki. Liczba pozostalych watkow: "+liczbaPozostalychWatkow());

                    BazaDanych.getBazaDanych().zaktualizujBaze();
                    PanelElementow.zaladujTabele();


                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }


            });


        thread.start();

    }

    public void zakonczonoWatek() {
        synchronized (lock) {
            liczbaZajetychWatkow--;
            SwingUtilities.invokeLater(() -> {
                progressBar.zwiekszProgress();
                progressBar.repaint();
            });
            lock.notifyAll();
        }
    }

    public int liczbaPozostalychWatkow() {
        return threadQueue.size();
    }

    public void resetQueue(){
        threadQueue.clear();
        System.out.println("Kolejka watkow wyczyszczona");
    }
}