package by.yayauheny.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.experimental.UtilityClass;

import java.sql.Connection;
import java.sql.SQLException;

@UtilityClass
public class ConnectionManager {

    private static final HikariConfig config = new HikariConfig("src/main/resources/application.properties");
    private static final HikariDataSource ds = new HikariDataSource(config);

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
