package com.kingrealms.realms.kits;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.ServerMode;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.pagination.IElement;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SerializableAs("Kit")
public class Kit implements ConfigurationSerializable, IElement {
    
    private int id; //mysql
    private String name; //mysql
    private SortedMap<Integer, KitTier> tiers = new TreeMap<>(); //mysql
    private long cooldown = -1; //mysql
    
    public Kit(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public Kit(String name) {
        this.name = name;
    }
    
    public Kit(Map<String, Object> serialized) {
        this.id = Integer.parseInt((String) serialized.get("id"));
        this.name = (String) serialized.get("name");
        this.cooldown = Long.parseLong((String) serialized.get("cooldown"));
        serialized.forEach((key, value) -> {
            if (key.startsWith("tier-")) {
                int position = Integer.parseInt(key.split("-")[1]);
                KitTier tier = (KitTier) value;
                tiers.put(position, tier);
            }
        });
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("id", id + "");
            put("name", name);
            put("cooldown", cooldown + "");
            tiers.forEach((position, tier) -> put("tier-" + position, tier));
        }};
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Collection<KitTier> getTiers() {
        return tiers.values();
    }
    
    public long getCooldown() {
        return cooldown;
    }
    
    public void addTier(KitTier tier) {
        this.tiers.put(tier.getPosition(), tier);
    }
    
    public void fixTierPositions() {
        if (this.tiers.size() == 1) {
            int key = this.tiers.firstKey();
            KitTier tier = this.tiers.get(key);
            if (key != 0) {
                this.tiers.remove(key);
                tier.setPosition(0);
                this.tiers.put(0, tier);
            }
        } else {
            Map<Integer, KitTier> newTiers = new TreeMap<>();
            AtomicInteger previous = new AtomicInteger(-1);
            this.tiers.forEach((position, tier) -> {
                if (position.equals(previous.get() + 1)) {
                    newTiers.put(position, tier);
                } else {
                    int newPos = previous.get() + 1;
                    newTiers.put(newPos, tier);
                    tier.setPosition(newPos);
                }
                previous.getAndIncrement();
            });
    
    
            this.tiers.clear();
            this.tiers.putAll(newTiers);
        }
    }
    
    public void insertTier(int position, KitTier tier) {
        if (this.tiers.containsKey(position)) {
            SortedMap<Integer, KitTier> tailMap = new TreeMap<>(this.tiers.tailMap(position));
            tier.setPosition(position);
            this.tiers.put(position, tier);
            tailMap.forEach((pos, t) -> {
                tiers.put(pos + 1, t);
                t.setPosition(pos + 1);
            });
        } else {
            this.tiers.put(position, tier);
        }
    }
    
    public KitResponse redeemKit(RealmProfile profile) {
        KitUseInfo kitUseInfo = profile.getKitUseInfo(this);
        KitTier tier;
        if (tiers.size() == 0) {
            return KitResponse.NOT_SETUP;
        }
        if (tiers.size() == 1) {
            tier = this.tiers.get(0);
        } else {
            if (kitUseInfo == null) {
                tier = this.tiers.get(tiers.firstKey());
            } else {
                if (kitUseInfo.getUses() < tiers.size()) {
                    tier = this.tiers.get(kitUseInfo.getUses());
                } else if (kitUseInfo.getUses() == tiers.size()) {
                    tier = this.tiers.get(0);
                } else {
                    int use = kitUseInfo.getUses() % this.tiers.size(); 
                    System.out.println(use);
                    tier = this.tiers.get(use);
                }
            }
        }
    
        if (tier == null) {
            return KitResponse.UNKNOWN_ERROR;
        }
        
        long lastUsed = -1;
        if (kitUseInfo != null) {
            lastUsed = kitUseInfo.getLastUsed();
        }
        
        if (Realms.getInstance().getServerMode() == ServerMode.PRODUCTION) {
            if (lastUsed != -1) {
                if (cooldown == -1) {
                    return KitResponse.NO_MORE_USES;
                }
                long cooldownExpire = lastUsed + this.cooldown;
                if (System.currentTimeMillis() < cooldownExpire) {
                    return KitResponse.COOLDOWN_ACTIVE;
                }
            }
        }
        tier.apply(profile);
        profile.addKitUse(this, 1);
        return KitResponse.SUCCESS;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setCooldown(long value) {
        this.cooldown = value;
    }
    
    public KitTier getTier(int t) {
        return this.tiers.get(t);
    }
    
    public void removeTier(KitTier tier) {
        this.tiers.remove(tier.getPosition());
    }
    
    @Override
    public String formatLine(String... args) {
        return " &8- &e" + id + " &d" + name + " &7- Tiers: " + tiers.size();
    }
}