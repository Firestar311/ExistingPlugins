package net.firecraftmc.api.model;

import com.google.common.collect.Lists;
import net.firecraftmc.api.FirecraftAPI;
import net.firecraftmc.api.enums.*;
import net.firecraftmc.api.model.Report.Response;
import net.firecraftmc.api.model.player.*;
import net.firecraftmc.api.model.server.FirecraftServer;
import net.firecraftmc.api.punishments.*;
import net.firecraftmc.api.punishments.Punishment.Type;
import net.firecraftmc.api.toggles.Toggle;
import net.firecraftmc.api.util.Utils;
import org.bukkit.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class Database {
    private Connection connection, serverConnection, loggerConnection;
    private final String user, database, password, hostname;
    private final int port;
    private long lastActivity = 0L;
    private final long timeout = TimeUnit.MINUTES.toMillis(5);
    private static final String profileUrlString = "https://sessionserver.mojang.com/session/minecraft/profile/{uuid}?unsigned=false";
    
    public Database(String user, String database, String password, int port, String hostname) {
        this.user = user;
        this.database = database;
        this.password = password;
        this.port = port;
        this.hostname = hostname;
        
        Thread timeoutThread = new Thread(() -> {
            while (Bukkit.getServer() != null) {
                if (!isClosed()) {
                    if ((lastActivity + timeout) >= System.currentTimeMillis()) {
                        System.out.println("Closing idle database connection.");
                        closeConnection();
                    }
                }
            }
        });
        timeoutThread.start();
    }
    
    public Punishment addPunishment(Punishment punishment) {
        int auto_id = getNextAutoId("punishments");
        String sql = "INSERT INTO `punishments` (`type`,`server`,`punisher`,`target`,`reason`,`date`,`active`,`expire`,`acknowledged`) VALUES ('{type}','{server}','{punisher}','{target}','{reason}','{date}','{active}','{expires}','{acknowledged}');";
        sql = sql.replace("{type}", punishment.getType().toString());
        sql = sql.replace("{server}", punishment.getServer());
        sql = sql.replace("{punisher}", punishment.getPunisher().toString());
        sql = sql.replace("{target}", punishment.getTarget().toString());
        sql = sql.replace("{reason}", punishment.getReason());
        sql = sql.replace("{date}", punishment.getDate() + "");
        sql = sql.replace("{active}", punishment.isActive() + "");
        sql = punishment instanceof TemporaryPunishment ? sql.replace("{expires}", punishment.getExpire() + "") : sql.replace("{expires}", "0");
        
        sql = punishment.getType().equals(Type.WARN) ? sql.replace("{acknowledged}", punishment.isAcknowledged() + "") : sql.replace("{acknowledged}", "false");
        updateSQL(sql);
        punishment.setId(auto_id);
        return punishment;
    }
    
    public void addProfile(FirecraftProfile profile) {
        String sql = "INSERT INTO `playerdata`(`uniqueid`, `lastname`, `mainrank`, `channel`, `vanish`, `online`, `server`, `ignored`) VALUES ('{uuid}','{name}','{rank}','{channel}','{vanish}','{online}','{server}','{ignored}')";
        sql = sql.replace("{uuid}", profile.getUniqueId().toString());
        sql = sql.replace("{name}", profile.getName());
        sql = sql.replace("{rank}", profile.getMainRank().toString());
        sql = sql.replace("{channel}", profile.getChannel().toString());
        sql = sql.replace("{vanish}", "false:false:false:false:false:false:false:false:false");
        sql = sql.replace("{online}", profile.isOnline() + "");
        sql = profile.getServer() != null ? sql.replace("{server}", profile.getServer().getName()) : sql.replace("{server}", "none");
        sql = getIgnored(sql, profile.getIgnored());
        
        updateSQL(sql);
    }
    
    private String getIgnored(String sql, List<UUID> ignored) {
        if (ignored.isEmpty()) {
            sql = sql.replace("{ignored}", "");
        } else {
            StringBuilder sb = new StringBuilder();
            for (UUID uuid : ignored) {
                sb.append(uuid.toString());
                if (!ignored.get(ignored.size() - 1).equals(uuid)) {
                    sb.append(":");
                }
            }
            sql = sql.replace("{ignored}", sb.toString());
        }
        return sql;
    }
    
    public FirecraftProfile getProfile(UUID uuid) {
        ResultSet set = querySQL("SELECT * FROM `playerdata` WHERE `uniqueid`='{uuid}';".replace("{uuid}", uuid.toString()));
        FirecraftProfile profile = null;
        try {
            if (set != null && set.next()) {
                String lastName = set.getString("lastname");
                Rank rank = Rank.valueOf(set.getString("mainrank"));
                Channel channel = Channel.valueOf(set.getString("channel"));
                String vanishString = set.getString("vanish");
                VanishSettings vanish = null;
                if (vanishString != null && !vanishString.equals("")) {
                    try {
                        vanish = VanishSettings.loadFromString(set.getString("vanish"));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Bukkit.getLogger().severe("Could not load vanish settings of " + uuid);
                    }
                }
                boolean online = set.getBoolean("online");
                String streamUrl = set.getString("streamurl");
                profile = new FirecraftProfile(uuid, rank);
                profile.setFirstJoined(set.getLong("firstjoined"));
                profile.setTimePlayed(set.getLong("timeplayed"));
                profile.setLastSeen(set.getLong("lastseen"));
                profile.setChannel(channel);
                if (vanish != null) {
                    profile.setVanishSettings(vanish);
                }
                profile.setOnline(online);
                profile.setName(lastName);
                profile.setStreamUrl(streamUrl);
                String rA = set.getString("reportchanges");
                if (rA != null && !rA.equals("")) {
                    String[] arr = rA.split(":");
                    for (String a : arr) {
                        profile.addUnseenReportAction(Integer.parseInt(a));
                    }
                }
                String i = set.getString("ignored");
                if (i != null && !i.equals("")) {
                    String[] arr = i.split(":");
                    for (String a : arr) {
                        profile.addIgnored(UUID.fromString(a));
                    }
                }
                
                try {
                    String[] tA = set.getString("toggles").split(",");
                    for (String t : tA) {
                        String[] tVArr = t.split("=");
                        Toggle toggle = Toggle.getToggle(tVArr[0]);
                        if (toggle != null) profile.setToggle(toggle, Boolean.parseBoolean(tVArr[1]));
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().severe("Error loading toggles: " + e.getMessage());
                    
                    for (Toggle toggle : Toggle.TOGGLES) {
                        profile.setToggle(toggle, toggle.getDefaultValue());
                    }
                }
            } else {
                profile = new FirecraftProfile(uuid, Rank.DEFAULT);
                String profileURL = profileUrlString.replace("{uuid}", uuid.toString().replace("-", ""));
                URL url = new URL(profileURL);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder buffer = new StringBuilder();
                int read;
                char[] chars = new char[256];
                while ((read = reader.read(chars)) != -1) {
                    buffer.append(chars, 0, read);
                }
                
                JSONObject json = (JSONObject) new JSONParser().parse(buffer.toString());
                profile.setName((String) json.get("name"));
                JSONArray properties = (JSONArray) json.get("properties");
                
                JSONObject property = (JSONObject) properties.get(0);
                String sN = (String) property.get("name");
                String sV = (String) property.get("value");
                String sS = (String) property.get("signature");
                
                String skinString = sN + ":" + sV + ":" + sS;
                updateSQL("UPDATE `playerdata` SET `skin`='{skin}' WHERE `uniqueid`='{uuid}';".replace("{skin}", skinString).replace("{uuid}", uuid.toString()));
                
                addProfile(profile);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return profile;
    }
    
    public FirecraftPlayer getPlayer(UUID uuid) {
        FirecraftPlayer player = new FirecraftPlayer(getProfile(uuid));
        ResultSet set = querySQL("SELECT * FROM `playerdata` WHERE `uniqueid`='{uuid}';".replace("{uuid}", uuid.toString()));
        try {
            boolean flying = set.getBoolean("isflying");
            boolean flightAllowed = set.getBoolean("allowedtofly");
            GameMode mode = GameMode.valueOf(set.getString("gamemode"));
            
            if (player.getPlayer() != null) {
                if (FirecraftAPI.isCore()) {
                    Bukkit.getScheduler().runTaskLater(FirecraftAPI.getFirecraftCore(), () -> {
                        player.getPlayer().setFlying(flying);
                        player.getPlayer().setAllowFlight(flightAllowed);
                        player.getProfile().setToggle(Toggle.FLIGHT, flightAllowed);
                        player.setGameMode(mode);
                    }, 20L);
                }
            }
        } catch (Exception ignored) {
        }
        return player;
    }
    
    public Skin getSkin(UUID uuid) {
        ResultSet set = querySQL("SELECT `skin` FROM `playerdata` WHERE `uniqueid`='{uuid}';".replace("{uuid}", uuid.toString()));
        try {
            if (set.next()) {
                String skinInfo = set.getString("skin");
                String[] skinArr = skinInfo.split(":");
                String skinName = "", skinValue = "", skinSignature = "";
                if (skinArr.length == 3) {
                    skinName = skinArr[0];
                    skinValue = skinArr[1];
                    skinSignature = skinArr[2];
                }
                return new Skin(uuid, skinName, skinSignature, skinValue);
            }
        } catch (Exception e) {
        }
        
        return null;
    }
    
    public NickInfo getNickname(UUID nicked) {
        ResultSet set = querySQL("SELECT `nick` FROM `playerdata` WHERE `uniqueid`='{uuid}';".replace("{uuid}", nicked.toString()));
        try {
            if (set.next()) {
                String[] arr = set.getString("nick").split(":");
                UUID uuid = UUID.fromString(arr[0]);
                Rank rank = Rank.valueOf(arr[1]);
                NickInfo info = new NickInfo(getPlayer(uuid));
                info.setRank(rank);
                return info;
            }
        } catch (Exception e) {
        }
        
        return null;
    }
    
    public void savePlayer(FirecraftPlayer player) {
        String sql = "UPDATE `playerdata` SET `uniqueid`='{uuid}',`lastname`='{name}',`mainrank`='{mainrank}',`channel`='{channel}',`nick`='{nick}',`vanish`='{vanish}',`firstjoined`='{firstjoined}',`timeplayed`='{timeplayed}',`lastseen`='{lastseen}',`toggles`='{toggles}',`balance`='0',`online`='{online}',`reportchanges`='{reportchanges}',`ignored`='{ignored}',`isflying`='{isflying}',`gamemode`='{gamemode}', `streamurl`='{streamurl}' WHERE `uniqueid` = '{uuid}';";
        sql = sql.replace("{uuid}", player.getUniqueId().toString());
        sql = sql.replace("{name}", player.getName());
        sql = sql.replace("{mainrank}", player.getMainRank().toString());
        sql = sql.replace("{channel}", player.getChannel().toString());
        sql = player.isNicked() ? sql.replace("{nick}", player.getNick().getProfile().getUniqueId().toString() + ":" + player.getNick().getRank().toString()) : sql.replace("{nick}", "");
        sql = sql.replace("{vanish}", player.getVanishSettings().toString());
        sql = sql.replace("{firstjoined}", player.getFirstJoined() + "");
        sql = sql.replace("{timeplayed}", player.getTimePlayed() + "");
        sql = sql.replace("{lastseen}", player.getLastSeen() + "");
        
        StringBuilder togglesBuilder = new StringBuilder();
        Map<Toggle, Boolean> toggles = player.getProfile().getToggles();
        Iterator<Entry<Toggle, Boolean>> iterator = toggles.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Toggle, Boolean> entry = iterator.next();
            togglesBuilder.append(entry.getKey().getName()).append("=").append(entry.getValue());
            if (iterator.hasNext()) togglesBuilder.append(",");
        }
        
        sql = sql.replace("{toggles}", togglesBuilder.toString());
        
        if (!player.getUnseenReportActions().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Integer id : player.getUnseenReportActions()) {
                sb.append(id);
                if (!id.equals(player.getUnseenReportActions().last())) {
                    sb.append(":");
                }
            }
            sql = sql.replace("{reportchanges}", sb.toString());
        } else {
            sql = sql.replace("{reportchanges}", "");
        }
        sql = sql.replace("{online}", player.isOnline() + "");
        
        sql = getIgnored(sql, player.getIgnored());
        
        sql = sql.replace("{isflying}", player.getPlayer().isFlying() + "");
        sql = sql.replace("{gamemode}", player.getPlayer().getGameMode().toString());
        sql = sql.replace("{streamurl}", (player.getStreamUrl() == null) ? "" : player.getStreamUrl());
        updateSQL(sql);
    }
    
    public String getPlayerName(UUID uuid) {
        ResultSet set = querySQL("SELECT `lastname` FROM `playerdata` WHERE `uniqueid`='{uuid}'".replace("{uuid}", uuid.toString()));
        String name = "";
        try {
            set.next();
            name = set.getString("lastname");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
        
    }
    
    public boolean getOnlineStatus(UUID uuid) {
        String sql = "SELECT * FROM `playerdata` WHERE `uniqueid`='{uuid}';".replace("{uuid}", uuid.toString());
        ResultSet set = querySQL(sql);
        try {
            set.next();
            return set.getBoolean("online");
        } catch (SQLException e) {
        }
        
        return false;
    }
    
    public Report saveReport(Report report) {
        int auto_id = getNextAutoId("reports");
        boolean insert = false;
        String sql;
        if (report.getId() == 0) {
            sql = "INSERT INTO `reports`(`reporter`, `target`, `assignee`, `reason`, `date`,`status`, `outcome`, `location`,`notes`) VALUES ('{reporter}','{target}','{assignee}','{reason}','{date}','{status}','{outcome}','{location}','{notes}');";
            insert = true;
        } else {
            sql = "UPDATE `reports` SET `reporter`='{reporter}',`target`='{target}',`assignee`='{assignee}',`reason`='{reason}',`date`='{date}',`status`='{status}',`outcome`='{outcome}',`location`='{location}',`notes`='{notes}' WHERE `id`='{id}';";
            sql = sql.replace("{id}", report.getId() + "");
        }
        sql = sql.replace("{reporter}", report.getReporter().toString());
        sql = sql.replace("{target}", report.getTarget().toString());
        sql = sql.replace("{reason}", report.getReason());
        sql = sql.replace("{assignee}", (report.getAssignee() == null) ? "" : report.getAssignee().toString());
        sql = sql.replace("{date}", report.getDate() + "");
        sql = sql.replace("{status}", report.getStatus().toString());
        sql = report.getOutcome() != null ? sql.replace("{outcome}", report.getOutcome().toString()) : sql.replace("{outcome}", "");
        sql = sql.replace("{location}", Utils.convertLocationToString(report.getLocation()));
        if (report.getResponses() != null && !report.getResponses().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < report.getResponses().size(); i++) {
                Response response = report.getResponses().get(i);
                sb.append(response.getTimestamp()).append(":").append(response.getResponder()).append(":").append(response.getMessage());
                if (i != report.getResponses().size() - 1) {
                    sb.append(",");
                }
            }
            sql = sql.replace("{notes}", sb.toString());
        } else {
            sql = sql.replace("{notes}", "");
        }
        
        updateSQL(sql);
        if (insert) report.setId(auto_id);
        return report;
    }
    
    public Report getReport(int reportId) {
        String sql = "SELECT * FROM `reports` WHERE `id` = '" + reportId + "';";
        ResultSet set = querySQL(sql);
        try {
            if (set.next()) {
                UUID reporter = UUID.fromString(set.getString("reporter"));
                UUID target = UUID.fromString(set.getString("target"));
                UUID assingnee = null;
                try {
                    assingnee = UUID.fromString(set.getString("assignee"));
                } catch (Exception e) {
                }
                String reporterName = getPlayerName(reporter);
                String targetName = getPlayerName(target);
                String assigneeName = (assingnee != null) ? getPlayerName(assingnee) : "";
                String reason = set.getString("reason");
                long date = set.getLong("date");
                Report.Status status = Report.Status.valueOf(set.getString("status"));
                Report.Outcome outcome = Report.Outcome.NONE;
                try {
                    outcome = Report.Outcome.valueOf(set.getString("outcome"));
                } catch (Exception e) {
                }
                Location location = Utils.getLocationFromString(set.getString("location"));
                List<Response> responses = new ArrayList<>();
                try {
                    String[] noteArr = set.getString("responses").split(",");
                    for (String n : noteArr) {
                        String[] nA = n.split(":");
                        long timestamp = Long.parseLong(nA[0]);
                        UUID noter = UUID.fromString(nA[1]);
                        responses.add(new Response(timestamp, noter, nA[2]));
                    }
                } catch (Exception e) {
                }
                return new Report(reportId, reporter, target, reason, date, assingnee, status, outcome, location, reporterName, targetName, assigneeName, responses);
            }
        } catch (Exception e) {
        }
        
        return null;
    }
    
    public int addReportChange(int id, String action) {
        int auto_id = getNextAutoId("reportracking");
        updateSQL("INSERT INTO `reportracking` (`reportid`,`action`) VALUES('{id}','{action}');".replace("{id}", id + "").replace("{action}", action));
        return auto_id;
    }
    
    public String getReportChange(int id) {
        ResultSet set = querySQL("SELECT * FROM `reportracking` WHERE `id`='{id}';".replace("{id}", id + ""));
        try {
            set.next();
            return set.getInt("reportid") + ":" + set.getString("action");
        } catch (SQLException e) {
        }
        return "";
    }
    
    public void updateOnlineStatus(UUID uuid, boolean online, String server) {
        updateSQL("UPDATE `playerdata` SET `online`='{online}',`server`='{server}' WHERE `uniqueid`='{uuid}';".replace("{online}", online + "").replace("{server}", server).replace("{uuid}", uuid.toString()));
    }
    
    public void updateNickname(FirecraftPlayer player) {
        updateSQL("UPDATE `playerdata` SET `nick`='{nick}' WHERE `uniqueid`='{uuid}';".replace("{nick}", player.getNick().getProfile().getUniqueId().toString() + ":" + player.getNick().getRank().toString()).replace("{uuid}", player.getUniqueId().toString()));
    }
    
    public void removeNickname(FirecraftPlayer player) {
        updateSQL("UPDATE `playerdata` SET `nick`='' WHERE `uniqueid`='{uuid}';".replace("{uuid}", player.getUniqueId().toString()));
    }
    
    public void updateVanish(FirecraftPlayer player) {
        String sql = "UPDATE `playerdata` SET `vanish`='{vanish}' WHERE `uniqueid`='{uuid}';".replace("{uuid}", player.getUniqueId().toString());
        sql = sql.replace("{vanish}", player.getVanishSettings().toString());
        updateSQL(sql);
    }
    
    public String getFTPrefix(UUID uuid) {
        ResultSet set = querySQL("SELECT * FROM `fctprefixes` WHERE `fctmember`='{uuid}';".replace("{uuid}", uuid.toString()));
        try {
            return set.next() ? set.getString("prefix") : null;
        } catch (Exception e) {
        }
        return null;
    }
    
    public boolean setFTPrefix(UUID uuid, String prefix) {
        String current = getFTPrefix(uuid);
        try {
            String sql;
            sql = current != null ? "UPDATE `fctprefixes` SET `prefix`='" + prefix + "' WHERE `fctmember` = '{uuid}';".replace("{uuid}", uuid.toString()) : "INSERT INTO `fctprefixes`(`fctmember`, `prefix`) VALUES ('{uuid}','{prefix}');".replace("{uuid}", uuid.toString()).replace("{prefix}", prefix);
            updateSQL(sql);
            return true;
        } catch (Exception e) {
        }
        return false;
    }
    
    public void removeFTPrefix(UUID uuid) {
        updateSQL("DELETE FROM `fctprefixes` WHERE `fctmember`='{uuid}';".replace("{uuid}", uuid.toString()));
    }
    
    public boolean hasActiveJail(UUID uuid) {
        ResultSet jailSet = querySQL("SELECT * FROM `punishments` WHERE `target`='{uuid}' AND `active`='true' AND `type`='JAIL';".replace("{uuid}", uuid.toString()));
        try {
            if (jailSet.next()) {
                return true;
            }
        } catch (Exception ex) {
        }
        
        return false;
    }
    
    public boolean hasUnacknowledgedWarnings(UUID uuid) {
        ResultSet warnSet = querySQL("SELECT * FROM `punishments` WHERE `target`='{uuid}' AND `acknowledged`='false' AND `type`='WARN';".replace("{uuid}", uuid.toString()));
        try {
            if (warnSet.next()) {
                return true;
            }
        } catch (Exception ex) {
        }
        return false;
    }
    
    public void updateDataColumn(UUID uuid, String column, String value) {
        String sql = "UPDATE `playerdata` SET `{column}`='{value}' WHERE `uniqueid`='{uuid}';";
        sql = sql.replace("{column}", column);
        sql = sql.replace("{value}", value);
        sql = sql.replace("{uuid}", uuid.toString());
        updateSQL(sql);
    }
    
    public Map<String, List<FirecraftPlayer>> getOnlineStaffMembers() {
        ResultSet set = querySQL("SELECT * FROM `playerdata` WHERE `online`='true'");
        
        Map<String, List<FirecraftPlayer>> onlineStaff = new HashMap<>();
        try {
            while (set.next()) {
                String server = set.getString("server");
                if (!server.equalsIgnoreCase("")) {
                    FirecraftPlayer p = getPlayer(UUID.fromString(set.getString("uniqueid")));
                    if (Rank.isStaff(p.getMainRank())) {
                        if (!onlineStaff.containsKey(server)) {
                            onlineStaff.put(server, new ArrayList<>(Lists.newArrayList(p)));
                        } else {
                            onlineStaff.get(server).add(p);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return onlineStaff;
    }
    
    public List<Report> getNotClosedReports() {
        List<Report> reports = new ArrayList<>();
        ResultSet reportSet = querySQL("SELECT * FROM `reports` WHERE `status` <> 'CLOSED';");
        try {
            while (reportSet.next()) {
                Report report = getReport(reportSet.getInt("id"));
                reports.add(report);
            }
        } catch (Exception ex) {
        }
        return reports;
    }
    
    public List<Punishment> getPunishments(UUID uuid) {
        List<Punishment> punishments = new ArrayList<>();
        ResultSet set = querySQL("SELECT * FROM `punishments` WHERE `target`='{target}';".replace("{target}", uuid.toString()));
        try {
            while (set.next()) {
                Punishment punishment = getPunishment(set.getInt("id"));
                if (punishment != null) punishments.add(punishment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return punishments;
    }
    
    public Punishment getPunishment(int id) {
        Punishment punishment = null;
        try {
            ResultSet puSet = querySQL("SELECT * FROM `punishments` where `id`='" + id + "';");
            if (puSet.next()) {
                Punishment.Type type = Punishment.Type.valueOf(puSet.getString("type"));
                String puServer = puSet.getString("server");
                UUID p = UUID.fromString(puSet.getString("punisher"));
                UUID t = UUID.fromString(puSet.getString("target"));
                String reason = puSet.getString("reason");
                long date = puSet.getLong("date");
                long expires = puSet.getLong("expire");
                boolean active = Boolean.parseBoolean(puSet.getString("active"));
                boolean acknowledged = Boolean.parseBoolean(puSet.getString("acknowledged"));
                String removedBy = puSet.getString("removedby");
                String punisherName = getPlayerName(p);
                String targetName = getPlayerName(t);
                String removerName = null;
                UUID remover = null;
                try {
                    remover = UUID.fromString(removedBy);
                    removerName = getPlayerName(remover);
                } catch (Exception e) {
                }
                
                if (type.equals(Type.BAN) || type.equals(Type.MUTE)) {
                    punishment = new PermanentPunishment(id, type, puServer, p, t, reason, date, active);
                } else if (type.equals(Type.TEMP_BAN) || type.equals(Type.TEMP_MUTE)) {
                    punishment = new TemporaryPunishment(id, type, puServer, p, t, reason, date, expires, active);
                } else {
                    punishment = new Punishment(id, type, puServer, p, t, reason, date, active);
                }
                
                if (punishment != null) {
                    punishment.setTargetName(targetName);
                    punishment.setPunisherName(punisherName);
                    if (removerName != null) {
                        punishment.setRemover(remover);
                        punishment.setRemoverName(removerName);
                    }
                    punishment.setAcknowledged(acknowledged);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return punishment;
    }
    
    @Deprecated
    public FirecraftServer getServer(String id) {
        ResultSet set = null;
        try {
            if (serverConnection == null || serverConnection.isClosed()) {
                openConnection();
            }
            Statement statement = serverConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            set = statement.executeQuery("SELECT * FROM `servers` WHERE `id` = '{id}';".replace("{id}", id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (set.next()) {
                String name = set.getString("name");
                ChatColor color = ChatColor.getByChar(set.getString("color").replace("§", ""));
                String ip = set.getString("ip");
                ServerType type = ServerType.valueOf(set.getString("type"));
                return new FirecraftServer(id, name, color, ip, type);
            }
        } catch (Exception e) {
        }
        
        return null;
    }
    
    @Deprecated
    public void saveServer(FirecraftServer server) {
        ResultSet set = null;
        try {
            Statement statement = serverConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            set = statement.executeQuery("SELECT * FROM `servers` WHERE `id` = '{id}';".replace("{id}", server.getId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        try {
            String sql;
            sql = !set.next() ? "INSERT INTO `servers`(`id`, `name`, `color`, `ip`, `type`) VALUES ('{id}','{name}','{color}','{ip}','{type}')" : "UPDATE `servers` SET `name`='{name}',`color`='{color}',`ip`='{ip}',`type`='{type}' WHERE `id`='{id}';";
            sql = sql.replace("{id}", server.getId());
            sql = sql.replace("{name}", server.getName());
            sql = sql.replace("{color}", server.getColor().toString());
            sql = sql.replace("{ip}", server.getIp());
            sql = sql.replace("{type}", server.getType().toString());
            Statement statement = serverConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public List<Punishment> getPunishments(UUID target, String type) {
        List<Punishment> punishments = new ArrayList<>();
        ResultSet set;
        if (type.equalsIgnoreCase("all")) {
            set = querySQL("SELECT * FROM `punishments` WHERE `target`='{target}';".replace("{target}", target.toString()));
        } else {
            Punishment.Type punishmentType = Punishment.Type.valueOf(type.toUpperCase());
            set = querySQL("SELECT * FROM `punishments` WHERE `target`='{target}' AND `type`='{type}';".replace("{target}", target.toString()).replace("{type}", punishmentType.toString()));
        }
        
        try {
            while (set.next()) {
                punishments.add(getPunishment(set.getInt("id")));
            }
        } catch (Exception e) {
        }
        
        return punishments;
    }
    
    @Deprecated
    public List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        ResultSet set = querySQL("SELECT * FROM `transactions`;");
        try {
            while (set.next()) {
                long date = set.getLong("date");
                UUID player = UUID.fromString(set.getString("player"));
                TransactionType type = TransactionType.valueOf(set.getString("type"));
                double amount = set.getDouble("amount");
                UUID admin = null, target = null;
                
                try {
                    admin = UUID.fromString(set.getString("admin"));
                } catch (Exception e) {
                }
                
                try {
                    target = UUID.fromString(set.getString("target"));
                } catch (Exception e) {
                }
                
                String ticketid = set.getString("ecoticketid");
                
                Transaction transaction = new Transaction(player, type, amount, date);
                if (admin != null) {
                    transaction.setAdmin(admin);
                }
                
                if (target != null) {
                    transaction.setTarget(target);
                }
                
                if (ticketid != null) {
                    transaction.setEcoTicketId(ticketid);
                }
                
                transactions.add(transaction);
            }
        } catch (Exception e) {
        }
        
        
        return transactions;
    }
    
    @Deprecated
    public List<Transaction> getTransactions(UUID player) {
        List<Transaction> transactions = new ArrayList<>();
        ResultSet set = querySQL("SELECT * FROM `transactions` WHERE `player`='{player}';".replace("{player}", player.toString()));
        try {
            while (set.next()) {
                long date = set.getLong("date");
                TransactionType type = TransactionType.valueOf(set.getString("type"));
                double amount = set.getDouble("amount");
                UUID admin = null, target = null;
                
                try {
                    admin = UUID.fromString(set.getString("admin"));
                } catch (Exception e) {
                }
                
                try {
                    target = UUID.fromString(set.getString("target"));
                } catch (Exception e) {
                }
                
                String ticketid = set.getString("ecoticketid");
                
                Transaction transaction = new Transaction(player, type, amount, date);
                if (admin != null) {
                    transaction.setAdmin(admin);
                }
                
                if (target != null) {
                    transaction.setTarget(target);
                }
                
                if (ticketid != null) {
                    transaction.setEcoTicketId(ticketid);
                }
                
                transactions.add(transaction);
            }
        } catch (Exception e) {
        }
        
        return transactions;
    }
    
    @Deprecated
    public void saveTransaction(Transaction transaction) {
        String sql = "INSERT INTO `transactions`(`date`, `player`, `type`, `amount`, `admin`, `target`, `ecoticketid`) VALUES ('{date}','{player}','{type}','{amount}','{admin}','{target}','{ecoticketid}');";
        sql = sql.replace("{date}", transaction.getDate() + "");
        sql = sql.replace("{player}", transaction.getPlayer().toString());
        sql = sql.replace("{type}", transaction.getType().toString());
        sql = sql.replace("{amount}", transaction.getAmount() + "");
        sql = sql.replace("{admin}", (transaction.getAdmin() == null) ? "" : transaction.getAdmin().toString());
        sql = sql.replace("{target}", (transaction.getTarget() == null) ? "" : transaction.getTarget().toString());
        sql = sql.replace("{ecoticketid}", (transaction.getEcoTicketId() == null) ? "" : transaction.getEcoTicketId());
        updateSQL(sql);
    }
    
    @Deprecated
    public Mail getMail(int id) {
        String sql = "SELECT * FROM `mail` WHERE `id`='" + id + "';";
        ResultSet set = querySQL(sql);
        try {
            if (set.next()) {
                long date = set.getLong("date");
                UUID sender = UUID.fromString(set.getString("sender"));
                UUID receiver = UUID.fromString(set.getString("receiver"));
                String text = set.getString("text");
                boolean read = set.getBoolean("read");
                return new Mail(id, date, sender, receiver, text, read);
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    @Deprecated
    public List<Mail> getMailBySender(UUID sender) {
        String sql = "SELECT * FROM `mail` WHERE `sender`='" + sender.toString() + "';";
        ResultSet set = querySQL(sql);
        List<Mail> mail = new ArrayList<>();
        try {
            while (set.next()) {
                int id = set.getInt("id");
                long date = set.getLong("date");
                UUID receiver = UUID.fromString(set.getString("receiver"));
                String text = set.getString("text");
                boolean read = set.getBoolean("read");
                mail.add(new Mail(id, date, sender, receiver, text, read));
            }
        } catch (Exception e) {
        }
        
        return mail;
    }
    
    @Deprecated
    public List<Mail> getMailByReceiver(UUID receiver) {
        String sql = "SELECT * FROM `mail` WHERE `receiver`='" + receiver.toString() + "';";
        ResultSet set = querySQL(sql);
        List<Mail> mail = new ArrayList<>();
        try {
            retrieveMail(receiver, set, mail);
        } catch (Exception e) {
        }
        
        return mail;
    }
    
    public static void retrieveMail(UUID receiver, ResultSet set, List<Mail> mail) throws SQLException {
        while (set.next()) {
            int id = set.getInt("id");
            long date = set.getLong("date");
            UUID sender = UUID.fromString(set.getString("sender"));
            String text = set.getString("text");
            boolean read = set.getBoolean("read");
            mail.add(new Mail(id, date, sender, receiver, text, read));
        }
    }
    
    @Deprecated
    public List<Mail> getMailByUUID(UUID uuid) {
        List<Mail> mail = new ArrayList<>();
        mail.addAll(getMailByReceiver(uuid));
        mail.addAll(getMailBySender(uuid));
        return mail;
    }
    
    @Deprecated
    public List<Mail> getAllMail() {
        String sql = "SELECT * FROM `mail`;";
        ResultSet set = querySQL(sql);
        List<Mail> mail = new ArrayList<>();
        try {
            retrieveMail(set, mail);
        } catch (Exception e) {
        }
        return mail;
    }
    
    public static void retrieveMail(ResultSet set, List<Mail> mail) throws SQLException {
        while (set.next()) {
            int id = set.getInt("id");
            long date = set.getLong("date");
            UUID sender = UUID.fromString(set.getString("sender"));
            UUID receiver = UUID.fromString(set.getString("receiver"));
            String text = set.getString("text");
            boolean read = set.getBoolean("read");
            mail.add(new Mail(id, date, sender, receiver, text, read));
        }
    }
    
    @Deprecated
    public Mail createMail(long date, UUID sender, UUID receiver, String text, boolean read) {
        int auto_id = getNextAutoId("mail");
        
        String sql = "INSERT INTO `mail` (`date`, `sender`, `receiver`, `text`, `read`) VALUES('{date}', '{sender}', '{receiver}', '{text}', '{read}');";
        sql = sql.replace("{date}", date + "");
        sql = sql.replace("{sender}", sender.toString());
        sql = sql.replace("{receiver}", receiver.toString());
        sql = sql.replace("{text}", text);
        sql = sql.replace("{read}", read + "");
        updateSQL(sql);
        return new Mail(auto_id, date, sender, receiver, text, read);
    }
    
    @Deprecated
    public void setMailRead(int id) {
        updateSQL("UPDATE `mail` SET `read`='true' WHERE `id`='" + id + "';");
    }
    
    @Deprecated
    public void saveChatMessage(UUID uuid, FirecraftServer server, long timestamp, Channel channel, String message) {
        try {
            PreparedStatement statement = loggerConnection.prepareStatement("INSERT INTO `player_chat` (`uuid`, `server`, `timestamp`, `channel`, `message`) VALUES (?, ?, ?, ?, ?);");
            statement.setString(1, uuid.toString());
            statement.setString(2, server.getId());
            statement.setString(3, timestamp + "");
            statement.setString(4, channel.name());
            statement.setString(5, message);
            
            statement.executeUpdate();
        } catch (Exception e) {
        }
    }
    
    @Deprecated
    public void saveCommand(UUID uuid, FirecraftServer server, long timestamp, String command) {
        try {
            PreparedStatement statement = loggerConnection.prepareStatement("INSERT INTO `player_cmd` (`uuid`, `server`, `timestamp`, `command`) VALUES (?, ?, ?, ?);");
            statement.setString(1, uuid.toString());
            statement.setString(2, server.getId());
            statement.setString(3, timestamp + "");
            statement.setString(4, command);
            
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {
        }
    }
    
    @Deprecated
    private int getNextAutoId(String table) {
        ResultSet auto = querySQL("SELECT `AUTO_INCREMENT` FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '{database}' AND TABLE_NAME = '{table}';".replace("{database}", database).replace("{table}", table));
        int auto_id = 0;
        try {
            auto.next();
            auto_id = auto.getInt("AUTO_INCREMENT");
        } catch (SQLException e) {
        }
        return auto_id;
    }
    
    @Deprecated
    public void openConnection() {
        String connectionURL = "jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(connectionURL, this.user, this.password);
            this.lastActivity = System.currentTimeMillis();
            System.out.println("Opened a database connection to " + connectionURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String serverConUrl = "jdbc:mysql://" + this.hostname + ":" + this.port + "/serverinfo";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            serverConnection = DriverManager.getConnection(serverConUrl, this.user, this.password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        String loggerConUrl = "jdbc:mysql://" + this.hostname + ":" + this.port + "/logger";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            loggerConnection = DriverManager.getConnection(loggerConUrl, this.user, this.password);
        } catch (Exception e) {
        }
        
        modifyDatabase();
    }
    
    @Deprecated
    private void modifyDatabase() {
        //TODO add more of the CREATE TABLE IF NOT EXISTS things for the other tables
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `{database}`.`mail` ( `id` INT NOT NULL AUTO_INCREMENT , `date` VARCHAR(1000) NOT NULL, `sender` VARCHAR(64) NOT NULL , `receiver` VARCHAR(64) NOT NULL , `text` VARCHAR(1000) NOT NULL , `read` VARCHAR(5) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;".replace("{database}", this.database));
            statement.executeUpdate("ALTER TABLE `playerdata` DROP `god`;");
            statement.executeUpdate("ALTER TABLE `playerdata` DROP `socialspy`;");
            statement.executeUpdate("ALTER TABLE `playerdata` ADD `toggles` VARCHAR(2000) NULL DEFAULT NULL AFTER `lastseen`;");
            statement.close();
        } catch (Exception e) {
        }
    }
    
    @Deprecated
    public boolean checkConnection() {
        return connection != null && !isClosed();
    }
    
    @Deprecated
    public Connection getConnection() {
        return connection;
    }
    
    @Deprecated
    public void closeConnection() {
        try {
            closeConnection(connection);
            connection = null;
            closeConnection(serverConnection);
            serverConnection = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Deprecated
    private void closeConnection(Connection con) throws SQLException {
        if (con != null) {
            con.close();
        }
    }
    
    @Deprecated
    public ResultSet querySQL(String query) {
        if (!checkConnection()) {
            openConnection();
        }
        
        try {
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            statement.closeOnCompletion();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.lastActivity = System.currentTimeMillis();
        
        return null;
    }
    
    @Deprecated
    public void updateSQL(String query) {
        if (!checkConnection()) {
            openConnection();
        }
        
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.lastActivity = System.currentTimeMillis();
    }
    
    @Deprecated
    private boolean isClosed() {
        if (connection != null) {
            try {
                return this.connection.isClosed();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}