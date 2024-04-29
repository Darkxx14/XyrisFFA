package dev.darkxx.ffa.api;

import dev.darkxx.ffa.arenas.ArenaManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class IArenas {

    /**
     * Sends an invalid command message to the sender.
     *
     * @param sender The command sender to whom the message will be sent.
     */
    public static void sendInvalidCommandMessage(CommandSender sender) {
        ArenaManager.sendInvalidCommandMessage(sender);
    }

    /**
     * Creates a new arena with the specified name.
     *
     * @param sender    The command sender creating the arena.
     * @param player    The player executing the command.
     * @param arenaName The name of the arena to create.
     */
    public static void createArena(CommandSender sender, Player player, String arenaName) {
        ArenaManager.createArena(sender, player, arenaName);
    }

    /**
     * Deletes the arena with the specified name.
     *
     * @param sender    The command sender deleting the arena.
     * @param arenaName The name of the arena to delete.
     */
    public static void deleteArena(CommandSender sender, String arenaName) {
        ArenaManager.deleteArena(sender, arenaName);
    }

    /**
     * Retrieves a list of all arena names.
     *
     * @return A list containing the names of all available arenas.
     */
    public static List<String> listArenas() {
        return ArenaManager.listArenas();
    }

    /**
     * Lists all available arenas to the sender.
     *
     * @param sender The command sender to whom the arena list will be sent.
     */
    public static void arenasList(CommandSender sender) {
        ArenaManager.arenasList(sender);
    }

    /**
     * Warps the specified player to the specified arena.
     *
     * @param sender    The command sender initiating the warp.
     * @param playerName The name of the player to warp.
     * @param arenaName The name of the arena to warp to.
     */
    public static void warp(CommandSender sender, String playerName, String arenaName) {
        ArenaManager.warp(sender, playerName, arenaName);
    }
}
