package com.stardevmc.titangames.util;

import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.arenas.chests.ChestTier;
import com.stardevmc.titangames.kits.Kit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public final class Messages {
    
    private static File file;
    private static FileConfiguration config;
    private Messages() {}
    
    public static void prepare() {
        file = new File(TitanGames.getInstance().getDataFolder(), "messages.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        config = YamlConfiguration.loadConfiguration(file);
        
        if (!config.contains("messages")) {
            config.set("messages.leave_arena", "&c{player} has left the arena");
            config.set("messages.killed_by_player", "&c{player} was killed by {killer} with {item}.");
            config.set("messages.killed_by_leaving", "&c{player} was killed by the arena wall.");
            config.set("messages.force_start_arena", "&b{player} &ehas force &astarted &eyour arena");
            config.set("messages.force_stop_arena", "&b{player} &ehas force &cstopped &eyour arena");
            config.set("messages.kit_selector_title", "&2Kit Selector");
            config.set("messages.join_arena", "&a{player} &ejoined the game. &a(&b{tributes}&a/&b{maxtributes}&a)");
            config.set("messages.win_arena", "&6&l{player} has won then game in {arena}");
            config.set("messages.arena_end", "&6&lArena {arena} has ended with no winner.");
            config.set("messages.game_start", "&aGame starting in &e{time} &asecond(s)!");
            config.set("messages.chest_add", "&aYou added the chest you are looking at to the arena &b{arena}");
            config.set("messages.create_chest_tier", "&aYou added the chest tier &b{value}, now you need to add items to it.");
            config.set("messages.add_item", "&aYou added the item in your hand to the chest tier &b{tier} &awith the chance &b{chance}");
            config.set("messages.invalid_arena_name", "&cThe name you provided for the arena does not match a valid arena.");
            config.set("messages.add_lobby_sign", "&aYou added the sign you are looking at to the arena &b{arena}");
            config.set("messages.add_spawn", "&aYou added the location you are currently at as a spawn for the arena &b{arena}");
            config.set("messages.create_arena", "&aYou created an arena with the name &b{arena}");
            config.set("messages.create_kit", "&aYou created a kit with the name &b{name}");
            config.set("messages.force_start_command", "&bYou forcefully &astarted &ethe arena &e{arena}");
            config.set("messages.force_stop_command", "&bYou forcefully &cstopped &ethe arena &e{arena}");
            config.set("messages.join_arena_command", "&bYou &ajoined &bthe arena &e{arena}");
            config.set("messages.leave_arena_command" , "&bYou &cleft &bthe arena &e{arena}");
            config.set("messages.remove_arena_command", "&aYou removed the arena &e{arena}");
            config.set("messages.remove_chest", "&aYou removed the chest you are looking at from &b{arena}");
            config.set("messages.remove_kit", "&aYou removed the kit &b{kit}");
            config.set("messages.remove_lobby_sign", "&aYou removed the lobby sign at &e({location})");
            config.set("messages.remove_spawn", "&aYou removed the spawn with the id &e{id} &bfrom &e{arena}");
            config.set("messages.change_sign_arena", "&aYou changed the arena of which that sign is associated with to &b{arena}");
            config.set("messages.kits_given", "&aAll selected kits have been given to the tributes.");
            config.set("messages.kits_countdown", "&aKits will be given in &e{time} second(s).");
            config.set("messages.kit_select", "&aYou have selected the kit &e{kit}");
            config.set("messages.create_lobby_sign_action", "&aYou created a lobby sign for &e{arena}");
        }
    
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String CREATE_LOBBY_SIGN_ACTION(Arena arena) {
        return config.getString("messages.create_lobby_sign_action").replace("{arena}", arena.getName());
    }
    
    public static String KIT_SELECT(Kit kit) {
        return config.getString("messages.kit_select").replace("{kit}", kit.getName());
    }
    
    public static String KITS_COUNTDOWN(int time) {
        return config.getString("messages.kits_countdown").replace("{time}", time + "");
    }
    
    public static String KITS_GIVEN() {
        return config.getString("messages.kits_given");
    }
    
    public static String CHANGE_SIGN_ARENA(Arena arena) {
        return config.getString("messages.change_sign_arena").replace("{arena}", arena.getName());
    }
    
    public static String REMOVE_SPAWN(int id, Arena arena) {
        return config.getString("messages.remove_spawn").replace("{id}", id + "").replace("{arena}", arena.getName());
    }
    
    public static String REMOVE_LOBBY_SIGN(Location loc) {
        return config.getString("messages.remove_lobby_sign").replace("{location}", loc.getX() + "," + loc.getY() + "," + loc.getZ());
    }
    
    public static String REMOVE_KIT(Kit kit) {
        return config.getString("messages.remove_kit").replace("{kit}", kit.getName());
    }
    
    public static String REMOVE_CHEST(Arena arena) {
        return config.getString("messages.remove_chest").replace("{chest}", arena.getName());
    }
    
    public static String REMOVE_ARENA(Arena arena) {
        return config.getString("messages.remove_arena_command").replace("{arena}", arena.getName());
    }
    
    public static String LEAVE_ARENA_COMMAND(Arena arena) {
        return config.getString("messages.leave_arena_command").replace("{arena}", arena.getName());
    }
    
    public static String JOIN_ARENA_COMMAND(Arena arena) {
        return config.getString("messages.join_arena_command").replace("{arena}", arena.getName());
    }
    
    public static String FORCE_STOP_COMMAND(Arena arena) {
        return config.getString("messages.force_stop_command").replace("{arena}", arena.getName());
    }
    
    public static String FORCE_START_COMMAND(Arena arena) {
        return config.getString("messages.force_start_command").replace("{arena}", arena.getName());
    }
    
    public static String CREATE_KIT(Kit kit) {
        return config.getString("messages.create_kit").replace("{name}", kit.getName());
    }
    
    public static String CREATE_ARENA(Arena arena) {
        return config.getString("messages.create_arena").replace("{arena}", arena.getName());
    }
    
    public static String ADD_SPAWN(Arena arena) {
        return config.getString("messages.add_spawn").replace("{arena}", arena.getName());
    }
    
    public static String LEAVE_ARENA(String player) {
        return config.getString("messages.leave_message").replace("{player}", player);
    }
    
    public static String KILLED_BY_PLAYER(Player killer, Player target, String item) {
        return config.getString("messages.killed_by_player").replace("{player}", target.getName())
                .replace("{killer}", killer.getName()).replace("{item}", item);
    }
    
    public static String KILLED_BY_LEAVING(Player player) {
        return config.getString("messages.killed_by_leaving").replace("{player}", player.getName());
    }
    
    public static String FORCE_START_ARENA(Player player) {
        return config.getString("messages.force_start_arena").replace("{player}", player.getName());
    }
    
    public static String FORCE_STOP_ARENA(Player player) {
        return config.getString("messages.force_stop_arena").replace("{player}", player.getName());
    }
    
    public static String KIT_SELECTOR_TITLE() {
        return config.getString("messages.kit_selector_title");
    }
    
    public static String JOIN_ARENA(Player player, Arena arena) {
        return config.getString("messages.join_arena").replace("{player}", player.getName())
                .replace("{tributes}", arena.getTributes().size() + "").replace("{maxtributes}", arena.getSpawnpoints().size() + "");
    }
    
    public static String WIN_ARENA(Player player, Arena arena) {
        return config.getString("messages.win_arena").replace("{player}", player.getName()).replace("{arena}", arena.getName());
    }
    
    public static String ARENA_END(Arena arena) {
        return config.getString("messages.arena_end").replace("{arena}", arena.getName());
    }
    
    public static String GAME_STARTING(int time) {
        return config.getString("messages.game_start").replace("{time}", time + "");
    }
    
    public static String CHEST_ADD(Arena arena) {
        return config.getString("messages.chest_add", arena.getName());
    }
    
    public static String CREATE_CHEST_TIER(ChestTier chestTier) {
        return config.getString("messages.create_chest_tier").replace("{value}", chestTier.getNumber() + "");
    }
    
    public static String ADD_ITEM(ChestTier tier, int chance) {
        return config.getString("messages.add_item").replace("{tier}", tier.getNumber() + "").replace("{chance}", chance + "");
    }
    
    public static String INVALID_ARENA_NAME() {
        return config.getString("messages.invalid_arena_name");
    }
    
    public static String ADD_LOBBY_SIGN(Arena arena) {
        return config.getString("messages.add_lobby_sign").replace("{arena}", arena.getName());
    }
}