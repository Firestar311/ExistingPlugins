package com.kingrealms.realms.spawners.gui;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.spawners.CustomSpawner;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.gui.GUIButton;
import com.starmediadev.lib.gui.PaginatedGUI;
import com.starmediadev.lib.util.EntityNames;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Material;

public class SpawnerGui extends PaginatedGUI {
    public SpawnerGui(CustomSpawner spawner) {
        super(Realms.getInstance(), "Spawner configuration", false, 27);
        String minTime = Utils.formatTime(CustomSpawner.MIN_SPAWNER_TIME);
        String maxTime = Utils.formatTime(CustomSpawner.MAX_SPAWNER_TIME);
    
        GUIButton infoButton = new GUIButton(ItemBuilder.start(Material.BEACON).withName("&lInformation").withLore("&dSpawns every &e" + minTime + " &d- &e" + maxTime, "&dSpawner Size: &e" + spawner.getAmount()).buildItem());
        GUIButton typeButton = new GUIButton(ItemBuilder.start(spawner.getEntityType()).withName("&e" + EntityNames.getName(spawner.getEntityType()) + " Spawner").buildItem());
        Material blockIcon;
        if (spawner.getSpawnBlock() == null) {
            blockIcon = Material.BARRIER;
        } else {
            blockIcon = spawner.getSpawnBlock();
        }
        
        GUIButton blockButton = new GUIButton(ItemBuilder.start(blockIcon).withName("&dBlock Whitelist").buildItem());
        blockButton.setListener(e -> new SpawnerBlockGui(spawner).openGUI(e.getWhoClicked()));
        
        setButton(4, infoButton);
        setButton(11, typeButton);
        setButton(15, blockButton);
    }
}