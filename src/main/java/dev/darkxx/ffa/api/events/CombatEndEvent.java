package dev.darkxx.ffa.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Represents an event that occurs when combat ends for a set of players.
 */
public class CombatEndEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Set<Player> players;
    private boolean cancelled;

    /**
     * Constructs a new CombatEndEvent with the specified set of players.
     *
     * @param players The set of players for which combat has ended.
     */
    public CombatEndEvent(Set<Player> players) {
        this.players = players;
    }

    /**
     * Gets the set of players for which combat has ended.
     *
     * @return The set of players for which combat has ended.
     */
    public Set<Player> getPlayers() {
        return players;
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
