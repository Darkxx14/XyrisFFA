package dev.darkxx.ffa.commands;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.utils.SitUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static dev.darkxx.ffa.Main.formatColors;

public class SitCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (!player.hasPermission("ffa.commands.sit")) {
            String noPermission = Main.getInstance().getConfig().getString("messages.no-permission", "&cNo Permission.");
            sender.sendMessage(formatColors(noPermission));
            return true;
        }

        if (SitUtil.isSitting(player)) {
            String alreadySitting = Main.getInstance().getConfig().getString("messages.already-sitting" , "&cYou are already sitting.");
            player.sendMessage(formatColors(alreadySitting));
            return true;
        }

        SitUtil.sit(player, player.getLocation().add(0, -0.7, 0));
        String sitting = Main.getInstance().getConfig().getString("messages.now-sitting" , "&aYou are now sitting.");
        player.sendMessage(formatColors(sitting));

        return true;
    }
}
