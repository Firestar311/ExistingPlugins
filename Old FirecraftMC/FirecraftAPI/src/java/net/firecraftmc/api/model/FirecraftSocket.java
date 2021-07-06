package net.firecraftmc.api.model;

import net.firecraftmc.api.interfaces.SocketListener;
import net.firecraftmc.api.model.server.FirecraftServer;
import net.firecraftmc.api.packets.FPacketServerConnect;
import net.firecraftmc.api.packets.FirecraftPacket;
import net.firecraftmc.api.plugin.IFirecraftCore;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class FirecraftSocket extends Thread {
    
    private java.net.Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private final IFirecraftCore plugin;
    private final String host;
    private final int port;
    
    private final List<SocketListener> socketListeners = new ArrayList<>();
    
    public FirecraftSocket(IFirecraftCore plugin, String host, int port) {
        this.plugin = plugin;
        plugin.getLogger().log(Level.INFO, "Connecting to the socket on " + port);
        this.host = host;
        this.port = port;
    }
    
    public void run() {
        while (!socket.isClosed()) {
           try {
               Object obj = this.inputStream.readObject();
               if (obj instanceof Integer) continue;
               if (!(obj instanceof FirecraftPacket)) {
                   System.out.println("Object received is not a FirecraftPacket.");
                   continue;
               }
               FirecraftPacket packet = (FirecraftPacket) obj;
    
               socketListeners.forEach(socketListener -> socketListener.handle(packet));
           } catch (ClassNotFoundException e) {
               plugin.getLogger().severe("Received an object from the proxy but could not find the class for it.");
           } catch (IOException e) {
               if (!e.getMessage().contains("socket closed"))
                   plugin.getLogger().severe("There was an exception with the input or output: " + e.getMessage());
               else {
                   plugin.getLogger().severe("The socket has been closed, stopping listening for packets.");
                   break;
               }
           } catch (Exception e) {
               plugin.getLogger().severe("There was an exception not related to the connection: " + e.getMessage());
               if (e.getMessage().toLowerCase().contains("already registered")) e.printStackTrace();
           }
        }
    }
    
    public void addSocketListener(SocketListener listener) {
        this.socketListeners.add(listener);
    }
    
    public void sendPacket(FirecraftPacket packet) {
        try {
            if (packet.getServerId() != null) this.outputStream.writeObject(packet);
        } catch (IOException e) {
        }
    }
    
    public void close() throws IOException {
        System.out.println("Close method called.");
        try {
            outputStream.close();
        } finally {
            try {
                inputStream.close();
            } finally {
                socket.close();
            }
        }
    }
    
    public boolean isOpen() {
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
    
    public void connect() {
        this.connect(host, port);
    }
    
    private void connect(String host, int port) {
        boolean retry = false;
        while (socket == null) {
            try {
                if (!retry) {
                    this.socket = new java.net.Socket(host, port);
                    if (socket.isConnected()) {
                        plugin.getLogger().log(Level.INFO, "Connected to the socket successfully!");
                        try {
                            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
                            this.inputStream = new ObjectInputStream(socket.getInputStream());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        plugin.getLogger().log(Level.INFO, "Sending ServerConnect Packet!");
                        FirecraftServer firecraftServer = plugin.getFCServer();
                        try {
                            this.outputStream.writeObject(new FPacketServerConnect(firecraftServer.getId()));
                        } catch (IOException e) {
                        }
                    }
                } else {
                    String[] ipSplit = host.split("\\.");
                    int last = Integer.parseInt(ipSplit[3]) + 1;
                    if (last == 255) {
                        return;
                    }
                    host = ipSplit[0] + "." + ipSplit[1] + "." + ipSplit[2] + "." + last;
                    this.socket = new java.net.Socket(host, port);
                    if (socket.isConnected()) {
                        plugin.getLogger().log(Level.INFO, "Connected to the socket successfully!");
                        try {
                            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
                            this.inputStream = new ObjectInputStream(socket.getInputStream());
                        } catch (Exception e) {
                        }
                        
                        plugin.getLogger().log(Level.INFO, "Sending ServerConnect Packet!");
                        try {
                            if (plugin.getFCServer() != null)
                                this.outputStream.writeObject(new FPacketServerConnect(plugin.getFCServer().getId()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e1) {
                    System.out.println("Sleep interrupted");
                }
                retry = true;
            }
        }
    }
    
    public List<SocketListener> getSocketListeners() {
        return socketListeners;
    }
    
    public Socket getJavaSocket() {
        return socket;
    }
}