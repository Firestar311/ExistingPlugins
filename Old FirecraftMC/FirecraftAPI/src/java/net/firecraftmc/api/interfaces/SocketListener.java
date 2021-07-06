package net.firecraftmc.api.interfaces;

import net.firecraftmc.api.packets.FirecraftPacket;

public interface SocketListener {
    void handle(FirecraftPacket packet);
}