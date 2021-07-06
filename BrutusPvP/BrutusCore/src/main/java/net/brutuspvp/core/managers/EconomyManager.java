package net.brutuspvp.core.managers;

import com.firestar311.fireutils.classes.Utils;
import net.brutuspvp.core.BrutusCore;
import net.brutuspvp.core.Perms;
import net.brutuspvp.core.enums.AccountPermission;
import net.brutuspvp.core.model.PersonalAccount;
import net.brutuspvp.core.model.ServerAccount;
import net.brutuspvp.core.model.SharedAccount;
import net.brutuspvp.core.model.abstraction.Account;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.*;

public class EconomyManager implements CommandExecutor, Listener {

    private FileConfiguration config;

    private BrutusCore plugin;

    private HashMap<UUID, PersonalAccount> personalAccounts = new HashMap<>();
    private HashMap<String, SharedAccount> sharedAccounts = new HashMap<>();
    private HashMap<String, ServerAccount> serverAccounts = new HashMap<>();

    public EconomyManager(BrutusCore plugin) {
        plugin.registerListener(this);
        this.plugin = plugin;
        File file = Utils.createYamlFile(plugin, "economy");
        config = Utils.createYamlConfig(plugin, file, "accounts", "shops", "jobs", "logs");

        this.loadData();
    }

    public void saveData() {

    }

    public void loadData() {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!this.personalAccounts.containsKey(e.getPlayer().getUniqueId())) {
            this.personalAccounts.put(e.getPlayer().getUniqueId(), new PersonalAccount(e.getPlayer().getUniqueId()));
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage("§cAll economy features are in testing, they are only accessible to operators.");
            return true;
        }
        boolean isPlayer = false;
        if (sender instanceof Player) {
            isPlayer = true;
        }

        if (cmd.getName().equalsIgnoreCase("money")) {
            if (args.length == 0) {
                sender.sendMessage("§aList of all Economy Commands.");
                sender.sendMessage("§6---> /money pay: §7Pays another player.");
                sender.sendMessage("§6---> /money tranfer: §7Allows transferring between accounts.");
                sender.sendMessage("§6---> /money withdraw: §7Withdraws money from an account and gives a bank note.");
                sender.sendMessage("§6---> /money set: §cAdmin command: Sets a players balance.");
                sender.sendMessage("§6---> /money take: §cAdmin Command: Takes money from a player.");
                sender.sendMessage("§6---> /money give: §cAdmin Command: Gives money to a player.");
            } else if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("pay")) {
                    if (!isPlayer) {
                        sender.sendMessage("§cYou must be a player to pay someone.");
                        return true;
                    } else {
                        Player player = (Player) sender;
                        if (!(args.length > 1)) {
                            player.sendMessage("§cYou did not provide enough arguments.");
                            return true;
                        }
                        Player target = null;
                        double amount = 0;
                        Account account = null;

                        for (String a : args) {
                            if (a.startsWith("target:") || a.startsWith("player:")) {
                                String name = a.split(":")[1];
                                target = Bukkit.getPlayer(name);
                            } else if (a.startsWith("amount:")) {
                                String am = a.split(":")[1];
                                try {
                                    amount = Double.parseDouble(am);
                                } catch (NumberFormatException e) {
                                    player.sendMessage("§cThat is not a valid number.");
                                    return true;
                                }
                            } else if (a.startsWith("account:") || a.startsWith("acc:")) {
                                String acc = a.split(":")[1];
                                if (acc.equalsIgnoreCase("personal")) {
                                    account = personalAccounts.get(player.getUniqueId());
                                } else {
                                    account = sharedAccounts.get(acc);
                                }
                            }
                        }

                        if (target == null) {
                            player.sendMessage("§cThe name you provided is invalid, they are not online.");
                            return true;
                        }

                        if (amount > 0.01) {
                            player.sendMessage("§cYou cannot pay less then the amount 0.01.");
                            return true;
                        }

                        if (account == null) {
                            account = personalAccounts.get(player.getUniqueId());
                        }

                        Account targetAccount = personalAccounts.get(target.getUniqueId());

                        if (account.transfer(player.getUniqueId(), targetAccount, amount)) {
                            if (account instanceof PersonalAccount) {
                                player.sendMessage("§aYou have paid §b" + amount + " §ato §b" + target.getName() + " §afrom your personal account.");
                                target.sendMessage("§aYou have been paid §b" + amount + " §afrom §b" + player.getName());
                            } else if (account instanceof SharedAccount) {
                                SharedAccount sharedAccount = (SharedAccount) account;
                                player.sendMessage("§aYou have paid §b" + amount + " §ato §b" + target.getName() + " §afrom the shared account §a" + sharedAccount.getName());
                                target.sendMessage("§aYou have been paid §b" + amount + " §afrom §b" + player.getName());
                            } else {
                                return true;
                            }
                        } else {
                            player.sendMessage("§cThere was an error paying that player.");
                            return true;
                        }
                    }
                } else if (args[0].equalsIgnoreCase("transfer")) {
                    if (!isPlayer) {
                        sender.sendMessage("§cOnly players may transfer between accounts.");
                        return true;
                    }

                    Player player = (Player) sender;
                    double amount = 0;
                    Account fromAccount = null;
                    Account toAccount = null;

                    if (!(args.length > 3)) {
                        player.sendMessage("§cYou did not provide enough arguments.");
                        return true;
                    }

                    for (String arg : args) {
                        if (arg.startsWith("amount:")) {
                            try {
                                amount = Double.parseDouble(arg);
                            } catch (NumberFormatException e) {
                                player.sendMessage("§cThe value you provided for the amount is invalid.");
                                return true;
                            }
                        }
                        if (arg.startsWith("from:")) {
                            Account account;
                            String acc = arg.split(":")[1];
                            switch (acc) {
                                case "personal":
                                    account = personalAccounts.get(player.getUniqueId());
                                    break;
                                default:
                                    account = sharedAccounts.get(acc);
                                    break;
                            }

                            if (account == null) {
                                player.sendMessage("§cThere was an error in retrieving the from account.");
                                return true;
                            }

                            fromAccount = account;
                        }
                        if (arg.startsWith("to:")) {
                            Account account;
                            String acc = arg.split(":")[1];
                            switch (acc) {
                                case "personal":
                                    account = personalAccounts.get(player.getUniqueId());
                                    break;
                                default:
                                    account = sharedAccounts.get(acc);
                                    break;
                            }

                            if (account == null) {
                                player.sendMessage("§cThere was an error in retrieving the to account.");
                                return true;
                            }
                            toAccount = account;
                        }
                    }

                    if (fromAccount.transfer(player.getUniqueId(), toAccount, amount)) {
                        if (fromAccount instanceof PersonalAccount) {
                            player.sendMessage("§aTransfered §b$" + amount + " §afrom your personal account the shared account §b" + toAccount.getName());
                            return true;
                        } else if (fromAccount instanceof SharedAccount) {
                            player.sendMessage("§aTransfered §b$" + amount + " §ato your personal account from the shared account " + fromAccount.getName());
                            return true;
                        }
                    } else {
                        player.sendMessage("§cThere was an error transfering between the accounts.");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("withdraw")) {
                    //TODO Implement this after custom items for banknotes
                    sender.sendMessage("§cThis feature is not yet implemented.");
                    return true;
                } else if (args[0].equalsIgnoreCase("set")) {
                    if (!sender.hasPermission(Perms.ECON_MONEY_SET)) {
                        sender.sendMessage("§cYou do not have permission to use that command.");
                        return true;
                    }
                    if (args.length == 3) {
                        OfflinePlayer of = null;
                        Account account = null;
                        try {
                            of = Bukkit.getOfflinePlayer(plugin.players().getUUID(args[1]));
                        } catch (NullPointerException e) {
                            // Might do something with this.
                        }

                        if (of != null) {
                            account = personalAccounts.get(of.getUniqueId());
                        }

                        if (account == null) {
                            account = sharedAccounts.get(args[1]);
                            if (account == null) {
                                account = serverAccounts.get(args[1]);
                            }
                        }

                        if (account == null) {
                            sender.sendMessage("§cCould not get an account using the provided player/name.");
                            return true;
                        }

                        double amount;
                        try {
                            amount = Double.parseDouble(args[2]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("The amount you provided is not a valid number.");
                            return true;
                        }

                        account.setBalance(amount);
                        if (account instanceof PersonalAccount) {
                            sender.sendMessage("§aSuccessfully set the balance of §b" + of.getName() + "§a's account to §b" + amount);
                            return true;
                        } else if (account instanceof SharedAccount) {
                            sender.sendMessage("§aSuccessfully set the balance of the shared account §b" + account.getName() + " §ato §b" + amount);
                            return true;
                        } else if (account instanceof ServerAccount) {
                            sender.sendMessage("§aSuccessfully set the balance of the server account §b" + account.getName() + " §ato §b" + amount);
                            return true;
                        }
                    } else {
                        sender.sendMessage("Invalid amount of arguments: /money pay <account> <amount>");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("take")) {
                    if (!sender.hasPermission(Perms.ECON_MONEY_TAKE)) {
                        sender.sendMessage("§cYou do not have permission to use that command.");
                        return true;
                    }
                    if (args.length == 3) {
                        OfflinePlayer of = null;
                        Account account = null;
                        try {
                            of = Bukkit.getOfflinePlayer(plugin.players().getUUID(args[1]));
                        } catch (NullPointerException e) {
                            // Might do something with this.
                        }

                        if (of != null) {
                            account = personalAccounts.get(of.getUniqueId());
                        }

                        if (account == null) {
                            account = sharedAccounts.get(args[1]);
                            if (account == null) {
                                account = serverAccounts.get(args[1]);
                            }
                        }

                        if (account == null) {
                            sender.sendMessage("§cCould not get an account using the provided player/name.");
                            return true;
                        }

                        double amount;
                        try {
                            amount = Double.parseDouble(args[2]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("The amount you provided is not a valid number.");
                            return true;
                        }

                        account.setBalance((account.getBalance() - amount));
                        if (account instanceof PersonalAccount) {
                            sender.sendMessage("§aSuccessfully removed §b" + amount + " §afrom §b" + of.getName() + "§a's personal account.");
                            return true;
                        } else if (account instanceof SharedAccount) {
                            sender.sendMessage("§aSuccessfully removed §b" + amount + " §afrom the shared account §b" + account.getName());
                            return true;
                        } else if (account instanceof ServerAccount) {
                            sender.sendMessage("§aSuccessfully removed §b" + amount + " §afrom the server account §b" + account.getName());
                            return true;
                        }
                    } else {
                        sender.sendMessage("Invalid amount of arguments: /money take <account> <amount>");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("give")) {
                    if (!sender.hasPermission(Perms.ECON_MONEY_GIVE)) {
                        sender.sendMessage("§cYou do not have permission to use that command.");
                        return true;
                    }
                    if (args.length == 3) {
                        OfflinePlayer of = null;
                        Account account = null;
                        try {
                            of = Bukkit.getOfflinePlayer(plugin.players().getUUID(args[1]));
                        } catch (NullPointerException e) {
                            // Might do something with this.
                        }

                        if (of != null) {
                            account = personalAccounts.get(of.getUniqueId());
                        }

                        if (account == null) {
                            account = sharedAccounts.get(args[1]);
                            if (account == null) {
                                account = serverAccounts.get(args[1]);
                            }
                        }

                        if (account == null) {
                            sender.sendMessage("§cCould not get an account using the provided player/name.");
                            return true;
                        }

                        double amount;
                        try {
                            amount = Double.parseDouble(args[2]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage("The amount you provided is not a valid number.");
                            return true;
                        }

                        account.setBalance((account.getBalance() + amount));
                        if (account instanceof PersonalAccount) {
                            sender.sendMessage("§aSuccessfully set the balance of §b" + of.getName() + "§a's account to §b" + amount);
                            return true;
                        } else if (account instanceof SharedAccount) {
                            sender.sendMessage("§aSuccessfully set the balance of the shared account §b" + account.getName());
                            return true;
                        } else if (account instanceof ServerAccount) {
                            sender.sendMessage("§aSuccessfully set the balance of the server account §b" + account.getName());
                            return true;
                        }
                    } else {
                        sender.sendMessage("Invalid amount of arguments: /money give <account> <amount>");
                        return true;
                    }
                } else {
                    sender.sendMessage("§cInvalid subcommand " + args[0]);
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("balance")) {
            if (args.length == 0) {
                if (isPlayer) {
                    Player player = (Player) sender;
                    PersonalAccount personalAccount = personalAccounts.get(player.getUniqueId());
                    ArrayList<SharedAccount> sAccounts = new ArrayList<>();
                    for (SharedAccount sharedAccount : sharedAccounts.values()) {
                        if (sharedAccount.isMember(player.getUniqueId())) {
                            sAccounts.add(sharedAccount);
                        }
                    }

                    player.sendMessage("§2---------Balance of accounts---------");
                    player.sendMessage("§aPersonal Account: $§e" + personalAccount.getBalance());
                    if (!sAccounts.isEmpty()) {
                        player.sendMessage("§2-----Balance of shared accounts-----");
                        for (SharedAccount acc : sAccounts) {
                            player.sendMessage("§9SharedAccount " + acc.getName() + ": $§e" + acc.getBalance());
                        }
                    }
                } else {
                    if (sender instanceof ConsoleCommandSender) {
                        sender.sendMessage("-----Balance of server accounts-----");
                        for (ServerAccount serverAccount : serverAccounts.values()) {
                            sender.sendMessage("ServerAccount " + serverAccount.getName() + ": $" + serverAccount.getBalance());
                        }
                    }
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("server")) {
                    sender.sendMessage("-----Balance of server accounts-----");
                    for (ServerAccount serverAccount : serverAccounts.values()) {
                        sender.sendMessage("ServerAccount " + serverAccount.getName() + ": $" + serverAccount.getBalance());
                    }
                } else {
                    if (!sender.hasPermission(Perms.ECON_BALANCE_OTHERS)) {
                        sender.sendMessage("§cYou do not have permission to use that command.");
                        return true;
                    }
                    OfflinePlayer offlinePlayer;
                    try {
                        offlinePlayer = Bukkit.getOfflinePlayer(plugin.players().getUUID(args[0]));
                    } catch (NullPointerException e) {
                        sender.sendMessage("§cThe name you provided is not a valid player.");
                        return true;
                    }

                    if (offlinePlayer == null) {
                        sender.sendMessage("§cThe name you provided is not a valid player.");
                        return true;
                    }

                    PersonalAccount personalAccount = personalAccounts.get(offlinePlayer.getUniqueId());
                    ArrayList<SharedAccount> sAccounts = new ArrayList<>();
                    for (SharedAccount sharedAccount : sharedAccounts.values()) {
                        if (sharedAccount.isMember(offlinePlayer.getUniqueId())) {
                            sAccounts.add(sharedAccount);
                        }
                    }

                    sender.sendMessage("§2---------Balance of accounts---------");
                    sender.sendMessage("§aPersonal Account: $§e" + personalAccount.getBalance());
                    if (!sAccounts.isEmpty()) {
                        sender.sendMessage("§2-----Balance of shared accounts-----");
                        for (SharedAccount acc : sAccounts) {
                            sender.sendMessage("§9SharedAccount " + acc.getName() + ": $§e" + acc.getBalance());
                        }
                    }
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("sharedaccount")) {
            if (isPlayer) {
                Player player = (Player) sender;
                if (args.length > 0) {
                    if (Utils.checkArguments(args, 0, "create", "c")) {
                        if (args.length > 0) {
                            this.sharedAccounts.put(args[1], new SharedAccount(args[1], player.getUniqueId()));
                            player.sendMessage("§aSuccessfully created a shared account with the name §b" + args[1]);
                            return true;
                        } else {
                            player.sendMessage("§cYou must provide a name for the shared account.");
                            return true;
                        }
                    } else if (Utils.checkArguments(args, 0, "add", "a")) {
                        if (args.length == 4) {
                            SharedAccount sharedAccount = sharedAccounts.get(args[1]);
                            if (sharedAccount == null) {
                                player.sendMessage("§cThe account name you provided is not valid.");
                                return true;
                            }

                            String[] rawPlayers = args[2].split(",");
                            String[] rawPermissions = args[3].toUpperCase().split(",");

                            Set<AccountPermission> permissions = new HashSet<>();
                            for (String p : rawPermissions) {
                                try {
                                    AccountPermission permission = AccountPermission.valueOf(p);
                                    permissions.add(permission);
                                } catch (Exception e) {
                                    player.sendMessage("§cOne or more of the permissions you provided is invalid. Available permissions are DEPOSIT, WITHDRAW, ADD, REMOVE");
                                    return true;
                                }
                            }

                            ArrayList<UUID> members = new ArrayList<>();
                            for (String p : rawPlayers) {
                                UUID uuid = plugin.players().getUUID(p);
                                if (uuid == null) {
                                    player.sendMessage("§cThe player §b" + p + " §chas not played on this server or is invalid.");
                                    continue;
                                }
                                members.add(uuid);
                            }
                            for (UUID uuid : members) {
                                if (sharedAccount.addMember(player.getUniqueId(), uuid, permissions)) {
                                    player.sendMessage("§aSuccessfully added §b" + Bukkit.getOfflinePlayer(uuid).getName() + " §ato your shared account.");
                                } else {
                                    player.sendMessage("§cThere was an error with adding that person as a member. Do you have permission?");
                                }
                            }
                        } else {
                            player.sendMessage("§cInvalid amount of arguments: /sharedaccount add <players> <permissions>");
                            return true;
                        }
                    } else if (Utils.checkArguments(args, 0, "remove", "r")) {
                        // /sharedaccount remove <account> <player>
                        if (args.length == 3) {
                            SharedAccount sharedAccount = sharedAccounts.get(args[1]);
                            if (sharedAccount == null) {
                                player.sendMessage("§cThe account name provided is not valid.");
                                return true;
                            }

                            UUID uuid = plugin.players().getUUID(args[2]);
                            if (!sharedAccount.removeMember(player.getUniqueId(), uuid)) {
                                player.sendMessage("§cCould not remove that player from the shared account.");
                                return true;
                            } else {
                                player.sendMessage("§aSuccessfully removed §b" + args[2] + " §ato the shared account §b" + sharedAccount.getName());
                                return true;
                            }
                        } else {
                            player.sendMessage("§cInvalid amount of arguments: /sharedaccount remove <account> <player>");
                            return true;
                        }
                    } else if (Utils.checkArguments(args, 0, "edit", "s")) {
                        player.sendMessage("§cThis sub-command is a work in progress.");
                        return true;
                    }
                } else {
                    player.sendMessage("§cYou must provide a subcommand.");
                    return true;
                }
            } else {
                sender.sendMessage("§cOnly players may use this subcommand.");
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("serveraccount")) {
            sender.sendMessage("§cThis command is a work in progress.");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("balancetop")) {
            sender.sendMessage("§cThis command is a work in progress.");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("shops")) {
            sender.sendMessage("§cThis command is a work in progress.");
            return true;
        }
        return true;
    }
}