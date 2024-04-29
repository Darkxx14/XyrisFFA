package dev.darkxx.ffa.api.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that occurs when a player teleports to the spawn location.
 */
public class TeleportToSpawnEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Location tspawnLocation;

    /**
     * Constructs a new PlayerTeleportToSpawnEvent with the specified player and spawn location.
     *
     * @param player         The player who is teleporting to the spawn location.
     * @param tspawnLocation  The location of the spawn.
     */
    public TeleportToSpawnEvent(Player player, Location tspawnLocation) {
        super(player);
        this.tspawnLocation = tspawnLocation;
    }

    /**
     * Gets the player who is teleporting to the spawn location.
     *
     * @return The player who is teleporting to the spawn location.
     */
    public Player getTeleportedPlayer() {
        return getPlayer();
    }

    /**
     * Gets the spawn location associated with this event.
     *
     * @return The spawn location associated with this event.
     */
    public Location getSpawnLocation() {
        return tspawnLocation;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
