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
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO player_stats (uuid, kills) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET kills = kills + ?");
                statement.setString(1, player.getUniqueId().toString());
                statement.setInt(2, killsToAdd);
                statement.setInt(3, killsToAdd);
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Failed to add kills for player " + player.getName() + " " + e.getMessage());
            }
        });
    }

    public static void addDeaths(Player player, int deathsToAdd) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO player_stats (uuid, deaths) VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET deaths = deaths + ?");
                statement.setString(1, player.getUniqueId().toString());
                statement.setInt(2, deathsToAdd);
                statement.setInt(3, deathsToAdd);
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Failed to add deaths for player " + player.getName() + " " + e.getMessage());
            }
        });
    }

    public static void updateKillStreak(Player player, int newKillStreak) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("UPDATE player_stats SET kill_streak = ? WHERE uuid = ?");
                statement.setInt(1, newKillStreak);
                statement.setString(2, player.getUniqueId().toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Failed to update kill streak for player " + player.getName() + " " + e.getMessage());
            }
        });
    }

    public static void updateMaxKillStreak(Player player, int newMaxKillStreak) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("UPDATE player_stats SET max_kill_streak = ? WHERE uuid = ?");
                statement.setInt(1, newMaxKillStreak);
                statement.setString(2, player.getUniqueId().toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Failed to update max kill streak for player " + player.getName() + " " + e.getMessage());
            }
        });
    }

    public static int getCurrentKills(UUID playerUUID) {
        int kills = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT kills FROM player_stats WHERE uuid = ?");
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                kills = resultSet.getInt("kills");
            }
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to get current kills for player " + playerUUID + " " + e.getMessage());
        }
        return kills;
    }

    public static int getCurrentDeaths(UUID playerUUID) {
        int deaths = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT deaths FROM player_stats WHERE uuid = ?");
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                deaths = resultSet.getInt("deaths");
            }
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to get current deaths for player " + playerUUID + " " + e.getMessage());
        }
        return deaths;
    }

    public static double calculateKDR(UUID playerUUID) {
        int kills = getCurrentKills(playerUUID);
        int deaths = getCurrentDeaths(playerUUID);
        if (deaths == 0) {
            return kills;
        }
        return Double.parseDouble(String.format("%.2f", ((double) kills) / deaths));
    }

    public static int getCurrentStreak(UUID playerUUID) {
        int killStreak = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT kill_streak FROM player_stats WHERE uuid = ?");
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                killStreak = resultSet.getInt("kill_streak");
            }
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to get current kill streak for player " + playerUUID + " " + e.getMessage());
        }
        return killStreak;
    }

    public static int getHighestStreak(UUID playerUUID) {
        int maxKillStreak = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT max_kill_streak FROM player_stats WHERE uuid = ?");
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                maxKillStreak = resultSet.getInt("max_kill_streak");
            }
            statement.close();
        } catch (SQLException e) {
            Bukkit.getLogger().severe("Failed to get highest kill streak for player " + playerUUID + " " + e.getMessage());
        }
        return maxKillStreak;
    }

    public static void editKills(UUID playerUUID, int newKills) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("UPDATE player_stats SET kills = ? WHERE uuid = ?");
                statement.setInt(1, newKills);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Failed to edit kills for player " + playerUUID + " " + e.getMessage());
            }
        });
    }

    public static void editDeaths(UUID playerUUID, int newDeaths) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("UPDATE player_stats SET deaths = ? WHERE uuid = ?");
                statement.setInt(1, newDeaths);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Failed to edit deaths for player " + playerUUID + " " + e.getMessage());
            }
        });
    }

    public static void editStreak(UUID playerUUID, int newStreak) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("UPDATE player_stats SET kill_streak = ? WHERE uuid = ?");
                statement.setInt(1, newStreak);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Failed to edit kill streak for player " + playerUUID + " " + e.getMessage());
            }
        });
    }

    public static void editHighestStreak(UUID playerUUID, int newHighestStreak) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            try {
                PreparedStatement statement = connection.prepareStatement("UPDATE player_stats SET max_kill_streak = ? WHERE uuid = ?");
                statement.setInt(1, newHighestStreak);
                statement.setString(2, playerUUID.toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                Bukkit.getLogger().severe("Failed to edit highest kill streak for player " + playerUUID + " " + e.getMessage());
            }
        });
    }
}
