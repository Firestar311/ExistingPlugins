package com.stardevmc.titangames.command;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandInfo {
    String description();
    String[] aliases();
    String usage();
    String permission() default "";
}