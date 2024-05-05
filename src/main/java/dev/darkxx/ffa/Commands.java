package dev.darkxx.ffa;

import dev.darkxx.ffa.arenas.ArenaManager;
import dev.darkxx.ffa.arenas.Arenas;
import dev.darkxx.ffa.arenas.coolarenas.CoolArenaManager;
import dev.darkxx.ffa.kits.Kits;
import dev.darkxx.ffa.spawnitems.Items;
import dev.darkxx.ffa.stats.edit.EditStats;
import dev.darkxx.ffa.kits.KitManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static dev.darkxx.ffa.Main.formatColors;
import static dev.darkxx.ffa.Main.prefix;

public class Commands implements CommandExecutor, TabCompleter {

    private final Main main;

    public Commands(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!label.equalsIgnoreCase("ffa")) {
            return false;
        }

        if (!sender.hasPermission("ffa.admin")) {
            String noPermission = Main.getInstance().getConfig().getString("messages.no-permission", "&cNo Permission.");
            sender.sendMessage(formatColors(noPermission));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(formatColors("&cInvalid Command Usage. /ffa <subcommand>"));
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            // Reload Command
            case "reload":
                Main.getInstance().reloadConfig();
                Items.loadSpawnItemsConfig();
                sender.sendMessage(formatColors(" "));
                sender.sendMessage(formatColors(prefix + "&7" + Main.getInstance().getDescription().getVersion()));
                sender.sendMessage(formatColors(" "));
                sender.sendMessage(formatColors("&7Successfully reloaded the configuration files."));
                sender.sendMessage(formatColors("&7Some things might not reload, please restart the server"));
                sender.sendMessage(formatColors("&7to apply the changes made in any of the plugin's files."));
                sender.sendMessage(formatColors(" "));
                break;
            // SpawnItems Command
            case "spawnitems":
                if (args.length < 2) {
                    Items.sendInvalidCommandMessage(sender);
                    return true;
                }
                String subCommandArgs = args[1].toLowerCase();
                switch (subCommandArgs) {
                    case "reload":
                        Items.loadSpawnItemsConfig();
                        sender.sendMessage(formatColors(prefix + "&7Spawnitems configuration reloaded."));
                        break;
                    case "give":
                        if (args.length < 3) {
                            Items.sendInvalidCommandMessage(sender);
                            return true;
                        }
                        Player targetPlayer = main.getServer().getPlayer(args[2]);
                        if (targetPlayer == null || !targetPlayer.isOnline()) {
                            sender.sendMessage(formatColors(prefix + "&7" + args[2] + " is not online."));
                            return true;
                        }
                        Items.giveSpawnItems(targetPlayer);
                        // sender.sendMessage(formatColors(prefix + ChatColor.GRAY + "Successfully given spawn items to " + targetPlayer.getName() + "."));
                        break;
                    default:
                        Items.sendInvalidCommandMessage(sender);
                        return true;
                }
                break;
            // Edit Stats Command
            case "editstats":
                if (args.length < 4) {
                    EditStats.sendInvalidCommandMessage(sender);
                    return true;
                }
                String playerName = args[1];
                Player player = Bukkit.getPlayer(playerName);
                if (player == null || !player.isOnline()) {
                    sender.sendMessage(formatColors(prefix + "&7Player " + playerName + " is not online."));
                    return true;
                }
                String statsType = args[2].toLowerCase();
                int value;
                try {
                    value = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(formatColors(prefix + "&7Invalid value. Please enter a valid number."));
                    return true;
                }
                switch (statsType) {
                    case "kills":
                        EditStats.editKills(player, value);
                        sender.sendMessage(formatColors(prefix + "&7Successfully edited kills for player " + playerName + "."));
                        break;
                    case "deaths":
                        EditStats.editDeaths(player, value);
                        sender.sendMessage(formatColors(prefix + "&7Successfully edited deaths for player " + playerName + "."));
                        break;
                    case "streak":
                        EditStats.editStreak(player, value);
                        sender.sendMessage(formatColors(prefix + "&7Successfully edited streak for player " + playerName + "."));
                        break;
                    case "max-streak":
                        EditStats.editHighestStreak(player, value);
                        sender.sendMessage(formatColors(prefix + "&7Successfully edited max streak for player " + playerName + "."));
                        break;
                    default:
                        EditStats.sendInvalidCommandMessage(sender);
                        return true;
                }
                break;
            // Kits Command
            case "kits":
                if (args.length < 2) {
                    KitManager.sendInvalidCommandMessage(sender);
                    return true;
                }
                String kitSubCommand = args[1].toLowerCase();
                switch (kitSubCommand) {
                    case "create":
                        if (args.length < 3) {
                            KitManager.sendInvalidCommandMessage(sender);
                            return true;
                        }
                        String kitName = args[2].toLowerCase();
                        KitManager.saveKit(sender, kitName);
                        sender.sendMessage(formatColors(prefix + "&7Kit " + kitName + " has been successfully created!"));
                        break;
                    case "delete":
                        if (args.length < 3) {
                            KitManager.sendInvalidCommandMessage(sender);
                            return true;
                        }
                        String kitToDelete = args[2].toLowerCase();
                        KitManager.deleteKit(kitToDelete);
                        sender.sendMessage(formatColors(prefix + "&7Kit " + kitToDelete + " has been successfully deleted!"));
                        break;
                    case "list":
                        KitManager.listKits(sender);
                        break;
                    case "give":
                        if (args.length < 4) {
                            KitManager.sendInvalidCommandMessage(sender);
                            return true;
                        }
                        String p = args[2];
                        Player targetPlayer = Bukkit.getPlayer(p);
                        if (targetPlayer == null || !targetPlayer.isOnline()) {
                            sender.sendMessage(formatColors(prefix + "&7Player " + p + " is not online."));
                            return true;
                        }
                        String kitToGive = args[3].toLowerCase();
                        KitManager.giveKit(targetPlayer, kitToGive);
                        sender.sendMessage(formatColors(prefix + "&7Kit " + kitToGive + " has been given to " + p + "."));
                        break;
                }
                break;
            // Arenas Command
            case "arenas":
                if (args.length < 2) {
                    ArenaManager.sendInvalidCommandMessage(sender);
                    return true;
                }
                String arenaSubCommand = args[1].toLowerCase();
                switch (arenaSubCommand) {
                    case "create":
                        if (args.length < 3 || !(sender instanceof Player)) {
                            ArenaManager.sendInvalidCommandMessage(sender);
                            return true;
                        }
                        Player thePlayerArena = (Player) sender;
                        String arenaNameArena = args[2];
                        ArenaManager.createArena(sender, thePlayerArena, arenaNameArena);
                        break;
                    case "delete":
                        if (args.length < 3) {
                            ArenaManager.sendInvalidCommandMessage(sender);
                            return true;
                        }
                        String arenaToDelete = args[2];
                        ArenaManager.deleteArena(sender, arenaToDelete);
                        break;
                    case "list":
                        List<String> arenas = ArenaManager.listArenas();
                        if (arenas.isEmpty()) {
                            sender.sendMessage(formatColors("&7There are no arenas."));
                        } else {
                            ArenaManager.arenasList(sender);
                        }
                        break;
                    case "warp":
                        if (args.length < 4) {
                            ArenaManager.sendInvalidCommandMessage(sender);
                            return true;
                        }
                        String tpTarget = args[2];
                        String targetArena = args[3];
                        ArenaManager.warp(sender, tpTarget, targetArena);
                        break;
                    default:
                        ArenaManager.sendInvalidCommandMessage(sender);
                        return true;
                }
                break;
            // Cool Arenas Command
            case "coolarenas":
                if (args.length < 2) {
                    CoolArenaManager.sendInvalidCommandMessage(sender);
                    return true;
                }
                String coolArenaSubCommand = args[1].toLowerCase();
                switch (coolArenaSubCommand) {
                    case "create":
                        if (args.length < 3 || !(sender instanceof Player)) {
                            CoolArenaManager.sendInvalidCommandMessage(sender);
                            return true;
                        }
                        Player thePlayerCoolArena = (Player) sender;
                        String arenaNameCoolArena = args[2];
                        String kitName = args[3];
                        CoolArenaManager.createCa(sender, thePlayerCoolArena, arenaNameCoolArena, kitName);
                        break;
                    case "delete":
                        if (args.length < 3) {
                            CoolArenaManager.sendInvalidCommandMessage(sender);
                            return true;
                        }
                        String arenaToDelete = args[2];
                        CoolArenaManager.deleteCa(sender, arenaToDelete);
                        break;
                    case "list":
                        List<String> coolArenas = CoolArenaManager.listCa();
                        if (coolArenas.isEmpty()) {
                            sender.sendMessage(formatColors("&7There are no cool arenas."));
                        } else {
                            CoolArenaManager.caArenasList(sender);
                        }
                        break;
                    case "warp":
                        if (args.length < 4) {
                            CoolArenaManager.sendInvalidCommandMessage(sender);
                            return true;
                        }
                        String tpTarget = args[2];
                        String targetArena = args[3];
                        CoolArenaManager.warp(sender, tpTarget, targetArena);
                        break;
                    default:
                        CoolArenaManager.sendInvalidCommandMessage(sender);
                        return true;
                }
                break;
            default:
                sender.sendMessage(formatColors("&cInvalid Command Usage."));
                return true;
        }
        return false;
    }

    private List<String> getOnlinePlayersNames() {
        List<String> playerNames = new ArrayList<>();
        for (org.bukkit.entity.Player player : Bukkit.getOnlinePlayers()) {
            playerNames.add(player.getName());
        }
        return playerNames;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("spawnitems");
            completions.add("editstats");
            completions.add("kits");
            completions.add("arenas");
            completions.add("coolarenas");
            completions.add("reload");
            return completions;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("spawnitems")) {
            List<String> completions = new ArrayList<>();
            completions.add("reload");
            completions.add("give");
            return completions;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("spawnitems") && args[1].equalsIgnoreCase("give")) {
            List<String> onlinePlayers = new ArrayList<>();
            for (Player player : main.getServer().getOnlinePlayers()) {
                onlinePlayers.add(player.getName());
            }
            return onlinePlayers;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("editstats")) {
            List<String> completions = new ArrayList<>();
            completions.add("kills");
            completions.add("deaths");
            completions.add("streak");
            completions.add("max-streak");
            return completions;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("kits")) {
            List<String> completions = new ArrayList<>();
            completions.add("create");
            completions.add("delete");
            completions.add("list");
            completions.add("give");
            return completions;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("kits") && args[1].equalsIgnoreCase("give")) {
            List<String> completions = new ArrayList<>();
            completions.addAll(getOnlinePlayersNames());
            return completions;
        } else if (args.length == 4 && args[0].equalsIgnoreCase("kits") && args[1].equalsIgnoreCase("give")) {
            List<String> completions = new ArrayList<>();
            completions.addAll(Kits.getKitNames());
            return completions;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("kits") && args[1].equalsIgnoreCase("delete")) {
            List<String> completions = new ArrayList<>();
            completions.addAll(Kits.getKitNames());
            return completions;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("arenas")) {
            List<String> completions = new ArrayList<>();
            completions.add("create");
            completions.add("delete");
            completions.add("list");
            completions.add("warp");
            return completions;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("arenas") && args[1].equalsIgnoreCase("warp")) {
            List<String> completions = new ArrayList<>();
            completions.addAll(getOnlinePlayersNames());
            return completions;
        } else if (args.length == 4 && args[0].equalsIgnoreCase("arenas") && args[1].equalsIgnoreCase("warp")) {
            List<String> completions = new ArrayList<>();
            completions.addAll(Arenas.getArenasList());
            return completions;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("arenas") && args[1].equalsIgnoreCase("delete")) {
            List<String> completions = new ArrayList<>();
            completions.addAll(Arenas.getArenasList());
            return completions;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("coolarenas")) {
            List<String> completions = new ArrayList<>();
            completions.add("create");
            completions.add("delete");
            completions.add("list");
            completions.add("warp");
            return completions;
        } else if (args.length == 4 && args[0].equalsIgnoreCase("coolarenas") && args[1].equalsIgnoreCase("create")) {
            List<String> completions = new ArrayList<>();
            completions.addAll(KitManager.getKitNames());
            return completions;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("coolarenas") && args[1].equalsIgnoreCase("warp")) {
            List<String> completions = new ArrayList<>();
            completions.addAll(getOnlinePlayersNames());
            return completions;
        } else if (args.length == 4 && args[0].equalsIgnoreCase("coolarenas") && args[1].equalsIgnoreCase("warp")) {
            List<String> completions = new ArrayList<>();
            completions.addAll(CoolArenaManager.listCa());
            return completions;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("coolarenas") && args[1].equalsIgnoreCase("delete")) {
            List<String> completions = new ArrayList<>();
            completions.addAll(CoolArenaManager.listCa());
            return completions;
        }
        return null;
    }
}