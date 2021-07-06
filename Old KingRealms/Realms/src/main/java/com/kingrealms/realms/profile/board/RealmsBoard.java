package com.kingrealms.realms.profile.board;

import com.starmediadev.lib.user.PlayerBoard;

public abstract class RealmsBoard extends PlayerBoard {
    public RealmsBoard(String name, String title) {
        super(name, title);
    }
    
    public abstract void updateLines();
}