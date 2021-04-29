package net.firecraftmc.maniacore.spigot.cmd;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.PlayerObject;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.spigot.user.SpigotUserManager;
import net.firecraftmc.maniacore.spigot.util.SpigotUtils;
import net.firecraftmc.maniacore.api.nickname.Nickname;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.maniacore.api.records.NicknameRecord;
import net.firecraftmc.maniacore.api.skin.Skin;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import net.firecraftmc.manialib.sql.IRecord;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static net.firecraftmc.maniacore.api.ranks.Rank.*;

@SuppressWarnings("DuplicatedCode")
public class NicknameCmd implements CommandExecutor {
    
    public static final Set<Rank> PERMITTED_RANKS = new HashSet<>(Arrays.asList(MEDIA, MEDIAPLUS, MOD, SR_MOD, ADMIN, OWNER, CONSOLE, ROOT));
    public static final Set<Rank> USABLE_RANKS = new HashSet<>(Arrays.asList(SURVIVALIST, FORAGER, SCAVENGER, DEFAULT));
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank = SpigotUtils.getRankFromSender(sender);
        
        if (cmd.getName().equalsIgnoreCase("nick")) {
            if (!PERMITTED_RANKS.contains(senderRank)) {
                sender.sendMessage(CenturionsUtils.color("&cYou are not allowed to use that command."));
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(CenturionsUtils.color("&cOnly players may use that command."));
                return true;
            }

            final Plugin maniaCore = Bukkit.getPluginManager().getPlugin("CenturionsCore");
            new BukkitRunnable() {
                public void run() {
                    try {
                        User user = CenturionsCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId());
                        if (!(args.length > 0)) {
                            user.sendMessage("&cUsage: /nick <displayName>");
                            user.sendMessage("&c&o  -s can be used for the skin name and -r can be used for the rank");
                            return;
                        }

                        if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("reset")) {
                            new BukkitRunnable() {
                                public void run() {
                                    user.resetNickname();
                                    user.sendMessage("&aReset your nickname.");
                                    user.getNickname().setActive(false);
                                    CenturionsCore.getInstance().getPlugin().runTaskAsynchronously(() -> new NicknameRecord(user.getNickname()).push(CenturionsCore.getInstance().getDatabase()));
                                }
                            }.runTask(maniaCore);
                            return;
                        }

                        user.sendMessage("&aSending in your request for a nickname.");

                        String name = args[0];
                        Skin skin = null;
                        Rank rank = DEFAULT;

                        user.sendMessage("&aDetermining the name, skin and rank to be used for your nickname.");

                        if (args.length > 1) {
                            for (int i = 1; i < args.length; i++) {
                                if (args[i].startsWith("-s")) {
                                    if (!(args.length > (i))) {
                                        user.sendMessage("&cYou must provide a skin name.");
                                        return;
                                    }

                                    String skinName = args[i + 1];
                                    User skinUser = CenturionsCore.getInstance().getUserManager().getUser(skinName);
                                    if (skinUser == null) {
                                        user.sendMessage("&cInvalid name for skin player.");
                                        return;
                                    }
                                    skin = skinUser.getSkin();
                                    if (skin == null) {
                                        skin = new Skin(skinUser.getUniqueId());
                                        if (skin == null) {
                                            user.sendMessage("&cCould not fetch the skin data.");
                                            return;
                                        } else {
                                            CenturionsCore.getInstance().getSkinManager().addSkin(skin);
                                        }
                                    }
                                } else if (args[i].startsWith("-r")) {
                                    if (!(args.length > (i))) {
                                        user.sendMessage("&cYou must provide a rank name.");
                                        return;
                                    }

                                    try {
                                        rank = valueOf(args[i + 1].toUpperCase());
                                    } catch (IllegalArgumentException e) {
                                        user.sendMessage("Invalid rank name.");
                                        return;
                                    }

                                    if (!USABLE_RANKS.contains(rank)) {
                                        if (!senderRank.equals(ROOT)) {
                                            user.sendMessage("&cYou are not allowed to use that rank.");
                                            return;
                                        }
                                    }
                                }
                            }
                        }

                        User target;
                        try {
                            target = CenturionsCore.getInstance().getUserManager().getUser(name);
                        } catch (Exception e) {
                            target = null;
                        }
                        if (target != null) {
                            PlayerObject playerObject = TimoCloudAPI.getUniversalAPI().getPlayer(target.getUniqueId());
                            if (playerObject != null) {
                                if (playerObject.isOnline()) {
                                    user.sendMessage("&cThat player is online, you cannot use that name.");
                                    return;
                                }
                            }

                            if (target.getRank().ordinal() <= user.getRank().ordinal()) {
                                user.sendMessage("&cThat player has a higher rank than you.");
                                return;
                            }
                        }
                        if (skin == null) {
                            if (target == null) {
                                skin = user.getSkin();
                            } else {
                                skin = target.getSkin();
                                if (skin == null) {
                                    skin = new Skin(target.getUniqueId());
                                    if (skin == null) {
                                        skin = user.getSkin();
                                    } else {
                                        CenturionsCore.getInstance().getSkinManager().addSkin(skin);
                                    }
                                }
                            }
                        }

                        if (CenturionsCore.getInstance().getNicknameManager().isBlacklisted(name)) {
                            sender.sendMessage(CenturionsUtils.color("&cThat name is blacklisted from being used."));
                            return;
                        }

                        Skin finalSkin = skin;
                        Rank finalRank = rank;
                        new BukkitRunnable() {
                            public void run() {
                                Nickname nickname = user.getNickname();
                                nickname.setName(name);
                                nickname.setSkinUUID(finalSkin.getUuid());
                                nickname.setRank(finalRank);
                                nickname.setActive(true);
                                user.applyNickname();
                                CenturionsCore.getInstance().getPlugin().runTaskAsynchronously(() -> new NicknameRecord(nickname).push(CenturionsCore.getInstance().getDatabase()));
                                user.sendMessage("&aSet your nickname to " + user.getDisplayName());
                            }
                        }.runTask(maniaCore);
                    } catch (Exception e) {
                        sender.sendMessage(CenturionsUtils.color("&cThere was an error setting your nickname."));
                    }
                }
            }.runTaskAsynchronously(maniaCore);
        } else if (cmd.getName().equalsIgnoreCase("unnick")) {
            if (!PERMITTED_RANKS.contains(senderRank)) {
                sender.sendMessage(CenturionsUtils.color("&cYou are not allowed to use that command."));
                return true;
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage(CenturionsUtils.color("&cOnly players may use that command."));
                return true;
            }

            User user = CenturionsCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId());
            user.resetNickname();
            user.getNickname().setActive(false);
            CenturionsCore.getInstance().getPlugin().runTaskAsynchronously(() -> new NicknameRecord(user.getNickname()).push(CenturionsCore.getInstance().getDatabase()));
            user.sendMessage("&aReset your nickname.");
        } else if (cmd.getName().equalsIgnoreCase("realname")) {
            if (senderRank.ordinal() > HELPER.ordinal()) {
                sender.sendMessage(CenturionsUtils.color("&fUnknown command. Type \"help\" for help."));
                return true;
            }

            User target = null;
            if (args.length > 0) {
                Player t = Bukkit.getPlayer(args[0]);
                if (t != null) {
                    target = CenturionsCore.getInstance().getUserManager().getUser(t.getUniqueId());
                } else {
                    for (User value : ((SpigotUserManager) CenturionsCore.getInstance().getUserManager()).getUsers().values()) {
                        if (value.getNickname().isActive()) {
                            if (value.getNickname().getName().equalsIgnoreCase(args[0])) {
                                target = value;
                            }
                        } else {
                            if (value.getName().equalsIgnoreCase(args[0])) {
                                target = value;
                            }
                        }
                    }
                }

                if (target == null) {
                    List<IRecord> nicknameRecords = CenturionsCore.getInstance().getDatabase().getRecords(NicknameRecord.class, "active", "true");
                    for (IRecord record : nicknameRecords) {
                        NicknameRecord nickRecord = (NicknameRecord) record;
                        if (nickRecord.toObject().getName().equalsIgnoreCase(args[0])) {
                            target = CenturionsCore.getInstance().getUserManager().getUser(nickRecord.toObject().getPlayer());
                        }
                    }
                }

                if (target == null) {
                    CenturionsCore.getInstance().getUserManager().getUser(args[0]);
                }
            }
            
            if (target == null) {
                sender.sendMessage(CenturionsUtils.color("&cCould not find a player with that name."));
                return true;
            }
            
            if (target.getRank().ordinal() < senderRank.ordinal()) {
                sender.sendMessage(CenturionsUtils.color("&cThat player does not have a nickname set."));
                return true;
            }
            
            if (!target.getNickname().isActive()) {
                sender.sendMessage(CenturionsUtils.color("&cThat player does not have a nickname set."));
                return true;
            }
            
            sender.sendMessage(CenturionsUtils.color("&aThe nicked player " + target.getNickname().getName() + " has the real name " + target.getName()));
        }
        return true;
    }
}