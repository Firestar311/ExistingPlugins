package com.kingrealms.realms.spawners.gui;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.spawners.CustomSpawner;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.gui.GUIButton;
import com.starmediadev.lib.gui.PaginatedGUI;
import com.starmediadev.lib.util.MaterialNames;
import org.bukkit.Material;

public class SpawnerBlockGui extends PaginatedGUI {
    public SpawnerBlockGui(CustomSpawner spawner) {
        super(Realms.getInstance(), "Spawner Block Configuration", true, 54);
        
        addButton(new GUIButton(ItemBuilder.start(Material.BARRIER).withName("&fNo Whitelist").buildItem()).setListener(e -> {
            spawner.setSpawnBlock(null);
            new SpawnerGui(spawner).openGUI(e.getWhoClicked());
        }));
        for (Material material : CustomSpawner.ALLOWED_BLOCKS) {
            ItemBuilder itemBuilder = ItemBuilder.start(material).withName("&f" + MaterialNames.getName(material));
            if (material == spawner.getSpawnBlock()) {
                itemBuilder.setGlowing(true);
            }
            GUIButton button = new GUIButton(itemBuilder.buildItem());
            button.setListener(e -> {
                spawner.setSpawnBlock(material);
                new SpawnerGui(spawner).openGUI(e.getWhoClicked());
            });
            
            addButton(button);
        }
    }
}