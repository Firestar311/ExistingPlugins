package net.firecraftmc.api.command;

import net.firecraftmc.api.enums.Rank;
import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.paginator.Paginatable;

import java.util.*;

public abstract class FirecraftCommand implements Paginatable {
    protected final String name;
    protected final String description;
    protected List<Rank> ranks = new ArrayList<>(Collections.singletonList(Rank.FIRECRAFT_TEAM));
    protected List<String> aliases = new ArrayList<>();
    protected boolean respectsRecordMode = true;

    protected final List<FirecraftCommand> subCommands = new ArrayList<>();

    public FirecraftCommand(String name, String description, List<Rank> ranks, List<String> aliases) {
        this.name = name;
        this.description = description;
        this.ranks = ranks;
        this.aliases = aliases;
    }

    public abstract void executePlayer(FirecraftPlayer player, String[] args);
    //public abstract void executeConsole(ConsoleCommandSender sender, String[] args);

    public FirecraftCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Rank> getRanks() {
        return ranks;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public FirecraftCommand addRank(Rank rank) {
        this.ranks.add(rank);
        return this;
    }

    public FirecraftCommand addAlias(String alias) {
        this.aliases.add(alias);
        return this;
    }

    public void addRanks(Rank... ranks) {
        Collections.addAll(this.ranks, ranks);
    }

    public FirecraftCommand addAliases(String... aliases) {
        Collections.addAll(this.aliases, aliases);
        return this;
    }

    public boolean hasAlias(String alias) {
        return this.aliases.contains(alias.toLowerCase());
    }

    public boolean canUse(Rank rank) {
        if (this.ranks.isEmpty()) return true;
        return this.ranks.contains(rank);
    }

    public boolean canUse(FirecraftPlayer player) {
        if (this.ranks.isEmpty()) return true;
        return this.ranks.contains(player.getMainRank());
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FirecraftCommand that = (FirecraftCommand) o;
        return Objects.equals(name, that.name);
    }

    public int hashCode() {
        return Objects.hash(name);
    }

    public FirecraftCommand addSubcommand(FirecraftCommand subCmd) {
        this.subCommands.add(subCmd);
        return this;
    }

    public void removeSubcommand(FirecraftCommand subCmd) {
        this.subCommands.remove(subCmd);
    }

    public FirecraftCommand getSubCommand(String name) {
        for (FirecraftCommand command : subCommands) {
            if (command.getName().equalsIgnoreCase(name) || command.hasAlias(name)) {
                return command;
            }
        }
        return null;
    }

    public boolean respectsRecordMode() {
        return respectsRecordMode;
    }

    public FirecraftCommand setRespectsRecordMode(boolean respectsRecordMode) {
        this.respectsRecordMode = respectsRecordMode;
        return this;
    }
    
    public void executeSubCommand(String name, FirecraftPlayer player,  String[] args) {
        FirecraftCommand subCmd = getSubCommand(name);
        if (subCmd != null) {
            subCmd.executePlayer(player, args);
        } else {
            player.sendMessage("<ec>Invalid sub command: §f" + name);
        }
    }
    
    public FirecraftCommand setBaseRank(Rank rank) {
        ranks.clear();
        for (Rank r : Rank.values()) {
            if (r.isEqualToOrHigher(rank)) {
                this.ranks.add(r);
            }
        }
        return this;
    }
    
    public void removeRank(Rank rank) {
        this.ranks.remove(rank);
    }
    
    public void removeRanks(Rank... ranks) {
        this.ranks.removeAll(Arrays.asList(ranks));
    }
    
    public final String formatLine() {
        String format;
        format = this.aliases.isEmpty() ? "&a/" + name + " &7" + description : "&a/" + name + " (" + String.join("|", aliases.toArray(new String[0])) + ") &6 - &7" + description;
        return format;
    }
}