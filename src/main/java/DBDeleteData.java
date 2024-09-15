import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DBDeleteData implements SQLRunnable{
    private final Logger log = LoggerFactory.getLogger(DBDeleteData.class);
    Connection connection;
    int[] numeryWierszy;
    DBDeleteData(Connection connection, int[] numeryWierszy) {
        this.connection=connection;
        this.numeryWierszy = numeryWierszy;
        Thread.currentThread().setName("DBDeleteDataThread");
    }

    @Override
    public void run(){
        Statement statement = null;
        if(numeryWierszy.length<1) return;
        String kluczGlowny = InformacjeOBazie.getActiveTableInfo().getKluczGlowny();
        String deleteCommand = "DELETE FROM "+InformacjeOBazie.getActiveTableName()+" WHERE "+kluczGlowny+" IN (";
        try {
            statement = connection.createStatement();
            List<Row> dane = BazaDanych.getBazaDanych().getDane();
            deleteCommand+=dane.get(numeryWierszy[0]).getPole(kluczGlowny).getWartosc();
            for(int i=1; i< numeryWierszy.length;i++){
                deleteCommand+=", "+dane.get(numeryWierszy[i]).getPole(kluczGlowny).getWartosc();
            }
            deleteCommand+=");";
            statement.execute(deleteCommand);
            log.debug("Data successfully deleted from database. Query: {})", deleteCommand);
        } catch (SQLException e) {
            log.error("Failed to delete data from database. Query: {})", deleteCommand);
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
