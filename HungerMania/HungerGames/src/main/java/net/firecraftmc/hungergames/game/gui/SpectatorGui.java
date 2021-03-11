package net.firecraftmc.hungergames.game.gui;

import net.firecraftmc.hungergames.HungerGames;
import net.firecraftmc.hungergames.game.Game;
import net.firecraftmc.hungergames.game.GamePlayer;
import net.firecraftmc.maniacore.spigot.gui.GUIButton;
import net.firecraftmc.maniacore.spigot.gui.Gui;
import net.firecraftmc.maniacore.spigot.util.ItemBuilder;
import org.bukkit.Material;

public class SpectatorGui extends Gui {
    public SpectatorGui(Game game, GamePlayer player, GamePlayer target) {
        super(HungerGames.getInstance(), target.getUser().getName(), false, 9);
        
        setButton(0, new GUIButton(ItemBuilder.start(Material.ENDER_PEARL).setDisplayName("&aTeleport").build()).setListener(e -> player.getUser().getBukkitPlayer().teleport(target.getUser().getBukkitPlayer().getLocation())));
        setButton(1, new GUIButton(ItemBuilder.start(Material.CHEST).setDisplayName("&eInventory").build()).setListener(e -> new SpectatorInventoryGui(game, player, target).openGUI(player.getUser().getBukkitPlayer())));
        setButton(2, new GUIButton(ItemBuilder.start(Material.EXP_BOTTLE).setDisplayName("&6Sponsor").build()).setListener(e -> new SponsorGui(game, player, target)));
        setButton(8, new GUIButton(ItemBuilder.start(Material.ARROW).setDisplayName("&fBack").build()).setListener(e -> new PlayersGui(game, target.getTeam())));
    }
}