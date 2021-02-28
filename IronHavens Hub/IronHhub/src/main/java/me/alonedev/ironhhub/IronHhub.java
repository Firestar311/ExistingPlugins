package me.alonedev.ironhhub;

import me.alonedev.ironhhub.Commands.Commands;
import me.alonedev.ironhhub.GUI.SocialsGUI;
import me.alonedev.ironhhub.Mechanics.*;
import me.alonedev.ironhhub.Utils.ConfigUpdater;
import me.alonedev.ironhhub.Utils.Util;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class IronHhub extends JavaPlugin implements Listener {

    public static int PlayersJoined;

    //Config Variables
    private String mainPath;
    public static final String PLUGIN_NAME = "Iron Havens";
    public static final String PREFIX = ChatColor.GOLD+"["+PLUGIN_NAME+"] ";
    public static int x;
    public static int y;
    public static int z;
    public static String BASE_PERMISSION;
    public static String MOTD;
    public static String spawnworld;
    public static int yaw;
    public static int pitch;
    public static String sound;

    public static String discordLink;


    private File dataFile = new File(getDataFolder(), "data.yml");
    private FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);

    @Override
    public void onEnable() {
        // Plugin startup logic
        Util.consoleMsg("--------------------------------------\n  \nIron Havens Hub has successfully loaded!\n  \n--------------------------------------");

        //commands
        this.getCommand("ih").setExecutor(new Commands(this));
        this.getCommand("ih").setTabCompleter(new CommandsTab());
        this.getCommand("socials").setExecutor(new SocialsGUI());
        this.getCommand("spawn").setExecutor(new Spawn(this));


        //Listeners
        getServer().getPluginManager().registerEvents(new VoidTP(), this);
        getServer().getPluginManager().registerEvents(new SocialsGUI(), this);
        getServer().getPluginManager().registerEvents(new ServerMOTD(this), this);
        getServer().getPluginManager().registerEvents(new Spawn(this), this);


        //Config
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();
        this.loadSettings();

        //Data

        if(!dataFile.exists()) {
            saveResource("data.yml", false);
        }

        getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Util.consoleMsg("--------------------------------------\n  \nIron Havens Hub has successfully unloaded!\n  \n--------------------------------------");
    }

    //
    //Config.yml
    //

    public void loadSettings() {
        this.mainPath = this.getDataFolder().getPath()+"/";

        //This gets the active config.yml (not the default)
        final File configfile = new File(this.mainPath, "config.yml");

        //This updates the active config.yml with new options and comments from the default config.yml
        final ConfigUpdater updater = new ConfigUpdater(this.getTextResource("config.yml"), configfile);
        final FileConfiguration cfg; //Config object
        cfg = updater.updateConfig(configfile, PREFIX);

        //Load settings from config.yml here:
        //General format is: cfg.get<Java type>(<key>, <default value>);
        this.x = cfg.getInt("x", 5);
        this.y = cfg.getInt("y", 64);
        this.z = cfg.getInt("z", -1000);
        this.BASE_PERMISSION = cfg.getString("base_permission", "ironhavens");
        this.MOTD = cfg.getString("MOTD", "&6[IronHavens]");
        this.spawnworld = cfg.getString("spawnworld", "spawn");
        this.yaw = cfg.getInt("spawnworld", 90);
        this.pitch = cfg.getInt("spawnworld", 0);
        this.discordLink = cfg.getString("Discord_Social_Link", "https://discord.gg/G5q8ds9eHH");
        this.sound = cfg.getString("Spawn_Sound", "block.anvil.fall");



    }

    //
    //Data.yml
    //


    //
    //Help command
    //

    public void help(final Player sender) {
        if(sender != null) {
            //If the sender is a player, sends the help message to the player
            if(!sender.hasPermission("IronHavensH.use") && !sender.hasPermission("IronHavensH.*")) return;
            sender.sendMessage(ChatColor.GREEN+PLUGIN_NAME+" commands:");
            sender.sendMessage(ChatColor.AQUA+"/IH"+ChatColor.WHITE+" - shows this help message");
            Util.sendIfPermitted("IronHavensH.reload", ChatColor.AQUA+"/IronHavens reload"+ChatColor.WHITE+" - reloads config.yml", sender);
        } else {
            //If the sender is the console, sends the help message to the console
            Util.consoleMsg(ChatColor.GREEN+PLUGIN_NAME+" commands:");
            Util.consoleMsg(ChatColor.AQUA+"/IH"+ChatColor.WHITE+" - shows this help message");
            Util.consoleMsg(ChatColor.AQUA+"/IH"+" reload"+ChatColor.WHITE+" - reloads config.yml");
        }
    }

    //
    // Data
    //


    public void saveDataYml(FileConfiguration ymlConfig, File ymlFile) {
        try {
            ymlConfig.save(ymlFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //
    // Getters
    //

    public FileConfiguration getDataConfig() {
        return dataConfig;
    }

    public File getDataFile() {
        return dataFile;
    }

}



