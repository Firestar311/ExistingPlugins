package com.firestar311.lib.pagination;

import com.firestar311.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.Map.Entry;

public class Paginator<T extends IElement> {
    
    private final SortedMap<Integer, Page<T>> pages;
    private String header = "";
    private String footer = "";
    
    public static final String DEFAULT_HEADER = "&6List of " + DefaultVariables.TYPE.getValue() + " &e({pagenumber}/{totalpages})";
    public static final String DEFAULT_FOOTER = "&6Type /" + DefaultVariables.COMMAND.getValue() + " {nextpage} for more";
    
    public Paginator(SortedMap<Integer, Page<T>> pages) {
        this.pages = new TreeMap<>(pages);
    }
    
    public Paginator() {
        this.pages = new TreeMap<>();
    }
    
    public Collection<Page<T>> getPages() {
        return pages.values();
    }
    
    public void setHeader(String header) {
        this.header = header;
    }
    
    public void setFooter(String footer) {
        this.footer = footer;
    }
    
    public String getHeader() {
        return header;
    }
    
    public String getFooter() {
        return footer;
    }
    
    public void display(CommandSender sender, int pageNumber, String... args) {
        Page<T> page = pages.get(pageNumber - 1);
        if (page == null) {
            sender.sendMessage("§cThere are no pages to display.");
            return;
        }
        String header = this.header.replace("{pagenumber}", pageNumber + "");
        header = header.replace("{totalpages}", pages.size() + "");
        sender.sendMessage(Utils.color(header));
        for (Entry<Integer, T> element : page.getElements().entrySet()) {
            if (!StringUtils.isEmpty(element.getValue().formatLine())) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', element.getValue().formatLine(args)));
            } else {
                sender.spigot().sendMessage(element.getValue().formatLineAsTextComponent(args));
            }
        }
        if (!(pageNumber == pages.size())) {
            String footer = this.footer.replace("{nextpage}", (pageNumber + 1) + "");
            sender.sendMessage(Utils.color(footer));
        }
    }
    
    public void display(CommandSender sender, String page, String... args) {
        int pageNumber;
        try {
            pageNumber = Integer.parseInt(page);
        } catch (NumberFormatException e) {
            sender.sendMessage(Utils.color("&cThe value for the page number is not a valid number."));
            return;
        }
        this.display(sender, pageNumber, args);
    }
}