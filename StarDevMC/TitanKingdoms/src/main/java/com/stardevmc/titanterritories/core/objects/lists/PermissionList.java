package com.stardevmc.titanterritories.core.objects.lists;

import com.stardevmc.titanterritories.core.objects.enums.Permission;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class PermissionList implements ConfigurationSerializable {
    
    private List<Permission> permissions = new ArrayList<>();
    
    public PermissionList() {}
    
    public PermissionList(Map<String, Object> serialized) {
        List<String> permissions = (List<String>) serialized.get("permissions");
        for (String o : permissions) {
            try {
                this.permissions.add(Permission.valueOf(o));
            } catch (Exception e) {}
        }
    }
    
    public PermissionList(PermissionList permissionList) {
        this.permissions.addAll(permissionList.getPermissions());
    }
    
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }
    
    public void addPermission(Permission... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
    }
    
    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        List<String> perms = new ArrayList<>();
        for (Permission perm : permissions) {
            perms.add(perm.name());
        }
        serialized.put("permissions", perms);
        return serialized;
    }
    
    public List<Permission> getPermissions() {
        return new ArrayList<>(permissions);
    }
    
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
    
    public String toString() {
        List<String> permStrings = new ArrayList<>();
        getPermissions().forEach(perm -> permStrings.add(perm.name()));
        return StringUtils.join(permStrings, ", ");
    }
}