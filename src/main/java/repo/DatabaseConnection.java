package repo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
                Properties properties = new Properties();
                properties.load(input);

                String url = properties.getProperty("db.url");
                String user = properties.getProperty("db.user");
                String password = properties.getProperty("db.password");

                connection = DriverManager.getConnection(url, user, password);
            } catch (IOException | SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to connect to the database", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
