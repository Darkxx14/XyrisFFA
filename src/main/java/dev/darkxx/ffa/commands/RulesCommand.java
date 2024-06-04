package dev.darkxx.ffa.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static dev.darkxx.ffa.Main.formatColors;

public class RulesCommand implements CommandExecutor {

    private final FileConfiguration config;

    public RulesCommand(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        if (!config.contains("messages.rules.format")) {
            player.sendMessage(formatColors("&cNo rules are configured."));
            return true;
        }

        List<String> rules = config.getStringList("messages.rules.format");
        if (rules.isEmpty()) {
            player.sendMessage(formatColors("&cNo rules are configured."));
            return true;
        }

        for (String rule : rules) {
            player.sendMessage(formatColors(PlaceholderAPI.setPlaceholders(player, (rule))));
        }
        return true;
    }
}
