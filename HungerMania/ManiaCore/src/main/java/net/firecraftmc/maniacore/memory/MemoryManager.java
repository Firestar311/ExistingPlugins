package net.firecraftmc.maniacore.memory;

import net.firecraftmc.maniacore.plugin.ManiaPlugin;

import java.util.HashSet;
import java.util.Set;

public class MemoryManager {
    
    private Set<net.firecraftmc.maniacore.memory.MemoryHook> memoryHooks = new HashSet<>();
    private Set<ManiaPlugin> maniaPlugins = new HashSet<>();
    
    public void addMemoryHook(net.firecraftmc.maniacore.memory.MemoryHook memoryHook) {
        this.memoryHooks.add(memoryHook);
    }
    
    public Set<MemoryHook> getMemoryHooks() {
        return new HashSet<>(memoryHooks);
    }
    
    public void addManiaPlugin(ManiaPlugin plugin) {
        this.maniaPlugins.add(plugin);
    }
    
    public Set<ManiaPlugin> getManiaPlugins() {
        return new HashSet<>(maniaPlugins);
    }
    
    public void removeMemoryHook(String name) {
        this.memoryHooks.removeIf(memoryHook -> memoryHook.getName().equalsIgnoreCase(name));
    }
}