import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;



public class InformacjeOTabeli {


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

    static InformacjeOTabeli informacjeOTabeli=null;

    private String kluczGlowny;
    private List<Kolumna> listaKolumn;
    private Connection connection;
    InformacjeOTabeli(Connection connection, BazaDanych.InformacjeOBazie informacjeOBazie, String nazwaTabeli){
        informacjeOTabeli=this;
        this.connection=connection;
        try {
            ustalKluczGlowny(informacjeOBazie, nazwaTabeli);
            zaktualizujListeKolumn(informacjeOBazie, nazwaTabeli);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void ustalKluczGlowny(BazaDanych.InformacjeOBazie informacjeOBazie, String nazwaTabeli) throws SQLException {
        Statement statement = null;
        try{
            statement = connection.createStatement();
            String getKluczGlownyQuery ="SELECT COLUMN_NAME " +
                    "FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_NAME='"+nazwaTabeli+"' AND TABLE_SCHEMA = '"+informacjeOBazie.nazwaBazy()+"' AND COLUMN_KEY = 'PRI';";
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

    private void zaktualizujListeKolumn(BazaDanych.InformacjeOBazie informacjeOBazie, String nazwaTabeli) throws SQLException {
        listaKolumn = new LinkedList<>();
        Statement statement = null;
        try{
            statement = connection.createStatement();
            String getKolumnyQuery ="SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE " +
                    "FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_NAME='"+nazwaTabeli+"' AND TABLE_SCHEMA = '"+informacjeOBazie.nazwaBazy()+"';";
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

    public static InformacjeOTabeli getInformacjeOTabeli() {
        return informacjeOTabeli;

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
