/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.api.annotation;

import at.jabberwocky.spi.Trait;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the ordering of Jabberwocky annotations. Any object/handler that
 * are order is 'greater' than those that are not annotate
 *
 * @author projects
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Trait
/**
 * The ordering number
 */
public @interface Order {

    public int value();
}
