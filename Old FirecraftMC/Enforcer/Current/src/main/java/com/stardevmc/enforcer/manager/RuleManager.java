package com.stardevmc.enforcer.manager;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.modules.base.Manager;
import com.stardevmc.enforcer.objects.punishment.Punishment;
import com.stardevmc.enforcer.objects.rules.Rule;
import com.stardevmc.enforcer.objects.rules.RuleViolation;
import com.stardevmc.enforcer.util.EnforcerUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.*;

public class RuleManager extends Manager {
    
    private SortedMap<Integer, Rule> rules = new TreeMap<>();
    
    public RuleManager(Enforcer plugin) {
        super(plugin, "rules", false);
        
        File rulesFile = new File(plugin.getDataFolder() + File.separator + "rules.yml");
        if (!rulesFile.exists()) {
            plugin.saveResource("rules.yml", true);
        }
        
        this.configManager.setup();
    }
    
    public void saveData() {
        FileConfiguration config = this.configManager.getConfig();
        config.set("rules", null);
        
        for (Entry<Integer, Rule> entry : this.rules.entrySet()) {
            config.set("rules." + entry.getValue().getInternalId(), entry.getValue());
        }
        
        this.configManager.saveConfig();
    }
    
    public void loadData() {
        FileConfiguration config = configManager.getConfig();
        ConfigurationSection rulesSection = config.getConfigurationSection("rules");
        if (rulesSection == null) {
            return;
        } else {
            for (String r : rulesSection.getKeys(false)) {
                try {
                    Rule rule = (Rule) rulesSection.get(r);
                    this.rules.put(rule.getId(), rule);
                } catch (Exception e) {}
            }
        }
        
        if (this.rules.isEmpty()) {
            this.rules.putAll(EnforcerUtils.getOldRules(config));
        }
    }
    
    public void addRule(Rule rule) {
        if (rule.getId() == -1) {
            int lastId = this.rules.lastKey();
            rule.setId(lastId + 1);
        }
        
        this.rules.put(rule.getId(), rule);
    }
    
    public void removeRule(int id) {
        this.rules.remove(id);
    }
    
    public Rule getRule(int id) {
        return this.rules.get(id);
    }
    
    public Rule getRule(String ruleString) {
        Rule rule = null;
        ruleString = StringUtils.strip(ruleString);
        try {
            int id = Integer.parseInt(ruleString);
            return this.rules.get(id);
        } catch (NumberFormatException e) {
            String ruleInternalName = ruleString.toLowerCase().replace(" ", "_");
            for (Rule r : this.getRules()) {
                if (r.getInternalId().equalsIgnoreCase(ruleInternalName)) {
                    rule = r;
                }
            }
        }
        
        return rule;
    }
    
    public Set<Rule> getRules() {
        return new TreeSet<>(this.rules.values());
    }
    
    public Entry<Integer, Integer> getNextViolation(UUID punisher, UUID target, Rule rule) {
        Set<Punishment> IPunishments = plugin.getPunishmentModule().getManager().getPunishmentsByRule(target, rule, plugin.getTrainingModule().getManager().isTrainingMode(punisher));
        if (IPunishments.isEmpty()) { return new SimpleEntry<>(1, 1); }
        int offense = IPunishments.size() + 1;
        RuleViolation previousOffense = rule.getViolation(IPunishments.size());
        if (previousOffense.getLength() != 0) {
            Punishment latestPunishment = null;
            for (Punishment IPunishment : IPunishments) {
                if (latestPunishment == null) {
                    latestPunishment = IPunishment;
                } else {
                    if (IPunishment.getDate() > latestPunishment.getDate()) {
                        latestPunishment = IPunishment;
                    }
                }
            }
            
            if (latestPunishment != null) {
                long expire = latestPunishment.getDate() + previousOffense.getLength();
                if (System.currentTimeMillis() > expire) {
                    return new SimpleEntry<>(latestPunishment.getViolationNumber(), latestPunishment.getViolationNumber());
                }
            }
        }
        if (offense > rule.getViolations().size()) {
            return new SimpleEntry<>(rule.getViolations().size(), offense);
        }
        return new SimpleEntry<>(IPunishments.size() + 1, IPunishments.size() + 1);
    }
}