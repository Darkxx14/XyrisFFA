package dev.darkxx.ffa.api.events;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that occurs when a player is warped to a specific location.
 */
public class PlayerWarpEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Player targetPlayer;
    private final Location destination;
    private final CommandSender sender;

    /**
     * Constructs a new PlayerWarpEvent with the specified parameters.
     *
     * @param sender       The command sender who initiated the warp.
     * @param targetPlayer The player being warped.
     * @param destination  The destination location.
     */
    public PlayerWarpEvent(CommandSender sender, Player targetPlayer, Location destination) {
        this.sender = sender;
        this.targetPlayer = targetPlayer;
        this.destination = destination;
    }

    /**
     * Gets the command sender who initiated the warp.
     *
     * @return The command sender who initiated the warp.
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Gets the player being warped.
     *
     * @return The player being warped.
     */
    public Player getTargetPlayer() {
        return targetPlayer;
    }

    /**
     * Gets the destination location.
     *
     * @return The destination location.
     */
    public Location getDestination() {
        return destination;
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
