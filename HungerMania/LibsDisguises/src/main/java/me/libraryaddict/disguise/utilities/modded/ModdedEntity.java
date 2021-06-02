package me.libraryaddict.disguise.utilities.modded;

/**
 * Created by libraryaddict on 14/04/2020.
 */
public class ModdedEntity {
    private Object entityType;
    private final String name;
    private final boolean living;
    private final String mod;
    private final String[] versions;
    private final String required;
    private int typeId;

    public ModdedEntity(Object entityType, String name, boolean living, String mod, String[] versions, String required, int typeId) {
        this.entityType = entityType;
        this.name = name;
        this.living = living;
        this.mod = mod;
        this.versions = versions;
        this.required = required;
        this.typeId = typeId;
    }

    public Object getEntityType() {
        return entityType;
    }

    public void setEntityType(Object entityType) {
        this.entityType = entityType;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public boolean isLiving() {
        return living;
    }

    public String getMod() {
        return mod;
    }

    public String[] getVersions() {
        return versions;
    }

    public String getRequired() {
        return required;
    }

    public int getTypeId() {
        return typeId;
    }
}
