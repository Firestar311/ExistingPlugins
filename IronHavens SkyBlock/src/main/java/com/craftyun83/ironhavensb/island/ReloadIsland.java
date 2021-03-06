package com.craftyun83.ironhavensb.island;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;

public class ReloadIsland {
	
	@SuppressWarnings("deprecation")
	public ReloadIsland(Player p, File file) throws FileNotFoundException, IOException, WorldEditException {
		
		World world = Bukkit.getWorld(p.getUniqueId().toString());
		
		ClipboardFormat format = ClipboardFormats.findByFile(file);
		
		try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
			
		    Clipboard clipboard = reader.read();
			   
			try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(world), -1)) {
				    
				Operation operation = new ClipboardHolder(clipboard)
				            .createPaste(editSession)
				            .to(BlockVector3.at(0, 80, 0))
				            .ignoreAirBlocks(false)
				            .build();
				Operations.complete(operation);
				    
			}
			   
		}
		
		p.teleport(new Location(Bukkit.getWorld(p.getUniqueId().toString()), 0, 81, 0));
		
	}

}
