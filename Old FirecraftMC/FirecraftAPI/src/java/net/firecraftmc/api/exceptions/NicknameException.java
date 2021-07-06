package net.firecraftmc.api.exceptions;

public class NicknameException extends RuntimeException {

    public NicknameException() { super(); }

    public NicknameException(String message) {
        super(message);
    }
}