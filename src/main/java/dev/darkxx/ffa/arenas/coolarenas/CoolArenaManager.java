package dev.darkxx.ffa.arenas.coolarenas;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.kits.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static dev.darkxx.ffa.Main.formatColors;
import static dev.darkxx.ffa.Main.prefix;
import static dev.darkxx.ffa.arenas.ArenaManager.lastArena;

public class CoolArenaManager {
    private static final Main main;
    public static final File coolArenasFolder;

    static {
        main = Main.getInstance();
        coolArenasFolder = new File(main.getDataFolder(), "arenas/coolarenas");

        if (!coolArenasFolder.exists()) {
            coolArenasFolder.mkdirs();
        }
    }

    public static void sendInvalidCommandMessage(CommandSender sender) {
        sender.sendMessage("\n");
        sender.sendMessage(formatColors("&b&lFFA &8| &7Invalid Command"));
        sender.sendMessage("\n");
        sender.sendMessage(formatColors("&b• &7/ffa coolarenas create <arenaName>"));
        sender.sendMessage(formatColors("&b• &7/ffa coolarenas delete <arenaName>"));
        sender.sendMessage(formatColors("&b• &7/ffa coolarenas warp <Player> <arenaName>"));
        sender.sendMessage(formatColors("&b• &7/ffa coolarenas list"));
        sender.sendMessage("\n");
    }

    public static void createCa(CommandSender sender, Player player, String arenaName, String kitName) {
        Location location = player.getLocation();
        File arenaFile = new File(coolArenasFolder, arenaName + ".yml");

        if (arenaFile.exists()) {
            player.sendMessage(formatColors(prefix + "&7A cool arena already exists with the name " + arenaName));
            return;
        }

        YamlConfiguration config = new YamlConfiguration();
        config.set("location.world", location.getWorld().getName());
        config.set("location.x", location.getX());
        config.set("location.y", location.getY());
        config.set("location.z", location.getZ());
        config.set("location.pitch", location.getPitch());
        config.set("location.yaw", location.getYaw());
        if (!KitManager.getKitNames().contains(kitName)) {
            player.sendMessage(formatColors(prefix + "&7The specified kit does not exist."));
            return;
        }
        config.set("kit", kitName);
        try {
            config.save(arenaFile);
            sender.sendMessage(formatColors(prefix + "&7Cool Arena " + arenaName + " created successfully!"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteCa(CommandSender sender, String arenaName) {
        File arenaFile = new File(coolArenasFolder, arenaName + ".yml");
        if (arenaFile.exists()) {
            arenaFile.delete();
            sender.sendMessage(formatColors(prefix + "&7Cool Arena " + arenaName + " deleted successfully!"));
        } else {
            sender.sendMessage(formatColors(prefix + "&7Cool Arena not found " + arenaName));
        }
    }

    public static List<String> listCa() {
        List<String> arenaNames = new ArrayList<>();
        File[] files = coolArenasFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                if (fileName.endsWith(".yml") && !fileName.equalsIgnoreCase("spawn.yml")) {
                    arenaNames.add(fileName.replace(".yml", ""));
                }
            }
        }
        return arenaNames;
    }

    public static void caArenasList(CommandSender sender) {
        sender.sendMessage(formatColors("\n"));
        sender.sendMessage(formatColors("&b&lFFA &8| &7CoolArenas"));
        sender.sendMessage(formatColors("\n"));

        List<String> arenaNames = listCa();
        for (String arenaName : arenaNames) {
            sender.sendMessage(formatColors("&b• &7" + arenaName));
            sender.sendMessage(formatColors("\n"));
        }
    }

    public static void warp(CommandSender sender, String playerName, String arenaName) {
        Player targetPlayer = Bukkit.getPlayerExact(playerName);
        File kitsFolder = Main.getKitsFolder();
        if (targetPlayer != null) {
            File arenaFile = new File(coolArenasFolder, arenaName + ".yml");
            if (arenaFile.exists()) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(arenaFile);
                String worldName = config.getString("location.world");
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    double x = config.getDouble("location.x");
                    double y = config.getDouble("location.y");
                    double z = config.getDouble("location.z");
                    float pitch = (float) config.getDouble("location.pitch");
                    float yaw = (float) config.getDouble("location.yaw");
                    Location location = new Location(world, x, y, z, yaw, pitch);
                    targetPlayer.teleport(location);
                    lastArena.put(targetPlayer, arenaName);
                    if (config.contains("kit")) {
                        String kitName = config.getString("kit");
                        KitManager.giveKit(targetPlayer, kitName);
                    }
                    sender.sendMessage(formatColors(prefix + "&7Warped " + targetPlayer.getName() + " to cool arena " + arenaName));
                }
            } else {
                sender.sendMessage(formatColors(prefix + "&7Cool Arena not found " + arenaName));
            }
        } else {
            sender.sendMessage(formatColors(prefix + "&7Player not found " + playerName));
        }
    }
}