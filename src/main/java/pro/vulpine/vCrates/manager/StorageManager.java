package pro.vulpine.vCrates.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import pro.vulpine.vCrates.VCrates;
import pro.vulpine.vCrates.instance.StorageMethod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class StorageManager {

    private final VCrates plugin;

    private HikariDataSource dataSource;

    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String password;

    public StorageManager(VCrates plugin, StorageMethod method, String host, String port, String database, String username, String password) {
        this.plugin = plugin;

        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;

        if (method == null) {
            method = StorageMethod.H2;
        }

        setup(method);
    }

    public void setup(StorageMethod method) {

        HikariConfig config = new HikariConfig();

        if (method == StorageMethod.H2) {

            String databasePath = plugin.getDataFolder().getAbsolutePath();
            config.setJdbcUrl("jdbc:h2:" + databasePath + "/database;MODE=MYSQL;AUTO_RECONNECT=TRUE");
            config.setDriverClassName("org.h2.Driver");
            config.setUsername("sa");
            config.setPassword("");

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(60000);
            config.setLeakDetectionThreshold(3000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(10000);

        } else if (method == StorageMethod.MYSQL) {

            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true&characterEncoding=utf8");
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setUsername(username);
            config.setPassword(password);

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setLeakDetectionThreshold(3000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(10000);

        }

        config.setAutoCommit(true);
        config.setValidationTimeout(3000);
        config.setConnectionTestQuery("SELECT 1");

        dataSource = new HikariDataSource(config);

    }

    public CompletableFuture<ResultSet> executeQuery(String query, Object... params) {

        return CompletableFuture.supplyAsync(() -> {

            Connection connection;
            PreparedStatement statement;
            ResultSet rs;

            try {

                connection = dataSource.getConnection();
                statement = connection.prepareStatement(query);

                for (int i = 0; i < params.length; i++) {

                    statement.setObject(i + 1, params[i]);

                }

                rs = statement.executeQuery();

                return rs;

            } catch (SQLException e) {

                throw new RuntimeException("Error while executing query: ", e);

            }

        });

    }

    public CompletableFuture<Integer> executeUpdate(String query, Object... params) {

        return CompletableFuture.supplyAsync(() -> {

            try (Connection connection = dataSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                for (int i = 0; i < params.length; i++) {

                    statement.setObject(i + 1, params[i]);

                }

                return statement.executeUpdate();

            } catch (SQLException e) {

                throw new RuntimeException("Error while executing query: ", e);

            }

        });

    }

    public void close() {

        if (dataSource != null) {

            dataSource.close();

        }

    }

}