package com.starmediadev.lib.user;

import com.starmediadev.lib.StarLib;
import com.starmediadev.lib.items.InventoryStore;
import com.starmediadev.lib.pagination.*;
import com.starmediadev.lib.util.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class UserCommand implements TabExecutor, Listener {
    
    private final StarLib plugin;
    
    public UserCommand(StarLib plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(args.length > 0)) {
            sender.sendMessage(Utils.color("&cYou must provide a name."));
            return true;
        }
        
        User user = plugin.getUserManager().getUser(args[0]);
        if (user == null) {
            sender.sendMessage(Utils.color("&cThat name did not match a valid player."));
            return true;
        }
        
        if (!(args.length > 1)) {
            sender.sendMessage(Utils.color("&cYou must provide a sub command."));
            return true;
        }
        
        if (Utils.checkCmdAliases(args, 1, "info", "i")) {
            OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(user.getUniqueId());
            sender.sendMessage(Utils.color("&e====== &bInfo about " + user.getLastName() + "&e ======"));
            sender.sendMessage(Utils.color("&e - Name: &r" + user.getLastName()));
            sender.sendMessage(Utils.color("&e - Whitelisted: &r" + offlinePlayer.isWhitelisted()));
            sender.sendMessage(Utils.color("&e - Played Before: &r" + offlinePlayer.hasPlayedBefore()));
            sender.sendMessage(Utils.color("&e - IPs: &r" + user.getIpAddresses().size()));
            sender.sendMessage(Utils.color("&e - UUID: &r" + user.getUniqueId().toString()));
            sender.sendMessage(Utils.color("&e - OP: &r" + offlinePlayer.isOp()));
            sender.sendMessage(Utils.color("&e - Sessions: &r" + user.getPlaySessions().size()));
            sender.sendMessage(Utils.color("&e - Deaths: &r" + user.getDeaths().size()));
            sender.sendMessage(Utils.color("&e - OP: &r" + offlinePlayer.isOp()));
            if (offlinePlayer.isOnline()) {
                Player player = offlinePlayer.getPlayer();
                sender.sendMessage(Utils.color("&e - Fly Mode: &r" + player.getAllowFlight() + " (isFlying: " + player.isFlying() + ")"));
                //God
                sender.sendMessage(Utils.color("&e - Health: &r" + player.getHealth() + "/" + player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
                sender.sendMessage(Utils.color("&e - Hunger: &r" + player.getFoodLevel() + "/20.0 (" + player.getSaturation() + " saturation)"));
                sender.sendMessage(Utils.color("&e - Exp: &r" + player.getExp() + " (Level " + player.getLevel() + ")"));
                String world = player.getWorld().getName();
                int x = player.getLocation().getBlockX(), y = player.getLocation().getBlockY(), z = player.getLocation().getBlockZ();
                sender.sendMessage(Utils.color("&e - Location: &r" + world + " (" + x + ", " + y + ", " + z + ")"));
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item != null) {
                    sender.sendMessage(Utils.color("&e - Item in Hand: &r" + player.getInventory().getItemInMainHand().getType().name()));
                } else {
                    sender.sendMessage(Utils.color("&e - Item in Hand: &rAIR"));
                }
            }
        } else if (Utils.checkCmdAliases(args, 1, "session", "sessions", "s")) {
            if (!(args.length > 2)) {
                sender.sendMessage(Utils.color("&cYou must provide a session id or a date."));
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 2, "list", "l")) {
                Paginator<PlaySession> paginator = PaginatorFactory.generatePaginator(7, new TreeSet<>(user.getPlaySessions()), new HashMap<>() {{
                    put(DefaultVariables.COMMAND, "user " + user.getLastName() + " session list");
                    put(DefaultVariables.TYPE, user.getLastName() + "'s Sessions");
                }});
                
                if (args.length > 3) {
                    paginator.display(sender, args[3]);
                } else {
                    paginator.display(sender, 1);
                }
            } else if (Utils.checkCmdAliases(args, 2, "view", "v")) {
                //sender.sendMessage(Utils.color("&eCalculating play sessions... This can take a bit."));
                new BukkitRunnable() {
                    public void run() {
                        Set<PlaySession> sessions = new HashSet<>();
                        try {
                            int id = Integer.parseInt(args[3]);
                            PlaySession session = user.getPlaySession(id);
                            if (session != null) {
                                sessions.add(session);
                            }
                        } catch (NumberFormatException e) {
                            if (args[3].equalsIgnoreCase("current")) {
                                PlaySession session = user.getCurrentSession();
                                if (session != null) {
                                    sessions.add(session);
                                }
                            }
                        }
                        
                        if (sessions.isEmpty()) {
                            short[] dateValues = Utils.parseDate(sender, args[3]);
                            
                            Calendar calendar = Calendar.getInstance();
                            if (dateValues[3] == -1 && dateValues[4] == -1 && dateValues[5] == -1) {
                                Pair<Long, Long> dayValues = Utils.getDayStartEnd(dateValues);
                                
                                for (long d = dayValues.getValue1(); d <= dayValues.getValue2(); d++) {
                                    PlaySession session = user.getPlaySession(TimeUnit.SECONDS.toMillis(d));
                                    if (session != null) {
                                        sessions.add(session);
                                    }
                                }
                            } else {
                                calendar.set(Calendar.HOUR, dateValues[3]);
                                calendar.set(Calendar.MINUTE, dateValues[4]);
                                calendar.set(Calendar.SECOND, dateValues[5]);
                                
                                long time = calendar.getTimeInMillis();
                                PlaySession session = user.getPlaySession(time);
                                if (session != null) {
                                    sessions.add(session);
                                }
                            }
                        }
                        
                        if (sessions.isEmpty()) {
                            sender.sendMessage(Utils.color("&cNo play sessions matched the criteria."));
                            return;
                        }
                        
                        if (sessions.size() == 1) {
                            PlaySession session = sessions.toArray(new PlaySession[0])[0];
                            if (args.length == 4) {
                                sender.sendMessage(Utils.color("&e====== &bInfo about " + user.getLastName() + " &e======"));
                                session.getDisplayMap().forEach((key, value) -> sender.sendMessage(Utils.color("&e - " + key + " : &r" + value)));
                            } else {
                                Bukkit.getScheduler().runTask(plugin, () -> {
                                    if (!(sender instanceof Player)) {
                                        sender.sendMessage(Utils.color("&cOnly players can view the inventories."));
                                        return;
                                    }
                                    
                                    Player player = (Player) sender;
                                    
                                    if (Utils.checkCmdAliases(args, 4, "inventory")) {
                                        Inventory inventory = Bukkit.createInventory(null, 45, user.getLastName() + "'s Inventory Snapshot");
                                        viewInventory(session, player, inventory);
                                    } else if (Utils.checkCmdAliases(args, 4, "enderchest")) {
                                        Inventory inventory = Bukkit.createInventory(null, 27, user.getLastName() + "'s Enderchest Snapshot");
                                        viewInventory(session, player, inventory);
                                    }
                                });
                            }
                        } else {
                            sender.sendMessage(Utils.color("&eFound &b" + sessions.size() + " &esessions, please specify a session id."));
                            StringBuilder sb = new StringBuilder();
                            for (PlaySession session : sessions) {
                                sb.append(session.getId()).append(" ");
                            }
                            sender.sendMessage(Utils.color("&eIDs: &b" + sb.toString()));
                        }
                    }
                }.runTaskAsynchronously(plugin);
            }
        } else if (Utils.checkCmdAliases(args, 1, "ip", "ips", "ipaddress", "ipaddresses")) {
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(Utils.color("&cOnly console may view ip addresses."));
                return true;
            }
            //TODO
        } else if (Utils.checkCmdAliases(args, 1, "deaths")) {
            if (!(args.length > 2)) {
                sender.sendMessage(Utils.color("&cYou must provide a sub command."));
                return true;
            }
            
            if (Utils.checkCmdAliases(args, 2, "list")) {
                Paginator<DeathSnapshot> paginator = PaginatorFactory.generatePaginator(7, new TreeSet<>(user.getDeaths()), new HashMap<>() {{
                    put(DefaultVariables.COMMAND, "user " + user.getLastName() + " deaths list");
                    put(DefaultVariables.TYPE, user.getLastName() + "'s Deaths");
                }});
                
                if (args.length > 3) {
                    paginator.display(sender, args[3]);
                } else {
                    paginator.display(sender, 1);
                }
            } else if (Utils.checkCmdAliases(args, 2, "restore")) {
                DeathSnapshot deathSnapshot;
                try {
                    int id = Integer.parseInt(args[3]);
                    deathSnapshot = user.getDeath(id);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Utils.color("&cYou provided an invalid number for the death id."));
                    return true;
                }
                
                Player target = Bukkit.getPlayer(user.getUniqueId());
                if (target == null) {
                    sender.sendMessage(Utils.color("&cThe target must be online in order to do that."));
                    return true;
                }
                
                ItemStack[] items = deathSnapshot.getItems();
                target.getInventory().setContents(items);
                String time = Constants.DATE_FORMAT.format(new Date(deathSnapshot.getTime()));
                target.setExp(deathSnapshot.getExp());
                target.setLevel(deathSnapshot.getLevel());
                target.sendMessage(Utils.color("&eYour items have been restored by &b" + sender.getName() + " &efrom your death at &b" + time));
                sender.sendMessage(Utils.color("&eRestored &b" + target.getName() + "&e's items from their death &b" + time));
            } else if (Utils.checkCmdAliases(args, 2, "view")) {
                if (!(args.length > 3)) {
                    sender.sendMessage(Utils.color("&cYou must provide an id, date or time"));
                    return true;
                }
                
                new BukkitRunnable() {
                    public void run() {
                        Set<DeathSnapshot> deaths = new HashSet<>();
                        try {
                            int id = Integer.parseInt(args[3]);
                            DeathSnapshot deathSnapshot = user.getDeath(id);
                            if (deathSnapshot != null) {
                                deaths.add(deathSnapshot);
                            }
                        } catch (NumberFormatException e) {}
                        
                        if (deaths.isEmpty()) {
                            short[] dateValues = Utils.parseDate(sender, args[3]);
                            
                            Calendar calendar = Calendar.getInstance();
                            if (dateValues[3] == -1 && dateValues[4] == -1 && dateValues[5] == -1) {
                                Pair<Long, Long> dayValues = Utils.getDayStartEnd(dateValues);
                                deaths.addAll(user.getDeaths(TimeUnit.SECONDS.toMillis(dayValues.getValue1()), TimeUnit.SECONDS.toMillis(dayValues.getValue2())));
                            } else {
                                calendar.set(Calendar.HOUR, dateValues[3]);
                                calendar.set(Calendar.MINUTE, dateValues[4]);
                                calendar.set(Calendar.SECOND, dateValues[5]);
                                
                                long start = calendar.getTimeInMillis();
                                calendar.set(Calendar.SECOND, dateValues[5] + 1);
                                long end = calendar.getTimeInMillis();
                                deaths.addAll(user.getDeaths(start, end));
                            }
                        }
                        
                        if (deaths.isEmpty()) {
                            sender.sendMessage(Utils.color("&cNo deaths matched the criteria."));
                            return;
                        }
                        
                        if (deaths.size() == 1) {
                            DeathSnapshot deathSnapshot = deaths.toArray(new DeathSnapshot[0])[0];
                            if (args.length == 4) {
                                sender.sendMessage(Utils.color("&e====== &bDeath Info for " + user.getLastName() + " &e======"));
                                deathSnapshot.getDisplayMap().forEach((key, value) -> sender.sendMessage(Utils.color("&e - " + key + " : &r" + value)));
                            } else {
                                Bukkit.getScheduler().runTask(plugin, () -> {
                                    if (!(sender instanceof Player)) {
                                        sender.sendMessage(Utils.color("&cOnly players can view the inventories."));
                                        return;
                                    }
                                    
                                    Player player = (Player) sender;
                                    
                                    if (Utils.checkCmdAliases(args, 4, "inventory")) {
                                        Inventory inventory = Bukkit.createInventory(null, 45, user.getLastName() + "'s Inventory Snapshot");
                                        ItemStack[] items = InventoryStore.stringToItems(deathSnapshot.getInventory());
                                        for (int i = 0; i < items.length; i++) {
                                            ItemStack itemStack = items[i];
                                            if (itemStack != null) {
                                                inventory.setItem(i, itemStack);
                                            }
                                        }
                                        player.openInventory(inventory);
                                    }
                                });
                            }
                        } else {
                            sender.sendMessage(Utils.color("&eFound &b" + deaths.size() + " deaths, &eplease specify a death id."));
                            StringBuilder sb = new StringBuilder();
                            for (DeathSnapshot session : deaths) {
                                sb.append(session.getId()).append(" ");
                            }
                            sender.sendMessage(Utils.color("&eIDs: &b" + sb.toString()));
                        }
                    }
                }.runTaskAsynchronously(plugin);
            }
        }
        return true;
    }
    
    private void viewInventory(PlaySession session, Player player, Inventory inventory) {
        ItemStack[] items = session.getLogoutInventory();
        for (int i = 0; i < items.length; i++) {
            ItemStack itemStack = items[i];
            if (itemStack != null) {
                inventory.setItem(i, itemStack);
            }
        }
        player.openInventory(inventory);
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> results = new ArrayList<>(), possibleResults = new ArrayList<>();
        
        if (args.length == 1) {
            for (User user : plugin.getUserManager().getUsers().values()) {
                possibleResults.add(user.getLastName());
            }
            
            results.addAll(Utils.getResults(args[0], possibleResults));
        } else if (args.length == 2) {
            if (!StringUtils.isEmpty(args[0])) {
                possibleResults.addAll(Arrays.asList("info", "session", "deaths"));
            }
            
            results.addAll(Utils.getResults(args[1], possibleResults));
        } else if (args.length == 3) {
            if (!(StringUtils.isEmpty(args[0]))) {
                if (Utils.checkCmdAliases(args, 1, "session", "sessions", "s")) {
                    possibleResults.addAll(Arrays.asList("list", "view"));
                } else if (Utils.checkCmdAliases(args, 1, "deaths")) {
                    possibleResults.addAll(Arrays.asList("list", "restore", "view"));
                }
            }
            
            results.addAll(Utils.getResults(args[2], possibleResults));
        } else if (args.length == 4) {
            if (!(StringUtils.isEmpty(args[0]))) {
                if (Utils.checkCmdAliases(args, 1, "session", "sessions", "s")) {
                    if (Utils.checkCmdAliases(args, 2, "view", "v")) {
                        possibleResults.add("current");
                        User user = plugin.getUserManager().getUser(args[0]);
                        if (user != null) {
                            for (PlaySession session : user.getPlaySessions()) {
                                possibleResults.add(session.getId() + "");
                            }
                        }
                    }
                } else if (Utils.checkCmdAliases(args, 1, "deaths")) {
                    if (Utils.checkCmdAliases(args, 2, "restore", "view")) {
                        User user = plugin.getUserManager().getUser(args[0]);
                        if (user != null) {
                            for (DeathSnapshot deathSnapshot : user.getDeaths()) {
                                possibleResults.add(deathSnapshot.getId() + "");
                            }
                        }
                    }
                }
            }
            
            results.addAll(Utils.getResults(args[3], possibleResults));
        } else if (args.length == 5) {
            if (!(StringUtils.isEmpty(args[0]))) {
                if (Utils.checkCmdAliases(args, 1, "session", "sessions", "s")) {
                    if (Utils.checkCmdAliases(args, 2, "view", "v")) {
                        if (!StringUtils.isEmpty(args[3])) {
                            possibleResults.addAll(Arrays.asList("inventory", "enderchest"));
                        }
                    }
                } else if (Utils.checkCmdAliases(args, 1, "deaths")) {
                    if (Utils.checkCmdAliases(args, 2, "restore", "view")) {
                        if (!StringUtils.isEmpty(args[3])) {
                            possibleResults.addAll(Collections.singletonList("inventory"));
                        }
                    }
                }
            }
            
            results.addAll(Utils.getResults(args[4], possibleResults));
        }
        
        return results;
    }
    
    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e) {
        if (e.getView().getTitle().contains("'s Inventory Snapshot")) {
            e.setCancelled(true);
        }
    }
}