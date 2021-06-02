package me.libraryaddict.disguise.utilities.parser;

import me.libraryaddict.disguise.disguisetypes.FlagWatcher;

import java.lang.invoke.MethodHandle;

/**
 * Created by libraryaddict on 21/05/2021.
 */
public class WatcherMethod {
    private final Class<? extends FlagWatcher> watcherClass;
    private final MethodHandle method;
    private final String name;
    private final Class returnType;
    private final Class param;
    private final boolean randomDefault;

    public WatcherMethod(Class<? extends FlagWatcher> watcherClass, MethodHandle method, String name, Class returnType, Class param, boolean randomDefault) {
        this.watcherClass = watcherClass;
        this.method = method;
        this.name = name;
        this.returnType = returnType;
        this.param = param;
        this.randomDefault = randomDefault;
    }

    public Class<? extends FlagWatcher> getWatcherClass() {
        return watcherClass;
    }

    public MethodHandle getMethod() {
        return method;
    }

    public String getName() {
        return name;
    }

    public Class getReturnType() {
        return returnType;
    }

    public Class getParam() {
        return param;
    }

    public boolean isRandomDefault() {
        return randomDefault;
    }
}
