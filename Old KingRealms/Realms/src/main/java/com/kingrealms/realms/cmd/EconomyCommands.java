package com.kingrealms.realms.cmd;

import com.kingrealms.realms.economy.EconomyResponse;
import com.kingrealms.realms.economy.account.*;
import com.kingrealms.realms.economy.tickets.EcoTicket;
import com.kingrealms.realms.economy.transaction.Transaction;
import com.kingrealms.realms.economy.transaction.TransactionHandler;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.territory.base.Territory;
import com.starmediadev.lib.pagination.*;
import com.starmediadev.lib.util.Pair;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

import static com.starmediadev.lib.util.Constants.NUMBER_FORMAT;

public class EconomyCommands extends BaseCommand {
    
    private final Set<String> accountCache = new HashSet<>();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("economy")) {
            if (!sender.hasPermission("realms.economy.admin")) {
                sender.sendMessage(Utils.color("&cYou do not have enough permission to use that command."));
                return true;
            }
            
            if (!(args.length > 0)) {
                sender.sendMessage(Utils.color("&cYou must provide a sub command."));
                return true;
            }
            
            AccountHandler accountHandler = plugin.getEconomyManager().getAccountHandler();
            TransactionHandler transactionHandler = plugin.getEconomyManager().getTransactionHandler();
            RealmProfile profile = plugin.getProfileManager().getProfile(sender);
            
            if (Utils.checkCmdAliases(args, 0, "give", "take")) {
                if (!(args.length > 2)) {
                    sender.sendMessage(Utils.color("&cUsage: /economy " + args[0] + " <account|territory|player> <amount>"));
                    return true;
                }
                
                Account account = accountHandler.getAccount(args[1]);
                if (account == null) {
                    sender.sendMessage(Utils.color("&cYou provided an invalid account identifier."));
                    return true;
                }
                
                
                double amount;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Utils.color("&cYou provided an invalid number for the amount."));
                    return true;
                }
                String amountFormat = NUMBER_FORMAT.format(amount);
                
                String type, action;
                
                Pair<EconomyResponse, EconomyResponse> response = null;
                if (Utils.checkCmdAliases(args, 0, "give")) {
                    if (!profile.hasPermission("realms.economy.admin.give")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    if (sender instanceof ConsoleCommandSender) {
                        response = transactionHandler.deposit(amount, account, "Administratively given by " + sender.getName());
                    } else if (sender instanceof Player) {
                        response = transactionHandler.deposit(profile, amount, account, "Administratively given by " + sender.getName());
                    }
                    type = "given";
                    action = "to";
                } else if (Utils.checkCmdAliases(args, 0, "take")) {
                    if (!profile.hasPermission("realms.economy.admin.take")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    if (sender instanceof ConsoleCommandSender) {
                        response = transactionHandler.withdraw(amount, account, "Administratively taken by " + sender.getName());
                    } else if (sender instanceof Player) {
                        response = transactionHandler.withdraw(profile, amount, account, "Administratively taken by " + sender.getName());
                    }
                    type = "taken";
                    action = "from";
                } else {
                    sender.sendMessage(Utils.color("&cInvalid sub command."));
                    return true;
                }
                
                EconomyResponse fromResponse = response.getValue1(), toResponse = response.getValue2();
                
                if (fromResponse == EconomyResponse.SUCCESS && toResponse == EconomyResponse.SUCCESS) {
                    account.sendMessageToOwner("&j" + amountFormat + " &ihas been " + type + " " + action + " the account by &j" + sender.getName());
                    profile.sendMessage("&iYou have " + type + " &j" + amountFormat + " &i" + action + " the account &j" + account.getAccountNumber());
                } else {
                    if (toResponse == EconomyResponse.NOT_ENOUGH_FUNDS) {
                        profile.sendMessage("&cThere is not enough funds in the target account.");
                    }
                    
                    if (fromResponse == EconomyResponse.MAX_BALANCE_REACHED) {
                        profile.sendMessage(Utils.color("&cThe server account has reached it's maximum balance."));
                    }
                    return true;
                }
            } else if (Utils.checkCmdAliases(args, 0, "transfer")) {
                if (!profile.hasPermission("realms.economy.admin.transfer")) {
                    profile.sendMessage("&cYou do not have permission to use that command.");
                    return true;
                }
                
                if (!(args.length > 3)) {
                    sender.sendMessage(Utils.color("&cUsage: /economy " + args[0] + " <amount> <fromAccount> <toAccount>"));
                    return true;
                }
                
                double amount;
                try {
                    amount = Double.parseDouble(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Utils.color("&cYou provided an invalid number for the amount."));
                    return true;
                }
                
                String amountFormat = NUMBER_FORMAT.format(amount);
                
                Account fromAccount = accountHandler.getAccount(args[2]);
                Account toAccount = accountHandler.getAccount(args[3]);
                
                if (fromAccount == null) {
                    sender.sendMessage(Utils.color("&cYou provided an invalid account identifier for the from account."));
                }
                
                if (toAccount == null) {
                    sender.sendMessage(Utils.color("&cYou provided an invalid account identifier for the to account."));
                }
                
                if (fromAccount == null || toAccount == null) { return true; }
                
                Pair<EconomyResponse, EconomyResponse> response = null;
                if (sender instanceof ConsoleCommandSender) {
                    response = transactionHandler.transfer(amount, fromAccount, toAccount, "Administratively transfered by " + sender.getName());
                } else if (sender instanceof Player) {
                    response = transactionHandler.transfer(profile, amount, fromAccount, toAccount, "Administratively transfered by " + sender.getName());
                }
                
                EconomyResponse fromResponse = response.getValue1(), toResponse = response.getValue2();
                
                if (fromResponse == EconomyResponse.SUCCESS && toResponse == EconomyResponse.SUCCESS) {
                    toAccount.sendMessageToOwner("&j" + amountFormat + " &ihas been transfered to the account by &j" + sender.getName());
                    fromAccount.sendMessageToOwner("&j" + amountFormat + " &ihas been transfered from the account by &j" + sender.getName());
                } else {
                    if (toResponse == EconomyResponse.NOT_ENOUGH_FUNDS) {
                        profile.sendMessage("&cThere is not enough funds in the from account.");
                    }
                    
                    if (fromResponse == EconomyResponse.MAX_BALANCE_REACHED) {
                        profile.sendMessage(Utils.color("&cThe to account has reached it's maximum balance."));
                    }
                    return true;
                }
            } else if (Utils.checkCmdAliases(args, 0, "transactions", "trans", "transaction")) {
                if (!sender.hasPermission("realms.economy.transactions.manage")) {
                    sender.sendMessage(Utils.color("&cYou do not have permission to perform that command."));
                    return true;
                }
                
                if (!(args.length > 1)) {
                    sender.sendMessage(Utils.color("&cYou must provide a sub command."));
                    return true;
                }
                
                if (Utils.checkCmdAliases(args, 1, "list", "l")) {
                    if (args.length > 3) {
                        if (Utils.checkCmdAliases(args, 2, "account", "a")) {
                            Account account = accountHandler.getAccount(args[3]);
                            if (account == null) {
                                profile.sendMessage("&cYou provided an invalid account identifier.");
                                return true;
                            }
                            
                            Set<Transaction> transactions = new TreeSet<>(transactionHandler.getTransactions(account.getAccountNumber()));
                            Paginator<Transaction> paginator = PaginatorFactory.generatePaginator(7, transactions, new HashMap<>() {{
                                put(DefaultVariables.COMMAND, "economy transactions list account " + args[3]);
                                put(DefaultVariables.TYPE, "Transactions");
                            }});
                            
                            if (args.length > 4) {
                                paginator.display(sender, args[4]);
                            } else {
                                paginator.display(sender, 1);
                            }
                        }
                    } else {
                        Set<Transaction> transactions = new TreeSet<>(transactionHandler.getTransactions());
                        Paginator<Transaction> paginator = PaginatorFactory.generatePaginator(7, transactions, new HashMap<>() {{
                            put(DefaultVariables.COMMAND, "economy transactions list");
                            put(DefaultVariables.TYPE, "Transactions");
                        }});
                        
                        if (args.length > 2) {
                            paginator.display(sender, args[2]);
                        } else {
                            paginator.display(sender, 1);
                        }
                    }
                    return true;
                } else if (Utils.checkCmdAliases(args, 1, "removeall", "addall")) {
                    if (!(args.length > 2)) {
                        sender.sendMessage(Utils.color("&cYou must provide an account identifier"));
                        return true;
                    }
                    
                    Account account = accountHandler.getAccount(args[2]);
                    if (account == null) {
                        profile.sendMessage("&cYou provided an invalid account identifier.");
                        return true;
                    }
                    
                    Set<Transaction> transactions = transactionHandler.getTransactions(account.getAccountNumber());
                    int modified = 0;
                    for (Transaction transaction : transactions) {
                        if (Utils.checkCmdAliases(args, 1, "removeall")) {
                            if (!profile.hasPermission("realms.economy.transactions.manage.removeall")) {
                                profile.sendMessage("&cYou do not have permission to use that command.");
                                return true;
                            }
                            transaction.setRemovedInfo(System.currentTimeMillis(), profile.getUniqueId().toString());
                            modified++;
                        } else if (Utils.checkCmdAliases(args, 1, "addall")) {
                            if (!profile.hasPermission("realms.economy.transactions.manage.addall")) {
                                profile.sendMessage("&cYou do not have permission to use that command.");
                                return true;
                            }
                            if (!transaction.isRemoved()) {
                                transaction.setRemovedInfo(-1, null);
                                modified++;
                            }
                        }
                    }
                    
                    profile.sendMessage("&iModified &j" + modified + " &itransactions for the account &j" + account.getAccountNumber());
                    return true;
                }
                
                if (!(args.length > 2)) {
                    sender.sendMessage(Utils.color("&cYou must provide a transaction id"));
                    return true;
                }
                
                long id;
                try {
                    id = Long.parseLong(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(Utils.color("&cYou provided an invalid number."));
                    return true;
                }
                
                Transaction transaction = transactionHandler.getTransation(id);
                if (transaction == null) {
                    sender.sendMessage(Utils.color("&cInvalid transaction id."));
                    return true;
                }
                
                if (Utils.checkCmdAliases(args, 1, "view", "v")) {
                    if (!profile.hasPermission("realms.economy.transactions.manage.view")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    profile.sendMessage(Utils.color("&g====== &hInfo about " + transaction.getId() + " &g======"));
                    transaction.getDisplayMap().forEach((key, value) -> profile.sendMessage(Utils.color("&g - " + key + ": &r" + value)));
                } else if (Utils.checkCmdAliases(args, 1, "remove", "r")) {
                    if (!profile.hasPermission("realms.economy.transactions.manage.remove")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    transaction.setRemovedInfo(System.currentTimeMillis(), profile.getUniqueId().toString());
                    profile.sendMessage("&iYou have removed the transaction &j" + transaction.getId());
                } else if (Utils.checkCmdAliases(args, 1, "add", "a")) {
                    if (!profile.hasPermission("realms.economy.transactions.manage.add")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    if (!transaction.isRemoved()) {
                        profile.sendMessage("&cThis command is only used for removed transactions. Please use /economy give or /economy set commands");
                        return true;
                    }
                    
                    transaction.setRemovedInfo(-1, null);
                }
            } else if (Utils.checkCmdAliases(args, 0, "accounts", "account", "acct")) {
                if (!sender.hasPermission("realms.economy.accounts.manage")) {
                    sender.sendMessage(Utils.color("&cYou do not have permission to perform that command."));
                    return true;
                }
                
                if (!(args.length > 1)) {
                    profile.sendMessage("&cYou must provide a sub command.");
                    return true;
                }
                
                if (Utils.checkCmdAliases(args, 1, "list", "l")) {
                    if (!profile.hasPermission("realms.economy.accounts.manage.list")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    Set<Account> accounts = new TreeSet<>(accountHandler.getAccounts());
                    Paginator<Account> paginator = PaginatorFactory.generatePaginator(7, accounts, new HashMap<>() {{
                        put(DefaultVariables.COMMAND, "economy accounts list");
                        put(DefaultVariables.TYPE, "Accounts");
                    }});
                    
                    if (args.length > 2) {
                        paginator.display(sender, args[2]);
                    } else {
                        paginator.display(sender, 1);
                    }
                    return true;
                }
    
                if (!(args.length > 2)) {
                    sender.sendMessage(Utils.color("&cYou must provide an account identifier"));
                    return true;
                }
    
                Account account = accountHandler.getAccount(args[2]);
                if (account == null) {
                    sender.sendMessage(Utils.color("&cYou provided an invalid account identifier."));
                    return true;
                }
                
                if (Utils.checkCmdAliases(args, 1, "view", "v")) {
                    if (!profile.hasPermission("realms.economy.accounts.manage.view")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    
                    profile.sendMessage(Utils.color("&g====== &hInfo about " + account.getAccountNumber() + " &g======"));
                    account.getDisplayMap().forEach((key, value) -> profile.sendMessage(Utils.color("&g - " + key + ": &r" + value)));
                } else if (Utils.checkCmdAliases(args, 1, "setname", "sn")) {
                    if (!profile.hasPermission("realms.economy.accounts.manage.setname")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    
                    if (!(args.length > 3)) {
                        sender.sendMessage(Utils.color("&cYou must provide a new name"));
                        return true;
                    }
                    
                    String name = StringUtils.join(args, " ", 3, args.length);
                    account.setName(name);
                    profile.sendMessage("&iYou set the name of the account &j" + account.getAccountNumber() + " &ito &j" + name);
                } else if (Utils.checkCmdAliases(args, 1, "setdescription", "sd")) {
                    if (!profile.hasPermission("realms.economy.accounts.manage.setdescription")) {
                        profile.sendMessage("&cYou do not have permission to use that command.");
                        return true;
                    }
                    
                    if (!(args.length > 3)) {
                        sender.sendMessage(Utils.color("&cYou must provide a new name"));
                        return true;
                    }
                    
                    String description = StringUtils.join(args, " ", 3, args.length);
                    account.setDescription(description);
                    profile.sendMessage("&iYou set the description of the account &j" + account.getAccountNumber() + " &ito &j" + description);
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("balance")) {
            if (sender instanceof ConsoleCommandSender) {
                ServerAccount serverAccount = plugin.getEconomyManager().getAccountHandler().getMainServerAccount();
                sender.sendMessage(Utils.color("&eServer Balance: " + NUMBER_FORMAT.format(serverAccount.getBalance())));
                sender.sendMessage(Utils.color("&eInfinite: " + NUMBER_FORMAT.format(serverAccount.hasInfiniteBalance())));
                if (!serverAccount.hasInfiniteBalance()) {
                    sender.sendMessage(Utils.color("&eMax Balance: " + NUMBER_FORMAT.format(serverAccount.getMaxBalance())));
                }
                return true;
            }
            
            if (!(sender instanceof Player)) {
                return true;
            }
            
            RealmProfile profile = plugin.getProfileManager().getProfile(sender);
            profile.sendMessage("&gBalance: &h" + NUMBER_FORMAT.format(profile.getAccount().getBalance()) + " &gcoins.");
            Territory playerTerritory = plugin.getTerritoryManager().getTerritory(profile);
            if (playerTerritory != null) {
                profile.sendMessage("&gHamlet's Balance: &h" + NUMBER_FORMAT.format(playerTerritory.getAccount().getBalance()));
            }
        } else if (cmd.getName().equalsIgnoreCase("balancetop")) {
            Map<RealmProfile, Double> balances = new HashMap<>();
            double serverTotal = 0;
            for (RealmProfile profile : plugin.getProfileManager().getProfiles()) {
                double balance = profile.getAccount().getBalance();
                if (balance > 0) {
                    balances.put(profile, balance);
                    serverTotal += balance;
                }
            }
            
            List<Map.Entry<RealmProfile, Double>> sortedEntries = new ArrayList<>(balances.entrySet());
            sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
            
            String[] lines = new String[sortedEntries.size()];
            for (int i = 0; i < sortedEntries.size(); i++) {
                lines[i] = com.kingrealms.realms.util.Constants.PLAYER_BASE_COLOR + (i + 1) + ". &a" + sortedEntries.get(i).getKey().getName() + " &fhas &d" + NUMBER_FORMAT.format(sortedEntries.get(i).getValue()) + " coins";
            }
            
            Paginator<StringElement> linePaginator = PaginatorFactory.generateStringPaginator(7, Arrays.asList(lines), new HashMap<>() {{
                put(DefaultVariables.COMMAND, "balancetop");
                put(DefaultVariables.TYPE, "Top Players by Balance");
            }});
            
            RealmProfile profile = plugin.getProfileManager().getProfile(sender);
            profile.sendMessage(com.kingrealms.realms.util.Constants.PLAYER_BASE_COLOR + "Server Total: " + com.kingrealms.realms.util.Constants.PLAYER_VARIABLE_COLOR + serverTotal);
            if (args.length > 0) {
                linePaginator.display(sender, args[0]);
            } else {
                linePaginator.display(sender, 1);
            }
        } else if (cmd.getName().equalsIgnoreCase("pay")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.color("&cOnly players may use that command."));
                return true;
            }
            
            RealmProfile profile = plugin.getProfileManager().getProfile(sender);
            if (!(args.length > 1)) {
                profile.sendMessage("&cYou must provide a player and an amount.");
                return true;
            }
            
            RealmProfile target = plugin.getProfileManager().getProfile(args[0]);
            if (target == null) {
                profile.sendMessage("&cThe name you provided is not a valid player.");
                return true;
            }
            
            if (target.equals(profile)) {
                profile.sendMessage("&cYou cannot pay yourself.");
                return true;
            }
            
            if (!target.isOnline()) {
                profile.sendMessage("&cSorry but " + target.getName() + " is not online.");
                return true;
            }
            
            double amount;
            try {
                amount = Double.parseDouble(args[1]);
            } catch (NumberFormatException e) {
                profile.sendMessage("&cSorry but " + args[1] + " is not a valid number.");
                return true;
            }
            
            Pair<EconomyResponse, EconomyResponse> responses = plugin.getEconomyManager().getTransactionHandler().transfer(profile, amount, profile.getAccount(), target.getAccount(), profile.getName() + " paid " + target.getName());
            EconomyResponse fromResponse = responses.getValue1();
            EconomyResponse toResponse = responses.getValue2();
            
            if (fromResponse == EconomyResponse.SUCCESS && toResponse == EconomyResponse.SUCCESS) {
                profile.sendMessage("&gYou paid &h" + NUMBER_FORMAT.format(amount) + " coins &gto &h" + target.getName());
                target.sendMessage("&gYou recieved &h" + NUMBER_FORMAT.format(amount) + " coins &gfrom &h" + profile.getName());
            } else if (fromResponse == EconomyResponse.NOT_ENOUGH_FUNDS) {
                profile.sendMessage("&cYou have insufficient funds to pay " + target.getName() + " " + NUMBER_FORMAT.format(amount));
            } else {
                profile.sendMessage("&cUnknown error paying " + target.getName());
            }
        } else if (cmd.getName().equalsIgnoreCase("withdraw")) {
            if (!(args.length > 0)) {
                sender.sendMessage(Utils.color("&cYou must provide an amount."));
                return true;
            }
    
            RealmProfile profile = plugin.getProfileManager().getProfile(sender);
            
            double amount;
            try {
                amount = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                profile.sendMessage("&cYou did not provide a valid number.");
                return true;
            }
            
            if (profile.getAccount().getBalance() < amount) {
                profile.sendMessage("&cYou do not have enough coins to withdraw that amount.");
                return true;
            }
    
            EcoTicket ticket = plugin.getEconomyManager().getTicketHandler().createTicket(profile, amount);
            profile.getInventory().addItem(ticket.getItem());
            profile.sendMessage("&gWithdrew &h" + amount + " &gand gave you an Economy Ticket.");
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> results = new ArrayList<>(), possibleResults = new ArrayList<>();
        
        if (cmd.getName().equalsIgnoreCase("economy")) {
            if (args.length == 1) {
                possibleResults.addAll(Arrays.asList("give", "take", "transfer", "accounts", "transactions"));
                results.addAll(getResults(args[0], possibleResults));
            } else if (args.length == 2) {
                if (Utils.checkCmdAliases(args, 0, "give", "take")) {
                    possibleResults.addAll(getAccountList());
                } else if (Utils.checkCmdAliases(args, 0, "view")) {
                    possibleResults.addAll(Collections.singletonList("account"));
                } else if (Utils.checkCmdAliases(args, 0, "transactions", "trans", "transaction")) {
                    possibleResults.addAll(Arrays.asList("list", "view", "remove", "add", "removeall", "addall"));
                } else if (Utils.checkCmdAliases(args, 0, "accounts", "account", "acct")) {
                    possibleResults.addAll(Arrays.asList("list", "view", "setname", "setdescription"));
                }
                
                results.addAll(getResults(args[1], possibleResults));
            } else if (args.length == 3) {
                if (Utils.checkCmdAliases(args, 0, "transfer")) {
                    if (!StringUtils.isEmpty(args[1])) {
                        possibleResults.addAll(getAccountList());
                    }
                } else if (Utils.checkCmdAliases(args, 0, "view")) {
                    if (Utils.checkCmdAliases(args, 1, "account")) {
                        possibleResults.addAll(getAccountList());
                    }
                } else if (Utils.checkCmdAliases(args, 0, "transactions", "trans", "transaction")) {
                    if (Utils.checkCmdAliases(args, 1, "view", "v")) {
                        for (Transaction transaction : plugin.getEconomyManager().getTransactionHandler().getTransactions()) {
                            possibleResults.add(transaction.getId() + "");
                        }
                    } else if (Utils.checkCmdAliases(args, 1, "remove", "r")) {
                        for (Transaction transaction : plugin.getEconomyManager().getTransactionHandler().getTransactions()) {
                            if (!transaction.isRemoved()) {
                                possibleResults.add(transaction.getId() + "");
                            }
                        }
                    } else if (Utils.checkCmdAliases(args, 1, "add", "a")) {
                        for (Transaction transaction : plugin.getEconomyManager().getTransactionHandler().getTransactions()) {
                            if (transaction.isRemoved()) {
                                possibleResults.add(transaction.getId() + "");
                            }
                        }
                    } else if (Utils.checkCmdAliases(args, 1, "removeall", "addall")) {
                        possibleResults.addAll(getAccountList());
                    } else if (Utils.checkCmdAliases(args, 1, "list", "l")) {
                        possibleResults.addAll(Collections.singletonList("account"));
                        int amount = (int) Math.ceil(plugin.getEconomyManager().getAccountHandler().getAccounts().size() / 7.0);
                        for (int i = 1; i <= amount; i++) {
                            possibleResults.add(i + "");
                        }
                    }
                } else if (Utils.checkCmdAliases(args, 0, "accounts", "account", "acct")) {
                    if (Utils.checkCmdAliases(args, 1, "view", "v", "setname", "sn", "setdescription", "sd")) {
                        possibleResults.addAll(getAccountList());
                    }
                }
                
                results.addAll(getResults(args[2], possibleResults));
            } else if (args.length == 4) {
                if (Utils.checkCmdAliases(args, 0, "transfer")) {
                    if (!StringUtils.isEmpty(args[1])) {
                        if (!StringUtils.isEmpty(args[2])) {
                            possibleResults.addAll(getAccountList());
                        }
                    }
                } else if (Utils.checkCmdAliases(args, 0, "view")) {
                    if (Utils.checkCmdAliases(args, 1, "account")) {
                        if (!StringUtils.isEmpty(args[2])) {
                            possibleResults.addAll(Collections.singletonList("transactions"));
                        }
                    }
                } else if (Utils.checkCmdAliases(args, 0, "transactions", "trans", "transaction")) {
                    if (Utils.checkCmdAliases(args, 1, "list", "l")) {
                        if (Utils.checkCmdAliases(args, 2, "account", "a")) {
                            possibleResults.addAll(getAccountList());
                        }
                    }
                }
                
                results.addAll(getResults(args[3], possibleResults));
            }
        } else if (cmd.getName().equalsIgnoreCase("pay")) {
            if (args.length == 1) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    possibleResults.add(player.getName());
                }
                
                results.addAll(Utils.getResults(args[0], possibleResults));
            }
        }
        
        return results;
    }
    
    private Set<String> getAccountList() {
        int totalAccounts = plugin.getEconomyManager().getAccountHandler().getAccounts().size() + plugin.getProfileManager().getProfiles().size() + plugin.getTerritoryManager().getTerritories().size();
        if (accountCache.size() == totalAccounts) {
            return new HashSet<>(accountCache);
        }
        
        Set<String> accounts = new HashSet<>();
        for (Account account : plugin.getEconomyManager().getAccountHandler().getAccounts()) {
            accounts.add(account.getAccountNumber() + "");
        }
        
        for (RealmProfile profile : plugin.getProfileManager().getProfiles()) {
            accounts.add(profile.getName());
        }
        
        for (Territory territory : plugin.getTerritoryManager().getTerritories()) {
            accounts.add(territory.getName().toLowerCase().replace(" ", "_"));
        }
        
        this.accountCache.clear();
        this.accountCache.addAll(accounts);
        return accounts;
    }
}