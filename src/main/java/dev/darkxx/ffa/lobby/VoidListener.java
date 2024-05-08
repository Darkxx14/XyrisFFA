package dev.darkxx.ffa.lobby;

import dev.darkxx.ffa.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class VoidListener implements Listener {

    private final Main main;

    public VoidListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player victim) {
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                event.setCancelled(true);
                SpawnManager.teleportToSpawn(victim);
            }
        }
    }
}