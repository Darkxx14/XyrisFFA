package dev.darkxx.ffa.commands;

import dev.darkxx.ffa.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static dev.darkxx.ffa.Main.formatColors;

public class HealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("ffa.commands.heal")) {
            String noPermission = Main.getInstance().getConfig().getString("messages.no-permission", "&cNo Permission.");
            sender.sendMessage(formatColors(noPermission));
            return true;
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);
                player.setSaturation(0);
                player.sendMessage(formatColors("&aYou have been healed."));
                return true;
            }

            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null || !targetPlayer.isOnline()) {
                player.sendMessage(formatColors("&cPlayer not found or not online."));
                return true;
            }

            targetPlayer.setHealth(targetPlayer.getMaxHealth());
            targetPlayer.setFoodLevel(20);
            player.setSaturation(0);
            player.sendMessage(formatColors("&a" + targetPlayer.getName() + " has been healed."));
            return true;
        } else {
            if (args.length == 0) {
                String usageHeal = Main.getInstance().getConfig().getString("usage.heal", "&cUsage, /heal <player>");
                sender.sendMessage(formatColors(usageHeal));
                return true;
            }

            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null || !targetPlayer.isOnline()) {
                sender.sendMessage((formatColors("&cPlayer not found or not online.")));
                return true;
            }

            targetPlayer.setHealth(targetPlayer.getMaxHealth());
            targetPlayer.setFoodLevel(20);
            targetPlayer.setSaturation(0);
            sender.sendMessage(formatColors("&a" + targetPlayer.getName() + " has been healed."));
            return true;
        }
    }
}
