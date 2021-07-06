package net.firecraftmc.api.paginator;

import java.util.*;

public class PaginatorFactory<T extends Paginatable> {

    private final Deque<T> elements = new LinkedList<>();
    private int maxElements = 5;
    private String header = "";
    private String footer = "";

    public PaginatorFactory() {}

    public PaginatorFactory<T> setMaxElements(int maxElements) {
        this.maxElements = maxElements;
        return this;
    }

    public PaginatorFactory<T> setHeader(String header) {
        this.header = header;
        return this;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public void addElement(T element) {
        this.elements.add(element);
    }

    public Paginator<T> build() {
        SortedMap<Integer, Page<T>> pages = new TreeMap<>();
        for (T element : elements) {
            Page<T> lastPage = pages.get(pages.size()-1);
            if (lastPage != null) {
                if (lastPage.getElements().size() < maxElements) {
                    lastPage.addElement(lastPage.getElements().size(), element);
                } else {
                    Page<T> page = new Page<>();
                    page.addElement(0, element);
                    pages.put(pages.size(), page);
                }
            } else {
                Page<T> page = new Page<>();
                page.addElement(0, element);
                pages.put(pages.size(), page);
            }
        }
        
        Paginator<T> paginator = new Paginator<>(pages);
        paginator.setHeader(header);
        paginator.setFooter(footer);
        return paginator;
    }
}