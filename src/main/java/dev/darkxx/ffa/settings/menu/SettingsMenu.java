package dev.darkxx.ffa.settings.menu;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.settings.SettingsManager;
import dev.darkxx.ffa.utils.gui.GuiBuilder;
import dev.darkxx.ffa.utils.gui.ItemBuilderGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static dev.darkxx.ffa.Main.formatColors;

public class SettingsMenu extends GuiBuilder {

    public static FileConfiguration settingsConfig;

    static {
        try {
            File configFile = new File(Main.getInstance().getDataFolder(), "menus/settings_menu.yml");
            if (!configFile.exists()) {
                configFile.getParentFile().mkdirs();
                Main.getInstance().saveResource("menus/settings_menu.yml", false);
            }
            settingsConfig = YamlConfiguration.loadConfiguration(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SettingsMenu() {
        super(27);
    }

    public static GuiBuilder menu(Player player) {
        if (settingsConfig == null) {
            Bukkit.getLogger().warning("Settings config is not loaded properly.");
            return new GuiBuilder(3 * 9, formatColors(settingsConfig.getString("menu.title")));
        }

        GuiBuilder inventory = new GuiBuilder(3 * 9, formatColors("Settings"));

        settingsConfig.getConfigurationSection("menu").getKeys(false).forEach(settingKey -> {
            String name = settingsConfig.getString("menu." + settingKey + ".name");
            String material = settingsConfig.getString("menu." + settingKey + ".material");
            int slot = settingsConfig.getInt("menu." + settingKey + ".slot");
            List<String> lore = settingsConfig.getStringList("menu." + settingKey + ".lore");

            if (material == null) {
                return;
            }

            try {
                Material mat = Material.valueOf(material);
                settingItem(inventory, player, settingKey, mat, slot, name, lore);
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Invalid Material value: " + material + " for setting: " + settingKey);
            }
        });

        return inventory;
    }

    private static void settingItem(GuiBuilder inventory, Player player, String settingKey, Material material, int slot, String name, List<String> lore) {
        if (settingsConfig == null) {
            Bukkit.getLogger().warning("Settings config is not loaded properly.");
            return;
        }

        String status = SettingsManager.getSettingStatus(player, settingKey);
        List<String> formattedLore = lore.stream()
                .map(line -> formatColors(line.replace("%status%", status)))
                .collect(Collectors.toList());

        ItemStack item = new ItemBuilderGUI(material)
                .name(formatColors(name))
                .lore(formattedLore.toArray(new String[0]))
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .build();

        inventory.setItem(slot, item, p -> {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                SettingsManager.toggleSetting(player, settingKey);
                String updatedStatus = SettingsManager.getSettingStatus(player, settingKey);
                List<String> updatedLore = lore.stream()
                        .map(line -> formatColors(line.replace("%status%", updatedStatus)))
                        .collect(Collectors.toList());
                ItemMeta meta = item.getItemMeta();
                meta.setLore(updatedLore);
                item.setItemMeta(meta);
                player.getOpenInventory().setItem(slot, item);
                player.playSound(player.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 1.0f, 1.0f);
            });
        });
    }

    public static FileConfiguration getSettingsConfig() {
        return settingsConfig;
    }
}
