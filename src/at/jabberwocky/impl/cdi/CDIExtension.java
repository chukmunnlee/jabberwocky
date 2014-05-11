/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.jabberwocky.impl.cdi;

import at.jabberwocky.api.annotation.JIDScoped;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

/**
 *
 * @author project
 */
public class CDIExtension implements Extension {
    
    private static final Logger logger = Logger.getLogger(CDIExtension.class.getName());
    
    public void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery bbdEvt) {
        
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "beforeBeanDiscovery");
        
        //Create Scope
        bbdEvt.addScope(JIDScoped.class, true, false);
        
    }
    
    public void afterBeanDiscovery(@Observes final AfterBeanDiscovery abdEvt) {
        
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "afterBeanDiscovery");
        
    }
    
}
