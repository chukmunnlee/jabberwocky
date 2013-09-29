/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.api.annotation;

import at.jabberwocky.spi.Trait;
import java.lang.annotation.*;

import javax.enterprise.context.NormalScope;

/**
 *
 * @author project
 */
@NormalScope(passivating = true)
@Retention(RetentionPolicy.RUNTIME)
@Trait
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Inherited
public @interface JIDScoped { 
    /*
    If we should target full JID
    */
    public boolean value() default false;
}
