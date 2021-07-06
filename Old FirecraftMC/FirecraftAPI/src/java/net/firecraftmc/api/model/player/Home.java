package net.firecraftmc.api.model.player;

import org.bukkit.Location;

import java.util.Objects;

/**
 * A class that represents a Home
 */
public class Home {
    private final String name;
    private final Location location;

    /**
     * Creates a new home based on a name and location
     * @param name The name of the home
     * @param location The location of the home
     */
    public Home(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    /**
     * @return The name of the home
     */
    public String getName() {
        return name;
    }

    /**
     * @return The location of the homes
     */
    public Location getLocation() {
        return location;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Home home = (Home) o;
        return Objects.equals(name, home.name);
    }

    public int hashCode() {
        return Objects.hash(name);
    }
}