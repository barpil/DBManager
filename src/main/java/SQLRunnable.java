import java.sql.Connection;

public interface SQLRunnable extends Runnable{
    default void poinformujOZakonczeniuWatku(){
        SQLThreadQueue.zakonczonoWatek();
    }
}
