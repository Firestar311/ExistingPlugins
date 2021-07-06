package com.starmediadev.lib.oldsql;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TableInfo {
    String name();
}
