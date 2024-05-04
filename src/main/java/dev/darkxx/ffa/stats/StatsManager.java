package dev.darkxx.ffa.stats;

import dev.darkxx.ffa.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import dev.darkxx.ffa.DatabaseManager;

public class StatsManager {
    private static final Connection connection = DatabaseManager.getConnection();

    public static void addKills(Player player, int killsToAdd) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO player_stats (uuid, kills) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET kills = kills + ?")) {
                statement.setString(1, player.getUniqueId().toString());
                statement.setInt(2, killsToAdd);
                statement.setInt(3, killsToAdd);
                statement.executeUpdate();
            } catch (SQLException e) {
                handleSQLException("Failed to add kills for player " + player.getName(), e);
            }
        });
    }

    public static void addDeaths(Player player, int deathsToAdd) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO player_stats (uuid, deaths) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET deaths = deaths + ?")) {
                statement.setString(1, player.getUniqueId().toString());
                statement.setInt(2, deathsToAdd);
                statement.setInt(3, deathsToAdd);
                statement.executeUpdate();
            } catch (SQLException e) {
                handleSQLException("Failed to add deaths for player " + player.getName(), e);
            }
        });
    }

    public static void updateKillStreak(Player player, int newKillStreak) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE player_stats SET kill_streak = ? WHERE uuid = ?")) {
                statement.setInt(1, newKillStreak);
                statement.setString(2, player.getUniqueId().toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                handleSQLException("Failed to update kill streak for player " + player.getName(), e);
            }
        });
    }

    public static void updateMaxKillStreak(Player player, int newMaxKillStreak) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE player_stats SET max_kill_streak = ? WHERE uuid = ?")) {
                statement.setInt(1, newMaxKillStreak);
                statement.setString(2, player.getUniqueId().toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                handleSQLException("Failed to update max kill streak for player " + player.getName(), e);
            }
        });
    }

    public static int getCurrentKills(UUID playerUUID) {
        return executeQueryForInt("SELECT kills FROM player_stats WHERE uuid = ?", playerUUID.toString());
    }

    public static int getCurrentDeaths(UUID playerUUID) {
        return executeQueryForInt("SELECT deaths FROM player_stats WHERE uuid = ?", playerUUID.toString());
    }

    public static double calculateKDR(UUID playerUUID) {
        int kills = getCurrentKills(playerUUID);
        int deaths = getCurrentDeaths(playerUUID);
        if (deaths == 0) {
            return (double) kills;
        } else {
            return Double.parseDouble(String.format("%.2f", ((double) kills) / deaths));
        }
    }

    public static int getCurrentStreak(UUID playerUUID) {
        return executeQueryForInt("SELECT kill_streak FROM player_stats WHERE uuid = ?", playerUUID.toString());
    }

    public static int getHighestStreak(UUID playerUUID) {
        return executeQueryForInt("SELECT max_kill_streak FROM player_stats WHERE uuid = ?", playerUUID.toString());
    }

    public static void editKills(UUID playerUUID, int newKills) {
        executeUpdate("UPDATE player_stats SET kills = ? WHERE uuid = ?", newKills, playerUUID.toString());
    }

    public static void editDeaths(UUID playerUUID, int newDeaths) {
        executeUpdate("UPDATE player_stats SET deaths = ? WHERE uuid = ?", newDeaths, playerUUID.toString());
    }

    public static void editStreak(UUID playerUUID, int newStreak) {
        executeUpdate("UPDATE player_stats SET kill_streak = ? WHERE uuid = ?", newStreak, playerUUID.toString());
    }

    public static void editHighestStreak(UUID playerUUID, int newHighestStreak) {
        executeUpdate("UPDATE player_stats SET max_kill_streak = ? WHERE uuid = ?", newHighestStreak, playerUUID.toString());
    }

    private static void executeUpdate(String query, Object... parameters) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            handleSQLException("Failed to execute update query", e);
        }
    }

    private static int executeQueryForInt(String query, Object... parameters) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            handleSQLException("Failed to execute query for int", e);
        }
        return 0;
    }

    private static void handleSQLException(String message, SQLException e) {
        Bukkit.getLogger().severe(message + " " + e.getMessage());
    }
}