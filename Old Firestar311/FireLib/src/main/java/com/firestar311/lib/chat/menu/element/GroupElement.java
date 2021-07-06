package com.firestar311.lib.chat.menu.element;

import com.firestar311.lib.chat.menu.IElementContainer;
import com.firestar311.lib.chat.util.Text;
import org.bukkit.entity.Player;

import java.util.*;

public class GroupElement extends Element implements IElementContainer {
    
    protected final IElementContainer parent;
    
    protected List<Element> elements;
    
    /**
     * Constructs an element at the given x and y coordinates.
     *
     * @param parent The parent element of this group. Usually a {@code ChatMenu} or another {@code GroupElement}
     * @param x      the x coordinate to put this element at
     * @param y      the y coordinate to put this element at
     */
    public GroupElement(IElementContainer parent, int x, int y) {
        super(x, y);
        this.parent = parent;
        this.elements = new ArrayList<>();
    }
    
    /**
     * @param element the element to add
     * @param <T>     the type of element to add
     * @return the element that was added
     */
    public <T extends Element> T add(T element) {
        Objects.requireNonNull(element);
        elements.add(element);
        elements.sort(Comparator.comparingInt(Element::getX));
        return element;
    }
    
    /**
     * Removes the specified element from this group.
     *
     * @param element the element to remove
     * @return true if the element was removed
     */
    public boolean remove(Element element) {
        return elements.remove(element);
    }
    
    /**
     * @return an unmodifiable list of all the elements in this group.
     */
    
    public List<Element> getElements() {
        return Collections.unmodifiableList(elements);
    }
    
    /**
     * @param element the element to interact with
     * @return the command used to interact with this element
     */
    
    public String getCommand(Element element) {
        int index = elements.indexOf(element);
        if (index == -1) { throw new IllegalArgumentException("Unable to interact with the provided element"); }
        return parent.getCommand(this) + index + " ";
    }
    
    public void openFor(Player player) {
        parent.openFor(player);
    }
    
    public void refresh() {
        parent.refresh();
    }
    
    public List<Text> render(IElementContainer context) {
        if (context != parent) {
            throw new IllegalStateException("Attempted to render GroupElement with non-parent context");
        }
        int height = getHeight();
        List<Text> lines = new ArrayList<>(height);
        for (int i = 0; i < height; i++) { lines.add(new Text()); }
        
        for (Element element : elements) {
            if (!element.isVisible()) { continue; }
            
            List<Text> elementTexts = element.render(this);
            for (int j = 0; j < elementTexts.size(); j++) {
                int lineY = element.getY() + j;
    
                if (lineY < 0 || lineY >= height) { continue; }
                
                Text text = lines.get(lineY);
                text.expandToWidth(element.getX());
                
                Text toAdd = elementTexts.get(j);
                toAdd.expandToWidth(element.getWidth());
                text.append(toAdd);
            }
        }
        
        return lines;
    }
    
    public void edit(IElementContainer container, String[] args) {
        int index = Integer.parseInt(args[0]);
        String[] newArgs = new String[args.length - 1];
        System.arraycopy(args, 1, newArgs, 0, newArgs.length);
        elements.get(index).edit(container, newArgs);
    }
    
    public int getWidth() {
        Element furthest = elements.stream().max(Comparator.comparingInt(Element::getRight)).orElse(null);
        if (furthest == null) { return 0; }
        return furthest.getRight();
    }
    
    public int getHeight() {
        Element furthest = elements.stream().max(Comparator.comparingInt(Element::getBottom)).orElse(null);
        if (furthest == null) { return 0; }
        return furthest.getBottom();
    }
}
