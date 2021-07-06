package com.firestar311.lib.util;

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
        
        StringBuilder sb = new StringBuilder();
        sb.append(Utils.color(completedColor));
        for (int i = 0; i < progressBars; i++) {
            sb.append(symbol);
        }
        
        sb.append(Utils.color(notCompletedColor));
        for (int i = 0; i < leftOver; i++) {
            sb.append(symbol);
        }
        
        return sb.toString();
    }
    
    public double getPercentage() {
        int percent = progress * 100 / 100;
        return Math.round(percent * 10.0);
    }
}