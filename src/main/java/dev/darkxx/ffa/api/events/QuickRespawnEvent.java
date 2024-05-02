package dev.darkxx.ffa.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that occurs when a player quick respawns.
 */
public class QuickRespawnEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final ItemStack item;
    private String lastArena;
    private String lastKit;
    private boolean cancelled;

    /**
     * Constructs a new QuickRespawnEvent.
     *
     * @param player    The player who quick respawns.
     * @param item      The item used for quick respawn.
     * @param lastArena The last arena the player was in before quick respawning.
     * @param lastKit   The last kit the player had before quick respawning.
     */
    public QuickRespawnEvent(@NotNull Player player, @NotNull ItemStack item, String lastArena, String lastKit) {
        this.player = player;
        this.item = item;
        this.lastArena = lastArena;
        this.lastKit = lastKit;
        this.cancelled = false;
    }

    /**
     * Gets the player who quick respawns.
     *
     * @return The player who quick respawns.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the item used for quick respawn.
     *
     * @return The item used for quick respawn.
     */
    public ItemStack getItem() {
        return item;
}

    /**
     * Gets the last arena the player was in before quick respawning.
     *
     * @return The last arena the player was in before quick respawning.
     */
    public String getLastArena() {
        return lastArena;
    }

    /**
     * Gets the last kit the player had before quick respawning.
     *
     * @return The last kit the player had before quick respawning.
     */
    public String getLastKit() {
        return lastKit;
    }

    /**
     * Checks if the event is cancelled.
     *
     * @return True if the event is cancelled, otherwise false.
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of the event.
     *
     * @param cancelled True to cancel the event, otherwise false.
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Gets the handler list for this event.
     *
     * @return The handler list for this event.
     */
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets the handler list for this event.
     *
     * @return The handler list for this event.
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
