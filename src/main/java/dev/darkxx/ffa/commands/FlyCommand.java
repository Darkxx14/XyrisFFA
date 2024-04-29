package dev.darkxx.ffa.commands;

import dev.darkxx.ffa.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static dev.darkxx.ffa.Main.formatColors;

public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("ffa.commands.fly")) {
            String noPermission = Main.getInstance().getConfig().getString("messages.no-permission", "&cNo Permission.");
            sender.sendMessage(formatColors(noPermission));
            return true;
        }

        toggleFlight(player);

        return true;
    }

    private void toggleFlight(Player player) {
        player.setAllowFlight(!player.getAllowFlight());

        if (player.getAllowFlight()) {
            String flyEnabled = Main.getInstance().getConfig().getString("flightEnabled", "&aYour fly has been enabled.");
            player.sendMessage(formatColors(flyEnabled));
        } else {
            String flyDisabled = Main.getInstance().getConfig().getString("flightDisabled", "&cYour fly has been disabled.");
            player.sendMessage(formatColors(flyDisabled));
        }
    }
}
