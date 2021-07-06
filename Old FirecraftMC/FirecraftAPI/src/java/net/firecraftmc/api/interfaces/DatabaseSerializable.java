package net.firecraftmc.api.interfaces;

import java.util.Map;

public interface DatabaseSerializable<T> extends DataSerializable {
    T getFromEntry(Map<String, Object> map);
}