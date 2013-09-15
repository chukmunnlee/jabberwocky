/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.impl.web;

import at.jabberwocky.api.annotation.*;
import at.jabberwocky.impl.core.Constants;
import java.util.HashSet;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;

import javax.servlet.annotation.HandlesTypes;

/**
 *
 * @author project
 */
@HandlesTypes({Message.class, IQ.class, Presence.class })
public class JabberwockyWebInitializer implements ServletContainerInitializer {
    
    private static final Logger logger = Logger.getLogger(JabberwockyWebInitializer.class.getName());

    @Override
    public void onStartup(Set<Class<?>> handlers, ServletContext ctx) throws ServletException {
        if (handlers.size() <= 0) {
            if (logger.isLoggable(Level.INFO))
                logger.log(Level.INFO, "No XMPP message handler found. Not starting component");
            return;
        }
        
        Set<Class<?>> messageHandlers = new HashSet<>();
        Set<Class<?>> iqHandlers = new HashSet<>();
        Set<Class<?>> presenceHandlers = new HashSet<>();
        
        for (Class<?> c: handlers)
            if (c.isAnnotationPresent(Message.class))
                messageHandlers.add(c);
            else if (c.isAnnotationPresent(IQ.class))
                iqHandlers.add(c);
            else if (c.isAnnotationPresent(Presence.class))
                presenceHandlers.add(c);
            else
                logger.log(Level.WARNING, "Unknown handler type: {0}", c.getName());
        
        String xmppComponent = ctx.getInitParameter(Constants.XMPP_COMPONENT);
        
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "Instantiating {0}", xmppComponent);
        
        
    }
    
}
