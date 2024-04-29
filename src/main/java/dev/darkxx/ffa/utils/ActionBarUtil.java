package dev.darkxx.ffa.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import static dev.darkxx.ffa.Main.formatColors;

/**
 * This class provides utility methods for sending action bar messages to players.
 */
public class ActionBarUtil {

    /**
     * Sends an action bar message to a player.
     *
     * @param player  the player to send the action bar message to
     * @param message the message to be displayed in the action bar
     */
    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formatColors(message)));
    }
}
