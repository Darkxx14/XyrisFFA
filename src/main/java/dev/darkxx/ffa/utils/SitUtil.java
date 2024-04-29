package dev.darkxx.ffa.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class provides utility methods for handling player sitting functionality.
 */
public class SitUtil {

    private static final Map<UUID, LivingEntity> chair = new HashMap<>();

    /**
     * Checks if a player is currently sitting.
     *
     * @param player the player to check
     * @return true if the player is sitting, false otherwise
     */
    public static boolean isSitting(Player player) {
        return chair.containsKey(player.getUniqueId());
    }

    /**
     * Gets the entity that the player is sitting on.
     *
     * @param player the player whose chair is to be retrieved
     * @return the entity that the player is sitting on, or null if the player is not sitting
     */
    public static LivingEntity getChair(Player player) {
        return chair.get(player.getUniqueId());
    }

    /**
     * Makes a player sit on a specified location.
     *
     * @param player the player to make sit
     * @param pos    the location where the player will sit
     */
    public static void sit(Player player, Location pos) {
        standup(player);

        World world = player.getWorld();

        Bat entity = (Bat) world.spawnEntity(pos, EntityType.BAT);

        entity.setAwake(true);
        entity.setAI(false);
        entity.setInvulnerable(true);
        entity.setCollidable(false);
        entity.setSilent(true);
        entity.setGravity(false);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,
                9999999, 1, false, false, false));
        entity.addPassenger(player);
        entity.setRotation(player.getLocation().getYaw(), player.getLocation().getPitch());

        chair.put(player.getUniqueId(), entity);
    }

    /**
     * Makes a player stand up from their chair.
     *
     * @param player the player to make stand up
     */
    public static void standup(Player player) {
        if (!isSitting(player)) {
            return;
        }

        LivingEntity chairEntity = getChair(player);

        chair.remove(player.getUniqueId());

        chairEntity.setInvulnerable(false);
        chairEntity.setHealth(0);
    }
}

    /*
    public static void closeAllChairs() {
        for (LivingEntity chair : chairs.values()) {
            if (!chair.isDead()) {
                chair.setInvulnerable(false);
                chair.setHealth(0);
            }
        }
        chairs.clear();
    }
     */