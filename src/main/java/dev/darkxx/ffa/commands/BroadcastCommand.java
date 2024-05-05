package dev.darkxx.ffa.commands;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.api.events.BroadcastEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static dev.darkxx.ffa.Main.formatColors;

public class BroadcastCommand implements CommandExecutor {

    private final FileConfiguration config;

    public BroadcastCommand(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("ffa.commands.broadcast")) {
            String noPermission = config.getString("messages.no-permission", "&cNo Permission.");
            sender.sendMessage(formatColors(noPermission));
            return true;
        }

        String message = String.join(" ", args);
        BroadcastEvent broadcastEvent = new BroadcastEvent(sender, message);
        Bukkit.getServer().getPluginManager().callEvent(broadcastEvent);

        if (broadcastEvent.isCancelled()) {
            return true;
        }

        if (args.length == 0) {
            String usageBcast = Main.getInstance().getConfig().getString("usage.broadcast", "&cUsage, /broadcast <message>");
            sender.sendMessage(formatColors(usageBcast));
            return true;
        }

        if (!config.contains("messages.broadcast.format")) {
            sender.sendMessage(formatColors("&cThe broadcast message is not configured."));
            return true;
        }

        List<String> broadcastMessageList = config.getStringList("messages.broadcast.format");
        if (broadcastMessageList.isEmpty()) {
            sender.sendMessage(formatColors("&cThe broadcast message is empty."));
            return true;
        }

        StringBuilder broadcastMessage = new StringBuilder();
        int size = broadcastMessageList.size();
        for (int i = 0; i < size; i++) {
            String line = broadcastMessageList.get(i);
            if (!line.isEmpty()) {
                line = line.replace("%player%", sender instanceof Player ? ((Player) sender).getDisplayName() : "Server");
                line = line.replace("%message%", String.join(" ", args));
                broadcastMessage.append(line);
                if (i < size - 1) {
                    broadcastMessage.append("\n");
                }
            }
        }
        sender.getServer().broadcastMessage(formatColors(PlaceholderAPI.setPlaceholders((Player) sender, broadcastMessage.toString())));
        return true;
    }
}