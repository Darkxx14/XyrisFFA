package dev.darkxx.ffa.stats;

import dev.darkxx.ffa.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static dev.darkxx.ffa.Main.formatColors;

public class Stats implements Listener {

    private final int killStreakThreshold;
    private final int deathStreakThreshold;
    private final FileConfiguration config;

    public Stats(FileConfiguration config) {
        this.config = config;
        this.killStreakThreshold = config.getInt("StreakThreshold");
        this.deathStreakThreshold = config.getInt("StreakLoseThreshold");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        CompletableFuture.runAsync(() -> {
            if (victim.getKiller() != null && !victim.getKiller().equals(victim)) {
                Player attacker = victim.getKiller();
                int currentStreak = StatsManager.getCurrentStreak(attacker.getUniqueId());
                currentStreak++;
                StatsManager.updateKillStreak(attacker, currentStreak);
                StatsManager.updateMaxKillStreak(attacker, Math.max(currentStreak, StatsManager.getHighestStreak(attacker.getUniqueId())));
                if (currentStreak % this.killStreakThreshold == 0) {
                    String playerName = attacker.getName();
                    int finalCurrentStreak = currentStreak;
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                        broadcastStreakMessage(playerName, finalCurrentStreak, "StreakMessage");
                    }, 3);
                }
                StatsManager.addKills(attacker, 1);
            }

            int currentStreak = StatsManager.getCurrentStreak(victim.getUniqueId());
            StatsManager.updateKillStreak(victim, 0);
            StatsManager.updateMaxKillStreak(victim, StatsManager.getHighestStreak(victim.getUniqueId()));
            StatsManager.addDeaths(victim, 1);

            if (currentStreak >= this.deathStreakThreshold) {
                String playerName = victim.getName();
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    broadcastStreakMessage(playerName, currentStreak, "StreakLose");
                }, 3);
            }
        });
    }

    private void broadcastStreakMessage(String playerName, int streak, String configPath) {
        List<String> messages = config.getStringList(configPath);
        for (String message : messages) {
            message = message.replace("%player%", playerName).replace("%streak%", String.valueOf(streak));
            Bukkit.broadcastMessage(formatColors(message));
        }
    }
}
