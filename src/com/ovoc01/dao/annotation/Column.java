package com.ovoc01.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@SuppressWarnings("unused")

/**
 *An annotations for column name who need to be used in a ObjectDao class
 * @author ovoc01
 * */
public @interface Column {
    String name() default "";
}
