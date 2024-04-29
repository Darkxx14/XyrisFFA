package dev.darkxx.ffa.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that occurs when combat starts between two players.
 */
public class CombatStartEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player attacker;
    private final Player victim;
    private boolean cancelled;

    /**
     * Constructs a new CombatStartEvent with the specified attacker and victim.
     *
     * @param attacker The player who initiated the combat.
     * @param victim   The player who became the victim of the attack.
     */
    public CombatStartEvent(Player attacker, Player victim) {
        this.attacker = attacker;
        this.victim = victim;
    }

    /**
     * Gets the attacker in this combat event.
     *
     * @return The attacker in this combat event.
     */
    public Player getAttacker() {
        return attacker;
    }

    /**
     * Gets the victim in this combat event.
     *
     * @return The victim in this combat event.
     */
    public Player getVictim() {
        return victim;
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
