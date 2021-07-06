package com.stardevmc.titanterritories.core.controller;

import com.firestar311.lib.pagination.Paginator;
import com.firestar311.lib.pagination.PaginatorFactory;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.enums.Permission;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.Announcement;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AnnouncementController<T extends IHolder> extends Controller<T> {
    private List<Announcement> announcements = new ArrayList<>();
    private AtomicInteger currentIndex = new AtomicInteger(0);
    private long interval = TimeUnit.MINUTES.toMillis(5);
    private long lastAnnouncement = 0;
    private BukkitRunnable runnable;
    
    private AnnouncementController() {
    }
    
    public AnnouncementController(T holder) {
        super(holder);
    }
    
    public static AnnouncementController deserialize(Map<String, Object> serialized) {
        AnnouncementController announcementController = new AnnouncementController();
        List<Announcement> announcements = new ArrayList<>();
        if (serialized.containsKey("amount")) {
            int amount = Integer.parseInt((String) serialized.get("amount"));
            for (int i = 0; i < amount; i++) {
                announcements.add((Announcement) serialized.get("announcement" + i));
            }
            long interval = Long.parseLong((String) serialized.get("interval"));
            int index = Integer.parseInt((String) serialized.get("index"));
            announcementController.currentIndex.set(index);
            announcementController.interval = interval;
        }
        announcementController.announcements = announcements;
        return announcementController;
    }
    
    public void handleCommand(Command cmd, IHolder holder, IUser user, String[] args) {
        if (!user.hasPermission(Permission.CONTROL_ANNOUNCEMENT)) {
            user.sendMessage("&cYou do not have the permission to manage announcements");
            return;
        }
        
        if (args.length == 1) {
            user.sendMessage("&aTotal Announcements: " + getAnnouncements().size());
            user.sendMessage("&aCurrent Announcement: " + getCurrentIndex());
            user.sendMessage("&aAnnouncement Interval: " + Utils.formatTime(getInterval()));
            long nextAnnouncementTime = getLastAnnouncementTime() + getInterval();
            long timeRemaining = nextAnnouncementTime - System.currentTimeMillis();
            //Might not work, needs testing
            user.sendMessage("&aNext announcement in: " + Utils.formatTime(timeRemaining));
            return;
        }
        
        if (Utils.checkCmdAliases(args, 1, "list", "l")) {
            String header = "&7List of Announcements &e({pagenumber}/{totalpages})", footer = "&7Type /" + cmd.getName() + " announcements list {nextpage} for more";
            Paginator<Announcement> paginator = PaginatorFactory.generatePaginator(header, footer, 7, getAnnouncements());
            if (args.length > 2) {
                paginator.display(user.getPlayer(), args[2]);
            } else {
                paginator.display(user.getPlayer(), 1);
            }
        } else if (Utils.checkCmdAliases(args, 1, "add", "a", "create", "c")) {
            UUID creator = user.getUniqueId();
            int order = getLastAnnouncementIndex() + 1;
            String text = StringUtils.join(args, " ", 2, args.length);
            Announcement announcement = new Announcement(creator, order, text);
            addAnnouncement(announcement);
            user.sendMessage("&aYou have added an announcement with the text: " + text);
        } else if (Utils.checkCmdAliases(args, 1, "remove", "r", "delete", "d")) {
            if (!(args.length > 2)) {
                user.sendMessage("&cYou do not have enough arguments");
                return;
            }
            int order;
            try {
                order = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                user.sendMessage(Utils.color("&cThe value you provided must be a number."));
                return;
            }
            Announcement announcement = getAnnouncement(order);
            if (announcement == null) {
                user.sendMessage(Utils.color("&cThere is no announcement with that id."));
                return;
            }
            
            removeAnnouncement(announcement);
            user.sendMessage(Utils.color("&aYou removed the announcement with the id " + order));
        } else if (Utils.checkCmdAliases(args, 1, "view", "v")) {
            if (!(args.length > 2)) {
                user.sendMessage("&cYou do not have enough arguments");
                return;
            }
            int order;
            try {
                order = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                user.sendMessage(Utils.color("&cThe value you provided must be a number."));
                return;
            }
            Announcement announcement = getAnnouncement(order);
            if (announcement == null) {
                user.sendMessage(Utils.color("&cThere is no announcement with that id."));
                return;
            }
            
            user.sendMessage("&aAnnouncement Info for: " + announcement.getOrder());
            user.sendMessage("&7Creator: " + TitanTerritories.getInstance().getMemberManager().getMember(announcement.getCreator()).getName());
            user.sendMessage("&7Text: " + announcement.getMessage());
        } else if (Utils.checkCmdAliases(args, 1, "edit", "e")) {
            if (!(args.length > 3)) {
                user.sendMessage("&cYou do not have enough arguments");
                return;
            }
            
            int order;
            try {
                order = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                user.sendMessage(Utils.color("&cThe value you provided must be a number."));
                return;
            }
            Announcement announcement = getAnnouncement(order);
            if (announcement == null) {
                user.sendMessage(Utils.color("&cThere is no announcement with that id."));
                return;
            }
            
            if (Utils.checkCmdAliases(args, 3, "order", "o")) {
                if (!(args.length > 3)) {
                    user.sendMessage(Utils.color("&cYou do not have enough arguments."));
                    return;
                }
                int newOrder;
                try {
                    newOrder = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    user.sendMessage(Utils.color("&cThe value you provided must be a number."));
                    return;
                }
                
                if (getAnnouncement(newOrder) != null) {
                    user.sendMessage("&cAn announcement with that order already exists.");
                    return;
                }
                announcement.setOrder(newOrder);
                user.sendMessage("&aYou set the order of that announcement to " + newOrder);
            } else if (Utils.checkCmdAliases(args, 3, "message", "msg")) {
                if (!(args.length > 4)) {
                    user.sendMessage("&cYou do not have enough arguments");
                    return;
                }
                
                String msg = StringUtils.join(args, " ", 4, args.length);
                if (StringUtils.isEmpty(msg)) {
                    user.sendMessage("&cThe new message does not have any content.");
                    return;
                }
                
                announcement.setMessage(msg);
                user.sendMessage("&aYou set the message for that announcement to " + msg);
            }
        } else if (Utils.checkCmdAliases(args, 1, "setinterval", "si")) {
            if (!(args.length > 2)) {
                user.sendMessage("&cYou do not have enough arguments");
                return;
            }
            
            long newInterval = Utils.parseTime(args[2]);
            if (newInterval == 0) {
                user.sendMessage("&cThere was a problem getting the time.");
                return;
            }
            
            setInterval(newInterval);
            user.sendMessage("&aYou set the interval of the announcements to " + Utils.formatTime(newInterval) + " and restarted the announcements.");
        } else if (Utils.checkCmdAliases(args, 1, "start", "enable")) {
            beginAnnouncements();
            user.sendMessage("&aYou have started the announcements");
        } else if (Utils.checkCmdAliases(args, 1, "stop", "disable")) {
            stopAnnouncements();
            user.sendMessage("&aYou have stopped the announcements");
        }
    }
    
    public List<Announcement> getAnnouncements() {
        return new ArrayList<>(announcements);
    }
    
    public int getCurrentIndex() {
        return currentIndex.get();
    }
    
    public long getInterval() {
        return interval;
    }
    
    public void setInterval(long interval) {
        this.interval = interval;
        try {
            runnable.cancel();
        } catch (Exception e) {}
        new BukkitRunnable() {
            public void run() {
                beginAnnouncements();
            }
        }.runTaskLater(TitanTerritories.getInstance(), 3L);
    }
    
    public int getLastAnnouncementIndex() {
        int greatestIndex = -1;
        for (Announcement announcement : announcements) {
            if (greatestIndex < announcement.getOrder()) {
                greatestIndex = announcement.getOrder();
            }
        }
        return greatestIndex;
    }
    
    public void addAnnouncement(Announcement announcement) {
        this.announcements.add(announcement);
    }
    
    public Announcement getAnnouncement(int order) {
        for (Announcement announcement : announcements) {
            if (announcement.getOrder() == order) {
                return announcement;
            }
        }
        return null;
    }
    
    public void removeAnnouncement(Announcement announcement) {
        this.announcements.remove(announcement);
    }
    
    public void beginAnnouncements() {
        this.lastAnnouncement = System.currentTimeMillis();
        getRunnable().runTaskTimer(TitanTerritories.getInstance(), 0, 20L);
    }
    
    public void stopAnnouncements() {
        runnable.cancel();
    }
    
    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = new AtomicInteger(currentIndex);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("amount", getAnnouncements().size() + "");
        serialized.put("interval", getInterval() + "");
        serialized.put("index", getCurrentIndex() + "");
        if (!getAnnouncements().isEmpty()) {
            for (int i = 0; i < getAnnouncements().size(); i++) {
                serialized.put("announcement" + i, getAnnouncements().get(i));
            }
        }
        return serialized;
    }
    
    public long getLastAnnouncementTime() {
        return lastAnnouncement;
    }
    
    private BukkitRunnable getRunnable() {
        if (this.runnable != null) {
            this.runnable.cancel();
        }
        this.runnable = new BukkitRunnable() {
            public void run() {
                boolean noAnnouncements = true;
                if (announcements.isEmpty()) { return; }
                long nextAnnouncement = lastAnnouncement + interval;
                if (nextAnnouncement < System.currentTimeMillis()) {
                    for (Announcement announcement : announcements) {
                        if (announcement.getOrder() == currentIndex.get()) {
                            noAnnouncements = false;
                            IUser creator = holder.getUserController().get(announcement.getCreator());
                            holder.sendMemberMessage(Utils.blankLine(35));
                            holder.sendMemberMessage("&b" + holder.getClass().getSimpleName().toUpperCase() + " ANNOUNCEMENT FROM &e" + creator.getName());
                            holder.sendMemberMessage("&a" + announcement.getMessage());
                            holder.sendMemberMessage(Utils.blankLine(35));
                            lastAnnouncement = System.currentTimeMillis();
                            break;
                        }
                    }
                
                    if (noAnnouncements) {
                        currentIndex.set(0);
                    } else { currentIndex.getAndIncrement(); }
                }
            }
        };
        return runnable;
    }
}