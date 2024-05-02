package dev.darkxx.ffa.commands;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.api.events.NicknameClearEvent;
import dev.darkxx.ffa.api.events.NicknameSetEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static dev.darkxx.ffa.Main.formatColors;

public class NickCommand implements CommandExecutor {

    private final Main main;
    private final File configFile;
    private static FileConfiguration dataConfig;
    private final List<String> blacklist;

    public NickCommand(Main main) {
        this.main = main;
        this.configFile = new File(main.getDataFolder(), "/data/nickname-data.yml");
        dataConfig = YamlConfiguration.loadConfiguration(configFile);
        this.blacklist = main.getConfig().getStringList("blacklisted-nicknames");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("ffa.commands.nickname")) {
            String noPermission = Main.getInstance().getConfig().getString("messages.no-permission", "&cNo Permission.");
            sender.sendMessage(formatColors(noPermission));
            return true;
        }

        if (args.length == 0) {
            String nickCmdUsage = Main.getInstance().getConfig().getString("usage.nickname", "&cUsage, /nick <nickname> | /nick clear");
            player.sendMessage(formatColors(nickCmdUsage));
            return true;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            clearNickname(player);
            return true;
        }

        String nickname = args[0];
        if (isBlacklisted(nickname)) {
            String nickBlacklisted = Main.getInstance().getConfig().getString("messages.nick-blacklisted", "&cThis nickname contains blacklisted words.");
            player.sendMessage(formatColors(nickBlacklisted));
            return true;
        }
        String nickChanged = Main.getInstance().getConfig().getString("messages.nick-changed", "&aYour nickname has been set to %nickname%").replace("%nickname%", nickname);
        player.sendMessage(formatColors(nickChanged));
        saveNickname(player.getUniqueId(), nickname);

        return true;
    }

    private void saveNickname(UUID playerId, String nickname) {
                NicknameSetEvent setEvent = new NicknameSetEvent(playerId, nickname);
                Bukkit.getServer().getPluginManager().callEvent(setEvent);
                if (setEvent.isCancelled()) {
                    return;
                }
                dataConfig.set(playerId.toString(), nickname);
                try {
                    dataConfig.save(configFile);
                } catch (IOException e) {
                    main.getLogger().warning("Could not save nickname data to nickname-data.yml " + e.getMessage());
                }
            }

    public static String getNickname(Player player) {
        String nickname = dataConfig.getString(player.getUniqueId().toString());
        return nickname != null ? nickname : player.getName();
    }

    private void clearNickname(Player player) {
                NicknameClearEvent clearEvent = new NicknameClearEvent(player);
                Bukkit.getServer().getPluginManager().callEvent(clearEvent);
                if (clearEvent.isCancelled()) {
                    return;
                }
                player.setDisplayName(player.getName());
                String nickCleared = Main.getInstance().getConfig().getString("messages.nick-cleared", "&aYour nickname has been cleared.");
                player.sendMessage(formatColors(nickCleared));
                dataConfig.set(player.getUniqueId().toString(), null);
                try {
                    dataConfig.save(configFile);
                } catch (IOException e) {
                    main.getLogger().warning("Could not save nickname data to nickname-data.yml " + e.getMessage());
                }
            }

    private boolean isBlacklisted(String nickname) {
        for (String word : blacklist) {
            if (nickname.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
