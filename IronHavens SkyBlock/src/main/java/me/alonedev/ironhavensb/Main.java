package me.alonedev.ironhavensb;

import me.alonedev.ironhavensb.commands.IslandCommands;
import me.alonedev.ironhavensb.listeners.BlockBreakListener;
import me.alonedev.ironhavensb.listeners.InventoryClickListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {

    public static Economy econ;

    public File DiamondMinedYML = new File(getDataFolder()+"/DiamondMined.yml");
    public FileConfiguration DiamondMinedConfig = YamlConfiguration.loadConfiguration(DiamondMinedYML);

    public File islandsYML = new File(getDataFolder()+"/islands.yml");
    public FileConfiguration islandsConfig = YamlConfiguration.loadConfiguration(islandsYML);

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        new InventoryClickListener(this);
        new IslandCommands(this);
        new BlockBreakListener(this);
        saveDefaultConfig();
        for (String world : getUnloadedWorlds()) {
            getServer().createWorld(new WorldCreator(world));
        }
    }

    public List<String> getUnloadedWorlds() {
        final Set<String> loaded = Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toSet());
        final List<String> unloaded = new ArrayList<>();

        for (final File folder : Bukkit.getWorldContainer().listFiles()) {
            if (!this.shallowWorldCheck(folder)) {
                continue;
            }
            final String name = folder.getName();
            if (!loaded.contains(name)) {
                unloaded.add(name);
            }
        }

        return unloaded;
    }

    private boolean shallowWorldCheck(final File folder) {
        return folder.isDirectory() && new File(folder, "level.dat").exists();
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public void saveDiamondMinedYML(FileConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveIslandYML(FileConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}