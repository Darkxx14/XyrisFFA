package dev.darkxx.ffa.commands;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.api.events.PlayerSuicideEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static dev.darkxx.ffa.Main.formatColors;

public class SuicideCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("ffa.commands.suicide")) {
            String noPermission = Main.getInstance().getConfig().getString("messages.no-permission", "&cNo Permission.");
            sender.sendMessage(formatColors(noPermission));
            return true;
        }

        if (!(sender instanceof Player player)) {
            return true;
        }

        PlayerSuicideEvent suicideEvent = new PlayerSuicideEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(suicideEvent);
        if (suicideEvent.isCancelled()) {
            return true;
        }
        player.setHealth(0);
        String iWantToSuicide = Main.getInstance().getConfig().getString("messages.i-want-to-die", "&cYou have committed suicide.");
        sender.sendMessage(formatColors(PlaceholderAPI.setPlaceholders(player, iWantToSuicide)));
        return true;
    }
}
