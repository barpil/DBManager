import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DBDeleteData implements SQLRunnable{
    Connection connection;
    int[] numeryWierszy;
    DBDeleteData(Connection connection, int[] numeryWierszy) {
        this.connection=connection;
        this.numeryWierszy = numeryWierszy;
    }

    @Override
    public void run(){
        Statement statement = null;
        if(numeryWierszy.length<1) return;
        try {
            statement = connection.createStatement();
            List<Row> dane = BazaDanych.getBazaDanych().getDane();
            String kluczGlowny = InformacjeOBazie.getActiveTableInfo().getKluczGlowny();
            String deleteQuery = "DELETE FROM "+InformacjeOBazie.getActiveTableName()+" WHERE "+kluczGlowny+" IN (";
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

        poinformujOZakonczeniuWatku();
    }

    @Override
    public void poinformujOZakonczeniuWatku() {
        SQLRunnable.super.poinformujOZakonczeniuWatku();
    }
}
