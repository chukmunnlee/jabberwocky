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
import static at.jabberwocky.impl.core.util.Utility.*;

/**
 *
 * @author project
 */
@WebListener
public class JabberwockyContextListener implements ServletContextListener {

    private final String DEFAULT_SERVICE = "java:comp/DefaultManagedExecutorService";

    private static final Logger logger = Logger.getLogger(
            JabberwockyContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        logger.log(Level.INFO, "Initializing Jabberwocky context");

        ManagedExecutorService executor;
        String name = sce.getServletContext().getInitParameter(EXECUTOR_SERVICE);

        if (isNullOrEmpty(name))
            name = DEFAULT_SERVICE;
        
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "Verifying executor service: {0}", name);        

        try {
            executor = (ManagedExecutorService) InitialContext.doLookup(name);
        } catch (NamingException ex) {
            logger.log(Level.SEVERE, "Cannot get executor service: {0} ", name);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
        logger.log(Level.INFO, "Destroying Jabberwocky context");
        
        ManagedExecutorService executor;
        String name = sce.getServletContext().getInitParameter(EXECUTOR_SERVICE);
        boolean shutown = (!(isNullOrEmpty(name) || DEFAULT_SERVICE.endsWith(name)));
        if (isNullOrEmpty(name))
            name = DEFAULT_SERVICE;        

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
