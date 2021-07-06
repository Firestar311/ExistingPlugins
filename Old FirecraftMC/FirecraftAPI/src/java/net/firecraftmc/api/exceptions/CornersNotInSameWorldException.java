package net.firecraftmc.api.exceptions;

public class CornersNotInSameWorldException extends Exception {
    public CornersNotInSameWorldException() {
        super("Corners are not in the same world.");
    }
}