package src;

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
        TYP_DANYCH_KOLUMNY
    }

    record Kolumna(String nazwaKolumny, String typDanych){
        String getInfo(InformacjeKolumny k){
            return switch (k){
                case NAZWA_KOLUMNY -> this.nazwaKolumny;
                case TYP_DANYCH_KOLUMNY -> this.typDanych;
            };
        }
    }

    private String kluczGlowny;
    private List<Kolumna> listaKolumn;
    private Connection connection;
    InformacjeOTabeli(Connection connection){
        this.connection=connection;
        try {
            ustalKluczGlowny();
            zaktualizujListeKolumn();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void ustalKluczGlowny() throws SQLException {
        Statement statement = null;
        try{
            statement = connection.createStatement();
            String getKluczGlownyQuery ="SELECT COLUMN_NAME " +
                    "FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_NAME='"+BazaDanych.getBazaDanych().getNazwaTabeli()+"' AND TABLE_SCHEMA = '"+BazaDanych.getBazaDanych().getNazwaBazy()+"' AND COLUMN_KEY = 'PRI';";
            ResultSet resultSet= statement.executeQuery(getKluczGlownyQuery);
            kluczGlowny=resultSet.getString(1);
        }catch(SQLException _){

        }finally {
            assert statement!=null;
            statement.close();
        }
    }

    private void zaktualizujListeKolumn() throws SQLException {
        listaKolumn = new LinkedList<>();
        Statement statement = null;
        try{
            statement = connection.createStatement();
            String getKolumnyQuery ="SELECT COLUMN_NAME, DATA_TYPE " +
                    "FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_NAME='"+BazaDanych.getBazaDanych().getNazwaTabeli()+"' AND TABLE_SCHEMA = '"+BazaDanych.getBazaDanych().getNazwaBazy()+"';";
            ResultSet resultSet= statement.executeQuery(getKolumnyQuery);
            while(resultSet.next()){
                listaKolumn.add(new Kolumna(resultSet.getString("COLUMN_NAME"),resultSet.getString("DATA_TYPE")));
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

    public int getLiczbaKolumn(){return listaKolumn.size();}

    public String getKluczGlowny(){return kluczGlowny;}



}
