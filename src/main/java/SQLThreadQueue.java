import javax.swing.*;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class SQLThreadQueue {
    final int LICZBA_OBSLUGIWANYCH_WATKOW = 2;

    private int liczbaZajetychWatkow;
    private final Queue<Thread> threadQueue = new LinkedList<>();
    private final Object lock = new Object();


    public void dodajWatek(SQLRunnable runnable){
        threadQueue.add(new Thread(runnable));
    }

    public synchronized void rozpocznijWykonywanie(){
        JProgressBar pb = PanelSterowania.getPanelSterowania().getProgressBar();
        pb.setValue(0);
        pb.setVisible(true);

        System.out.println("Rozpoczeto wykonywanie watkow!");
        liczbaZajetychWatkow = 0;
        while(!threadQueue.isEmpty()){
            if(liczbaZajetychWatkow<LICZBA_OBSLUGIWANYCH_WATKOW){
                threadQueue.poll().start();
                liczbaZajetychWatkow++;
            }
            while(liczbaZajetychWatkow>=LICZBA_OBSLUGIWANYCH_WATKOW){
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
    public synchronized void zakonczonoWatek(){
        liczbaZajetychWatkow--;
        lock.notifyAll();
    }

    public int liczbaPozostalychWatkow() {
        return threadQueue.size();
    }

}

