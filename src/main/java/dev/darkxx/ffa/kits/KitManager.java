package dev.darkxx.ffa.kits;

import dev.darkxx.ffa.api.events.KitGiveEvent;
import org.bukkit.Bukkit;
import dev.darkxx.ffa.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static dev.darkxx.ffa.Main.formatColors;

public class KitManager {

    public static Map<Player, String> lastKit = new HashMap<>();

    public static void sendInvalidCommandMessage(CommandSender sender) {
        sender.sendMessage("\n");
        sender.sendMessage(formatColors("&b&lFFA &8| &7Invalid Command"));
        sender.sendMessage("\n");
        sender.sendMessage(formatColors("&b• &7/ffa kits create <KitName>"));
        sender.sendMessage(formatColors("&b• &7/ffa kits delete <KitName>"));
        sender.sendMessage(formatColors("&b• &7/ffa kits give <Player> <KitName>"));
        sender.sendMessage(formatColors("&b• &7/ffa kits list"));
        sender.sendMessage("\n");
    }

    public static File createKitsFolder() {
        File folder = new File(Main.getInstance().getDataFolder(), "kits");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    public static void saveKit(CommandSender sender, String kitName) {
        File kitsFolder = createKitsFolder();
        File kitFile = new File(kitsFolder, kitName + ".yml");
        try {
            kitFile.createNewFile();
            FileConfiguration kits = YamlConfiguration.loadConfiguration(kitFile);
            if (sender instanceof Player) {
                Player player = (Player) sender;

                // Save inventory
                kits.set("inventory", player.getInventory().getContents());
                kits.set("armor", player.getInventory().getArmorContents());
                kits.set("offhand", player.getInventory().getItemInOffHand());

                // Save active potion effects
                Collection<PotionEffect> activeEffects = player.getActivePotionEffects();
                for (PotionEffect effect : activeEffects) {
                    kits.set("effects." + effect.getType().getName() + ".duration", effect.getDuration());
                    kits.set("effects." + effect.getType().getName() + ".amplifier", effect.getAmplifier());
                }
            }
            kits.save(kitFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteKit(String kitName) {
        File kitFile = new File(Main.getInstance().getKitsFolder(), kitName + ".yml");
        if (kitFile.exists()) {
            kitFile.delete();
        }
    }

    public static void giveKit(Player player, String kitName) {
        KitGiveEvent kitGiveEvent = new KitGiveEvent(player, kitName);
        Bukkit.getServer().getPluginManager().callEvent(kitGiveEvent);
        if (kitGiveEvent.isCancelled()) {
            return;
        }
        File kitsFolder = createKitsFolder();
        File kitFile = new File(kitsFolder, kitName + ".yml");
        if (kitFile.exists()) {
            try {
                FileConfiguration kitConfig = YamlConfiguration.loadConfiguration(kitFile);
                List<?> inventory = kitConfig.getList("inventory");
                List<?> armor = kitConfig.getList("armor");
                ItemStack offhand = kitConfig.getItemStack("offhand");

                if (inventory != null && armor != null) {

                    player.getInventory().setContents(((List<ItemStack>) inventory).toArray(new ItemStack[0]));
                    player.getInventory().setArmorContents(((List<ItemStack>) armor).toArray(new ItemStack[0]));
                    player.getInventory().setItemInOffHand(offhand);
                    player.getActivePotionEffects().clear();

                    if (kitConfig.contains("effects")) {
                        for (String effectName : kitConfig.getConfigurationSection("effects").getKeys(false)) {
                            PotionEffectType type = PotionEffectType.getByName(effectName);
                            if (type != null) {
                                int duration = kitConfig.getInt("effects." + effectName + ".duration");
                                int amplifier = kitConfig.getInt("effects." + effectName + ".amplifier");
                                player.addPotionEffect(new PotionEffect(type, duration, amplifier));
                            }
                        }
                    }
                    lastKit.put(player, kitName);
                }
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage(formatColors("&cAn error occurred while giving the kit."));
            }
        } else {
            player.sendMessage(formatColors("&cNo kit found."));
        }
    }

    public static void listKits(CommandSender sender) {
        File kitsFolder = Main.getInstance().getKitsFolder();
        File[] kitFiles = kitsFolder.listFiles();
        if (kitFiles != null) {
            sender.sendMessage(formatColors("\n"));
            sender.sendMessage(formatColors("&b&lFFA &8| &7Kits"));
            sender.sendMessage(formatColors("\n"));
            for (File kitFile : kitFiles) {
                if (kitFile.isFile()) {
                    String kitName = kitFile.getName().replace(".yml", "");
                    sender.sendMessage(formatColors("&b• &7" + kitName));
                    sender.sendMessage(formatColors("\n"));
                }
            }
        }
    }

    public static List<String> getKitNames() {
        List<String> kitNames = new ArrayList<>();
        File[] kitFiles = Main.getInstance().getKitsFolder().listFiles();
        if (kitFiles != null) {
            for (File kitFile : kitFiles) {
                if (kitFile.isFile()) {
                    String kitName = kitFile.getName().replace(".yml", "");
                    kitNames.add(kitName);
                }
            }
        }
        return kitNames;
    }

    public static boolean isKitExisting(String kitName) {
        File kitFile = new File(Main.getKitsFolder(), kitName + ".yml");
        return kitFile.exists();
    }
}
