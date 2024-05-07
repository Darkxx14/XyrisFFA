package dev.darkxx.ffa.utils;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import dev.darkxx.ffa.Main;
import dev.darkxx.ffa.api.events.QuickRespawnEvent;
import dev.darkxx.ffa.commands.PingCommand;
import dev.darkxx.ffa.settings.SettingsManager;
import dev.darkxx.ffa.tasks.UpdateTask;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

import static dev.darkxx.ffa.Main.formatColors;
import static dev.darkxx.ffa.Main.prefix;
import static dev.darkxx.ffa.arenas.Arenas.getLastArena;
import static dev.darkxx.ffa.kits.Kits.getLastKit;
import static org.bukkit.Bukkit.getLogger;

public class MiscListener implements Listener {

    private final Main main;
    Scoreboard board;
    Objective objective;
    private boolean healthBarEnabled;
    private Set<String> disabledWorlds;
    private String healthBarDisplayNameFormat;

    public MiscListener(Main main) {
        this.main = main;
        loadConfigSettings();
        if (healthBarEnabled) {
            this.createHealthBar();
        }
    }

    private void loadConfigSettings() {
        ConfigurationSection config = main.getConfig().getConfigurationSection("healthbar");
        if (config != null) {
            healthBarEnabled = config.getBoolean("enabled", false);

            List<String> disabledWorldNames = config.getStringList("disabled-worlds");
            if (disabledWorldNames != null) {
                disabledWorlds = new HashSet<>(disabledWorldNames);
            }

            healthBarDisplayNameFormat = config.getString("display-name-format", "&c❤");
        }
    }

    @EventHandler
    public void onPressurePlate(PlayerInteractEvent e) {
        if (e.getAction() != org.bukkit.event.block.Action.PHYSICAL) return;
        if (!e.getClickedBlock().getType().name().contains("PLATE")) return;
        double horizontalForce = main.getConfig().getDouble("launchpad.force");
        Bukkit.getScheduler().runTask(main, () -> {
            e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().multiply(horizontalForce).setY(0.5));
        });
    }

    @EventHandler
    public void QuickRespawn(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName() ||
                !meta.getDisplayName().toLowerCase().contains("respawn")) {
            return;
        }

        String clickAction = Main.getInstance().getConfig().getString("quick-respawn.click-action", "BOTH");
        if (!clickAction.equalsIgnoreCase("BOTH") &&
                ((clickAction.equalsIgnoreCase("LEFT") && !e.getAction().name().contains("LEFT")) ||
                        (clickAction.equalsIgnoreCase("RIGHT") && !e.getAction().name().contains("RIGHT")))) {
            return;
        }

        String materialName = Main.getInstance().getConfig().getString("quick-respawn.material");
        if (materialName == null) {
            return;
        }

        Material material = Material.matchMaterial(materialName);
        if (material == null || item.getType() != material) {
            return;
        }

        Player player = e.getPlayer();
        String playerName = player.getName();
        String kit = getLastKit(player);
        String arena = getLastArena(player);
        QuickRespawnEvent onQuickR = new QuickRespawnEvent(player, item, arena, kit);
        Bukkit.getServer().getPluginManager().callEvent(onQuickR);

        if (onQuickR.isCancelled()) {
            return;
        }

        if (!kit.equals("none") && !arena.equals("none")) {
            String kitCmd = "ffa kits give " + playerName + " " + kit;
            String arenaCmd = "ffa arenas warp " + playerName + " " + arena;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), kitCmd);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), arenaCmd);

            String messageType = Main.getInstance().getConfig().getString("quick-respawn.message-type", "action bar");
            String message = Main.getInstance().getConfig().getString("quick-respawn.message", "&aTeleported to your last location.");

            if (messageType.equalsIgnoreCase("chat")) {
                player.sendMessage(formatColors(message));
            } else if (messageType.equalsIgnoreCase("action bar")) {
                ActionBarUtil.sendActionBar(player, message);
            } else {
                getLogger().severe("Unknown message type specified in configuration " + messageType);
            }
        }
    }

    public static ItemStack createQuickRespawnItem() {
        String materialName = Main.getInstance().getConfig().getString("quick-respawn.material");
        Material material = Material.matchMaterial(materialName);

        if (material == null) {
            Main.getInstance().getLogger().severe("Invalid material specified for quick-respawn item " + materialName);
            return null;
        }

        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            String itemName = Main.getInstance().getConfig().getString("quick-respawn.item-name", "§aQuick Respawn");
            List<String> lore = Main.getInstance().getConfig().getStringList("quick-respawn.lore");

            meta.setDisplayName(formatColors(itemName));

            if (lore != null && !lore.isEmpty()) {
                List<String> coloredLore = new ArrayList<>();
                for (String line : lore) {
                    coloredLore.add(formatColors(line));
                }
                meta.setLore(coloredLore);
            }

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(meta);
        }
        return itemStack;
    }

    public static void giveQuickRespawn(Player player, ItemStack itemStack, int slot) {
        Inventory playerInventory = player.getInventory();
        playerInventory.setItem(slot, itemStack);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getEntity();
            if (e.getHitBlock() != null && e.getHitBlock().getType() != Material.AIR) {
                arrow.remove();
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (UpdateTask.isOutdated) {
            if (player.isOp()) {
                player.sendMessage(formatColors(" "));
                player.sendMessage(formatColors(prefix));
                player.sendMessage(formatColors("&7Hey, seems like FFA plugin is outdated,"));
                player.sendMessage(formatColors("&7please upgrade the plugin to the latest version v" + UpdateTask.latestVersion + "."));
                player.sendMessage(formatColors(" "));
            }
        }
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            SettingsManager.ensurePlayerSettings(player);
            if (healthBarEnabled && !isWorldDisabled(player.getWorld())) {
                updateHealthBar(player);
            }
        });
        String joinMsg = Main.getInstance().getConfig().getString("join-message");
        e.setJoinMessage(formatColors(PlaceholderAPI.setPlaceholders(player, joinMsg)));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        String quitMsg = Main.getInstance().getConfig().getString("quit-message");
        e.setQuitMessage(formatColors(PlaceholderAPI.setPlaceholders(player, quitMsg)));
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            Player player = e.getPlayer();
            if (healthBarEnabled && !isWorldDisabled(player.getWorld())) {
                updateHealthBar(player);
            } else {
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
        });
    }

    private void createHealthBar() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        board = manager.getNewScoreboard();

        String displayName = formatColors(healthBarDisplayNameFormat);
        this.objective = this.board.registerNewObjective("healthBar", "health");
        this.objective.setDisplayName(displayName);
        this.objective.setDisplaySlot(DisplaySlot.BELOW_NAME);

        Bukkit.getOnlinePlayers().forEach(this::updateHealthBar);
    }

    private void updateHealthBar(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            if (isWorldDisabled(player.getWorld())) {
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            } else {
                player.setScoreboard(board);
            }
        });
    }

    private boolean isWorldDisabled(World world) {
        return disabledWorlds != null && disabledWorlds.contains(world.getName());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String message = e.getMessage();

        String format = Main.getInstance().getConfig().getString("chat-format");
        String formatMsg = PlaceholderAPI.setPlaceholders(player, format)
                .replace("%message%", message)
                .replace("%player%", player.getName());
        e.setFormat(formatColors(formatMsg));

        for (Player recipient : Bukkit.getOnlinePlayers()) {
            String playerName = recipient.getName();
            if (message.contains(playerName)) {
                if (SettingsManager.hasEnabledSetting(recipient, "mentionSound")) {
                    recipient.playSound(recipient.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1.0f, 1.0f);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Player) {
            Player clickedPlayer = (Player) e.getRightClicked();
            Player player = e.getPlayer();
            int ping = PingCommand.getPing(clickedPlayer);
            String mesg = main.getConfig().getString("show-ping-on-right-click.message")
                    .replace("%ping%", String.valueOf(ping))
                    .replace("%clicked_player%", clickedPlayer.getName());
            if (main.getConfig().getBoolean("show-ping-on-right-click.enabled")) {
                ActionBarUtil.sendActionBar(player, mesg);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Player attacker = player.getKiller();
        if (attacker != null) {
            if (SettingsManager.hasEnabledSetting(attacker, "autoGG")) {
                Bukkit.getScheduler().runTaskLater(main, () -> {
                    attacker.chat("gg");
                }, 4L);
            }
        }
        if (main.getConfig().getBoolean("disableDeathDrops")) {
            e.getDrops().clear();
        }
    }

    public static void heal(Player player) {
        player.getInventory().clear();
        player.getActivePotionEffects().forEach(pe -> player.removePotionEffect(pe.getType()));
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setSaturation(0);
        player.setFireTicks(0);
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            Player player = e.getPlayer();
            if (e.isSneaking()) {
                if (SitUtil.isSitting(player)) {
                    SitUtil.standup(player);
                }
            }
        });
    }
}