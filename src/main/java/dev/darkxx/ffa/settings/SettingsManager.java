package dev.darkxx.ffa.settings;

import dev.darkxx.ffa.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SettingsManager {

    private static final Main main;
    private static File configFile;
    private static FileConfiguration config;

    static {
        main = Main.getInstance();
        configFile = new File(main.getDataFolder(), "data/settings-data.yml");
        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                main.saveResource("data/settings-data.yml", false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static boolean hasEnabledSetting(Player player, String settingName) {
        ConfigurationSection playerSettings = config.getConfigurationSection("players." + player.getUniqueId());
        if (playerSettings == null) {
            return false;
        }
        return playerSettings.getBoolean(settingName, false);
    }

    public static void toggleSetting(Player player, String settingName) {
        String playerUUID = player.getUniqueId().toString();
        ConfigurationSection playerSettings = config.getConfigurationSection("players." + playerUUID);
        if (playerSettings == null) {
            playerSettings = config.createSection("players." + playerUUID);
        }
        boolean currentValue = playerSettings.getBoolean(settingName, false);
        playerSettings.set(settingName, !currentValue);
        saveConfig();
    }

    public static void setSettingValue(Player player, String settingName, boolean value) {
        String playerUUID = player.getUniqueId().toString();
        ConfigurationSection playerSettings = config.getConfigurationSection("players." + playerUUID);
        if (playerSettings == null) {
            playerSettings = config.createSection("players." + playerUUID);
        }
        playerSettings.set(settingName, value);
        saveConfig();
    }

    public static void ensurePlayerSettings(Player player) {
        String playerUUID = player.getUniqueId().toString();
        ConfigurationSection playerSettings = config.getConfigurationSection("players." + playerUUID);
        if (playerSettings == null) {
            playerSettings = config.createSection("players." + playerUUID);
            addDefaultSettings(playerSettings);
            saveConfig();
        } else {
            addMissingSettings(playerSettings);
        }
    }

    private static void addDefaultSettings(ConfigurationSection playerSettings) {
        List<String> settingsToAdd = Arrays.asList("OldDamageTilt", "privateMessages", "autoGG", "mentionSound", "toggleQuickRespawn");
        for (String setting : settingsToAdd) {
            if (setting.equals("autoGG") || setting.equals("mentionSound")) {
                playerSettings.set(setting, false);
            } else {
                playerSettings.set(setting, true);
            }
        }
    }

    private static void addMissingSettings(ConfigurationSection playerSettings) {
        List<String> settingsToAdd = Arrays.asList("OldDamageTilt", "privateMessages", "autoGG", "mentionSound", "toggleQuickRespawn");
        for (String setting : settingsToAdd) {
            if (!playerSettings.contains(setting)) {
                if (setting.equals("autoGG") || setting.equals("mentionSound")) {
                    playerSettings.set(setting, false);
                } else {
                    playerSettings.set(setting, true);
                }
            }
        }
        saveConfig();
    }

    public static String getSettingStatus(Player player, String settingName) {
        boolean isEnabled = hasEnabledSetting(player, settingName);
        return isEnabled ? "§aENABLED" : "§cDISABLED";
    }

    private static void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            main.getLogger().warning("Could not save settings data file " + e.getMessage());
        }
    }
}
