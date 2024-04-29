package dev.darkxx.ffa.api.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event that occurs when a broadcast message is sent.
 */
public class BroadcastEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private CommandSender sender;
    private String message;
    private boolean cancelled = false;

    /**
     * Constructs a new BroadcastEvent with the specified sender and message.
     *
     * @param sender  The command sender who initiated the broadcast.
     * @param message The message to be broadcasted.
     */
    public BroadcastEvent(CommandSender sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    /**
     * Gets the command sender who initiated the broadcast.
     *
     * @return The command sender who initiated the broadcast.
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * Gets the message to be broadcasted.
     *
     * @return The message to be broadcasted.
     */
    public String getMessage() {
        return message;
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
