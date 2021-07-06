package com.kingrealms.realms.moderation.inv;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.gui.GUIButton;
import com.starmediadev.lib.gui.PaginatedGUI;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class InvseeGui extends PaginatedGUI {
    
    public InvseeGui(RealmProfile profile, RealmProfile target) {
        super(Realms.getInstance(), target.getName() + "'s Inventory", false, 45, true);
    
        ItemStack[] contents = target.getBukkitPlayer().getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack itemStack = contents[i];
            GUIButton button = new GUIButton(itemStack);
//            button.setAllowRemoval(true);
//            this.allowedToInsert(i);
//            button.setListener(e -> {
//                if (!profile.hasPermission("realms.moderation.invsee.interact")) {
//                    profile.sendMessage("&cYou do not have permission to use that command.");
//                    return;
//                }
//    
//                target.getBukkitPlayer().getInventory().setItem(e.getSlot(), e.getClickedInventory().getItem(e.getSlot()));
//            });
            setButton(i, button);
        }
    }
}