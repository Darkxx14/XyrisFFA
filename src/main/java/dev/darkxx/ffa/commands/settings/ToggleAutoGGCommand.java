package dev.darkxx.ffa.commands.settings;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.settings.SettingsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleAutoGGCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        SettingsManager.toggleSetting(player, "autoGG");

        String toggleMessage = Main.getInstance().getConfig().getString("messages.setting_toggle");

        String status = SettingsManager.getUnformattedSettingStatus(player, "autoGG");
        String msg = toggleMessage
                .replace("{setting_name}", "AutoGG")
                .replace("{setting_status}", status);

        player.sendMessage(Main.formatColors(msg));

        return true;
    }
}
