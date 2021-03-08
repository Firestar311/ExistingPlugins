package net.firecraftmc.hungergames.newsettings;

import lombok.Getter;
import net.firecraftmc.hungergames.newsettings.enums.Setting;
import net.firecraftmc.hungergames.records.GameSettingRecord;
import net.firecraftmc.maniacore.api.ManiaCore;
import net.firecraftmc.manialib.collection.ListMap;
import net.firecraftmc.manialib.sql.IRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SettingsManager {

    @Getter private Map<String, SettingGroup> settingsGroup = new HashMap<>();

    public void loadData() {
        List<IRecord> records = ManiaCore.getInstance().getDatabase().getRecords(GameSettingRecord.class, null, null);
        ListMap<String, GameSetting> settings = new ListMap<>();
        if (records.isEmpty()) {
            for (Setting value : Setting.values()) {
                GameSetting setting = new GameSetting("default", value.name(), value.getDefaultValue(), value.getType(), value.getUnit());
                ManiaCore.getInstance().getDatabase().addRecordToQueue(new GameSettingRecord(setting));
                settings.add("default", setting);
            }
            ManiaCore.getInstance().getDatabase().pushQueue();
        } else {
            for (IRecord record : records) {
                GameSetting setting = ((GameSettingRecord) record).toObject();
                settings.add(setting.getGroupName(), setting);
            }
        }

        for (Setting setting : Setting.values()) {
            for (String key : settings.keySet()) {
                boolean containsSetting = false;
                for (GameSetting gameSetting : settings.get(key)) {
                    if (gameSetting.getSettingName().equalsIgnoreCase(setting.name())) {
                        containsSetting = true;
                        break;
                    }
                }

                if (containsSetting)
                    continue;
                GameSetting s = new GameSetting("default", setting.name(), setting.getDefaultValue(), setting.getType(), setting.getUnit());
                ManiaCore.getInstance().getDatabase().addRecordToQueue(new GameSettingRecord(s));
                settings.add(key, s);
            }
        }

        for (Entry<String, List<GameSetting>> entry : settings.entrySet()) {
            Map<Setting, GameSetting> groupSettings = new HashMap<>();
            for (GameSetting gameSetting : entry.getValue()) {
                Setting setting = Setting.valueOf(gameSetting.getSettingName());
                groupSettings.put(setting, gameSetting);
            }
            this.settingsGroup.put(entry.getKey(), new SettingGroup(entry.getKey(), groupSettings));
        }
    }
}
