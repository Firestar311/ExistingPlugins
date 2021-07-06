package com.kingrealms.realms.profile.gui;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.gui.GUIButton;
import com.starmediadev.lib.gui.PaginatedGUI;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MailboxGui extends PaginatedGUI {
    public MailboxGui(RealmProfile profile) {
        super(Realms.getInstance(), profile.getName() + "'s Mailbox", true, 54);
        if (profile.getMailbox().getItems().isEmpty()) {
            profile.sendMessage("&cYou do not have any items in your mailbox");
            return;
        }
    
        List<ItemStack> itemStacks = profile.getMailbox().getItems();
        for (int i = 0; i < itemStacks.size(); i++) {
            ItemStack itemStack = itemStacks.get(i);
            GUIButton button = new GUIButton(itemStack);
            int finalI = i;
            button.setListener(e -> {
                profile.getInventory().addItem(itemStack);
                profile.getMailbox().removeItem(itemStack);
                removeButton(finalI);
                e.getClickedInventory().setItem(e.getSlot(), null);
            });
            setButton(i, button);
        }
    }
}