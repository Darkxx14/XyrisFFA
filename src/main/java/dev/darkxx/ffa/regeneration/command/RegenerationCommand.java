package dev.darkxx.ffa.regeneration.command;

import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.regeneration.RegenerationImpl;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static dev.darkxx.ffa.Main.formatColors;
import static dev.darkxx.ffa.Main.prefix;

public class RegenerationCommand implements CommandExecutor, TabCompleter {
    private static final List<String> ACTIONS = Arrays.asList("create", "delete", "start", "corner1", "corner2", "regenerate");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player) && !sender.hasPermission("ffa.admin")) {
            String noPermission = Main.getInstance().getConfig().getString("messages.no-permission", "&cNo Permission.");
            sender.sendMessage(formatColors(noPermission));
            return true;
        }

        if (args.length == 0) {
            RegenerationImpl.sendInvalidCommandMessage(sender);
            return true;
        }

        String action = args[0].toLowerCase();
        String arenaName = args.length > 1 ? args[1] : null;

        if (!ACTIONS.contains(action)) {
            RegenerationImpl.sendInvalidCommandMessage(sender);
            return true;
        }

        RegenerationImpl arena = RegenerationImpl.arena(arenaName);

        switch (action) {
            case "create":
                if (arena != null) {
                    sender.sendMessage(Main.formatColors(prefix + "&7An arena with that name already exists."));
                    return true;
                }
                arena = new RegenerationImpl(arenaName, null, null);
                arena.save();
                RegenerationImpl.saveAll();
                RegenerationImpl.loadAll();
                RegenerationImpl.arenas().add(arena);
                sender.sendMessage(Main.formatColors(prefix + "&7Arena " + arenaName + " has been successfully created."));
                break;
            case "delete":
                if (arena == null) {
                    sender.sendMessage(Main.formatColors(prefix + "&7Arena not found."));
                    return true;
                }

                arena.delete();
                sender.sendMessage(Main.formatColors(prefix + "&7Arena " + arenaName + " deleted."));
                break;
            case "corner1":
            case "corner2":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Main.formatColors(prefix + "&7This command can only be executed by players."));
                    return true;
                }

                Player player = (Player) sender;
                Location playerLocation = player.getLocation();

                if (arena == null) {
                    sender.sendMessage(Main.formatColors(prefix + "&7Arena not found."));
                    return true;
                }

                if (action.equals("corner1")) {
                    arena.corner1(playerLocation);
                } else {
                    arena.corner2(playerLocation);
                }

                arena.save();
                sender.sendMessage(Main.formatColors(prefix + "&7Corner " + action.substring(6) + " set successfully."));
                break;
            case "start":
                if (arena == null) {
                    sender.sendMessage(Main.formatColors(prefix + "&7Arena not found."));
                    return true;
                }

                arena.load();
                if (arena.corner1() != null && arena.corner2() != null) {
                    arena.saveSchematic();
                    arena.start();
                    sender.sendMessage(Main.formatColors(prefix + "&7Regeneration started."));
                } else {
                    sender.sendMessage(Main.formatColors(prefix + "&7Regeneration failed to start. You need to have both corner1 and corner2 set."));
                }
                break;
            case "regenerate":
                if (arena == null) {
                    sender.sendMessage(Main.formatColors(prefix + "&7Arena not found."));
                    return true;
                }

                arena.load();
                arena.regenerate();
                sender.sendMessage(Main.formatColors(prefix + "&7Arena " + arenaName + " has been regenerated."));
                break;
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return ACTIONS;
        } else if (args.length == 2) {
            return RegenerationImpl.arenas().stream().map(RegenerationImpl::name).collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
