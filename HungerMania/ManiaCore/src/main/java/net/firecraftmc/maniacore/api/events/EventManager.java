package net.firecraftmc.maniacore.api.events;

import net.firecraftmc.maniacore.api.records.EventInfoRecord;
import net.firecraftmc.maniacore.api.ManiaCore;
import net.firecraftmc.manialib.sql.IRecord;

import java.util.*;
import java.util.Map.Entry;

public class EventManager {
    
    private net.firecraftmc.maniacore.api.events.EventInfo activeEvent;
    private ManiaCore maniaCore;
    private Map<Integer, net.firecraftmc.maniacore.api.events.EventInfo> events = new HashMap<>();
    
    public EventManager(ManiaCore maniaCore) {
        this.maniaCore = maniaCore;
    }
    
    public void loadData() {
        List<IRecord> records = maniaCore.getDatabase().getRecords(net.firecraftmc.maniacore.api.records.EventInfoRecord.class, null, null);
        for (IRecord record : records) {
            if (record instanceof net.firecraftmc.maniacore.api.records.EventInfoRecord) {
                net.firecraftmc.maniacore.api.records.EventInfoRecord eventRecord = (net.firecraftmc.maniacore.api.records.EventInfoRecord) record;
                net.firecraftmc.maniacore.api.events.EventInfo eventInfo = eventRecord.toObject();
                if (eventInfo.isActive()) {
                    this.activeEvent = eventInfo;
                }
                this.events.put(eventInfo.getId(), eventInfo);
            }
        }
    }
    
    public Map<Integer, net.firecraftmc.maniacore.api.events.EventInfo> getEvents() {
        return events;
    }
    
    public net.firecraftmc.maniacore.api.events.EventInfo getActiveEvent() {
        return activeEvent;
    }
    
    public void saveData() {
        for (Entry<Integer, net.firecraftmc.maniacore.api.events.EventInfo> entry : this.events.entrySet()) {
            maniaCore.getDatabase().addRecordToQueue(new EventInfoRecord(entry.getValue()));
        }
        maniaCore.getDatabase().pushQueue();
    }
    
    public void setActiveEvent(EventInfo eventInfo) {
        this.activeEvent = eventInfo;
    }
}