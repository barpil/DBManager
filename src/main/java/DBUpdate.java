import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUpdate extends DBConnector implements Runnable{
    String query;
    DBUpdate(Connection connection) {
        super(connection);
        query="SELECT * FROM " + BazaDanych.getBazaDanych().getNazwaTabeli() + ";";
    }

    DBUpdate(Connection connection, String selectQuery){
        super(connection);
        query=selectQuery;
    }

    @Override
    public void run() {
        Statement statement = null;
        BazaDanych.getBazaDanych().getDane().clear();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            BazaDanych.getBazaDanych().setInformacjeOTabeli(new InformacjeOTabeli(connection, BazaDanych.getBazaDanych().getInformacjeOBazie(), BazaDanych.getBazaDanych().getNazwaTabeli() ));
            InformacjeOTabeli informacjeOTabeli = BazaDanych.getBazaDanych().getInformacjeOTabeli();
            System.out.println(BazaDanych.getBazaDanych().getInformacjeOBazie().nazwyTabel());
            {
                Row dodawanyRzad;
                int liczbaKolumn = informacjeOTabeli.getLiczbaKolumn();
                while (resultSet.next()) {
                    dodawanyRzad = new Row(liczbaKolumn);
                    for (int i = 0; i < liczbaKolumn; i++) {
                        String nazwaKolumny = informacjeOTabeli.getInformacjaOKolumnie(i, InformacjeOTabeli.InformacjeKolumny.NAZWA_KOLUMNY);
                        switch (informacjeOTabeli.getInformacjaOKolumnie(i, InformacjeOTabeli.InformacjeKolumny.TYP_DANYCH_KOLUMNY)) {
                            case "int":
                                dodawanyRzad.addPole(nazwaKolumny, resultSet.getInt(nazwaKolumny));
                                break;
                            case "varchar":
                                dodawanyRzad.addPole(nazwaKolumny, resultSet.getString(nazwaKolumny));
                                break;
                            default:
                                dodawanyRzad.addPole(nazwaKolumny+" (type error)", null );
                                break; //Mozna dodac informacje o bledzie
                        }
                    }
                    BazaDanych.getBazaDanych().getDane().add(dodawanyRzad);
                }
            }
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
