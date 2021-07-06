package com.starmediadev.com.common.enums;

public enum EnumGamePhase {
    //DEATH_ANNOUNCEMENTS(-1), DISUSSION(40), VOTING(30), DEFENSE(10), JUDGEMENT(15), LASTWORDS(10), NIGHT(30), WIN(40), NONE(0);

    DEATH_ANNOUNCEMENTS(-1), DISUSSION(5), VOTING(5), DEFENSE(5), JUDGEMENT(5), LASTWORDS(5), NIGHT(5), WIN(5), NONE(0);

    final int time;
    EnumGamePhase(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }
}
