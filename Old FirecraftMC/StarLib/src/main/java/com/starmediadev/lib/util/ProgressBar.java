package com.starmediadev.lib.util;

public class ProgressBar {
    
    private final int max;
    private final int totalBars;
    private final String symbol;
    private final String completedColor;
    private final String notCompletedColor;
    private int progress;
    
    public ProgressBar(int progress, int max, int totalBars, String symbol, String completedColor, String notCompletedColor) {
        this.progress = progress;
        this.max = max;
        this.totalBars = totalBars;
        this.symbol = symbol;
        this.completedColor = completedColor;
        this.notCompletedColor = notCompletedColor;
    }
    
    public void setProgress(int completed) {
        this.progress = completed;
    }
    
    public String display() {
        float percent = (float) progress / max;
        int progressBars = (int) (totalBars * percent);
        int leftOver = (totalBars - progressBars);
    
        return Utils.color(completedColor) + String.valueOf(symbol).repeat(Math.max(0, progressBars)) + Utils.color(notCompletedColor) + String.valueOf(symbol).repeat(Math.max(0, leftOver));
    }
    
    public double getPercentage() {
        int percent = progress * 100 / 100;
        return Math.round(percent * 10.0);
    }
}