package net.firecraftmc.maniacore.spigot.perks.gui;

import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.spigot.gui.GUIButton;
import net.firecraftmc.maniacore.spigot.gui.Gui;
import net.firecraftmc.maniacore.spigot.perks.Perk;
import net.firecraftmc.maniacore.spigot.user.SpigotUser;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;

public class PerkMainGui extends Gui {
    public PerkMainGui(SpigotUser user) {
        super(Bukkit.getPluginManager().getPlugin("ManiaCorePlugin"), "Perks", false, 27);
    
        for (Perk perk : net.firecraftmc.maniacore.spigot.perks.Perks.PERKS) {
            GUIButton button = new GUIButton(perk.getIcon(user));
            button.setListener(e -> {
                if (e.getClick() == ClickType.LEFT) {
                    perk.handlePurchase(user);
                } else if (e.getClick() == ClickType.RIGHT) {
                    net.firecraftmc.maniacore.spigot.perks.PerkInfo perkInfo = ((SpigotUser) CenturionsCore.getInstance().getUserManager().getUser(e.getWhoClicked().getUniqueId())).getPerkInfo(perk);
                    perkInfo.setActive(true);
                    new net.firecraftmc.maniacore.spigot.perks.PerkInfoRecord(perkInfo).push(CenturionsCore.getInstance().getDatabase());
                }
                refreshInventory(e.getWhoClicked());
            });
            addButton(button);
        }
    }
}
