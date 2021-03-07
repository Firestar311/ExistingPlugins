package net.firecraftmc.maniacore.api.skin;

import net.firecraftmc.maniacore.api.records.SkinRecord;
import net.firecraftmc.maniacore.api.ManiaCore;
import net.firecraftmc.manialib.sql.IRecord;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class SkinManager {
    
    private ManiaCore maniaCore;
    
    private final Set<net.firecraftmc.maniacore.api.skin.Skin> skins = new HashSet<>();
    
    public SkinManager(ManiaCore maniaCore) {
        this.maniaCore = maniaCore;
    }
    
    public void loadFromDatabase() {
        List<IRecord> records = maniaCore.getDatabase().getRecords(net.firecraftmc.maniacore.api.records.SkinRecord.class, null, null);
        for (IRecord record : records) {
            net.firecraftmc.maniacore.api.records.SkinRecord skinRecord = (net.firecraftmc.maniacore.api.records.SkinRecord) record;
            this.skins.add(skinRecord.toObject());
        }
    }
    
    public net.firecraftmc.maniacore.api.skin.Skin getSkin(UUID uuid) {
        for (net.firecraftmc.maniacore.api.skin.Skin skin : skins) {
            if (skin.getUuid().equals(uuid)) {
                return skin;
            }
        }
        
        return null;
    }
    
    public void getSkin(UUID uuid, Consumer<net.firecraftmc.maniacore.api.skin.Skin> consumer) {
        net.firecraftmc.maniacore.api.skin.Skin skin = getSkin(uuid);
        if (skin == null) {
            new Thread(() -> {
                net.firecraftmc.maniacore.api.skin.Skin newSkin = new net.firecraftmc.maniacore.api.skin.Skin(uuid);
                consumer.accept(newSkin);
                addSkin(newSkin);
            }).start();
        }
    }
    
    public synchronized void addSkin(net.firecraftmc.maniacore.api.skin.Skin skin) {
        synchronized (this.skins) {
            for (net.firecraftmc.maniacore.api.skin.Skin s : this.skins) {
                if (s.getUuid().equals(skin.getUuid())) {
                    return;
                }
            }
            this.skins.add(skin);
            ManiaCore.getInstance().getPlugin().runTaskAsynchronously(() -> ManiaCore.getInstance().getDatabase().pushRecord(new SkinRecord(skin)));
        }
    }
    
    public Set<Skin> getSkins() {
        return this.skins;
    }
}
