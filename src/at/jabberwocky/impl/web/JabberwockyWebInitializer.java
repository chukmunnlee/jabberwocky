/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.impl.web;

import at.jabberwocky.api.annotation.*;
import java.util.HashSet;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;

import javax.servlet.annotation.HandlesTypes;

import static at.jabberwocky.api.Configurables.*;
import at.jabberwocky.spi.*;
import at.jabberwocky.spi.SubdomainConfiguration;
import at.jabberwocky.spi.XMPPComponent;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

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
        
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "Reading xep-0114.xml");
        
        SubdomainConfiguration config = readConfig(ctx);
        
        String componetnClassName = ctx.getInitParameter(XMPP_COMPONENT);        
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "Instantiating {0}", componetnClassName);
        
        XMPPComponent xmppComponent = instantiateComponent(componetnClassName);
        
        try {
            xmppComponent.initialize(handlers, config);
            //Call pre connect
            xmppComponent.connect();
        } catch (XMPPComponentException ex) {
            logger.log(Level.SEVERE, "Error during XMPPComponent initialization", ex);
            throw new ServletException("Error during XMPPComponent initialization", ex);
        }
        
        //Call post connect
        
        //Perform initialization of the thread pool
    }
    
    private SubdomainConfiguration readConfig(final ServletContext ctx) 
            throws ServletException {
        
        SubdomainConfiguration subdomainConfig = null;
        
        try (InputStream is = ctx.getResourceAsStream(XEP_0114)) {
            BufferedInputStream bis = new BufferedInputStream(is);
            JAXBContext jaxbCtx = JAXBContext.newInstance(SubdomainConfiguration.class);
            Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            subdomainConfig = (SubdomainConfiguration)unmarshaller.unmarshal(bis);
        } catch (IOException | JAXBException ex) {
            logger.log(Level.SEVERE, "Cannot read xep-0114.xml file", ex);
            throw new ServletException("Cannot read xep-0114.xml file", ex);
        }
        
        return (subdomainConfig);        
    }
    
    private XMPPComponent instantiateComponent(final String name) throws ServletException {
 
        Object instance = null;
        
        try {
            Class<?> clazz = Class.forName(name);
            instance = clazz.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException 
                | InstantiationException ex) {
            logger.log(Level.SEVERE, "Cannot initialize XMPP component: " + name, ex);
            throw new ServletException("Cannot initialize XMPP component: " + name, ex);
        }
        if (!(instance instanceof XMPPComponent)) {
            logger.log(Level.SEVERE, "{0} does not implement at.jabberwocky.spi.XMPPComponent"
                    , name);
            throw new ServletException(name 
                    + "does not implement at.jabberwocky.spi.XMPPComponent");
        }
            
        return ((XMPPComponent)instance);
    }
    
}
