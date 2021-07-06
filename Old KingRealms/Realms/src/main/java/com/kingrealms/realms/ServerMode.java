package com.kingrealms.realms;

import java.util.concurrent.TimeUnit;

public enum ServerMode {
    PRODUCTION, POST_SEASON {
        public int getMinCropGrow() { return 0; }
        public int getMaxCropGrow() { return 0; }
        public long getMinSpawnerTime() { return 0; }
        public long getMaxSpawnerTime() { return 0; }
    }, ARCHIVE {
        public int getMinCropGrow() { return 0; }
        public int getMaxCropGrow() { return 0; }
        public long getMinSpawnerTime() { return 0; }
        public long getMaxSpawnerTime() { return 0; }
    }, MAINTENANCE {
        public int getMinCropGrow() { return 0; }
        public int getMaxCropGrow() { return 0; }
        public long getMinSpawnerTime() { return 0; }
        public long getMaxSpawnerTime() { return 0; }
    }, PREVIEW, DEVELOPMENT {
        public int getMinCropGrow() { return 1; }
        public int getMaxCropGrow() { return 2; }
        public long getMinSpawnerTime() { return 1; }
        public long getMaxSpawnerTime() { return 2; }
    };
    
    private int minCropGrow, maxCropGrow;
    private long minSpawnerTime, maxSpawnerTime;
    
    ServerMode() {
        this.minSpawnerTime = TimeUnit.SECONDS.toMillis(30);
        this.maxSpawnerTime = TimeUnit.MINUTES.toMillis(1);
        this.minCropGrow = (int) TimeUnit.SECONDS.toMillis(15);
        this.maxCropGrow = (int) TimeUnit.SECONDS.toMillis(30);
    }
    
    public long getMinSpawnerTime() {
        return minSpawnerTime;
    }
    
    public long getMaxSpawnerTime() {
        return maxSpawnerTime;
    }
    
    public int getMinCropGrow() {
        return minCropGrow;
    }
    
    public int getMaxCropGrow() {
        return maxCropGrow;
    }
}