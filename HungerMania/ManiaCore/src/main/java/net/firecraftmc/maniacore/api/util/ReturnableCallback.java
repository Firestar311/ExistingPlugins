package net.firecraftmc.maniacore.api.util;

public interface ReturnableCallback<T, R> {
    R callback(T t);
}
