package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.kits.Kit;
import com.stardevmc.titangames.util.Messages;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@CommandInfo(description = "Creates a kit", aliases = {"createkit", "ck"}, usage = "<kitName> <permission> <displaymaterial>", permission = "titangames.createkit")
public class CreateKitCommand extends GameCommand {
    
    private TitanGames plugin;
    public CreateKitCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length > 2)) {
            player.sendMessage(Utils.color("&cYou need to provide the kit name, permission or display material"));
            return;
        }
        
        String name = args[0];
        String permission = args[1];
        Material displayMaterial = Material.getMaterial(args[2].toUpperCase());
        
        if (StringUtils.isEmpty(name)) {
            player.sendMessage(Utils.color("&cThe Kit name cannot be empty."));
            return;
        }
        
        if (StringUtils.isEmpty(permission)) {
            player.sendMessage(Utils.color("&cThe kit permission cannot be empty."));
            return;
        }
    
        ItemStack[] items = player.getInventory().getContents();
        Kit kit = new Kit(name, permission, Arrays.asList(items), displayMaterial);
        this.plugin.getKitManager().addKit(kit);
        player.sendMessage(Utils.color(Messages.CREATE_KIT(kit)));
    }
}