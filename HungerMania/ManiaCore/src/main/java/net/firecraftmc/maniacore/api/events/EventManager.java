package net.firecraftmc.maniacore.api.events;

import net.firecraftmc.maniacore.api.records.EventInfoRecord;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.manialib.sql.IRecord;

import java.util.*;
import java.util.Map.Entry;

public class EventManager {
    
    private EventInfo activeEvent;
    private CenturionsCore centurionsCore;
    private Map<Integer, EventInfo> events = new HashMap<>();
    
    public EventManager(CenturionsCore centurionsCore) {
        this.centurionsCore = centurionsCore;
    }
    
    public void loadData() {
        List<IRecord> records = centurionsCore.getDatabase().getRecords(EventInfoRecord.class, null, null);
        for (IRecord record : records) {
            if (record instanceof EventInfoRecord) {
                EventInfoRecord eventRecord = (EventInfoRecord) record;
                EventInfo eventInfo = eventRecord.toObject();
                if (eventInfo.isActive()) {
                    this.activeEvent = eventInfo;
                }
                this.events.put(eventInfo.getId(), eventInfo);
            }
        }
    }
    
    public Map<Integer, EventInfo> getEvents() {
        return events;
    }
    
    public EventInfo getActiveEvent() {
        return activeEvent;
    }
    
    public void saveData() {
        for (Entry<Integer, EventInfo> entry : this.events.entrySet()) {
            centurionsCore.getDatabase().addRecordToQueue(new EventInfoRecord(entry.getValue()));
        }
        centurionsCore.getDatabase().pushQueue();
    }
    
    public void setActiveEvent(EventInfo eventInfo) {
        this.activeEvent = eventInfo;
    }
}