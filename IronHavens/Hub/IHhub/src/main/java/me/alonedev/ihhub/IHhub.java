package me.alonedev.ihhub;

import me.alonedev.ihhub.Commands.SpawnCommand;
import me.alonedev.ihhub.Events.*;
import me.alonedev.ihhub.GUIS.SocialsGUI;
import me.alonedev.ihhub.Mechanics.ServerMOTD;
import me.alonedev.ihhub.Utils.Util;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class IHhub extends JavaPlugin {

    private File dataFile = new File(getDataFolder(), "data.yml");
    private FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);

    @Override
    public void onEnable() {

        saveDefaultConfig();
        loadConfig();
        RegisterCommands();
        RegisterEvents();

        // Plugin startup logic
        Util.consoleMsg("============================= \n \n IronHavens Hub has enabled! \n \n =============================");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }




    public void RegisterCommands() {
        this.getCommand("spawn").setExecutor(new SpawnCommand(this));
        this.getCommand("socials").setExecutor(new SocialsGUI(this));
    }
    public void RegisterEvents() {
        getServer().getPluginManager().registerEvents(new VoidTP(this), this);
        getServer().getPluginManager().registerEvents(new ServerMOTD(this), this);
        getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
        getServer().getPluginManager().registerEvents(new JoinEvent(this), this);
        getServer().getPluginManager().registerEvents(new OnRespawn(this), this);
        getServer().getPluginManager().registerEvents(new QuitEvent(this), this);
        getServer().getPluginManager().registerEvents(new SocialsGUI(this), this);
    }
    public void loadConfig() {
        if(!dataFile.exists()) {
            saveResource("data.yml", false);
        }
    }

    //YAML Getters

    public FileConfiguration getDataConfig() {
        return dataConfig;
    }

    public File getDataFile() {
        return dataFile;
    }

}
