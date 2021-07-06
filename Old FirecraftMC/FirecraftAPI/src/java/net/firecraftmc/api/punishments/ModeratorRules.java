package net.firecraftmc.api.punishments;

import net.firecraftmc.api.punishments.Punishment.Type;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public final class ModeratorRules {
    private static final TreeMap<Integer, Rule> rules = new TreeMap<>();
    
    private static final ModeratorRules instance = new ModeratorRules();
    
    private ModeratorRules() {
        Rule abusingExploits = new Rule(0, "Abusing Exploits", "Using any feature or bug that was not intended in either Minecraft or a plugin for personal gain.");
        abusingExploits.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(7));
        abusingExploits.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(12));
        abusingExploits.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(30));
        abusingExploits.addPunishment(Type.BAN, 0);
        
        Rule compromisedAccount = new Rule(1, "Compromised Account", "Account has been compromised/hacked, not in owner's posession.");
        compromisedAccount.addPunishment(Type.BAN, 0);
        
        Rule advertising = new Rule(2, "Advertising", "Putting an IP/link in chat that is not related to FirecraftMC");
        advertising.addPunishment(Type.TEMP_MUTE, TimeUnit.HOURS.toMillis(12));
        advertising.addPunishment(Type.TEMP_MUTE, TimeUnit.DAYS.toMillis(3));
        advertising.addPunishment(Type.TEMP_MUTE, TimeUnit.DAYS.toMillis(7));
        advertising.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(14));
        advertising.addPunishment(Type.BAN, 0);
        
        Rule punishmentEvading = new Rule(3, "Punishment Evading", "Attempting to get around a currently active punishment.");
        punishmentEvading.addPunishment(Type.BAN, 0);
        
        Rule spamming = new Rule(4, "Spam", "Delibertly sending multiple messages that are the same or similar in a short period of time or multiple identical characters in the same message");
        spamming.addPunishment(Type.WARN, -1);
        spamming.addPunishment(Type.TEMP_MUTE, TimeUnit.HOURS.toMillis(12));
        spamming.addPunishment(Type.TEMP_MUTE, TimeUnit.DAYS.toMillis(1));
        spamming.addPunishment(Type.TEMP_MUTE, TimeUnit.DAYS.toMillis(7));
        spamming.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(3));
        spamming.addPunishment(Type.MUTE, 0);
        
        Rule denialThreats = new Rule(5, "Denial Threats", "Threatening to DDoS / DoS / DoX another user or FirecraftAPI.");
        denialThreats.addPunishment(Type.BAN, 0);
        
        Rule deathThreats = new Rule(6, "Death Threats", "Threatening to physically harm or kill another player or staff member. This will not be tolerated even if it's intended to be a joke or a prank.");
        deathThreats.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(30));
        deathThreats.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(90));
        deathThreats.addPunishment(Type.BAN, 0);
        
        Rule disrespect = new Rule(7, "Disrespect", "Being mean or disrespectful towards a player or staff member.");
        disrespect.addPunishment(Type.WARN, -1);
        disrespect.addPunishment(Type.TEMP_MUTE, TimeUnit.HOURS.toMillis(12));
        disrespect.addPunishment(Type.TEMP_MUTE, TimeUnit.DAYS.toMillis(7));
        disrespect.addPunishment(Type.MUTE, 0);
        
        Rule unfairAdvantage = new Rule(8, "Unfair Advantage", "Using any mod or client that gives an unfair advantage over other players.");
        unfairAdvantage.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(30));
        unfairAdvantage.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(60));
        unfairAdvantage.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(90));
        unfairAdvantage.addPunishment(Type.BAN, 0);
        
        Rule gamethrowing = new Rule(9, "Game Throwing", "Betraying your team or working against them.");
        gamethrowing.addPunishment(Type.KICK, -1);
        gamethrowing.addPunishment(Type.TEMP_BAN, TimeUnit.HOURS.toMillis(12));
        gamethrowing.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(2));
        gamethrowing.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(7));
        gamethrowing.addPunishment(Type.BAN, 0);
        
        Rule inappLanguage = new Rule(10, "Inapp. Language", "Using explicit language in chat.");
        inappLanguage.addPunishment(Type.WARN, -1);
        inappLanguage.addPunishment(Type.TEMP_MUTE, TimeUnit.HOURS.toMillis(1));
        inappLanguage.addPunishment(Type.TEMP_MUTE, TimeUnit.DAYS.toMillis(1));
        inappLanguage.addPunishment(Type.TEMP_MUTE, TimeUnit.DAYS.toMillis(7));
        inappLanguage.addPunishment(Type.TEMP_MUTE, TimeUnit.DAYS.toMillis(30));
        
        Rule harmfulLinks = new Rule(11, "Harmful Links", "Links in chat that users may find inappropriate or offensive");
        harmfulLinks.addPunishment(Type.TEMP_MUTE, TimeUnit.DAYS.toMillis(7));
        harmfulLinks.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(14));
        harmfulLinks.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(30));
        harmfulLinks.addPunishment(Type.BAN, 0);
        
        Rule inappSkin, inappCape, inappName;
        inappSkin = new Rule(12, "Inapp. Skin", "Having an indecent skin that may offend other players.");
        inappSkin.addPunishment(Type.BAN, 0);
        
        inappCape = new Rule(13, "Inapp. Cape", "Having an indecent cape that may offend other players.");
        inappCape.addPunishment(Type.BAN, 0);
        
        inappName = new Rule(14, "Inapp. Name", "Having an indecent name that may offend other players or that may be considered spam.");
        inappName.addPunishment(Type.BAN, 0);
        
        Rule scamming = new Rule(15, "Scamming", "Tricking users into thinking something, then going back on word.");
        scamming.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(7));
        scamming.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(14));
        scamming.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(30));
        scamming.addPunishment(Type.BAN, 0);
        
        Rule soliciting = new Rule(16, "Soliciting", "Using IRL money to buy or sell something from our network.");
        soliciting.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(7));
        soliciting.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(14));
        soliciting.addPunishment(Type.BAN, 0);
        
        Rule impersonation = new Rule(17, "Impersonation", "Pretending to be someone with a special rank (Staff/Media) when you are not.");
        impersonation.addPunishment(Type.WARN, -1);
        impersonation.addPunishment(Type.TEMP_BAN, TimeUnit.HOURS.toMillis(12));
        impersonation.addPunishment(Type.TEMP_BAN, TimeUnit.DAYS.toMillis(7));
        
        Rule antagonising = new Rule(18, "Antagonising", "Provoking players or staff members to get a negative reaction.");
        antagonising.addPunishment(Type.WARN, -1);
        antagonising.addPunishment(Type.TEMP_MUTE, TimeUnit.MINUTES.toMillis(15));
        antagonising.addPunishment(Type.TEMP_MUTE, TimeUnit.MINUTES.toMillis(30));
        antagonising.addPunishment(Type.TEMP_MUTE, TimeUnit.HOURS.toMillis(1));
        antagonising.addPunishment(Type.TEMP_MUTE, TimeUnit.DAYS.toMillis(1));
        antagonising.addPunishment(Type.TEMP_MUTE, TimeUnit.DAYS.toMillis(7));
        antagonising.addPunishment(Type.MUTE, 0);
        
        addRules(abusingExploits, compromisedAccount, advertising, punishmentEvading, spamming, denialThreats, deathThreats, disrespect, unfairAdvantage,
                gamethrowing, inappLanguage, harmfulLinks, inappCape, inappSkin, inappCape, scamming, soliciting, impersonation, antagonising);
    }
    
    public static SortedMap<Integer, Rule> getRules() {
        return rules;
    }
    
    public static Rule getRule(int id) {
        return rules.get(id);
    }
    
    public static Rule getRule(String name) {
        for (Rule rule : rules.values()) {
            if (rule.getName().equalsIgnoreCase(name)) {
                return rule;
            }
        }
        return null;
    }
    
    public static void addRules(Rule... rs) {
        for (Rule r : rs) {
            rules.put(r.getId(), r);
        }
    }
    
    public static ModeratorRules getInstance() {
        return instance;
    }
}