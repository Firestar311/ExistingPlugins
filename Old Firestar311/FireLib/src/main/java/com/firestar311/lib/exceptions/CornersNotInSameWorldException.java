package com.firestar311.lib.exceptions;

public class CornersNotInSameWorldException extends RuntimeException {
    public CornersNotInSameWorldException() {
        super("Corners are not in the same world.");
    }
}