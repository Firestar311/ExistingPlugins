package me.libraryaddict.disguise.utilities.reflection;


/**
 * Created by libraryaddict on 17/02/2020.
 */
public class WatcherInfo {
    private int added = -1;
    private int removed = -1;
    private boolean deprecated;
    private String returnType;
    private boolean randomDefault;
    private String watcher;
    private String method;
    private String param;
    private String descriptor;

    public void setAdded(int added) {
        this.added = added;
    }

    public void setRemoved(int removed) {
        this.removed = removed;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void setRandomDefault(boolean randomDefault) {
        this.randomDefault = randomDefault;
    }

    public void setWatcher(String watcher) {
        this.watcher = watcher;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public int getAdded() {
        return added;
    }

    public int getRemoved() {
        return removed;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public String getReturnType() {
        return returnType;
    }

    public boolean isRandomDefault() {
        return randomDefault;
    }

    public String getWatcher() {
        return watcher;
    }

    public String getMethod() {
        return method;
    }

    public String getParam() {
        return param;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public boolean isSupported() {
        if (getAdded() >= 0 && added > ReflectionManager.getVersion().ordinal()) {
            return false;
        }

        return getRemoved() < 0 || removed > ReflectionManager.getVersion().ordinal();
    }
}
