package dev.darkxx.ffa.commands.settings;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.settings.SettingsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleMentionSoundCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        SettingsManager.toggleSetting(player, "mentionSound");

        String toggleMessage = Main.getInstance().getConfig().getString("messages.setting_toggle");

        String status = SettingsManager.getUnformattedSettingStatus(player, "mentionSound");
        String msg = toggleMessage
                .replace("{setting_name}", "Mention Sound")
                .replace("{setting_status}", status);

        player.sendMessage(Main.formatColors(msg));

        return true;
    }
}

