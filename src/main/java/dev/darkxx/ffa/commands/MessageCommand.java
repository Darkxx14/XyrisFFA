package dev.darkxx.ffa.commands;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.settings.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static dev.darkxx.ffa.Main.formatColors;

public class MessageCommand implements CommandExecutor {
    private final FileConfiguration config;
    final Map<Player, Player> lastMessaged = new HashMap<>();

    public MessageCommand(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            String usageMessage = Main.getInstance().getConfig().getString("usage.message", "&c/message <player> <message>");
            player.sendMessage(formatColors(usageMessage));
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(formatColors("&cPlayer not found or not online."));
            return true;
        }

        String message = String.join(" ", args).substring(args[0].length() + 1);
        sendMessage(player, targetPlayer, message);
        return true;
    }

    private String formatMessage(String format, Player sender, Player recipient, String message) {
        format = format.replace("%sender%", sender.getName())
                .replace("%recipient%", recipient.getName())
                .replace("%message%", message);
        return format;
    }

    void sendMessage(Player sender, Player recipient, String message) {
        if (!SettingsManager.hasEnabledSetting(recipient, "privateMessages")) {
            String msgsDisabled = Main.getInstance().getConfig().getString("messages.private-messages-disabled", "&c%player% has their Private Messages disabled.")
                    .replace("%player%", recipient.getName());
            sender.sendMessage(formatColors(msgsDisabled));
            return;
        }

        String senderFormat = config.getString("messages.message_format.sender", "&7[To %recipient%] &b%message%").trim();
        String recipientFormat = config.getString("messages.message_format.recipient", "&7[From %sender%] &b%message%").trim();

        String formattedSenderMessage = formatMessage(senderFormat, sender, recipient, message);
        String formattedRecipientMessage = formatMessage(recipientFormat, sender, recipient, message);

        sender.sendMessage(formatColors(formattedSenderMessage));
        recipient.sendMessage(formatColors(formattedRecipientMessage));

        lastMessaged.put(sender, recipient);
        lastMessaged.put(recipient, sender);
    }
}
