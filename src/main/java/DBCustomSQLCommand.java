import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBCustomSQLCommand implements SQLRunnable {
    Connection connection;
    private final String textCommand;
    private SQLConsole sqlConsole;
    DBCustomSQLCommand(Connection connection, String textCommand, SQLConsole sqlConsole) {
        this.connection=connection;
        this.textCommand=textCommand;
        this.sqlConsole=sqlConsole;
    }

    @Override
    public void run() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            if(sqlConsole.isQuery()){
                statement.executeQuery(textCommand);
                sqlConsole.passResult(statement.getResultSet());
            }
            else{
                statement.execute(textCommand);
            }

        } catch (SQLException e) {
            if(e.getErrorCode()==0){
                try {
                    statement.execute(textCommand);
                } catch (SQLException ex) {
                    SQLConsole.poinformujOBledzie(ex);
                }
            }else {
                SQLConsole.poinformujOBledzie(e);
            }

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

    @Override
    public void poinformujOZakonczeniuWatku() {
        SQLRunnable.super.poinformujOZakonczeniuWatku();
    }
}
