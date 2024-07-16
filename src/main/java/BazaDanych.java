import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BazaDanych {
    private static BazaDanych bazaDanych;

    /*
        nazwaSerwera: localhost
        port: 3306
        nazwaBazy: bazatestowa
        nazwaUzytkownika: root
        haslo: TestowanieSQL1

        tabele: users, lista
     */


    private String nazwaBazy;
    private final String nazwaSerwera;
    private final String port;
    private String nazwaTabeli;
    private String nazwaUzytkownika;
    private String hasloUzytkownika;
    private List<Row> dane = new LinkedList<>();
    private final java.sql.Connection connection;

    private InformacjeOTabeli informacjeOTabeli;

    private BazaDanych(String nazwaSerwera, String port, String nazwaBazy, String nazwaUzytkownika, String hasloUzytkownika) throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Klasa Connection odpowiada za polaczenie z baza danych
            connection = DriverManager.getConnection("jdbc:mysql://"+nazwaSerwera+":"+port+"/" + nazwaBazy, nazwaUzytkownika, hasloUzytkownika);
            this.nazwaSerwera = nazwaSerwera;
            this.port = port;
            this.nazwaBazy = nazwaBazy;
            this.nazwaUzytkownika = nazwaUzytkownika;
            this.hasloUzytkownika = hasloUzytkownika;

        }catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static BazaDanych getBazaDanych() {
//        if (bazaDanych == null) {
//            bazaDanych = new BazaDanych();
//        }
        return bazaDanych;
    }

    public void zaktualizujBaze() throws SQLException {
        Statement statement = null;
        dane.clear();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + nazwaTabeli + ";");
            informacjeOTabeli = new InformacjeOTabeli(connection);
            {
                Row dodawanyRzad;
                int liczbaKolumn = informacjeOTabeli.getLiczbaKolumn();
                while (resultSet.next()) {
                    dodawanyRzad = new Row(liczbaKolumn);
                    for (int i = 0; i < liczbaKolumn; i++) {
                        String nazwaKolumny = informacjeOTabeli.getInformacjaOKolumnie(i, InformacjeOTabeli.InformacjeKolumny.NAZWA_KOLUMNY);
                        switch (informacjeOTabeli.getInformacjaOKolumnie(i, InformacjeOTabeli.InformacjeKolumny.TYP_DANYCH_KOLUMNY)) {
                            case "int":
                                dodawanyRzad.addPole(nazwaKolumny, Integer.parseInt(resultSet.getString(nazwaKolumny)));
                                break;
                            case "varchar":
                                dodawanyRzad.addPole(nazwaKolumny, resultSet.getString(nazwaKolumny));
                                break;
                            default:
                                dodawanyRzad.addPole(nazwaKolumny+" (type error)", null );
                                break; //Mozna dodac informacje o bledzie
                        }
                    }
                    dane.add(dodawanyRzad);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            assert statement != null;
            statement.close();
        }

    }

    public void dodajDane(int id, String nazwa) throws SQLException { //Trzeba bedzie wymyslic inny mechanizm dodawania danych ktory bedzie mozliwy do korzystania dla dowolnej bazy
        Statement statement = null;
        try {
            statement = connection.createStatement();
            String insertQuery = "INSERT INTO users(idusers, nazwa)\n" +
                    "VALUES (" + id + ",'" + nazwa + "');";
            statement.execute(insertQuery);
        } catch (SQLException e) {
            throw new RuntimeException();
        } finally {
            assert statement != null;
            statement.close();
        }
        zaktualizujBaze();
    }

    public void usunDane(int numerWiersza) throws SQLException { //Tez bedzie trzeba dostosowac dla dowolnej bazy
        Statement statement = null;
        try {
            statement = connection.createStatement();
            String kluczGlowny = informacjeOTabeli.getKluczGlowny();
            String deleteQuery = "DELETE FROM "+nazwaTabeli+" WHERE "+kluczGlowny+"='" + dane.get(numerWiersza).getPole(kluczGlowny).getWartosc() + "';";
            statement.execute(deleteQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            assert statement != null;
            statement.close();
        }
        zaktualizujBaze();
        PanelElementow.zaladujTabele();
    }

    public void usunDane(String nazwa) throws SQLException { //Tez bedzie trzeba dostosowac do dowolnej bazy
        Statement statement = null;
        try {
            statement = connection.createStatement();
            String deleteQuery = "DELETE FROM users WHERE nazwa='" + nazwa + "';";
            statement.execute(deleteQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            assert statement != null;
            statement.close();
        }
        zaktualizujBaze();
    }

    public void sortujDane(String by, String order) throws SQLException {
        Statement statement = null;
        dane.clear();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM "+ nazwaTabeli+" ORDER BY " + by + " " + order + ";");
            informacjeOTabeli = new InformacjeOTabeli(connection);
            {
                Row dodawanyRzad;
                int liczbaKolumn = informacjeOTabeli.getLiczbaKolumn();
                while (resultSet.next()) {
                    dodawanyRzad = new Row(liczbaKolumn);
                    for (int i = 0; i < liczbaKolumn; i++) {
                        String nazwaKolumny = informacjeOTabeli.getInformacjaOKolumnie(i, InformacjeOTabeli.InformacjeKolumny.NAZWA_KOLUMNY);
                        switch (informacjeOTabeli.getInformacjaOKolumnie(i, InformacjeOTabeli.InformacjeKolumny.TYP_DANYCH_KOLUMNY)) {
                            case "int":
                                dodawanyRzad.addPole(nazwaKolumny, Integer.parseInt(resultSet.getString(nazwaKolumny)));
                                break;
                            case "varchar":
                                dodawanyRzad.addPole(nazwaKolumny, resultSet.getString(nazwaKolumny));
                                break;
                            default:
                                break; //Mozna dodac informacje o bledzie
                        }
                    }
                    dane.add(dodawanyRzad);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            assert statement != null;
            statement.close();
        }

    }

    public void zaktualizujID() {

        Statement statement = null;
        try {
            zaktualizujBaze();
            statement = connection.createStatement();
            String kluczGlowny = informacjeOTabeli.getKluczGlowny();
            for (int nr = 1; nr < getDane().size() + 1; nr++) {
                if (nr != (int) getDane().get(nr - 1).getPole(0).getWartosc()) {
                    String updateIDQuery = "UPDATE USERS SET " + kluczGlowny + " = " + nr + " WHERE " + kluczGlowny + " = " + getDane().get(nr - 1).getPole(0).getWartosc() + ";";
                    statement.execute(updateIDQuery);
                }
            }
            zaktualizujBaze();
            PanelElementow.zaladujTabele();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void ustawBaze(String nazwaSerwera, String port, String nazwaBazy, String nazwaUzytkownika, String hasloUzytkownika) throws SQLException {
        bazaDanych = new BazaDanych(nazwaSerwera, port, nazwaBazy, nazwaUzytkownika, hasloUzytkownika);
    }

    public List<Row> getDane() {
        return dane;
    }

    public Object[][] getData() {
        int liczbaKolumn = getInformacjeOTabeli().getLiczbaKolumn();
        Object[][] obj = new Object[getDane().size()][liczbaKolumn];
        for (int i = 0; i < getDane().size(); i++) {
            for(int j=0;j<liczbaKolumn;j++){
                obj[i][j]=getDane().get(i).getPole(j).getWartosc();
            }

        }
        return obj;
    }

    public List<String> getNazwyTabel() throws SQLException {
        List<String> tabele = new ArrayList<>(0);
        Statement statement = connection.createStatement();
        String getTablesQueries = "SHOW TABLES;";

        ResultSet resultSet = statement.executeQuery(getTablesQueries);
        while(resultSet.next()){
            String nazwa = resultSet.getString(1);
            tabele.add(nazwa);

        }
        return tabele;
    }

    public InformacjeOTabeli getInformacjeOTabeli() {return informacjeOTabeli;}

    public String getNazwaBazy() {return nazwaBazy;}
    public String getNazwaTabeli() {return nazwaTabeli;}
    public void setNazwaBazy(String nazwaBazy) {this.nazwaBazy = nazwaBazy;}
    public void setNazwaTabeli(String nazwaTabeli) {this.nazwaTabeli = nazwaTabeli;}
    public void setNazwaUzytkownika(String nazwaUzytkownika) {this.nazwaUzytkownika = nazwaUzytkownika;}
    public void setHasloUzytkownika(String hasloUzytkownika) {this.hasloUzytkownika = hasloUzytkownika;}


}

class Row {


    List<Pole<?>> listaPol;

    Row(int liczbaKolumn) {
        listaPol = new ArrayList<>(liczbaKolumn);
    }

    @Override
    public String toString() {
        return listaPol.toString();
    }

    @Override
    public int hashCode() {
        return listaPol.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return listaPol.equals(obj);
    }

    public Pole getPole(int nrKolumny) {
        return listaPol.get(nrKolumny);
    }

    public Pole<?> getPole(String nazwaKolumny) {
        for (Pole<?> pole : listaPol) {
            if (pole.getNazwaKolumny().equals(nazwaKolumny)) return pole;
        }
        return null;
    }

    public void addPole(Pole<?> pole) {
        listaPol.add(pole);
    }

    public <T> void addPole(String nazwaKolumny, T wartosc) {
        listaPol.add(new Pole<>(nazwaKolumny, wartosc));
    }
}

class Pole<T> {
    private String nazwaKolumny;
    private T wartosc;

    Pole(String nazwaKolumny, T wartosc) {
        this.nazwaKolumny = nazwaKolumny;
        this.wartosc = wartosc;
    }

    public String getNazwaKolumny() {
        return nazwaKolumny;
    }

    public T getWartosc() {
        return wartosc;
    }

    @Override
    public String toString(){
        return "["+nazwaKolumny+":"+wartosc+"]";
    }

}