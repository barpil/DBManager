import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBCustomSQLCommand implements SQLRunnable {
    private final Logger log = LoggerFactory.getLogger(DBCustomSQLCommand.class);
    Connection connection;
    private final String textCommand;
    private SQLConsole sqlConsole;
    DBCustomSQLCommand(Connection connection, String textCommand, SQLConsole sqlConsole) {
        this.connection=connection;
        this.textCommand=textCommand;
        this.sqlConsole=sqlConsole;
        Thread.currentThread().setName("DBCustomSQLCommandThread");
    }

    @Override
    public void run() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            if(sqlConsole.isQuery()){
                statement.executeQuery(textCommand);
                sqlConsole.passResult(statement.getResultSet());
                log.debug("Select query successfully performed: ({})", textCommand);
            }
            else{
                statement.execute(textCommand);
                log.debug("SQL command successfully performed: ({})", textCommand);
            }

        } catch (SQLException e) {
            if(e.getErrorCode()==0){
                try {
                    statement.execute(textCommand);
                    log.debug("SQL command successfully performed: ({})", textCommand);
                } catch (SQLException ex) {
                    SQLConsole.poinformujOBledzie(ex);
                    log.debug("Failed to perform SQL command: ({})", textCommand);
                }
            }else {
                SQLConsole.poinformujOBledzie(e);
                log.debug("Failed to permorm select query command: ({})", textCommand);
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
