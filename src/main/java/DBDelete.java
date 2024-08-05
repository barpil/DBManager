import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DBDelete extends DBConnector implements SQLRunnable{
    int[] numeryWierszy;
    DBDelete(Connection connection, int[] numeryWierszy) {
        super(connection);
        this.numeryWierszy = numeryWierszy;
    }

    @Override
    public void run(){
        Statement statement = null;
        if(numeryWierszy.length<1) return;
        try {
            statement = connection.createStatement();
            List<Row> dane = BazaDanych.getBazaDanych().getDane();
            String kluczGlowny = BazaDanych.getBazaDanych().getInformacjeOTabeli().getKluczGlowny();
            String deleteQuery = "DELETE FROM "+BazaDanych.getBazaDanych().getNazwaTabeli()+" WHERE "+kluczGlowny+" IN (";
            deleteQuery+=dane.get(numeryWierszy[0]).getPole(kluczGlowny).getWartosc();
            for(int i=1; i< numeryWierszy.length;i++){
                deleteQuery+=", "+dane.get(numeryWierszy[i]).getPole(kluczGlowny).getWartosc();
            }
            deleteQuery+=");";
            statement.execute(deleteQuery);
            System.out.println("Wysłano query: "+deleteQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            assert statement != null;
            try {
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

         //BEDZIE TRZEBA POUSUWAC TO LADOWANIE I AKTUALIZACJE SKORO ROBIE KOLEJKE THREAD
        poinformujOZakonczeniuWatku();
    }

    @Override
    public void poinformujOZakonczeniuWatku() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        BazaDanych.getBazaDanych().getSqlThreadQueue().zakonczonoWatek();
    }
}
