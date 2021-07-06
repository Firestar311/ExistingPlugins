package com.stardevmc.enforcer.modules.punishments.gui.button;

import com.stardevmc.enforcer.objects.enums.Visibility;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.gui.GUIButton;
import com.starmediadev.lib.util.Utils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

public class VisibilityButton extends GUIButton {
    
    private Visibility visibility;
    
    public VisibilityButton(Visibility visibility) {
        super(ItemBuilder.start(visibility.getMaterial()).withName("&a" + visibility.name()).withLore(Utils.wrapLore(35, visibility.getDescription())).buildItem());
        this.visibility = visibility;
    }
    
    public Visibility getVisibility() {
        return visibility;
    }
    
    public void setSelected(boolean value) {
        if (value) {
            this.setItem(ItemBuilder.start(this.getItem()).clearEnchants().withEnchantment(Enchantment.ARROW_DAMAGE, 1).withItemFlags(ItemFlag.HIDE_ENCHANTS).buildItem());
        } else {
            this.setItem(ItemBuilder.start(this.getItem()).clearEnchants().buildItem());
        }
    }
}