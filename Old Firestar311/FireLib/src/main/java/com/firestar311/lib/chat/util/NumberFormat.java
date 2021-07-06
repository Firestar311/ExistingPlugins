package com.firestar311.lib.chat.util;

public interface NumberFormat {
    NumberFormat FRACTION = (v, l) -> String.format("%d/%d", v + 1, l);
    NumberFormat NONE = (v, l) -> "";
    NumberFormat PERCENTAGE = (v, l) -> String.format("%.1f%%", ((double) (v + 1) / l) * 100);
    
    String format(int value, int length);
}
