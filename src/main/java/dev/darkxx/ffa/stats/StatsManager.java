package dev.darkxx.ffa.stats;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import dev.darkxx.ffa.DatabaseManager;

public class StatsManager {
    private static final Connection connection = DatabaseManager.getConnection();
    private static final ConcurrentMap<UUID, PlayerStats> cache = new ConcurrentHashMap<>();

    public static void addKills(Player player, int killsToAdd) {
        UUID uuid = player.getUniqueId();
        cache.computeIfAbsent(uuid, StatsManager::load).kills += killsToAdd;
    }

    public static void addDeaths(Player player, int deathsToAdd) {
        UUID uuid = player.getUniqueId();
        cache.computeIfAbsent(uuid, StatsManager::load).deaths += deathsToAdd;
    }

    public static void updateKillStreak(Player player, int newKillStreak) {
        UUID uuid = player.getUniqueId();
        cache.computeIfAbsent(uuid, StatsManager::load).killStreak = newKillStreak;
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
        PlayerStats stats = cache.computeIfAbsent(playerUUID, StatsManager::load);
        if (stats.deaths == 0) {
            return (double) stats.kills;
        } else {
            return Double.parseDouble(String.format("%.2f", ((double) stats.kills) / stats.deaths));
        }
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

    public static void save() {
        cache.forEach((uuid, stats) -> {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO player_stats (uuid, kills, deaths, kill_streak, max_kill_streak) VALUES (?, ?, ?, ?, ?) ON CONFLICT(uuid) DO UPDATE SET kills = ?, deaths = ?, kill_streak = ?, max_kill_streak = ?")) {
                statement.setString(1, uuid.toString());
                statement.setInt(2, stats.kills);
                statement.setInt(3, stats.deaths);
                statement.setInt(4, stats.killStreak);
                statement.setInt(5, stats.maxKillStreak);
                statement.setInt(6, stats.kills);
                statement.setInt(7, stats.deaths);
                statement.setInt(8, stats.killStreak);
                statement.setInt(9, stats.maxKillStreak);
                statement.executeUpdate();
            } catch (SQLException e) {
                handleSQLException("Failed to save stats for player " + uuid, e);
            }
        });
    }

    public static PlayerStats load(UUID playerUUID) {
        PlayerStats stats = new PlayerStats();
        try (PreparedStatement statement = connection.prepareStatement("SELECT kills, deaths, kill_streak, max_kill_streak FROM player_stats WHERE uuid = ?")) {
            statement.setString(1, playerUUID.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    stats.kills = resultSet.getInt("kills");
                    stats.deaths = resultSet.getInt("deaths");
                    stats.killStreak = resultSet.getInt("kill_streak");
                    stats.maxKillStreak = resultSet.getInt("max_kill_streak");
                }
            }
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
