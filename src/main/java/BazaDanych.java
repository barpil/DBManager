import java.sql.*;
import java.util.*;

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

    private final List<Row> dane = new LinkedList<>();

    private BazaDanych(String nazwaSerwera, String port, String nazwaBazy, String nazwaUzytkownika, String hasloUzytkownika) throws SQLException {
        InformacjeOBazie.createDataBaseInfo(nazwaSerwera, port, nazwaBazy, nazwaUzytkownika, hasloUzytkownika);
    }

    public static BazaDanych getBazaDanych() {
        return bazaDanych;
    }

    synchronized public void zaktualizujBaze() {
        Thread thread = new Thread(new DBUpdate(InformacjeOBazie.getConnection()));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    public void dodajDane(List<Row> dane) {
        utworzWatek(new DBAddData(InformacjeOBazie.getConnection(), dane));
    }



    public void usunDane(int[] numeryWierszy) {
        utworzWatek(new DBDeleteData(InformacjeOBazie.getConnection(), numeryWierszy));

    }

    public void customSQLCommand(SQLConsole okno, String textCommand){
        utworzWatek(new DBCustomSQLCommand(InformacjeOBazie.getConnection(), textCommand, okno));
    }

    public void sortujDane(String by, String order) throws SQLException {
        Statement statement = null;
        dane.clear();
        try {
            statement = InformacjeOBazie.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM "+ InformacjeOBazie.getActiveTableName()+" ORDER BY " + by + " " + order + ";");

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


    public static void changeDatabase(String nazwaSerwera, String port, String nazwaBazy, String nazwaUzytkownika, String hasloUzytkownika) throws SQLException {
        bazaDanych = new BazaDanych(nazwaSerwera, port, nazwaBazy, nazwaUzytkownika, hasloUzytkownika);

    }

    private void utworzWatek(SQLRunnable runnable){
        SQLThreadQueue.dodajWatek(runnable);
    }

    public List<Row> getDane() {
        return dane;
    }

    public Object[][] getData() {
        int liczbaKolumn = InformacjeOBazie.getActiveTableInfo().getLiczbaKolumn();
        Object[][] obj = new Object[getDane().size()][liczbaKolumn];
        for (int i = 0; i < getDane().size(); i++) {
            for(int j=0;j<liczbaKolumn;j++){
                obj[i][j]=getDane().get(i).getPole(j).getWartosc();
            }

        }
        return obj;
    }

    public void changeTable(String nazwaTabeli) {InformacjeOBazie.changeActiveTableInfo(nazwaTabeli);}


}

class Row implements Iterable<Pole<?>>{


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

    @Override
    public Iterator<Pole<?>> iterator() {
        return new MyIterator();
    }

    private class MyIterator implements Iterator<Pole<?>>{
        private int index = 0;
        @Override
        public boolean hasNext() {
            return index<Row.this.listaPol.size();
        }

        @Override
        public Pole<?> next() {
            if(hasNext()){
                return Row.this.listaPol.get(index++);
            }else{
                throw new NoSuchElementException();
            }
        }
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