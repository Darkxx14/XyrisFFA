package dev.darkxx.ffa.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that occurs when a kit is given to a player.
 */
public class KitGiveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String kitName;
    private boolean cancelled;

    /**
     * Constructs a new KitGiveEvent with the specified player and kit name.
     *
     * @param player   The player who receives the kit.
     * @param kitName  The name of the kit given to the player.
     */
    public KitGiveEvent(Player player, String kitName) {
        this.player = player;
        this.kitName = kitName;
    }

    /**
     * Gets the player who receives the kit.
     *
     * @return The player who receives the kit.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the name of the kit given to the player.
     *
     * @return The name of the kit given to the player.
     */
    public String getKitName() {
        return kitName;
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
