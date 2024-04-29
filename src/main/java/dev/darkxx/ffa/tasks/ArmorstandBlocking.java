package dev.darkxx.ffa.tasks;

import dev.darkxx.ffa.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ArmorstandBlocking extends BukkitRunnable {

    private final Main plugin;

    public ArmorstandBlocking(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                for (Entity entity : player.getNearbyEntities(0.5, 0.5, 0.5)) {
                    if (entity instanceof ArmorStand) {
                        Vector vector = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(-0.9);
                        player.setVelocity(vector);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }
}
