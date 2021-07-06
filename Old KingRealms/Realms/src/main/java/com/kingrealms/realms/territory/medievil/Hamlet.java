package com.kingrealms.realms.territory.medievil;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.channel.channels.territory.HamletChannel;
import com.kingrealms.realms.plot.Plot;
import com.kingrealms.realms.plot.claimed.HamletPlot;
import com.kingrealms.realms.territory.base.Settlement;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;
import java.util.UUID;

@SerializableAs("Hamlet")
public class Hamlet extends Settlement {
    public Hamlet(String name) {
        super(name);
    }
    
    public Hamlet(Map<String, Object> serialized) {
        super(serialized);
    }
    
    @Override
    public void sendMemberMessage(String message) {
        super.sendMemberMessage("&8&l[&s&lH&8&l] &r" + message);
    }
    
    @Override
    public void createChannel() {
        channel = new HamletChannel(this, System.currentTimeMillis());
        Realms.getInstance().getChannelManager().registerChannel(channel);
        this.channelId = channel.getId();
    }
    
    @Override
    public void addPlot(Plot plot, UUID actor, long time) {
        this.plots.add(new HamletPlot(this, plot, actor, time));
    }
}