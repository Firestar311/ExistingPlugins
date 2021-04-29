package net.firecraftmc.maniacore.memory;

import net.firecraftmc.maniacore.plugin.CenturionsPlugin;

import java.util.HashSet;
import java.util.Set;

public class MemoryManager {
    
    private Set<MemoryHook> memoryHooks = new HashSet<>();
    private Set<CenturionsPlugin> centurionsPlugins = new HashSet<>();
    
    public void addMemoryHook(MemoryHook memoryHook) {
        this.memoryHooks.add(memoryHook);
    }
    
    public Set<MemoryHook> getMemoryHooks() {
        return new HashSet<>(memoryHooks);
    }
    
    public void addManiaPlugin(CenturionsPlugin plugin) {
        this.centurionsPlugins.add(plugin);
    }
    
    public Set<CenturionsPlugin> getManiaPlugins() {
        return new HashSet<>(centurionsPlugins);
    }
    
    public void removeMemoryHook(String name) {
        this.memoryHooks.removeIf(memoryHook -> memoryHook.getName().equalsIgnoreCase(name));
    }
}