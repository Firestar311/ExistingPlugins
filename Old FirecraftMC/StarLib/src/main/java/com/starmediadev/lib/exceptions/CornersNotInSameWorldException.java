package com.starmediadev.lib.exceptions;

public class CornersNotInSameWorldException extends RuntimeException {
    public CornersNotInSameWorldException() {
        super("Corners are not in the same world.");
    }
}