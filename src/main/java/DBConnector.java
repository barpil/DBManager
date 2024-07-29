import java.sql.Connection;

public class DBConnector{
    protected Connection connection;
    DBConnector(Connection connection){
        this.connection=connection;
    }

}
