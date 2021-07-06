package net.firecraftmc.api.model;

import net.firecraftmc.api.enums.Rank;
import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.model.server.FirecraftServer;
import net.firecraftmc.api.packets.*;
import net.firecraftmc.api.packets.staffchat.*;
import net.firecraftmc.api.plugin.IFirecraftProxy;
import net.firecraftmc.api.util.Messages;
import net.firecraftmc.api.util.Utils;

import java.io.*;
import java.util.Collection;

public class ProxyWorker extends Thread {
    
    private static IFirecraftProxy plugin;
    private final java.net.Socket socket;
    private FirecraftServer server;
    
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    
    public ProxyWorker(IFirecraftProxy main, java.net.Socket socket) {
        plugin = main;
        this.socket = socket;
        try {
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void run() {
        while (!socket.isClosed()) {
            try {
                FirecraftPacket packet;
                Object obj = this.inputStream.readObject();
                if (obj == null) continue;
                if (obj instanceof Integer) continue;
                if (obj instanceof FirecraftPacket) {
                    packet = (FirecraftPacket) obj;
                } else {
                    System.out.println("Object received was not a FirecraftPacket.");
                    continue;
                }
                
                if (packet instanceof FPacketServerConnect) {
                    this.server = plugin.getServer(packet.getServerId());
                    String format = Utils.Chat.formatServerConnect(server);
                    if (!plugin.getPlayers().isEmpty()) plugin.getPlayers().forEach(fp -> fp.sendMessage(format));
                } else if (packet instanceof FPacketServerDisconnect) {
                    FPacketServerDisconnect serverDisconnect = (FPacketServerDisconnect) packet;
                    String format = Utils.Chat.formatServerDisconnect(plugin.getServer(serverDisconnect.getServerId()));
                    if (!plugin.getPlayers().isEmpty()) plugin.getPlayers().forEach(fp -> fp.sendMessage(format));
                    sendToAll(packet);
                } else if (packet instanceof FPacketServerPlayerJoin) {
                    FPacketServerPlayerJoin sPJ = (FPacketServerPlayerJoin) packet;
                    FPacketPlayerJoin nPacket = new FPacketPlayerJoin(sPJ.getServerId(), sPJ.getUuid());
                    sendToAll(nPacket);
                    continue;
                } else if (packet instanceof FPacketPunish) {
                    Utils.Socket.handlePunish(packet, plugin.getFCDatabase(), plugin.getPlayers());
                } else if (packet instanceof FPacketPunishRemove) {
                    Utils.Socket.handleRemovePunish(packet, plugin.getFCDatabase(), plugin.getPlayers());
                } else if (packet instanceof FPacketAcknowledgeWarning) {
                    String format = Utils.Chat.formatAckWarning(plugin.getServer(packet.getServerId()).getName(), ((FPacketAcknowledgeWarning) packet).getWarnedName());
                    if (!plugin.getPlayers().isEmpty()) {
                        plugin.getPlayers().forEach(p -> {
                            p.sendMessage("");
                            p.sendMessage(format);
                            p.sendMessage("");
                        });
                    }
                } else if (packet instanceof FPacketSocketBroadcast) {
                    FPacketSocketBroadcast socketBroadcast = ((FPacketSocketBroadcast) packet);
                    String message = Messages.socketBroadcast(socketBroadcast.getMessage());
                    if (!plugin.getPlayers().isEmpty()) plugin.getPlayers().forEach(p -> p.sendMessage(message));
                } else if (packet instanceof FPacketReport) {
                    if (!plugin.getPlayers().isEmpty())
                        Utils.Socket.handleReport(packet, server, plugin.getFCDatabase(), plugin.getPlayers());
                } else if (packet instanceof FPacketStaffChat) {
                    FPacketStaffChat staffChatPacket = ((FPacketStaffChat) packet);
                    FirecraftPlayer staffMember = plugin.getFCDatabase().getPlayer(staffChatPacket.getPlayer());
                    Collection<FirecraftPlayer> players = plugin.getPlayers();
                    if (packet instanceof FPStaffChatJoin) {
                        String format = Utils.Chat.formatStaffJoinLeave(server, staffMember, "joined");
                        Utils.Chat.sendStaffChatMessage(players, staffMember, format);
                    } else if (packet instanceof FPStaffChatQuit) {
                        String format = Utils.Chat.formatStaffJoinLeave(server, staffMember, "left");
                        Utils.Chat.sendStaffChatMessage(players, staffMember, format);
                    } else if (packet instanceof FPStaffChatMessage) {
                        FPStaffChatMessage staffMessage = (FPStaffChatMessage) packet;
                        String format = Utils.Chat.formatStaffMessage(plugin.getServer(staffChatPacket.getServerId()), staffMember, staffMessage.getMessage());
                        if (!players.isEmpty()) {
                            players.forEach(p -> {
                                if (Rank.isStaff(p.getMainRank())) {
                                    p.sendMessage(format);
                                }
                            });
                        }
                    } else if (packet instanceof FPStaffChatSetNick) {
                        FPStaffChatSetNick setNick = ((FPStaffChatSetNick) packet);
                        String format = Utils.Chat.formatSetNick(plugin.getServer(staffChatPacket.getServerId()), staffMember, setNick.getProfile());
                        Utils.Chat.sendStaffChatMessage(players, staffMember, format);
                    } else if (packet instanceof FPStaffChatResetNick) {
                        String format = Utils.Chat.formatResetNick(plugin.getServer(staffChatPacket.getServerId()), staffMember);
                        Utils.Chat.sendStaffChatMessage(players, staffMember, format);
                    } else if (packet instanceof FPSCVanishToggle) {
                        String format = Utils.Chat.formatVanishToggle(plugin.getServer(staffChatPacket.getServerId()), staffMember, staffMember.isVanished());
                        Utils.Chat.sendStaffChatMessage(players, staffMember, format);
                    } else if (packet instanceof FPSCSetGamemode) {
                        FPSCSetGamemode setGamemode = (FPSCSetGamemode) packet;
                        String format = Utils.Chat.formatSetGamemode(server, staffMember, setGamemode.getMode());
                        Utils.Chat.sendStaffChatMessage(players, staffMember, format);
                    } else if (packet instanceof FPSCSetGamemodeOthers) {
                        FPSCSetGamemodeOthers setGamemodeOthers = (FPSCSetGamemodeOthers) packet;
                        FirecraftPlayer target = plugin.getFCDatabase().getPlayer(setGamemodeOthers.getTarget());
                        String format = Utils.Chat.formatSetGamemodeOthers(server, staffMember, setGamemodeOthers.getMode(), target);
                        Utils.Chat.sendStaffChatMessage(players, staffMember, format);
                    } else if (packet instanceof FPSCTeleport) {
                        FPSCTeleport teleport = (FPSCTeleport) packet;
                        FirecraftPlayer target = plugin.getFCDatabase().getPlayer(teleport.getTarget());
                        String format = Utils.Chat.formatTeleport(server, staffMember, target);
                        Utils.Chat.sendStaffChatMessage(players, staffMember, format);
                    } else if (packet instanceof FPSCTeleportOthers) {
                        FPSCTeleportOthers teleportOthers = (FPSCTeleportOthers) packet;
                        FirecraftPlayer target1 = plugin.getFCDatabase().getPlayer(teleportOthers.getTarget1());
                        FirecraftPlayer target2 = plugin.getFCDatabase().getPlayer(teleportOthers.getTarget2());
                        String format = Utils.Chat.formatTeleportOthers(server, staffMember, target1, target2);
                        Utils.Chat.sendStaffChatMessage(players, staffMember, format);
                    } else if (packet instanceof FPSCTeleportHere) {
                        FPSCTeleportHere tpHere = (FPSCTeleportHere) packet;
                        FirecraftPlayer target = plugin.getFCDatabase().getPlayer(tpHere.getTarget());
                        String format = Utils.Chat.formatTeleportHere(server, staffMember, target);
                        Utils.Chat.sendStaffChatMessage(players, staffMember, format);
                    } else if (packet instanceof FPReportAssignOthers) {
                        FPReportAssignOthers assignOthers = ((FPReportAssignOthers) packet);
                        String format = Utils.Chat.formatReportAssignOthers(server.getName(), staffMember.getName(), assignOthers.getAssignee(), assignOthers.getId());
                        if (!players.isEmpty()) {
                            players.forEach(p -> {
                                if (Rank.isStaff(p.getMainRank())) {
                                    p.sendMessage(format);
                                }
                            });
                        }
                    } else if (packet instanceof FPReportAssignSelf) {
                        FPReportAssignSelf assignSelf = ((FPReportAssignSelf) packet);
                        String format = Utils.Chat.formatReportAssignSelf(server.getName(), staffMember.getName(), assignSelf.getId());
                        if (!players.isEmpty()) {
                            players.forEach(p -> {
                                if (Rank.isStaff(p.getMainRank())) {
                                    p.sendMessage(format);
                                }
                            });
                        }
                    } else if (packet instanceof FPReportSetOutcome) {
                        FPReportSetOutcome setOutcome = ((FPReportSetOutcome) packet);
                        String format = Utils.Chat.formatReportSetOutcome(server.getName(), staffMember.getName(), setOutcome.getId(), setOutcome.getOutcome());
                        if (!players.isEmpty()) {
                            players.forEach(p -> {
                                if (Rank.isStaff(p.getMainRank())) {
                                    p.sendMessage(format);
                                }
                            });
                        }
                    } else if (packet instanceof FPReportSetStatus) {
                        FPReportSetStatus setStatus = ((FPReportSetStatus) packet);
                        String format = Utils.Chat.formatReportSetStatus(server.getName(), staffMember.getName(), setStatus.getId(), setStatus.getStatus());
                        if (!players.isEmpty()) {
                            players.forEach(p -> {
                                if (Rank.isStaff(p.getMainRank())) {
                                    p.sendMessage(format);
                                }
                            });
                        }
                    }
                }
                sendToAll(packet);
            } catch (ClassNotFoundException e) {
                plugin.getLogger().severe("Received an object from the proxy but could not find the class for it.");
            } catch (IOException e) {
                if (!e.getMessage().contains("socket closed"))
                    plugin.getLogger().severe("There was an exception with the input or output: " + e.getMessage());
                else {
                    break;
                }
            } catch (Exception e) {
            
            }
        }
    }
    
    public static void sendToAll(FirecraftPacket packet) {
        for (ProxyWorker worker : plugin.getProxyWorkers()) {
            try {
                if (worker.isConnected()) {
                    worker.outputStream.writeObject(packet);
                } else {
                    worker.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void disconnect() throws IOException {
        System.out.println("Disconnect method called.");
        try {
            if (outputStream != null) outputStream.close();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } finally {
                if (socket != null) socket.close();
            }
        }
    }
    
    public FirecraftServer getServerName() {
        return server;
    }
    
    public boolean isConnected() {
        boolean connected = true;
        try {
            outputStream.writeObject(0);
        } catch (IOException e) {
            if (e.getMessage().toLowerCase().contains("socket closed") || e.getMessage().toLowerCase().contains("connection reset") || e.getMessage().toLowerCase().contains("broken pipe")) {
                connected = false;
            }
        }
        
        return connected;
    }
}