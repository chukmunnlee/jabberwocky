/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.jabberwocky.api.annotation;

import at.jabberwocky.spi.Trait;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.enterprise.context.NormalScope;
import javax.inject.Qualifier;

/**
 *
 * @author project
 */

@NormalScope(passivating = true)
@Retention(RetentionPolicy.RUNTIME)
@Trait
@Qualifier
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Inherited
public @interface PacketParam {
    //If null use the field or parameter name to resolve
    public String value() default "";
}
