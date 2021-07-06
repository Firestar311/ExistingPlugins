package com.firestar311.lib.chat.menu.element;

import com.firestar311.lib.chat.menu.ChatMenuAPI;
import com.firestar311.lib.chat.menu.IElementContainer;
import com.firestar311.lib.chat.util.Text;
import net.md_5.bungee.api.chat.*;

import java.util.Collections;
import java.util.List;

/**
 * A button that opens a link when clicked.
 */
public class LinkButtonElement extends Element {
    
    protected String text;
    
    protected String link;
    
    /**
     * Constructs a new {@code LinkButtonElement}
     *
     * @param x    the x coordinate
     * @param y    the y coordinate
     * @param text the text to display
     * @param link the link
     * @throws IllegalArgumentException if text contains newlines
     */
    public LinkButtonElement(int x, int y, String text, String link) {
        super(x, y);
        if (text.contains("\n")) { throw new IllegalArgumentException("Button text cannot contain newline"); }
        this.text = text;
        this.link = link;
    }
    
    public List<Text> render(IElementContainer context) {
        BaseComponent[] components = TextComponent.fromLegacyText(text);
        ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_URL, link);
        for (BaseComponent component : components) { component.setClickEvent(click); }
        
        return Collections.singletonList(new Text(components));
    }
    
    public void edit(IElementContainer container, String[] args) {
    }
    
    /**
     * @return the text that displays for this button
     */
    
    public String getText() {
        return text;
    }
    
    /**
     * @param text the new text to display
     */
    public void setText(String text) {
        if (text.contains("\n")) { throw new IllegalArgumentException("Button text cannot contain newline"); }
        this.text = text;
    }
    
    /**
     * @return the link
     */
    
    public String getLink() {
        return link;
    }
    
    /**
     * @param link the new link
     */
    public void setLink(String link) {
        this.link = link;
    }
    
    public int getWidth() {
        return ChatMenuAPI.getWidth(text);
    }
    
    public int getHeight() {
        return 1;
    }
}
