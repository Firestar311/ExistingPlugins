package net.firecraftmc.api.interfaces;

public interface ConfigSerializable<T> extends DataSerializable {
    T getFromString(String string);
}