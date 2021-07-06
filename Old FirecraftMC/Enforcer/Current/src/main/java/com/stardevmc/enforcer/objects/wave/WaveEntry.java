package com.stardevmc.enforcer.objects.wave;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.punishment.Punishment;
import com.stardevmc.enforcer.objects.wave.Wave.State;
import com.starmediadev.lib.pagination.IElement;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

import static net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND;
import static net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT;

public class WaveEntry implements IElement, ConfigurationSerializable {
    
    protected int index;
    protected String waveId;
    protected Status status = Status.UNDECIDED;
    protected Punishment punishment;
    protected Actor reviewer;
    
    public WaveEntry(int index, String waveId, Punishment punishment) {
        this.index = index;
        this.waveId = waveId;
        this.punishment = punishment;
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("index", this.index);
        serialized.put("waveId", this.waveId);
        serialized.put("status", this.status.name());
        serialized.put("punishment", this.punishment.getId());
        serialized.put("reviewer", this.reviewer);
        return serialized;
    }
    
    public static WaveEntry deserialize(Map<String, Object> serialized) {
        int index = (int) serialized.get("index");
        String waveId = (String) serialized.get("waveId");
        Status status = Status.valueOf((String) serialized.get("status"));
        Punishment punishment = Enforcer.getInstance().getPunishmentModule().getManager().getPunishment((String) serialized.get("punishment"));
        Actor reviewer = (Actor) serialized.get("reviewer");
        WaveEntry waveEntry = new WaveEntry(index, waveId, punishment);
        waveEntry.status = status;
        waveEntry.reviewer = reviewer;
        return waveEntry;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public void setApproved(Actor reviewer) {
        this.reviewer = reviewer;
        this.status = Status.APPROVED;
    }
    
    public void setRejected(Actor reviewer) {
        this.reviewer = reviewer;
        this.status = Status.REJECTED;
    }
    
    public int getIndex() {
        return index;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public Punishment getPunishment() {
        return punishment;
    }
    
    public Actor getReviewer() {
        return reviewer;
    }
    
    @Override
    public TextComponent formatLineAsTextComponent(String... args) {
        //TODO Click and hover events
        Wave wave = Enforcer.getInstance().getWaveModule().getManager().getWave(this.waveId);
        BaseComponent[] approve = new ComponentBuilder("[√]").color(ChatColor.GREEN).bold(true)
                .event(new ClickEvent(RUN_COMMAND, "/wave " + waveId + " approve " + this.index))
                .event(new HoverEvent(SHOW_TEXT, new ComponentBuilder("Approve this punishment").create())).create();
        BaseComponent[] reject = new ComponentBuilder("[×]").color(ChatColor.RED).bold(true)
                .event(new ClickEvent(RUN_COMMAND, "/wave " + waveId + " reject " + this.index))
                .event(new HoverEvent(SHOW_TEXT, new ComponentBuilder("Reject this punishment").create())).create();
        
        BaseComponent[] actor = new ComponentBuilder(this.punishment.getActor().getName()).create();
        BaseComponent[] target = new ComponentBuilder(this.punishment.getTarget().getName()).create();
        BaseComponent[] betweenActorTarget = new ComponentBuilder(" -> ").create();
        
        ComponentBuilder bodyBuilder = new ComponentBuilder("");
        if (wave.getState() == State.UNDER_REVIEW) {
            if (this.status == Status.APPROVED) {
                bodyBuilder.color(ChatColor.GREEN);
            } else if (this.status == Status.REJECTED) {
                bodyBuilder.color(ChatColor.RED);
            } else {
                bodyBuilder.color(ChatColor.YELLOW);
            }
        }
        
        bodyBuilder.event(new ClickEvent(RUN_COMMAND, "/punishment " + punishment.getId() + " info"))
                .event(new HoverEvent(SHOW_TEXT, new ComponentBuilder("Show punishment info").create())).append(actor).append(betweenActorTarget).append(target);
        
        ComponentBuilder builder = new ComponentBuilder("");
        if (wave.getState() == State.UNDER_REVIEW) {
            if (this.status == Status.APPROVED) {
                builder.append(reject);
            } else if (this.status == Status.REJECTED) {
                builder.append(approve);
            } else {
                builder.append(approve).append(reject);
            }
        }
        
        builder.append(" ").append(bodyBuilder.create());
        return new TextComponent(builder.create());
    }
    
    public enum Status {
        UNDECIDED, APPROVED, REJECTED
    }
}