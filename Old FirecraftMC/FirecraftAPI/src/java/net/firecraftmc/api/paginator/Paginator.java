package net.firecraftmc.api.paginator;

import net.firecraftmc.api.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

public class Paginator<T extends Paginatable> {

    private final SortedMap<Integer, Page<T>> pages;
    private String header = "";
    private String footer = "";

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

    public void display(Player player, int pageNumber) {
        Page<T> page = pages.get(pageNumber-1);
        if(page == null) {
            player.sendMessage("§cThere are no pages to display.");
            return;
        }
        String header = this.header.replace("{pagenumber}", pageNumber + "");
        header = header.replace("{totalpages}", pages.size() + "");
        player.sendMessage(Utils.color(header));
        for(Entry<Integer, T> element : page.getElements().entrySet()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', element.getValue().formatLine()));
        }
        if(!(pageNumber == pages.size())) {
            String footer = this.footer.replace("{nextpage}", (pageNumber + 1) + "");
            player.sendMessage(Utils.color(footer));
        }
    }
}