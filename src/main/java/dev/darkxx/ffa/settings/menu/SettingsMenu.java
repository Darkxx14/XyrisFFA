package dev.darkxx.ffa.settings.menu;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.settings.SettingsManager;
import dev.darkxx.ffa.utils.gui.GuiBuilder;
import dev.darkxx.ffa.utils.gui.ItemBuilderGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

import static dev.darkxx.ffa.Main.formatColors;

public class SettingsMenu extends GuiBuilder {

    private static Main main;

    public SettingsMenu(Main main) {
        super(27);
        this.main = main;
    }

    public static GuiBuilder createSettingsMenu(Player player, Main main) {
        GuiBuilder inventory = new GuiBuilder(3 * 9, formatColors("Settings"));

        // Directional Damage Tilt Setting
        ItemStack toggleTilt = new ItemBuilderGUI(Material.NETHERITE_SWORD)
                .name(formatColors("&eOld Damage Tilt &7(1.19.4+)"))
                .lore(formatColors("&7Disable the new directional damage tilt that"), formatColors("&7was removed in 1.3.1, but added back in 1.19.4"), formatColors("  "), formatColors("&7Status: " + SettingsManager.getSettingStatus(player, "OldDamageTilt")))
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .build();
        inventory.setItem(11, toggleTilt, p -> {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                SettingsManager.toggleSetting(player, "OldDamageTilt");
                String updatedStatus = SettingsManager.getSettingStatus(player, "OldDamageTilt");
                ItemMeta meta = toggleTilt.getItemMeta();
                meta.setLore(Arrays.asList(formatColors("&7Disable the new directional damage tilt that"), formatColors("&7was removed in 1.3.1, but added back in 1.19.4"), formatColors("  "), formatColors("&7Status: " + updatedStatus)));
                toggleTilt.setItemMeta(meta);
                player.getOpenInventory().setItem(11, toggleTilt);
                player.playSound(player.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 1.0f, 1.0f);
            });
        });

        // Private Messages Setting
        ItemStack togglePM = new ItemBuilderGUI(Material.WRITABLE_BOOK)
                .name(formatColors("&ePrivate Messages"))
                .lore(formatColors("&7Toggle your private messages (PM)"), formatColors("  "), formatColors("&7Status: " + SettingsManager.getSettingStatus(player, "privateMessages")))

                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .build();
        inventory.setItem(12, togglePM, p -> {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                SettingsManager.toggleSetting(player, "privateMessages");
                String updatedStatus = SettingsManager.getSettingStatus(player, "privateMessages");
                ItemMeta meta = togglePM.getItemMeta();
                meta.setLore(Arrays.asList(formatColors("&7Toggle your private messages (PM)"), formatColors("  "), formatColors("&7Status: " + updatedStatus)));
                togglePM.setItemMeta(meta);
                player.getOpenInventory().setItem(12, togglePM);
                player.playSound(player.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 1.0f, 1.0f);
            });
        });

        // Auto GG Setting
        ItemStack toggleGG = new ItemBuilderGUI(Material.SKELETON_SKULL)
                .name(formatColors("&eAuto GG"))
                .lore(formatColors("&7Send a message 'gg' automatically when killed someone"), formatColors("  "), formatColors("&7Status: " + SettingsManager.getSettingStatus(player, "autoGG")))
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .build();
        inventory.setItem(13, toggleGG, p -> {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                SettingsManager.toggleSetting(player, "autoGG");
                String updatedStatus = SettingsManager.getSettingStatus(player, "autoGG");
                ItemMeta meta = toggleGG.getItemMeta();
                meta.setLore(Arrays.asList(formatColors("&7Send a message 'gg' automatically when killed someone"), formatColors("  "), formatColors("&7Status: " + updatedStatus)));
                toggleGG.setItemMeta(meta);
                player.getOpenInventory().setItem(13, toggleGG);
                player.playSound(player.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 1.0f, 1.0f);
            });
        });

        // Mention Sound Setting
        ItemStack toggleMention = new ItemBuilderGUI(Material.ENDER_PEARL)
                .name(formatColors("&eMention Sound"))
                .lore(formatColors("&7Toggle the sound when you are mentioned in the chat"), formatColors("  "), formatColors("&7Status: " + SettingsManager.getSettingStatus(player, "mentionSound")))
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .build();
        inventory.setItem(14, toggleMention, p -> {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                SettingsManager.toggleSetting(player, "mentionSound");
                String updatedStatus = SettingsManager.getSettingStatus(player, "mentionSound");
                ItemMeta meta = toggleMention.getItemMeta();
                meta.setLore(Arrays.asList(formatColors("&7Toggle the sound when you are mentioned in the chat"), formatColors("  "), formatColors("&7Status: " + updatedStatus)));
                toggleMention.setItemMeta(meta);
                player.getOpenInventory().setItem(14, toggleMention);
                player.playSound(player.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 1.0f, 1.0f);
            });
        });

        // QuickRespawn Setting
        ItemStack toggleQuickRespawn = new ItemBuilderGUI(Material.FEATHER)
                .name(formatColors("&eQuick Respawn Item"))
                .lore(formatColors("&7Toggle to get a quick respawn item on death"), formatColors("  "), formatColors("&7Status: " + SettingsManager.getSettingStatus(player, "toggleQuickRespawn")))
                .flags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
                .build();
        inventory.setItem(15, toggleQuickRespawn, p -> {
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
                SettingsManager.toggleSetting(player, "toggleQuickRespawn");
                String updatedStatus = SettingsManager.getSettingStatus(player, "toggleQuickRespawn");
                ItemMeta meta = toggleQuickRespawn.getItemMeta();
                meta.setLore(Arrays.asList(formatColors("&7Toggle to get a quick respawn item on death"), formatColors("  "), formatColors("&7Status: " + updatedStatus)));
                toggleQuickRespawn.setItemMeta(meta);
                player.getOpenInventory().setItem(15, toggleQuickRespawn);
                player.playSound(player.getLocation(), Sound.UI_LOOM_TAKE_RESULT, 1.0f, 1.0f);
            });
        });
        return inventory;
    }
}