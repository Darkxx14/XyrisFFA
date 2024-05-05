// Credits to VelocityArenas for some methods.

package dev.darkxx.ffa.regeneration;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.*;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.*;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static dev.darkxx.ffa.Main.formatColors;

public class RegenerationImpl {
    private Runnable regenTask;
    private static final List<RegenerationImpl> arenas = new ArrayList<>();
    private Location corner1;
    private Location corner2;
    private String name;
    private static final String PLUGIN_NAME = "FFA";
    private static final String ARENAS_DIR = "plugins/" + PLUGIN_NAME + "/regeneration/";
    private static final String SCHEMATICS_DIR = "plugins/" + PLUGIN_NAME + "/regeneration/schematics/";

    public RegenerationImpl(String name, Location corner1, Location corner2) {
        this.name = name;
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public static List<String> names() {
        return arenas.stream().map(RegenerationImpl::name).toList();
    }

    public static RegenerationImpl arena(String name) {
        return arenas.stream().filter(arena -> arena.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Location corner1() {
        return corner1;
    }

    public void corner1(Location corner1) {
        this.corner1 = corner1;
    }

    public Location corner2() {
        return corner2;
    }

    public void corner2(Location corner2) {
        this.corner2 = corner2;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public static List<RegenerationImpl> arenas() {
        return new ArrayList<>(arenas);
    }

    public void start() {
        if (regenTask != null) {
            stop();
        }

        // Uncomment if regenTask implementation is provided
        // long regenDelay = 50L;
        // this.regenTask = new RegenerationTask(this);
        // this.regenTask.runTaskTimer(Utils.getPlugin(), 0L, regenDelay);
    }

    public void regenerate() {
        FaweAPI.getTaskManager().async(this::paste);
    }

    public void stop() {
        if (regenTask != null) {
            try {
                regenTask.cancel();
                regenTask = null;
            } catch (IllegalStateException ignored) {
            }
        }
    }

    public void delete() {
        stop();
        deleteFiles();
        arenas.remove(this);
    }

    private void deleteFiles() {
        File configFile = new File(ARENAS_DIR, name + ".yml");
        File schemFile = new File(SCHEMATICS_DIR, name + ".schem");

        if (configFile.exists()) {
            configFile.delete();
        }

        if (schemFile.exists()) {
            schemFile.delete();
        }
    }

    public void save() {
        try {
            Files.createDirectories(Path.of(ARENAS_DIR));
            File configFile = new File(ARENAS_DIR, name + ".yml");
            YamlConfiguration config = new YamlConfiguration();
            config.set("name", name);
            config.set("corner1", corner1);
            config.set("corner2", corner2);
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        File configFile = new File(ARENAS_DIR, name + ".yml");
        if (configFile.exists()) {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                name = config.getString("name");
                corner1 = config.getLocation("corner1");
                corner2 = config.getLocation("corner2");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadAll() {
        try {
            File[] arenaFiles = new File(ARENAS_DIR).listFiles((dir, name) -> name.endsWith(".yml"));
            if (arenaFiles != null) {
                for (File arenaFile : arenaFiles) {
                    String arenaName = arenaFile.getName().replace(".yml", "");
                    RegenerationImpl arena = new RegenerationImpl(arenaName, null, null);
                    arena.load();
                    arenas.add(arena);
                    arena.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveAll() {
        try {
            Files.createDirectories(Path.of(ARENAS_DIR));
            for (RegenerationImpl arena : arenas) {
                File configFile = new File(ARENAS_DIR, arena.name() + ".yml");
                YamlConfiguration config = new YamlConfiguration();
                config.set("name", arena.name());
                config.set("corner1", arena.corner1());
                config.set("corner2", arena.corner2());
                config.save(configFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // no error
    public void saveSchematic() {
        try {
            File schem = schematic();
            Location corner1 = this.corner1();
            World world = BukkitAdapter.adapt(corner1.getWorld());
            CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(corner1).toBlockPoint(),
                    BukkitAdapter.adapt(this.corner2()).toBlockPoint());
            BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
            clipboard.setOrigin(BukkitAdapter.adapt(corner1).toBlockPoint());
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(world, region, clipboard, region.getMinimumPoint());
            forwardExtentCopy.setCopyingBiomes(true);
            forwardExtentCopy.setCopyingEntities(false);
            Operations.complete(forwardExtentCopy);
            try (ClipboardWriter writer = BuiltInClipboardFormat.FAST.getWriter(Files.newOutputStream(schem.toPath()))) {
                writer.write(clipboard);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File schematic() {
        try {
            Files.createDirectories(Path.of(SCHEMATICS_DIR));
            return new File(SCHEMATICS_DIR, name + ".schem");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void paste() {
        if (corner1() != null && corner2() != null) {
            File file = schematic();
            Location corner1 = corner1();
            BlockVector3 to = BukkitAdapter.adapt(corner1).toBlockPoint();
            World world = FaweAPI.getWorld(corner1.getWorld().getName());
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            if (format != null) {
                try (ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()))) {
                    Clipboard clipboard = reader.read();
                    try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                        Operation operation = (new ClipboardHolder(clipboard)).createPaste(editSession)
                                .ignoreAirBlocks(false).to(to).build();
                        Operations.complete(operation);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Something attempted to paste an arena without a corner set.");
        }
    }

    public static void sendInvalidCommandMessage(@NotNull CommandSender sender) {
        sender.sendMessage(formatColors("\n"));
        sender.sendMessage(formatColors("&b&lFFA &8| &7Invalid Command"));
        sender.sendMessage(formatColors("\n"));
        sender.sendMessage(formatColors("&b• &7/regeneration create <name>"));
        sender.sendMessage(formatColors("&b• &7/regeneration corner1 <name>"));
        sender.sendMessage(formatColors("&b• &7/regeneration corner2 <name>"));
        sender.sendMessage(formatColors("&b• &7/regeneration start <name>"));
        sender.sendMessage(formatColors("&b• &7/regeneration regenerate <name>"));
        sender.sendMessage(formatColors("&b• &7/regeneration delete <name>"));
        sender.sendMessage(formatColors("\n"));
    }
}