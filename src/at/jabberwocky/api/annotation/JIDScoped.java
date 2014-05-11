/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.api.annotation;

import at.jabberwocky.spi.Trait;
import java.lang.annotation.*;

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
@Target({ElementType.TYPE})
@Inherited
public @interface JIDScoped {
    /*
     If we should target full JID
     */        
    public enum Type { Jid, Full, RequestResponse };
    
    public Type value() default Type.Full;
}
