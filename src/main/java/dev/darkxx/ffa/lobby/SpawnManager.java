package dev.darkxx.ffa.lobby;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.api.events.TeleportToSpawnEvent;
import dev.darkxx.ffa.settings.SettingsManager;
import dev.darkxx.ffa.spawnitems.Items;
import dev.darkxx.ffa.utils.MiscListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;

import static dev.darkxx.ffa.Main.formatColors;
import static dev.darkxx.ffa.Main.prefix;
import static dev.darkxx.ffa.utils.MiscListener.createQuickRespawnItem;

public class SpawnManager implements Listener {

    private static final Main main = Main.getInstance();
    private static final FileConfiguration spawnConfig = YamlConfiguration.loadConfiguration(new File(main.getDataFolder(), "arenas/spawn.yml"));

    public SpawnManager() {
        main.getServer().getPluginManager().registerEvents(this, main);
        File spawnFile = new File(main.getDataFolder(), "arenas/spawn.yml");
        if (!spawnFile.exists()) {
            main.saveResource("arenas/spawn.yml", false);
        }
    }

    public void setSpawn(Player player) {
        Location location = player.getLocation();
        spawnConfig.set("spawn.world", location.getWorld().getName());
        spawnConfig.set("spawn.x", location.getX());
        spawnConfig.set("spawn.y", location.getY());
        spawnConfig.set("spawn.z", location.getZ());
        spawnConfig.set("spawn.yaw", location.getYaw());
        spawnConfig.set("spawn.pitch", location.getPitch());
        saveSpawnConfig();
        player.sendMessage(formatColors(prefix + "&7Spawn point has been successfully set."));
    }

    public static void teleportToSpawn(Player p) {
        String worldName = spawnConfig.getString("spawn.world");
        if (worldName != null) {
            World world = p.getServer().getWorld(worldName);
            double x = spawnConfig.getDouble("spawn.x");
            double y = spawnConfig.getDouble("spawn.y");
            double z = spawnConfig.getDouble("spawn.z");
            float yaw = (float) spawnConfig.getDouble("spawn.yaw");
            float pitch = (float) spawnConfig.getDouble("spawn.pitch");
            Location tspawnLocation = new Location(world, x, y, z, yaw, pitch);
            TeleportToSpawnEvent spawnTpEvent = new TeleportToSpawnEvent(p, tspawnLocation);
            Bukkit.getServer().getPluginManager().callEvent(spawnTpEvent);
            if (spawnTpEvent.isCancelled()) {
                return;
            }
            p.teleport(tspawnLocation);
            MiscListener.heal(p);
            Items.giveSpawnItems(p);
            if (main.getConfig().getBoolean("smoothSpawnTeleport")) {
                Bukkit.getScheduler().runTaskLater(main, () -> {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 8, 255, false, false));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 8, 255, false, false));
                }, 2);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MiscListener.heal(player);
        teleportToSpawn(player);
        Items.giveSpawnItems(player);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ItemStack quickRespawnItem = createQuickRespawnItem();
        MiscListener.heal(player);
        teleportToSpawn(player);
        Items.giveSpawnItems(player);
        boolean quickRespawnEnabled = main.getConfig().getBoolean("quick-respawn.enabled");
        int quickRespawnSlot = main.getConfig().getInt("quick-respawn.slot");
        if (SettingsManager.hasEnabledSetting(player, "toggleQuickRespawn")) {
            if (quickRespawnEnabled) {
                if (quickRespawnItem != null) {
                    Bukkit.getScheduler().runTaskLater(main, () -> {
                        MiscListener.giveQuickRespawn(player, quickRespawnItem, quickRespawnSlot);
                    }, 2);
                } else {
                    player.sendMessage(formatColors("&cFailed to create quick respawn item. Please check your configuration."));
                }
            }
        }
    }

    private void saveSpawnConfig() {
        try {
            spawnConfig.save(new File(main.getDataFolder(), "arenas/spawn.yml"));
        } catch (IOException e) {
            main.getLogger().warning("Could not save spawn config to file.");
        }
    }
}
