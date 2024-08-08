package dev.darkxx.ffa.stats;

import dev.darkxx.ffa.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StatsManager {
    private static final Connection connection = DatabaseManager.getConnection();
    private static final ConcurrentMap<UUID, StatsManager.PlayerStats> cache = new ConcurrentHashMap<>();

    public static void addKills(Player player, int killsToAdd) {
        UUID uuid = player.getUniqueId();
        StatsManager.PlayerStats stats = cache.computeIfAbsent(uuid, StatsManager::load);
        stats.kills += killsToAdd;
    }

    public static void addDeaths(Player player, int deathsToAdd) {
        UUID uuid = player.getUniqueId();
        StatsManager.PlayerStats stats = cache.computeIfAbsent(uuid, StatsManager::load);
        stats.deaths += deathsToAdd;
    }

    public static void updateKillStreak(Player player, int newKillStreak) {
        UUID uuid = player.getUniqueId();
        StatsManager.PlayerStats stats = cache.computeIfAbsent(uuid, StatsManager::load);
        stats.killStreak = newKillStreak;
        if (newKillStreak > stats.maxKillStreak) {
            stats.maxKillStreak = newKillStreak;
        }
    }

    public static void updateMaxKillStreak(Player player, int newMaxKillStreak) {
        UUID uuid = player.getUniqueId();
        cache.computeIfAbsent(uuid, StatsManager::load).maxKillStreak = newMaxKillStreak;
    }

    public static int getCurrentKills(UUID playerUUID) {
        return cache.computeIfAbsent(playerUUID, StatsManager::load).kills;
    }

    public static int getCurrentDeaths(UUID playerUUID) {
        return cache.computeIfAbsent(playerUUID, StatsManager::load).deaths;
    }

    public static double calculateKDR(UUID playerUUID) {
        StatsManager.PlayerStats stats = cache.computeIfAbsent(playerUUID, StatsManager::load);
        return stats.deaths == 0 ? (double) stats.kills : Double.parseDouble(String.format("%.2f", (double) stats.kills / (double) stats.deaths));
    }

    public static int getCurrentStreak(UUID playerUUID) {
        return cache.computeIfAbsent(playerUUID, StatsManager::load).killStreak;
    }

    public static int getHighestStreak(UUID playerUUID) {
        return cache.computeIfAbsent(playerUUID, StatsManager::load).maxKillStreak;
    }

    public static void editKills(UUID playerUUID, int newKills) {
        cache.computeIfAbsent(playerUUID, StatsManager::load).kills = newKills;
    }

    public static void editDeaths(UUID playerUUID, int newDeaths) {
        cache.computeIfAbsent(playerUUID, StatsManager::load).deaths = newDeaths;
    }

    public static void editStreak(UUID playerUUID, int newStreak) {
        cache.computeIfAbsent(playerUUID, StatsManager::load).killStreak = newStreak;
    }

    public static void editHighestStreak(UUID playerUUID, int newHighestStreak) {
        cache.computeIfAbsent(playerUUID, StatsManager::load).maxKillStreak = newHighestStreak;
    }

    public static void save(UUID playerUUID) {
        StatsManager.PlayerStats stats = cache.get(playerUUID);
        if (stats == null) return;

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO player_stats (uuid, kills, deaths, kill_streak, max_kill_streak) VALUES (?, ?, ?, ?, ?) " +
                            "ON CONFLICT(uuid) DO UPDATE SET kills = ?, deaths = ?, kill_streak = ?, max_kill_streak = ?");

            statement.setString(1, playerUUID.toString());
            statement.setInt(2, stats.kills);
            statement.setInt(3, stats.deaths);
            statement.setInt(4, stats.killStreak);
            statement.setInt(5, stats.maxKillStreak);
            statement.setInt(6, stats.kills);
            statement.setInt(7, stats.deaths);
            statement.setInt(8, stats.killStreak);
            statement.setInt(9, stats.maxKillStreak);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            handleSQLException("Failed to save stats for player " + playerUUID, e);
        }
    }

    public static void saveAll() {
        cache.forEach((uuid, stats) -> save(uuid));
    }

    public static StatsManager.PlayerStats load(UUID playerUUID) {
        StatsManager.PlayerStats stats = new StatsManager.PlayerStats();

        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT kills, deaths, kill_streak, max_kill_streak FROM player_stats WHERE uuid = ?");

            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                stats.kills = resultSet.getInt("kills");
                stats.deaths = resultSet.getInt("deaths");
                stats.killStreak = resultSet.getInt("kill_streak");
                stats.maxKillStreak = resultSet.getInt("max_kill_streak");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException("Failed to load stats for player " + playerUUID, e);
        }

        return stats;
    }

    private static void handleSQLException(String message, SQLException e) {
        Bukkit.getLogger().severe(message + " " + e.getMessage());
    }

    public static class PlayerStats {
        int kills = 0;
        int deaths = 0;
        int killStreak = 0;
        int maxKillStreak = 0;
    }
}
