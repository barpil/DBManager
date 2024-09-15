import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUpdate implements Runnable{
    private final Logger log = LoggerFactory.getLogger(DBUpdate.class);
    Connection connection;
    String query;
    DBUpdate(Connection connection) {
        this.connection=connection;
        query="SELECT * FROM " + InformacjeOBazie.getActiveTableName() + ";";
        Thread.currentThread().setName("DBUpdateThread");
    }

    DBUpdate(Connection connection, String selectQuery){
        this.connection=connection;
        query=selectQuery;
    }

    @Override
    public void run() {
        Statement statement = null;
        BazaDanych.getBazaDanych().getDane().clear();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            {
                Row dodawanyRzad;
                int liczbaKolumn = InformacjeOBazie.getActiveTableInfo().getLiczbaKolumn();
                while (resultSet.next()) {
                    dodawanyRzad = new Row(liczbaKolumn);
                    for (int i = 0; i < liczbaKolumn; i++) {
                        String nazwaKolumny = InformacjeOBazie.getActiveTableInfo().getInformacjaOKolumnie(i, InformacjeOTabeli.InformacjeKolumny.NAZWA_KOLUMNY);
                        switch (InformacjeOBazie.getActiveTableInfo().getInformacjaOKolumnie(i, InformacjeOTabeli.InformacjeKolumny.TYP_DANYCH_KOLUMNY)) {
                            case "int":
                                dodawanyRzad.addPole(nazwaKolumny, resultSet.getInt(nazwaKolumny));
                                break;
                            case "varchar":
                                dodawanyRzad.addPole(nazwaKolumny, resultSet.getString(nazwaKolumny));
                                break;
                            case "date":
                                dodawanyRzad.addPole(nazwaKolumny, resultSet.getDate(nazwaKolumny));
                                break;
                            case "decimal":
                                dodawanyRzad.addPole(nazwaKolumny, resultSet.getBigDecimal(nazwaKolumny));
                                break;
                            case "bit":
                                dodawanyRzad.addPole(nazwaKolumny, resultSet.getShort(nazwaKolumny));
                                break;
                            case "boolean":
                                dodawanyRzad.addPole(nazwaKolumny, resultSet.getBoolean(nazwaKolumny));
                                break;
                            case "time":
                                dodawanyRzad.addPole(nazwaKolumny, resultSet.getTime(nazwaKolumny));
                                break;
                            default:
                                dodawanyRzad.addPole(nazwaKolumny+" (type error)", null );
                                log.error("Unknown data type encountered while trying to add data to table: {}", InformacjeOBazie.getActiveTableInfo().getInformacjaOKolumnie(i, InformacjeOTabeli.InformacjeKolumny.TYP_DANYCH_KOLUMNY));
                                break;
                        }
                    }
                    BazaDanych.getBazaDanych().getDane().add(dodawanyRzad);
                }
            }
            log.debug("Database data updated successfully. Table: {}", InformacjeOBazie.getActiveTableName());
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

    }
}
