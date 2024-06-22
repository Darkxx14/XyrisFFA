package dev.darkxx.ffa;

import dev.darkxx.ffa.bstats.Metrics;
import dev.darkxx.ffa.combat.CombatTagger;
import dev.darkxx.ffa.commands.SettingsCommand;
import dev.darkxx.ffa.commands.MessageCommand;
import dev.darkxx.ffa.commands.PingCommand;
import dev.darkxx.ffa.commands.RulesCommand;
import dev.darkxx.ffa.commands.SuicideCommand;
import dev.darkxx.ffa.commands.SitCommand;
import dev.darkxx.ffa.commands.HealCommand;
import dev.darkxx.ffa.commands.NickCommand;
import dev.darkxx.ffa.commands.BroadcastCommand;
import dev.darkxx.ffa.commands.StatsCommand;
import dev.darkxx.ffa.commands.ReplyCommand;
import dev.darkxx.ffa.commands.CoolArenaCommand;
import dev.darkxx.ffa.deathmessages.DeathMessagesManager;
import dev.darkxx.ffa.commands.FlyCommand;
import dev.darkxx.ffa.kits.KitManager;
import dev.darkxx.ffa.lobby.VoidListener;
import dev.darkxx.ffa.lobby.SpawnCommands;
import dev.darkxx.ffa.lobby.SpawnManager;
import dev.darkxx.ffa.regeneration.RegenerationImpl;
import dev.darkxx.ffa.regeneration.command.RegenerationCommand;
import dev.darkxx.ffa.settings.OldDamageTilt;
import dev.darkxx.ffa.spawnitems.Items;
import dev.darkxx.ffa.stats.Stats;
import dev.darkxx.ffa.expansion.Placeholders;
import dev.darkxx.ffa.stats.StatsManager;
import dev.darkxx.ffa.tasks.ClipboardCleaner;
import dev.darkxx.ffa.tasks.UpdateTask;
import dev.darkxx.ffa.utils.MiscListener;
import dev.darkxx.ffa.utils.gui.GuiManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Main extends JavaPlugin {

    private static Main instance;
    private DatabaseManager dbm;
    public static String prefix;
    private FileConfiguration config;
    private Stats stats;
    private SpawnManager spawnManager;
    private DeathMessagesManager deathMessagesManager;
    private MessageCommand messageCommand;
    private static File kitsFolder;
    private CombatTagger combatTagger;

    @Override
    public void onEnable() {
        instance = this;
        PlaceholderAPI();
        GuiManager.register(this);
        saveDefaultConfig();
        config = getConfig();
        prefix = config.getString("prefix", "&b&lFFA &7|&r");
        DatabaseManager.connect();
        kitsFolder = KitManager.createKitsFolder();
        Register();
        Commands();
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(formatColors(prefix + "&7Saving data..."));

        try {
            StatsManager.save();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(formatColors(prefix + "&cFailed to save stats " + e.getMessage()));
            e.printStackTrace();
        }

        try {
            DatabaseManager.disconnect();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(formatColors(prefix + "&cFailed to disconnect from database " + e.getMessage()));
            e.printStackTrace();
        }

        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);

        try {
            RegenerationImpl.saveAll();
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(formatColors(prefix + "&cFailed to save regeneration data " + e.getMessage()));
            e.printStackTrace();
        }
    }

    private void PlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Placeholders().register();
        } else {
            Bukkit.getConsoleSender().sendMessage(formatColors(" "));
            Bukkit.getConsoleSender().sendMessage(formatColors("&b&lFFA"));
            Bukkit.getConsoleSender().sendMessage(formatColors(" "));
            Bukkit.getConsoleSender().sendMessage(formatColors("&#FF0000PlaceholderAPI not found."));
            Bukkit.getConsoleSender().sendMessage(formatColors("&#FF0000This plugin has a dependency on PlaceholderAPI."));
            Bukkit.getConsoleSender().sendMessage(formatColors("&#FF0000Please install PlaceholderAPI to use this plugin."));
            Bukkit.getConsoleSender().sendMessage(formatColors(" "));
            Bukkit.getConsoleSender().sendMessage(formatColors("&7Made with &c❤️ &7 by the Xyris Team!"));
            Bukkit.getConsoleSender().sendMessage(formatColors(" "));

            Bukkit.getScheduler().runTaskLater(this, () -> {
                Bukkit.getPluginManager().disablePlugin(this);
            }, 20L);
        }
    }

    private void Register() {
        // SpawnItems
        new Items(this);
        // Database
        dbm = new DatabaseManager();
        // FFA Commands
        getCommand("ffa").setExecutor(new Commands(this));
        getCommand("ffa").setTabCompleter(new Commands(this));
        // Stats
        stats = new Stats(config);
        getServer().getPluginManager().registerEvents(stats, this);
        // MiscListener - Utils
        MiscListener misc = new MiscListener(this);
        getServer().getPluginManager().registerEvents(misc, this);
        // Spawn
        spawnManager = new SpawnManager();
        getServer().getPluginManager().registerEvents(spawnManager, this);
        getCommand("setspawn").setExecutor(new SpawnCommands(spawnManager, this));
        getCommand("spawn").setExecutor(new SpawnCommands(spawnManager, this));
        getServer().getPluginManager().registerEvents(new VoidListener(this), this);
        // Death Messages
        deathMessagesManager = new DeathMessagesManager(this);
        // Combat Tagger
        int combatTimer = getConfig().getInt("combat-tagger.combat-timer");
        combatTagger = new CombatTagger(this, combatTimer);
        getServer().getPluginManager().registerEvents(combatTagger, this);
        // BStats
        // The plugin is named "xFFA" because the name "FFA" is already taken on BStats. "x" stands for Xyris, which is our organization's name.
        Metrics metrics = new Metrics(this, 21736);
        metrics.addCustomChart(new Metrics.SimplePie("xffa-chart", () -> "xFFA"));
        // ProtocolLib
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            new OldDamageTilt(this);
        } else {
            Bukkit.getConsoleSender().sendMessage(formatColors(prefix + "&7ProtocolLib not found. Some features may be disabled. [OldDamageTilt setting], [none]"));
        }
        // Regeneration
        if (Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
            getCommand("regeneration").setExecutor(new RegenerationCommand());
            getCommand("regeneration").setTabCompleter(new RegenerationCommand());
            RegenerationImpl.loadAll();
        } else {
            Bukkit.getConsoleSender().sendMessage(formatColors(prefix + "&7FastAsyncWorldEdit not found, Regeneration will not work."));
        }
        // Updater
        UpdateTask.run();
        // Clipboard Cleaner
        long delay = 0L;
        long period = 10800L * 20L;
        new ClipboardCleaner().runTaskTimerAsynchronously(this, delay, period);
    }

    private void Commands() {
        getCommand("coolarena").setExecutor(new CoolArenaCommand());
        getCommand("coolarena").setTabCompleter(new CoolArenaCommand());
        getCommand("suicide").setExecutor(new SuicideCommand());
        getCommand("broadcast").setExecutor(new BroadcastCommand(getConfig()));
        getCommand("rules").setExecutor(new RulesCommand(getConfig()));
        getCommand("sit").setExecutor(new SitCommand());
        getCommand("stats").setExecutor(new StatsCommand());
        getCommand("heal").setExecutor(new HealCommand());
        messageCommand = new MessageCommand(config);
        getCommand("message").setExecutor(messageCommand);
        getCommand("reply").setExecutor(new ReplyCommand(messageCommand));
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("ping").setExecutor(new PingCommand());
        getCommand("nickname").setExecutor(new NickCommand(this));
        getCommand("settings").setExecutor(new SettingsCommand());
    }

    public static Main getInstance() {
        return instance;
    }

    public static File getKitsFolder() {
        return kitsFolder;
    }

    /**
     * This method formats color codes in a given message string.
     * Color codes are represented using '&' followed by hexadecimal color codes or RGB values.
     *
     * @param message the input message containing color codes
     * @return the formatted message with color codes replaced by their respective colors
     */
    public static String formatColors(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String colorCode = matcher.group();
            ChatColor color = ChatColor.of(colorCode.substring(1));
            message = message.replace(colorCode, color.toString());
        }
        return message;
    }
}