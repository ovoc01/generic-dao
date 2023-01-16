package com.ovoc01.dao.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@SuppressWarnings("unused")

/**
 *An annotations used to set Primary Key in a ObjectDao class instance.
 * @author ovoc01
 * */
public @interface PrimaryKey {
    String prefix() default "";
    String seqComp() default "";
}
