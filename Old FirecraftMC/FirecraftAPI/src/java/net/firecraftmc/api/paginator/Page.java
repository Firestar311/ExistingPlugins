package net.firecraftmc.api.paginator;

import java.util.SortedMap;
import java.util.TreeMap;

public class Page<T extends Paginatable> {

    private SortedMap<Integer, T> elements = new TreeMap<>();

    public Page(SortedMap<Integer, T> initialElements) {
        elements = initialElements;
    }

    public Page() {}

    public void addElement(Integer position, T element) {
        elements.put(position, element);
    }

    public void removeElement(Integer position) {
        elements.remove(position);
    }

    public SortedMap<Integer, T> getElements() {
        return this.elements;
    }

    public T getElement(Integer key) {
        return this.elements.get(key);
    }
}