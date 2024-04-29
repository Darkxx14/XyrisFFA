package dev.darkxx.ffa.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class NicknameSetEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final UUID playerId;
    private final String nickname;
    private boolean cancelled = false;

    public NicknameSetEvent(UUID playerId, String nickname) {
        this.playerId = playerId;
        this.nickname = nickname;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
