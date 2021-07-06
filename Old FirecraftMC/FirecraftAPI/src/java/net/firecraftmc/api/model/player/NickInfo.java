package net.firecraftmc.api.model.player;

import net.firecraftmc.api.enums.Rank;

import java.io.Serializable;

/**
 * The class that contains the information related to the nickname.
 * There will be more use to this in the future when more features are added to nicknames (Like the ability to set a custom rank)
 */
public class NickInfo implements Serializable {
    public static final long serialVersionUID = 2L;

    private FirecraftPlayer nickProfile;
    private Rank rank;

    public NickInfo() {
    }

    /**
     * Creates a new NickInfo with the profile
     *
     * @param nickProfile The profile associated with the nickname
     */
    public NickInfo(FirecraftPlayer nickProfile) {
        this.nickProfile = nickProfile;
    }

    /**
     * @return The profile used for the nickname
     */
    public FirecraftPlayer getProfile() {
        return nickProfile;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }
}
