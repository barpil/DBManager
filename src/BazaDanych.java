package src;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class BazaDanych {
    private static BazaDanych bazaDanych;
    private List<Row> dane = new LinkedList<>();
    private java.sql.Connection connection;
    final String NAZWA_BAZY = "bazatestowa";
    private List<String> listaKolumn;
    private BazaDanych(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Klasa Connection odpowiada za polaczenie z baza danych
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+NAZWA_BAZY, "root", "TestowanieSQL1");
            zaktualizujBaze();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void zaktualizujBaze() throws SQLException {
        Statement statement = null;
        dane.clear();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM USERS");
            zaktualizujNazwyKolumn();
            while(resultSet.next()){
                dane.add(new Row(resultSet.getInt("idusers"),resultSet.getString("nazwa")));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            assert statement!=null;
            statement.close();
        }

    }

    public void dodajDane(int id, String nazwa) throws SQLException {
        Statement statement=null;
        try {
            statement = connection.createStatement();
            String insertQuery= "INSERT INTO users(idusers, nazwa)\n" +
                    "VALUES ("+id+",'"+nazwa+"');";
            statement.execute(insertQuery);
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        finally {
            assert statement != null;
            statement.close();
        }
        zaktualizujBaze();
    }

    public void usunDane(int id) throws SQLException {
        Statement statement=null;
        try{
            statement = connection.createStatement();
            String deleteQuery= "DELETE FROM users WHERE idusers='"+id+"';";
            statement.execute(deleteQuery);
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }finally{
            assert statement != null;
            statement.close();
        }
        zaktualizujBaze();
    }
    public void usunDane(String nazwa) throws SQLException {
        Statement statement=null;
        try{
            statement = connection.createStatement();
            String deleteQuery= "DELETE FROM users WHERE nazwa='"+nazwa+"';";
            statement.execute(deleteQuery);
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }finally{
            assert statement != null;
            statement.close();
        }
        zaktualizujBaze();
    }

    public void zaktualizujNazwyKolumn() throws SQLException {
        listaKolumn = new LinkedList<>();
        Statement statement = null;
        try{
            statement = connection.createStatement();
            String getNazwyKolumnQuery="SELECT COLUMN_NAME " +
                    "FROM information_schema.COLUMNS " +
                    "WHERE TABLE_NAME = 'users' AND TABLE_SCHEMA = DATABASE();";
            ResultSet resultSet= statement.executeQuery(getNazwyKolumnQuery);
            while(resultSet.next()){
                listaKolumn.add(resultSet.getString("COLUMN_NAME"));
            }
        }catch(SQLException _){

        }finally {
            assert statement!=null;
            statement.close();
        }
    }

    public void sortujDane(String by, String order) throws SQLException {
        Statement statement = null;
        dane.clear();
        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM USERS ORDER BY "+by+" "+order+";");
            while(resultSet.next()){
                dane.add(new Row(resultSet.getInt("idusers"),resultSet.getString("nazwa")));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            assert statement!=null;
            statement.close();
        }

    }

    public void zaktualizujID(){
        Statement statement = null;
        try{
            statement = connection.createStatement();
            for(int nr=1;nr<getDane().size()+1;nr++){
                if(nr!=getDane().get(nr-1).getId()){
                    String updateIDQuery="UPDATE USERS SET idusers = "+nr+" WHERE idusers = "+getDane().get(nr-1).getId()+";";
                    statement.execute(updateIDQuery);
                }
            }
            zaktualizujBaze();
            PanelElementow.getPanelElementow().updateModel();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Row> getDane() {
        return dane;
    }

    public Object[][] getData(){
        Object[][] obj = new Object[getDane().size()][2];
        for(int i=0;i<getDane().size();i++){
            obj[i][0]=getDane().get(i).getId();
            obj[i][1]=getDane().get(i).getNazwa();
        }
        return obj;
    }

    public static BazaDanych getBazaDanych(){
        if(bazaDanych==null){
            bazaDanych=new BazaDanych();
        }
        return bazaDanych;
    }

    public List<String> getListaKolumn(){
        return listaKolumn;
    }

    class Row{
        private int id;
        private String nazwa;
        Row(int id, String nazwa){
            this.id=id;
            this.nazwa=nazwa;
        }
        @Override
        public String toString(){
            return id+";"+nazwa;
        }
        public int getId() {
            return id;
        }

        public String getNazwa() {
            return nazwa;
        }
    }
}
