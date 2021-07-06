package me.alonedev.ihhub.Managers;

import java.util.HashMap;

public class BookManager {
    String title, author, link, clickText, hoverText;

    public BookManager(String title, String author, String link, String clickText, String hoverText) {
        this.title = title;
        this.author = author;
        this.link = link;
        this.clickText = clickText;
        this.hoverText = hoverText;
    }

    public String getTitle() {return title;}
    public String getAuthor() {return author;}
    public String getLink() {return link;}
    public String getClickText() {return clickText;}
    public String getHoverText() {return hoverText;}

    static HashMap<Integer, BookManager> books = new HashMap<Integer, BookManager>();

    public static void addBook(int id, BookManager book) {
        books.put(id, book);
    }

    public static BookManager getBook(int id) {return books.get(id);}

}
