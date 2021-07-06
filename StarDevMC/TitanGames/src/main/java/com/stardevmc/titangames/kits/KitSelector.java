package com.stardevmc.titangames.kits;

import com.firestar311.lib.builder.ItemBuilder;
import com.firestar311.lib.gui.GUIButton;
import com.firestar311.lib.gui.PaginatedGUI;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.entity.Player;

public class KitSelector extends PaginatedGUI {
    
    static {
        PaginatedGUI.prepare(TitanGames.getInstance());
    }
    
    public KitSelector() {
        super(TitanGames.getInstance(), "&5Kit Selector", false);
        
        TitanGames plugin = TitanGames.getInstance();
        for (Kit kit : plugin.getKitManager().getKits()) {
            GUIButton button = new GUIButton(ItemBuilder.start(kit.getDisplayMaterial()).withName("&e" + kit.getName()).buildItem());
            button.setListener(e -> {
                Player player = (Player) e.getWhoClicked();
                Arena arena = plugin.getArenaManager().getArena((player));
                if (arena == null) return;
                arena.setPlayerKit(player, kit);
                player.sendMessage(Utils.color(Messages.KIT_SELECT(kit)));
                player.closeInventory();
            });
            addButton(button);
        }
    }
}
