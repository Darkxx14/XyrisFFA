package dev.darkxx.ffa.lobby;

import dev.darkxx.ffa.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;
import java.util.Set;

public class VoidListener implements Listener {

    private final Main main;
    private final Set<Player> teleportedPlayers;

    public VoidListener(Main main) {
        this.main = main;
        this.teleportedPlayers = new HashSet<>();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player victim) {
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                if (!teleportedPlayers.contains(victim)) {
                    event.setCancelled(true);
                    SpawnManager.teleportToSpawn(victim);
                    teleportedPlayers.add(victim);
                    Bukkit.getScheduler().runTaskLater(main, () -> teleportedPlayers.remove(victim), 30L);
                }
            }
        }
    }
}
