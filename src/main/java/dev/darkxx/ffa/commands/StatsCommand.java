package dev.darkxx.ffa.commands;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.stats.StatsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static dev.darkxx.ffa.Main.formatColors;

public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (args.length > 0) {
            UUID targetUUID = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
            displayStats(player, targetUUID);
        } else {
            displayStats(player, player.getUniqueId());
        }
        return true;
    }

    private String getPlayerName(UUID playerUUID) {
        return Bukkit.getOfflinePlayer(playerUUID).getName();
    }

    private List<String> getFormattedStats(UUID targetUUID) {
        List<String> formattedStats = new ArrayList<>();
        FileConfiguration config = Main.getInstance().getConfig();
        String playerName = getPlayerName(targetUUID);

        for (String format : config.getStringList("messages.stats.format")) {
            format = format.replace("%player%", playerName)
                    .replace("%kills%", String.valueOf(StatsManager.getCurrentKills(targetUUID)))
                    .replace("%deaths%", String.valueOf(StatsManager.getCurrentDeaths(targetUUID)))
                    .replace("%kdr%", String.valueOf(StatsManager.calculateKDR(targetUUID)))
                    .replace("%streak%", String.valueOf(StatsManager.getCurrentStreak(targetUUID)))
                    .replace("%maxstreak%", String.valueOf(StatsManager.getHighestStreak(targetUUID)));
            formattedStats.add(formatColors(format));
        }
        return formattedStats;
    }

    private void displayStats(Player player, UUID targetUUID) {
        List<String> formattedStats = getFormattedStats(targetUUID);
        formattedStats.forEach(player::sendMessage);
    }
}