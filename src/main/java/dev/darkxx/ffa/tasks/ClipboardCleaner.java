package dev.darkxx.ffa.tasks;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClipboardCleaner extends BukkitRunnable {
private final Path clipboarddir = Paths.get("plugins/FastAsyncWorldEdit/clipboard");

    public void run() {
        try {
            delete(clipboarddir);
            Bukkit.getLogger().info("Clipboard directory deleted successfully.");
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to delete clipboard directory " + e.getMessage());
        }
    }

    private void delete(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walk(path)
                    .sorted((o1, o2) -> o1.compareTo(o2))
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
