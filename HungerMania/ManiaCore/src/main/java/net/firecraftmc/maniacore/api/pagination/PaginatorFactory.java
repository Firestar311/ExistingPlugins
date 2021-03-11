package net.firecraftmc.maniacore.api.pagination;

import java.util.*;
import java.util.Map.Entry;

public class PaginatorFactory<T extends net.firecraftmc.maniacore.api.pagination.IElement> {

    private final Deque<T> elements = new LinkedList<>();
    private int maxElements = 5;
    private String header = "";
    private String footer = "";
    
    private Map<net.firecraftmc.maniacore.api.pagination.DefaultVariables, String> defaultVariables = new HashMap<>();

    public PaginatorFactory() {}
    
    //TODO Add a sort method
    
    public PaginatorFactory(net.firecraftmc.maniacore.api.pagination.Paginator<T> paginator) {
        for (Page<T> page : paginator.getPages()) {
            if (this.maxElements != page.getElements().size()) {
                this.maxElements = page.getElements().size();
            }
            this.elements.addAll(page.getElements().values());
        }
        
        this.header = paginator.getHeader();
        this.footer = paginator.getFooter();
    }

    public PaginatorFactory<T> setMaxElements(int maxElements) {
        this.maxElements = maxElements;
        return this;
    }
    
    public PaginatorFactory<T> setHeader(String header) {
        this.header = header;
        return this;
    }

    public PaginatorFactory<T> setFooter(String footer) {
        this.footer = footer;
        return this;
    }

    public void addElement(T element) {
        this.elements.add(element);
    }
    
    @SafeVarargs
    public final void addElements(T... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }
    
    public void updateElements(Collection<T> elements) {
        this.elements.clear();
        this.elements.addAll(elements);
    }
    
    public PaginatorFactory<T> useDefaultHeader() {
        this.header = net.firecraftmc.maniacore.api.pagination.Paginator.DEFAULT_HEADER;
        return this;
    }
    
    public PaginatorFactory<T> useDefaultFooter() {
        this.footer = net.firecraftmc.maniacore.api.pagination.Paginator.DEFAULT_FOOTER;
        return this;
    }
    
    public PaginatorFactory<T> useDefaultHeaderAndFooter() {
        useDefaultHeader();
        useDefaultFooter();
        return this;
    }
    
    public PaginatorFactory<T> setVariable(net.firecraftmc.maniacore.api.pagination.DefaultVariables defaultVariables, String value) {
        this.defaultVariables.put(defaultVariables, value);
        return this;
    }
    
    public PaginatorFactory<T> replaceVariables() {
        for (Entry<net.firecraftmc.maniacore.api.pagination.DefaultVariables, String> entry : this.defaultVariables.entrySet()) {
            header = header.replace(entry.getKey().getValue(), entry.getValue());
            footer = footer.replace(entry.getKey().getValue(), entry.getValue());
        }
        return this;
    }
    
    public static <T extends net.firecraftmc.maniacore.api.pagination.IElement> net.firecraftmc.maniacore.api.pagination.Paginator<T> generatePaginator(String header, String footer, int maxElements, Collection<T> elements) {
        PaginatorFactory<T> factory = new PaginatorFactory<>();
        factory.setHeader(header).setFooter(footer).setMaxElements(maxElements);
        factory.elements.addAll(elements);
        return factory.build();
    }
    
    public static <T extends IElement> net.firecraftmc.maniacore.api.pagination.Paginator<T> generatePaginator(int maxElements, Collection<T> elements, Map<net.firecraftmc.maniacore.api.pagination.DefaultVariables, String> variables) {
        PaginatorFactory<T> factory = new PaginatorFactory<>();
        factory.defaultVariables.putAll(variables);
        factory.useDefaultHeader().useDefaultFooter().setMaxElements(maxElements).replaceVariables();
        factory.elements.addAll(elements);
        return factory.build();
    }
    
    public static <T extends StringElement> net.firecraftmc.maniacore.api.pagination.Paginator<StringElement> generateStringPaginator(String header, String footer, int maxElements, Collection<String> elements) {
        PaginatorFactory<StringElement> factory = new PaginatorFactory<>();
        factory.setHeader(header).setFooter(footer).setMaxElements(maxElements);
        for (String s : elements) {
            factory.addElement(new StringElement(s));
        }
        return factory.build();
    }
    
    public static <T extends StringElement> net.firecraftmc.maniacore.api.pagination.Paginator<StringElement> generateStringPaginator(int maxElements, Collection<String> elements, Map<DefaultVariables, String> variables) {
        PaginatorFactory<StringElement> factory = new PaginatorFactory<>();
        factory.defaultVariables.putAll(variables);
        factory.useDefaultHeader().useDefaultFooter().setMaxElements(maxElements).replaceVariables();
        for (String s : elements) {
            factory.addElement(new StringElement(s));
        }
        return factory.build();
    }

    public net.firecraftmc.maniacore.api.pagination.Paginator<T> build() {
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
        
        net.firecraftmc.maniacore.api.pagination.Paginator<T> paginator = new Paginator<>(pages);
        paginator.setHeader(header);
        paginator.setFooter(footer);
        return paginator;
    }
}