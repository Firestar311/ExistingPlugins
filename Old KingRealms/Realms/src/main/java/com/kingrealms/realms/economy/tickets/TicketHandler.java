package com.kingrealms.realms.economy.tickets;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.economy.transaction.Transaction;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.Code;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TicketHandler implements Listener {
    
    private final Map<String, EcoTicket> tickets = new HashMap<>();
    
    public TicketHandler() {
        Realms.getInstance().getServer().getPluginManager().registerEvents(this, Realms.getInstance());
    }
    
    public void saveData(ConfigurationSection section) {
        for (EcoTicket ticket : getTickets()) {
            section.set(ticket.getId(), ticket);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR) {
            if (e.getPlayer().getInventory().getItemInMainHand() != null) {
                EcoTicket ecoTicket = getEcoTicket(e.getPlayer().getInventory().getItemInMainHand());
                if (ecoTicket == null) return;
                if (ecoTicket.hasBeenUsed()) {
                    e.getPlayer().sendMessage(Utils.color("&cThat ticket has already been used."));
                    return;
                }
                
                e.setCancelled(true);
                
                RealmProfile profile = RealmsAPI.getProfile(e.getPlayer());
                if (ecoTicket.getTransaction().getFromAccount() == profile.getAccount().getAccountNumber()) {
                    profile.sendMessage("&cYou cannot claim an eco ticket you created.");
                    return;
                }
                ecoTicket.getTransaction().setToAccount(profile.getAccount().getAccountNumber());
                ecoTicket.useTicket(System.currentTimeMillis(), profile.getAccount().getAccountNumber(), profile.getUniqueId());
                profile.sendMessage("&gYou have claimed an eco ticket worth &h" + ecoTicket.getAmount());
                e.getPlayer().getInventory().removeItem(e.getPlayer().getInventory().getItemInMainHand());
                new BukkitRunnable() {
                    public void run() {
                        profile.getAccount().sendMessageToOwner("&8[&a■&8] &a+ &7" + ecoTicket.getAmount() + " &bcoins");
                    }
                }.runTaskLater(Realms.getInstance(), 1L);
            }
        }
    }
    
    public EcoTicket getEcoTicket(ItemStack itemStack) {
        try {
            String id = NBTWrapper.getNBTString(itemStack, "ecoticketid");
            if (StringUtils.isEmpty(id)) return null;
            return this.tickets.get(id);    
        } catch (Exception e) {}
        return null;
    }
    
    public EcoTicket createTicket(RealmProfile profile, double amount) {
        EcoTicket ecoTicket = new EcoTicket(profile.getAccount().getAccountNumber(), System.currentTimeMillis(), amount, profile.getUniqueId());
        String id;
        do {
            id = Code.generateNewCode(12, true);
        } while (tickets.containsKey(id));
        ecoTicket.setId(id);
        tickets.put(id, ecoTicket);
        Transaction transaction = new Transaction(ecoTicket.getCreationDate(), ecoTicket.getAmount(), ecoTicket.getWithdrawAccount(), -2, "Eco Ticket " + id);
        Realms.getInstance().getEconomyManager().getTransactionHandler().addTransactionWithMsg(transaction);
        ecoTicket.setTransaction(transaction);
        return ecoTicket;
    }
    
    public Set<EcoTicket> getTickets() {
        return new HashSet<>(tickets.values());
    }
    
    public void clearTickets() {
        this.tickets.clear();
    }
    
    public void loadData(ConfigurationSection section) {
        if (section != null) {
            for (String t : section.getKeys(false)) {
                EcoTicket ticket = (EcoTicket) section.get(t);
                this.tickets.put(ticket.getId(), ticket);
            }
        }
    }
}