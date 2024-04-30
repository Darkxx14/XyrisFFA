package dev.darkxx.ffa.commands;

import dev.darkxx.ffa.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static dev.darkxx.ffa.Main.formatColors;

public class PingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendPingInfo(player, player);
            return true;
        }
        if (args.length == 1) {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null || !targetPlayer.isOnline()) {
                player.sendMessage(ChatColor.RED + "Player " + args[0] + " is not online.");
                return true;
            }
            sendPingInfo(player, targetPlayer);
            return true;
        }
        String usagePing = Main.getInstance().getConfig().getString("usage.ping", "&cUsage, /ping [playerName]");
        player.sendMessage(formatColors(usagePing));
        return true;
    }

    private void sendPingInfo(Player sender, Player target) {
        int ping = getPing(target);
        ChatColor color = getColorForPing(ping);

        List<String> messages = Main.getInstance().getConfig().getStringList("messages.ping.format");
        String otherPlayer = target.getName() + "'s";

        for (String message : messages) {
            message = message.replace("%player%", sender.equals(target) ? "Your" : otherPlayer);
            message = message.replace("%ping%", Integer.toString(ping));
            message = message.replace("%color%", color.toString());

            sender.sendMessage(formatColors(message));
        }
    }

    public static int getPing(Player player) {
        return player.getPing();
    }

    private ChatColor getColorForPing(int ping) {
        ChatColor color = ChatColor.GRAY;

        for (String category : Main.getInstance().getConfig().getConfigurationSection("messages.ping.format.colors").getKeys(false)) {
            int threshold = Main.getInstance().getConfig().getInt("messages.ping.format.colors." + category + ".threshold");
            String colorCode = Main.getInstance().getConfig().getString("messages.ping.format.colors." + category + ".color");

            if (ping < threshold) {
                color = ChatColor.valueOf(colorCode);
                break;
            }
        }

        return color;
    }
}
