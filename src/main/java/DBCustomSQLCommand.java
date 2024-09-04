import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DBCustomSQLCommand extends DBConnector implements SQLRunnable {
    private final String textCommand;
    DBCustomSQLCommand(Connection connection, String textCommand) {
        super(connection);
        this.textCommand=textCommand;
    }

    @Override
    public void run() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(textCommand);
        } catch (SQLException e) {
            throw new RuntimeException();
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
