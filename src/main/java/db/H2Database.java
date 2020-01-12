package db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class H2Database {

  private Connection connection;

  public Connection getConnection() throws IOException, SQLException {
    if (connection == null) {
      Properties prop = getProperties();

      connection = DriverManager
          .getConnection(
              prop.getProperty("db.url"),
              prop.getProperty("db.username"),
              prop.getProperty("db.password"));
    }
    return connection;
  }

  private Properties getProperties() throws IOException {
    InputStream input = H2Database.class.getClassLoader().getResourceAsStream("config.properties");
    Properties prop = new Properties();
    prop.load(input);
    return prop;
  }
}
