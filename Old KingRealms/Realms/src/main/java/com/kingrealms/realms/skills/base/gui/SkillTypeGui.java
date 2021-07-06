package com.kingrealms.realms.skills.base.gui;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.skills.SkillType;
import com.kingrealms.realms.skills.base.Skill;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.gui.GUIButton;
import com.starmediadev.lib.gui.PaginatedGUI;
import com.starmediadev.lib.util.*;
import org.bukkit.Material;

public class SkillTypeGui extends PaginatedGUI {
    public SkillTypeGui(RealmProfile profile, SkillType type) {
        super(Realms.getInstance(), Utils.capitalizeEveryWord(type.name()), true, 54);
        setButton(4, new GUIButton(ItemBuilder.start(Material.BEACON).withName("&b&lSkill Info").withLore("&6Total " + Utils.capitalizeEveryWord(type.name()) +  " XP: " + Constants.NUMBER_FORMAT.format(profile.getSkillExperience().getOrDefault(SkillType.FARMING, 0D))).buildItem()));
        setButton(0, new GUIButton(ItemBuilder.start(Material.ARROW).withName("&c&lBack").buildItem()).setListener(e -> new SkillMainGui(profile).openGUI(profile.getBukkitPlayer())));
        
        Skill skill = Realms.getInstance().getSkillManager().getSkill(type);
        
        skill.getLevels().forEach((pos, level) -> setButton(9 + pos - 1, new GUIButton(level.getIcon())));
    }
}