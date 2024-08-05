public interface SQLRunnable extends Runnable{
    default void poinformujOZakonczeniuWatku(){
        BazaDanych.getBazaDanych().getSqlThreadQueue().zakonczonoWatek();
    }
}
