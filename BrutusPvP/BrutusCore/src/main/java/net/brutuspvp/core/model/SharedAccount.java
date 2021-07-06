package net.brutuspvp.core.model;

import net.brutuspvp.core.enums.AccountPermission;
import net.brutuspvp.core.model.abstraction.Account;

import java.util.*;

public class SharedAccount extends Account {

    private String name;
    private HashMap<UUID, Set<AccountPermission>> members = new HashMap<>();
    private HashMap<UUID, Double> contributions = new HashMap<>();

    public SharedAccount(String name, UUID owner) {
        super(owner);
        this.name = name;
        members.put(owner, new HashSet<>(Arrays.asList(AccountPermission.values())));
    }

    public SharedAccount(UUID owner, double balance) {
        super(owner, balance);
    }

    public boolean deposit(UUID uuid, double amount) {
        return false;
    }

    public boolean withdraw(UUID uuid, double amount) {
        return false;
    }

    public Set<UUID> getMembers() {return members.keySet();}

    public boolean addMember(UUID adder, UUID memberToBe, Set<AccountPermission> permissions) {
        if (this.members.containsKey(adder) || this.owner.equals(adder)) {
            Set<AccountPermission> adderPerms = members.get(adder);
            if (adderPerms.contains(AccountPermission.ADD) || this.owner.equals(adder)) {
                this.members.put(memberToBe, permissions);
                return true;
            }
        }
        return false;
    }

    public boolean removeMember(UUID remover, UUID toRemove) {
        if (this.members.containsKey(remover) || this.owner.equals(remover)) {
            Set<AccountPermission> adderPerms = members.get(remover);
            if (adderPerms.contains(AccountPermission.REMOVE) || this.owner.equals(remover)) {
                this.members.remove(toRemove);
                return true;
            }
        }
        return false;
    }

    public boolean transferOwnership(UUID oldOwner, UUID newOwner) {
        if (oldOwner.equals(owner)) {
            this.owner = newOwner;
            this.members.put(oldOwner, new HashSet<>(Arrays.asList(AccountPermission.values())));
            this.members.put(newOwner, new HashSet<>(Arrays.asList(AccountPermission.values())));
            return true;
        }
        return false;
    }

    public double getContributions(UUID member) { return this.contributions.get(member); }

    public String getName() { return name; }

    public boolean isMember(UUID uuid) {
        return this.owner.equals(uuid) || this.members.containsKey(uuid);
    }
}