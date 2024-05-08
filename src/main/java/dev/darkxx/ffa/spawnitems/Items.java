package dev.darkxx.ffa.spawnitems;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.api.events.SpawnItemsGiveEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Set;

import static dev.darkxx.ffa.Main.formatColors;
import static dev.darkxx.ffa.Main.prefix;
import static org.bukkit.Bukkit.getServer;

public class Items {
    private static Main main;

    private static FileConfiguration spawnItemsConfig;

    public Items(Main plugin) {
        this.main = plugin;
        getServer().getPluginManager().registerEvents(new EventListener(main), main);
        loadSpawnItemsConfig();
    }

    public static void giveSpawnItems(Player player) {
        Set<String> itemList = getSpawnItemsConfig().getConfigurationSection("items").getKeys(false);
        if (itemList == null || itemList.isEmpty()) {
            player.sendMessage(formatColors(prefix + ChatColor.GRAY + "No items configured in the plugin!"));
            return;
        }

        SpawnItemsGiveEvent spawnItemGevent = new SpawnItemsGiveEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(spawnItemGevent);

        if (spawnItemGevent.isCancelled()) {
            return;
        }

        player.getInventory().clear();

        for (String itemKey : itemList) {
            String path = "items." + itemKey;
            String materialName = getSpawnItemsConfig().getString(path + ".item");
            String name = getSpawnItemsConfig().getString(path + ".name");
            int slot = getSpawnItemsConfig().getInt(path + ".slot");
            boolean hideAttributes = getSpawnItemsConfig().getBoolean(path + ".hide-attributes");
            String rightClickCommand = getSpawnItemsConfig().getString(path + ".right-click-command");
            assert materialName != null;
            Material material = Material.matchMaterial(materialName);
            if (material == null) {
                main.getLogger().warning("Invalid material for item " + itemKey + ": " + materialName);
                continue;
            }
            ItemStack itemStack = new ItemStack(material);
            ItemMeta meta = itemStack.getItemMeta();
            String formattedName = formatColors(name);
            meta.setDisplayName(formattedName);
            if (hideAttributes) {
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }
            if (rightClickCommand != null) {
                meta.getPersistentDataContainer().set(new NamespacedKey(main, "right-click-command"), PersistentDataType.STRING, rightClickCommand);
            }
            Bukkit.getScheduler().runTaskLater(main, () -> {
                itemStack.setItemMeta(meta);
                player.getInventory().setItem(slot, itemStack);
            }, 1);
        }
    }

    public static void sendInvalidCommandMessage(@NotNull CommandSender sender) {
        sender.sendMessage(formatColors("\n"));
        sender.sendMessage(formatColors("&b&lFFA &8| &7Invalid Command"));
        sender.sendMessage(formatColors("\n"));
        sender.sendMessage(formatColors("&b• &7/spawnitems reload"));
        sender.sendMessage(formatColors("&b• &7/spawnitems give <player>\n"));
        sender.sendMessage(formatColors("\n"));
    }

    public static void loadSpawnItemsConfig() {
        File configFile = new File(main.getDataFolder(), "spawnitems.yml");
        if (!configFile.exists()) {
            main.saveResource("spawnitems.yml", false);
        }
        spawnItemsConfig = YamlConfiguration.loadConfiguration(configFile);
    }

    public static FileConfiguration getSpawnItemsConfig() {
        return spawnItemsConfig;
    }
}
