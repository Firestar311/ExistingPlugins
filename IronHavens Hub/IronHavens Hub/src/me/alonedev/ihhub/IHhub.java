package me.alonedev.ihhub;

import me.alonedev.ihhub.Mechanics.VoidTP;
import me.alonedev.ihhub.Utils.ConfigUpdater;
import me.alonedev.ihhub.commands.AdminCommands;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class IHhub extends JavaPlugin implements Listener {

    //Variables
    private String mainPath;
    public static final String PLUGIN_NAME = "Iron Havens";
    public static final String PREFIX = ChatColor.GOLD+"["+PLUGIN_NAME+"] ";



    @Override
    public void onEnable() {
        // Plugin startup logic
        Util.consoleMsg("--------------------------------------\n  \nIron Havens Hub has successfully loaded!\n  \n--------------------------------------");
        this.getCommand("IronHaven").setExecutor(new AdminCommands(this));
        getServer().getPluginManager().registerEvents(new VoidTP(), this);
        this.getConfig().options().copyDefaults();
        this.saveDefaultConfig();
        this.loadSettings();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Util.consoleMsg("--------------------------------------\n  \nIron Havens Hub has successfully unloaded!\n  \n--------------------------------------");
    }


    public void loadSettings() {
        //This gets the path of the server/plugins/template/ folder
        this.mainPath = this.getDataFolder().getPath()+"/";

        //This gets the active config.yml (not the default)
        final File file = new File(this.mainPath, "config.yml");

        //This updates the active config.yml with new options and comments from the default config.yml
        final ConfigUpdater updater = new ConfigUpdater(this.getTextResource("config.yml"), file);
        final FileConfiguration cfg; //Config object
        cfg = updater.updateConfig(file, PREFIX);

        //Load settings from config.yml here:
        //General format is: cfg.get<Java type>(<key>, <default value>);
        /* this.x = cfg.getInt("coords.x", 5);
         * this.y = cfg.getDouble("coords.y", 64.0);
         * this.z = cfg.getString("coords.z", "-1000");
         */
    }


    public void help(final Player sender) {
        if(sender != null) {
            //If the sender is a player, sends the help message to the player
            if(!sender.hasPermission("IronHavensH.use") && !sender.hasPermission("IronHavensH.*")) return;
            sender.sendMessage(ChatColor.GREEN+PLUGIN_NAME+" commands:");
            sender.sendMessage(ChatColor.AQUA+"/IronHavens"+ChatColor.WHITE+" - shows this help message");
            Util.sendIfPermitted("IronHavensH.reload", ChatColor.AQUA+"/IronHavens reload"+ChatColor.WHITE+" - reloads config.yml", sender);
        } else {
            //If the sender is the console, sends the help message to the console
            Util.consoleMsg(ChatColor.GREEN+PLUGIN_NAME+" commands:");
            Util.consoleMsg(ChatColor.AQUA+"/IronHavens"+ChatColor.WHITE+" - shows this help message");
            Util.consoleMsg(ChatColor.AQUA+"/IronHavens"+" reload"+ChatColor.WHITE+" - reloads config.yml");
        }
    }
}



