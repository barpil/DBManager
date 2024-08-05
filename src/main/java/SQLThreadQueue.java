import javax.swing.*;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class SQLThreadQueue {
    final int LICZBA_OBSLUGIWANYCH_WATKOW = 2;

    private int liczbaZajetychWatkow;
    private final Queue<Thread> threadQueue = new LinkedList<>();
    private final Object lock = new Object();
    private ThreadProgressBar progressBar;

    public void dodajWatek(SQLRunnable runnable){
        threadQueue.add(new Thread(runnable));
        System.out.println("Dodano wątek.");
    }

    public synchronized void rozpocznijWykonywanie(){
        progressBar = new ThreadProgressBar();
        threadQueue.add(new Thread(new WatekTestowy()));
        System.out.println("Rozpoczeto wykonywanie watkow!");
        liczbaZajetychWatkow = 0;
        while(!threadQueue.isEmpty()){
            if(liczbaZajetychWatkow<LICZBA_OBSLUGIWANYCH_WATKOW){
                threadQueue.poll().start();
                liczbaZajetychWatkow++;
            }
            while(liczbaZajetychWatkow>=LICZBA_OBSLUGIWANYCH_WATKOW){
                System.out.println("Czekanie...");
                try {
                    synchronized (lock) {
                        lock.wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            //System.out.println("Wznowiono!");
        }
        while(liczbaZajetychWatkow!=0){
            System.out.println("Oczekuje na zakończenie wątków...");
            synchronized (lock){
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        try {
            System.out.println("Zakonczono watki");
            BazaDanych.getBazaDanych().zaktualizujBaze();
            System.out.println("Zaktualizowano baze");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        PanelElementow.zaladujTabele();
        System.out.println("Zaladowano tabele");
    }

    public void zakonczonoWatek(){
        synchronized (lock) {
            liczbaZajetychWatkow--;
            progressBar.zwiekszProgress();
            lock.notifyAll();
        }
    }

    public int liczbaPozostalychWatkow() {
        return threadQueue.size();
    }
}
