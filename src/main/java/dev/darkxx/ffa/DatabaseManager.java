package dev.darkxx.ffa;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import static dev.darkxx.ffa.Main.formatColors;
import static dev.darkxx.ffa.Main.prefix;

public class DatabaseManager {

    private static HikariDataSource dataSource;

    public static void connect() {
        try {
            File dataFolder = Main.getInstance().getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }

            Bukkit.getConsoleSender().sendMessage(formatColors(prefix + "&7Establishing connection to the Database..."));

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:sqlite:" + dataFolder.getAbsolutePath() + "/playerdata.db");

            dataSource = new HikariDataSource(config);

            Bukkit.getConsoleSender().sendMessage(formatColors(prefix + "&7Successfully connected to the Database."));
            createTables();
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to establish connection to the database: " + e.getMessage());
        }
    }

    public static void disconnect() {
        try {
            if (dataSource != null) {
                Bukkit.getConsoleSender().sendMessage(formatColors(prefix + "&7Closing the Database connection..."));
                dataSource.close();
                Bukkit.getConsoleSender().sendMessage(formatColors(prefix + "&7Database connection closed successfully."));
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("Failed to close the database connection: " + e.getMessage());
        }
    }

    private static void createTables() {
        try (Connection connection = dataSource.getConnection()) {
            try (var statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS player_stats (" +
                        "uuid VARCHAR(36) PRIMARY KEY," +
                        "kills INT DEFAULT 0," +
                        "deaths INT DEFAULT 0," +
                        "kill_streak INT DEFAULT 0," +
                        "max_kill_streak INT DEFAULT 0)");
            }
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to create tables " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        if (dataSource == null) {
            throw new IllegalStateException("Data source is not initialized.");
        }
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to obtain a connection from the data source.", e);
        }
    }
}