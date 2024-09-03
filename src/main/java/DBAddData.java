import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DBAddData extends DBConnector implements SQLRunnable{
    List<Row> dodawaneDane;
    DBAddData(Connection connection, List<Row> dodawaneDane) {
        super(connection);
        this.dodawaneDane = dodawaneDane;
    }

    @Override
    public void poinformujOZakonczeniuWatku() {
        SQLRunnable.super.poinformujOZakonczeniuWatku();
    }

    @Override
    public void run() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            String[] nazwyKolumn= InformacjeOTabeli.getInformacjeOTabeli().getNazwyKolumn();
            int liczbaKolumn = InformacjeOTabeli.informacjeOTabeli.getLiczbaKolumn();
            String insertQuery = "INSERT INTO "+BazaDanych.getBazaDanych().getNazwaTabeli()+"(";
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
                    switch (InformacjeOTabeli.informacjeOTabeli.getInformacjaOKolumnie(numerKolumny, InformacjeOTabeli.InformacjeKolumny.TYP_DANYCH_KOLUMNY)){
                        case "int":
                            insertQuery+=dodawaneDane.get(numerWiersza).getPole(numerKolumny).getWartosc();
                            break;
                        case "varchar":
                            insertQuery+="'"+dodawaneDane.get(numerWiersza).getPole(numerKolumny).getWartosc()+"'";
                            break;
                        default:
                            System.out.println("Nieznany typ danych!: "+InformacjeOTabeli.getInformacjeOTabeli().getInformacjaOKolumnie(numerKolumny, InformacjeOTabeli.InformacjeKolumny.TYP_DANYCH_KOLUMNY));
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
            throw new RuntimeException();
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
