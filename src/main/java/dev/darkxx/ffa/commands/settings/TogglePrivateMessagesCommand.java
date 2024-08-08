package dev.darkxx.ffa.commands.settings;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.settings.SettingsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TogglePrivateMessagesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        SettingsManager.toggleSetting(player, "privateMessages");

        String toggleMessage = Main.getInstance().getConfig().getString("messages.setting_toggle");

        String status = SettingsManager.getUnformattedSettingStatus(player, "privateMessages");
        String msg = toggleMessage
                .replace("{setting_name}", "Private Messages")
                .replace("{setting_status}", status);

        player.sendMessage(Main.formatColors(msg));

        return true;
    }
}
