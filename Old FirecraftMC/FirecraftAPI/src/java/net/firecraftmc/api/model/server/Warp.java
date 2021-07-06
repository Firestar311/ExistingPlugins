package net.firecraftmc.api.model.server;

import net.firecraftmc.api.enums.Rank;
import org.bukkit.Location;

import java.util.Objects;

/**
 * Class that represents a Warp
 */
public class Warp {
    private final String name;
    private final Location location;
    private final Rank minimumRank;

    /**
     * Creates a warp for players to teleport to if they meet the minimum rank provided
     * @param name The warp name
     * @param location The warp location
     * @param minimumRank The minimum rank for the warp
     */
    public Warp(String name, Location location, Rank minimumRank) {
        this.name = name;
        this.location = location;
        this.minimumRank = minimumRank;
    }

    /**
     * Creates a warp with a default minimum rank (Private)
     * @param name The name of the warp
     * @param location The location of the warp
     */
    public Warp(String name, Location location) {
        this.name = name;
        this.location = location;
        this.minimumRank = Rank.DEFAULT;
    }

    /**
     * @return The warp name
     */
    public String getName() {
        return name;
    }

    /**
     * @return The warp location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @return The minimum rank required to teleport to the warp
     */
    public Rank getMinimumRank() {
        return minimumRank;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Warp warp = (Warp) o;
        return Objects.equals(name, warp.name);
    }

    public int hashCode() {
        return Objects.hash(name);
    }
}