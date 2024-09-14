import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InformacjeOBazie {

    private static InformacjeOBazie informacjeOBazie;

    public static String getDatabaseName() {
        return nazwaBazy;
    }


    public static String getActiveTableName() {
        return nazwaWybranejTabeli;
    }

    public static List<String> getTableNames() {
        return nazwyTabel;
    }

    public static InformacjeOTabeli getActiveTableInfo() {
        return informacjeOTabeli;
    }

    private static String nazwaBazy;
    private static String nazwaSerwera;
    private static String port;
    private static String nazwaWybranejTabeli;
    private static String nazwaUzytkownika;
    private static String hasloUzytkownika;
    private static List<String> nazwyTabel;

    public static Connection getConnection() {
        return connection;
    }

    private static java.sql.Connection connection;
    private static InformacjeOTabeli informacjeOTabeli;

    private InformacjeOBazie(String serverName, String port, String databaseName, String username, String userPassword){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://"+serverName+":"+port+"/" + databaseName, username, userPassword);
            this.nazwaSerwera=serverName;
            this.port= port;
            this.nazwaBazy=databaseName;
            this.nazwaUzytkownika=username;
            this.hasloUzytkownika=userPassword;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try{
            updateTableNames();
            changeActiveTableInfo(nazwyTabel.get(0));
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private void updateTableNames() throws SQLException {
        if (nazwyTabel==null) {
            nazwyTabel = new ArrayList<>(0);
            Statement statement = connection.createStatement();
            String getTablesQueries = "SHOW TABLES;";

            ResultSet resultSet = statement.executeQuery(getTablesQueries);
            while(resultSet.next()){
                String nazwa = resultSet.getString(1);
                nazwyTabel.add(nazwa);
            }
        }
    }

    public static void changeActiveTableInfo(String tableName){
        nazwaWybranejTabeli=tableName;
        informacjeOTabeli= new InformacjeOTabeli(connection, nazwaBazy, nazwaWybranejTabeli);

    }

    public static void createDataBaseInfo(String serverName, String port, String databaseName, String username, String userPassword){
        informacjeOBazie=new InformacjeOBazie(serverName, port, databaseName, username, userPassword);
    }

    public static InformacjeOBazie getInformacjeOBazie(){
        return informacjeOBazie;
    }
}


class InformacjeOTabeli {


    enum InformacjeKolumny{
        NAZWA_KOLUMNY,
        TYP_DANYCH_KOLUMNY,
        IS_NULLABLE
    }

    record Kolumna(String nazwaKolumny, String typDanych, String isNullable){
        String getInfo(InformacjeKolumny k){
            return switch (k){
                case NAZWA_KOLUMNY -> this.nazwaKolumny;
                case TYP_DANYCH_KOLUMNY -> this.typDanych;
                case IS_NULLABLE -> this.isNullable;

            };
        }
    }

    private String kluczGlowny;
    private List<Kolumna> listaKolumn;
    private Connection connection;
    InformacjeOTabeli(Connection connection, String nazwaBazy, String nazwaTabeli){
        this.connection=connection;
        try {
            ustalKluczGlowny(nazwaBazy, nazwaTabeli);
            zaktualizujListeKolumn(nazwaBazy, nazwaTabeli);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void ustalKluczGlowny(String nazwaBazy, String nazwaTabeli) throws SQLException {
        Statement statement = null;
        try{
            statement = connection.createStatement();
            String getKluczGlownyQuery ="SELECT COLUMN_NAME " +
                    "FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_NAME='"+nazwaTabeli+"' AND TABLE_SCHEMA = '"+nazwaBazy+"' AND COLUMN_KEY = 'PRI';";
            ResultSet resultSet= statement.executeQuery(getKluczGlownyQuery);
            resultSet.next();
            kluczGlowny=resultSet.getString(1);
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally {
            assert statement!=null;
            statement.close();
        }
    }

    private void zaktualizujListeKolumn(String nazwaBazy, String nazwaTabeli) throws SQLException {
        listaKolumn = new LinkedList<>();
        Statement statement = null;
        try{
            statement = connection.createStatement();
            String getKolumnyQuery ="SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE " +
                    "FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_NAME='"+nazwaTabeli+"' AND TABLE_SCHEMA = '"+nazwaBazy+"';";
            ResultSet resultSet= statement.executeQuery(getKolumnyQuery);
            while(resultSet.next()){
                listaKolumn.add(new Kolumna(resultSet.getString("COLUMN_NAME"),resultSet.getString("DATA_TYPE"), resultSet.getString("IS_NULLABLE")));
            }
        }catch(SQLException _){

        }finally {
            assert statement!=null;
            statement.close();
        }
    }

    public List<String> getInformacjaOKolumnie(InformacjeKolumny informacjeKolumny){
        List<String> lista = new ArrayList<>(0);
        for(Kolumna k: listaKolumn){
            lista.add(k.getInfo(informacjeKolumny));
        }
        return lista;
    }



    public String getInformacjaOKolumnie(int numerKolumny, InformacjeKolumny informacjeKolumny){
        return listaKolumn.get(numerKolumny).getInfo(informacjeKolumny);
    }

    public String[] getNazwyKolumn(){
        String[] nazwyKolumn = new String[getLiczbaKolumn()];
        for(int i=0;i<getLiczbaKolumn();i++){
            nazwyKolumn[i]=getInformacjaOKolumnie(i,InformacjeKolumny.NAZWA_KOLUMNY);
        }
        return nazwyKolumn;
    }


    public int getLiczbaKolumn(){return listaKolumn.size();}

    public String getKluczGlowny(){return kluczGlowny;}



}

