import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DBAddData implements SQLRunnable{
    Connection connection;
    List<Row> dodawaneDane;
    DBAddData(Connection connection, List<Row> dodawaneDane) {
        this.connection=connection;
        this.dodawaneDane = dodawaneDane;
    }

    @Override
    public void poinformujOZakonczeniuWatku() {
        SQLRunnable.super.poinformujOZakonczeniuWatku();
    }

    @Override
    public void run() {
        if(dodawaneDane.size()==0){
            return;
        }
        Statement statement = null;
        try {
            statement = connection.createStatement();
            String[] nazwyKolumn= InformacjeOBazie.getTableNames().toArray(new String[0]);
            int liczbaKolumn = InformacjeOBazie.getActiveTableInfo().getLiczbaKolumn();
            String insertQuery = "INSERT INTO "+InformacjeOBazie.getActiveTableName()+"(";
            for(int numerKolumny=0;numerKolumny<liczbaKolumn;numerKolumny++){
                insertQuery+=nazwyKolumn[numerKolumny];
                if(numerKolumny!=liczbaKolumn-1){
                    insertQuery+=", ";
                }
            }
            insertQuery+=") VALUES";
            for(int numerWiersza=0;numerWiersza<dodawaneDane.size();numerWiersza++){
                insertQuery+=" (";
                for(int numerKolumny=0;numerKolumny<liczbaKolumn;numerKolumny++){
                    switch (InformacjeOBazie.getActiveTableInfo().getInformacjaOKolumnie(numerKolumny, InformacjeOTabeli.InformacjeKolumny.TYP_DANYCH_KOLUMNY)){
                        case "int":
                            insertQuery+=dodawaneDane.get(numerWiersza).getPole(numerKolumny).getWartosc();
                            break;
                        case "varchar":
                            insertQuery+="'"+dodawaneDane.get(numerWiersza).getPole(numerKolumny).getWartosc()+"'";
                            break;
                        default:
                            System.out.println("Nieznany typ danych!: "+InformacjeOBazie.getActiveTableInfo().getInformacjaOKolumnie(numerKolumny, InformacjeOTabeli.InformacjeKolumny.TYP_DANYCH_KOLUMNY));
                    }

                    if(numerKolumny!=liczbaKolumn-1){
                        insertQuery+=", ";
                    }
                }
                insertQuery+=")";
                if(numerWiersza!= dodawaneDane.size()-1){
                    insertQuery+=", ";
                }
                else{
                    insertQuery+=";";
                }
            }
            
            statement.execute(insertQuery);
        } catch (SQLException e) {
            SQLThreadQueue.logError(e);
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
