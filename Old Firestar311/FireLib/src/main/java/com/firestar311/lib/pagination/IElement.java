package com.firestar311.lib.pagination;

import net.md_5.bungee.api.chat.TextComponent;

public interface IElement {
    default String formatLine(String... args) {
        return "";
    }
    default String getName() {
        return "";
    }
    default TextComponent formatLineAsTextComponent(String... args) {
        return null;
    }
}