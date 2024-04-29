package dev.darkxx.ffa.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that occurs when a player commits suicide.
 */
public class PlayerSuicideEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private boolean cancelled;

    /**
     * Constructs a new PlayerSuicideEvent with the specified player.
     *
     * @param player The player who committed suicide.
     */
    public PlayerSuicideEvent(Player player) {
        this.player = player;
    }

    /**
     * Gets the player who committed suicide.
     *
     * @return The player who committed suicide.
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
     * Gets whether this event is cancelled.
     *
     * @return true if this event is cancelled, false otherwise.
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets whether this event is cancelled.
     *
     * @param cancelled true to cancel this event, false otherwise.
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
