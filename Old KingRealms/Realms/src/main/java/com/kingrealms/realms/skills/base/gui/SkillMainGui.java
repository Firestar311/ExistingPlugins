package com.kingrealms.realms.skills.base.gui;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.skills.base.Skill;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.gui.GUIButton;
import com.starmediadev.lib.gui.PaginatedGUI;
import com.starmediadev.lib.util.Constants;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SkillMainGui extends PaginatedGUI {
    public SkillMainGui(RealmProfile profile) {
        super(Realms.getInstance(), "&dSkills", false, 18);
        ItemStack infoIcon = ItemBuilder.start(Material.BEACON).withLore("", "&eTotal XP: " + Constants.NUMBER_FORMAT.format(profile.getTotalExperience())).buildItem();
        GUIButton infoButton = new GUIButton(infoIcon);
        setButton(4, infoButton);
        
        int pos = 9;
        for (Skill skill : Realms.getInstance().getSkillManager().getSkills()) {
            ItemStack icon = ItemBuilder.start(skill.getIconMaterial()).withName("&b" + Utils.capitalizeEveryWord(skill.getType().name())).buildItem();
            GUIButton button = new GUIButton(icon);
            button.setListener(e -> e.getWhoClicked().openInventory(new SkillTypeGui(profile, skill.getType()).getInventory()));
            setButton(pos++, button);
        }
    }
}