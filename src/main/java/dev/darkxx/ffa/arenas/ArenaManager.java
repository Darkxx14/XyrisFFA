package dev.darkxx.ffa.arenas;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.api.events.PlayerWarpEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.darkxx.ffa.Main.formatColors;
import static dev.darkxx.ffa.Main.prefix;

public class ArenaManager {
    public static Map<Player, String> lastArena = new HashMap<>();
    private static final Main main;
    public static final File arenasFolder;

    static {
        main = Main.getInstance();
        arenasFolder = new File(main.getDataFolder(), "arenas");

        if (!arenasFolder.exists()) {
            arenasFolder.mkdirs();
        }
    }

    public static void sendInvalidCommandMessage(CommandSender sender) {
        sender.sendMessage("\n");
        sender.sendMessage(formatColors("&b&lFFA &8| &7Invalid Command"));
        sender.sendMessage("\n");
        sender.sendMessage(formatColors("&b• &7/ffa arenas create <arenaName>"));
        sender.sendMessage(formatColors("&b• &7/ffa arenas delete <arenaName>"));
        sender.sendMessage(formatColors("&b• &7/ffa arenas warp <Player> <arenaName>"));
        sender.sendMessage(formatColors("&b• &7/ffa arenas list"));
        sender.sendMessage("\n");
    }

    public static void createArena(CommandSender sender, Player player, String arenaName) {
        Location location = player.getLocation();
        File arenaFile = new File(arenasFolder, arenaName + ".yml");

        if (arenaFile.exists()) {
            player.sendMessage(formatColors(prefix + "&7An arena already exists with the name " + arenaName));
            return;
        }

        YamlConfiguration config = new YamlConfiguration();
        config.set("location.world", location.getWorld().getName());
        config.set("location.x", location.getX());
        config.set("location.y", location.getY());
        config.set("location.z", location.getZ());
        config.set("location.pitch", location.getPitch());
        config.set("location.yaw", location.getYaw());

        try {
            config.save(arenaFile);
            sender.sendMessage(formatColors(prefix + "&7Arena " + arenaName + " created successfully!"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteArena(CommandSender sender, String arenaName) {
        File arenaFile = new File(arenasFolder, arenaName + ".yml");
        if (arenaFile.exists()) {
            arenaFile.delete();
            sender.sendMessage(formatColors(prefix + "&7Arena " + arenaName + " deleted successfully!"));
        } else {
            sender.sendMessage(formatColors(prefix + "&7Arena not found " + arenaName));
        }
    }

    public static List<String> listArenas() {
        List<String> arenaNames = new ArrayList<>();
        File[] files = arenasFolder.listFiles();
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

    public static void arenasList(CommandSender sender) {
        sender.sendMessage(formatColors("\n"));
        sender.sendMessage(formatColors("&b&lFFA &8| &7Arenas"));
        sender.sendMessage(formatColors("\n"));

        List<String> arenaNames = listArenas();
        for (String arenaName : arenaNames) {
            sender.sendMessage(formatColors("&b• &7" + arenaName));
            sender.sendMessage(formatColors("\n"));
        }
    }

    public static void warp(CommandSender sender, String playerName, String arenaName) {
        Player targetPlayer = Bukkit.getPlayerExact(playerName);
        if (targetPlayer != null) {
            File arenaFile = new File(arenasFolder, arenaName + ".yml");
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
                    PlayerWarpEvent Warpevent = new PlayerWarpEvent(sender, targetPlayer, location);
                    Bukkit.getServer().getPluginManager().callEvent(Warpevent);
                    if (!Warpevent.isCancelled()) {
                        targetPlayer.teleport(location);
                        lastArena.put(targetPlayer, arenaName);
                        sender.sendMessage(formatColors(prefix + "&7Warped " + targetPlayer.getName() + " to arena " + arenaName));
                    }
                } else {
                    sender.sendMessage(formatColors(prefix + "&7Arena not found " + arenaName));
                }
            } else {
                sender.sendMessage(formatColors(prefix + "&7Player not found " + playerName));
            }
        }
    }
}