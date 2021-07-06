package com.starmediadev.lib.oldsql;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RecordField {
    String colName() default "";
    boolean canBeNull() default true;
    String colType() default "";
    boolean unique() default false;
}
