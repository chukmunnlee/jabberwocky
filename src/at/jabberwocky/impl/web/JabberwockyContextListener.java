/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.impl.web;

import javax.servlet.*;
import java.util.logging.*;
import javax.naming.*;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.servlet.annotation.WebListener;

import static at.jabberwocky.api.Configurables.*;
import at.jabberwocky.impl.core.Constants;
import static at.jabberwocky.impl.core.util.Utility.*;
import at.jabberwocky.spi.SubdomainConfiguration;
import at.jabberwocky.spi.XMPPComponent;
import at.jabberwocky.spi.XMPPComponentException;

/**
 *
 * @author project
 */
@WebListener
public class JabberwockyContextListener implements ServletContextListener {   

    private static final Logger logger = Logger.getLogger(
            JabberwockyContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        logger.log(Level.INFO, "Initializing Jabberwocky context");                

        ManagedExecutorService executor;
        
        XMPPComponent xmppComponent = (XMPPComponent)sce.getServletContext()
                .getAttribute(Constants.XMPP_COMPONENT_OBJECT);
        SubdomainConfiguration config = xmppComponent.getConfiguration();
        String name = config.getProperties().get(EXECUTOR_SERVICE).getValue();
        
        if (null == xmppComponent) {
            logger.log(Level.SEVERE, "Cannot locate XMPPComponet.");
            return;
        }
        
        //Fire pre connect        
        
        //Fire post connect
        
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "Verifying executor service: {0}", name);        

        try {
            executor = (ManagedExecutorService) InitialContext.doLookup(name);
        } catch (NamingException ex) {
            logger.log(Level.SEVERE, "Cannot get executor service: {0} ", name);
            return;
        }                
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
        logger.log(Level.INFO, "Destroying Jabberwocky context");
        
        XMPPComponent xmppComponent = (XMPPComponent)sce.getServletContext()
                .getAttribute(Constants.XMPP_COMPONENT_OBJECT);
        SubdomainConfiguration config = xmppComponent.getConfiguration();
        String name = config.getProperties().get(EXECUTOR_SERVICE).getValue();
        ManagedExecutorService executor;

        boolean shutown = (!(isNullOrEmpty(name) || Constants.DEFAULT_SERVICE.endsWith(name)));
        if (isNullOrEmpty(name))
            name = Constants.DEFAULT_SERVICE;        

        //Shutdown XMPP listener thread
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "Killing all packet listener threads");
        
        //Shutdown executor 
        if (shutown) {
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Shutting down executor: {0}", name);
            }
            try {
                executor = (ManagedExecutorService) InitialContext.doLookup(name);
                executor.shutdownNow();
            } catch (NamingException ex) {
                logger.log(Level.WARNING, "Cannot get executor service: {0}", name);
            }
        }
    }

}
