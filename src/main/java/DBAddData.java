import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DBAddData implements SQLRunnable{
    private final Logger log = LoggerFactory.getLogger(DBAddData.class);
    Connection connection;
    List<Row> dodawaneDane;
    DBAddData(Connection connection, List<Row> dodawaneDane) {
        this.connection=connection;
        this.dodawaneDane = dodawaneDane;
        Thread.currentThread().setName("DBAddDataThread");
    }

    @Override
    public void poinformujOZakonczeniuWatku() {
        SQLRunnable.super.poinformujOZakonczeniuWatku();

    }

    @Override
    public void run() {
        if(dodawaneDane.isEmpty()){
            return;
        }
        Statement statement = null;
        String insertCommand = "INSERT INTO "+InformacjeOBazie.getActiveTableName()+"(";
        try {
            statement = connection.createStatement();
            String[] nazwyKolumn= InformacjeOBazie.getActiveTableInfo().getNazwyKolumn();
            int liczbaKolumn = InformacjeOBazie.getActiveTableInfo().getLiczbaKolumn();
            for(int numerKolumny=0;numerKolumny<liczbaKolumn;numerKolumny++){
                System.out.println("Nazwa: "+nazwyKolumn[numerKolumny]);
                insertCommand+=nazwyKolumn[numerKolumny];
                if(numerKolumny!=liczbaKolumn-1){
                    insertCommand+=", ";
                }
            }
            insertCommand+=") VALUES";
            for(int numerWiersza=0;numerWiersza<dodawaneDane.size();numerWiersza++){
                insertCommand+=" (";
                for(int numerKolumny=0;numerKolumny<liczbaKolumn;numerKolumny++){
                    switch (InformacjeOBazie.getActiveTableInfo().getInformacjaOKolumnie(numerKolumny, InformacjeOTabeli.InformacjeKolumny.TYP_DANYCH_KOLUMNY)){
                        case "int":
                            insertCommand+=dodawaneDane.get(numerWiersza).getPole(numerKolumny).getWartosc();
                            break;
                        case "varchar":
                            insertCommand+="'"+dodawaneDane.get(numerWiersza).getPole(numerKolumny).getWartosc()+"'";
                            break;
                        default:
                            log.error("Unknown data type encountered while trying to add data to table: {}", InformacjeOBazie.getActiveTableInfo().getInformacjaOKolumnie(numerKolumny, InformacjeOTabeli.InformacjeKolumny.TYP_DANYCH_KOLUMNY));
                    }

                    if(numerKolumny!=liczbaKolumn-1){
                        insertCommand+=", ";
                    }
                }
                insertCommand+=")";
                if(numerWiersza!= dodawaneDane.size()-1){
                    insertCommand+=", ";
                }
                else{
                    insertCommand+=";";
                }
            }
            
            statement.execute(insertCommand);
            log.debug("Successfully inserted new data to database. Command: {}", insertCommand);
        } catch (SQLException e) {
            SQLThreadQueue.logError(e);
            log.error("Failed to add new data to database. Command: {}", insertCommand);
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

}
