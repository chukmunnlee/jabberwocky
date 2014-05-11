/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.api.annotation;

import at.jabberwocky.spi.Trait;
import java.lang.annotation.*;

import javax.inject.Qualifier;

/**
 *
 * @author project
 */
@Qualifier
@Trait
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
public @interface Message {

    //Parameter name
    public String value() default "";

}
