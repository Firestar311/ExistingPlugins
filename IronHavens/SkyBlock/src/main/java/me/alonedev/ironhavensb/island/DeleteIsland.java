package me.alonedev.ironhavensb.island;

import me.alonedev.ironhavensb.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class DeleteIsland {
    public void unloadIsland(World world) {
        if (world != null) {
            Bukkit.getServer().unloadWorld(world, true);
        }
    }

    public boolean deleteWorld(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            //Technically more code, but using the Java NIO classes, it is both faster and better
            try {
                Files.walkFileTree(path.toPath(), new SimpleFileVisitor<Path>() {
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (Files.isDirectory(file)) {
                            return FileVisitResult.CONTINUE;
                        } else {
                            Files.delete(file);
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {}
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteWorld(file);
                } else {
                    file.delete();
                }
            }
        }
        return (path.delete());
    }

    public DeleteIsland(File path, World world, Main plugin, Player p) {
        p.performCommand("spawn");
        unloadIsland(world);
        Bukkit.getScheduler().runTaskLater(plugin, () -> deleteWorld(path), 60L);
        plugin.islandsConfig.set(p.getName(), null);
        plugin.saveIslandYML(plugin.islandsConfig, plugin.islandsYML);
    }
}