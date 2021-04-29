package net.firecraftmc.hungergames.settings;

import net.firecraftmc.hungergames.HungerGames;
import net.firecraftmc.hungergames.records.GameSettingsRecord;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.events.EventInfo;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import net.firecraftmc.manialib.sql.IRecord;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsManager implements CommandExecutor {

    private HungerGames plugin;
    private GameSettings defaultSettings;
    private Map<Integer, GameSettings> otherSettings = new HashMap<>();
    
    public SettingsManager(HungerGames plugin) {
        this.plugin = plugin;
    }
    
    public void load() {
        this.otherSettings.clear();
        List<IRecord> records = plugin.getCenturionsCore().getDatabase().getRecords(GameSettingsRecord.class, null, null);
        for (IRecord record : records) {
            if (record instanceof GameSettingsRecord) {
                GameSettings settings = ((GameSettingsRecord) record).toObject();
                if (settings.getName() != null) {
                    if (settings.getName().equalsIgnoreCase("default")) {
                        this.defaultSettings = settings;
                    } else {
                        this.otherSettings.put(settings.getId(), settings);
                    }
                }
            }
        }
        
        if (defaultSettings == null) {
            this.defaultSettings = new GameSettings();
            plugin.getCenturionsCore().getDatabase().pushRecord(new GameSettingsRecord(defaultSettings));
        }
    }
    
    public GameSettings getCurrentSettings() {
        EventInfo eventInfo = CenturionsCore.getInstance().getEventManager().getActiveEvent();
        if (eventInfo != null) {
            if (eventInfo.getServers().contains(CenturionsCore.getInstance().getServerManager().getCurrentServer().getName())) {
                return this.otherSettings.get(eventInfo.getSettingsId());
            }
        }
        return defaultSettings;
    }
    
    public Map<Integer, GameSettings> getOtherSettings() {
        return otherSettings;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (CenturionsUtils.checkCmdAliases(args, 0, "create")) {
            if (!(args.length > 1)) {
                sender.sendMessage(CenturionsUtils.color("&cYou must provide a name"));
                return true;
            }
        
            String name = StringUtils.join(args, " ", 1, args.length);
        
            for (GameSettings value : this.otherSettings.values()) {
                if (value.getName().equalsIgnoreCase(name)) {
                    sender.sendMessage(CenturionsUtils.color("&cThere is already a settings value with that name."));
                    return true;
                }
            }
            
            GameSettings gameSettings = new GameSettings();
            gameSettings.setName(name);
            plugin.getCenturionsCore().getDatabase().pushRecord(new GameSettingsRecord(gameSettings));
            if (gameSettings.getId() == 0) {
                sender.sendMessage(CenturionsUtils.color("&cThere was an error saving the new settings to the database."));
                return true;
            }
            
            this.otherSettings.put(gameSettings.getId(), gameSettings);
            sender.sendMessage(CenturionsUtils.color("&aCreated a new set of settings with the name &b" + gameSettings.getName() + " &aand the id &b" + gameSettings.getId()));
        }
        
        return true;
    }
}