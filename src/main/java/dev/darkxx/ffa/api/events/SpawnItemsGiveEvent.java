package dev.darkxx.ffa.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that occurs when spawn items are given to a player.
 */
public class SpawnItemsGiveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private boolean cancelled = false;

    /**
     * Constructs a new SpawnItemsGiveEvent with the specified player.
     *
     * @param player The player who receives the spawn items.
     */
    public SpawnItemsGiveEvent(Player player) {
        this.player = player;
    }

    /**
     * Gets the player who receives the spawn items.
     *
     * @return The player who receives the spawn items.
     */
    public Player getPlayer() {
        return player;
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

    /**
     * Checks if the event is cancelled.
     *
     * @return true if the event is cancelled, false otherwise.
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets whether the event is cancelled.
     *
     * @param cancelled true to cancel the event, false to allow it.
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
