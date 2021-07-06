package com.stardevmc.chat.defaultrooms;

import com.stardevmc.chat.Chatroom;
import com.stardevmc.chat.api.ServerOwner;
import org.bukkit.Material;

public class StaffRoom extends Chatroom {
    public StaffRoom() {
        super("staff", new ServerOwner(), "&bStaff", "titanchat.room.staff", "&8[&bS&8] &r{displayname}&8: &b{message}", Material.ENDER_PEARL);
        this.description = "This is the staff channel.";
        this.setAutoJoin(true);
    }
}