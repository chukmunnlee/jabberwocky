/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.impl.web;

import at.jabberwocky.api.annotation.*;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;

import javax.servlet.annotation.HandlesTypes;

import static at.jabberwocky.api.Configurables.*;

import at.jabberwocky.impl.core.Constants;
import at.jabberwocky.impl.core.io.JabberwockyComponentConnection;
import at.jabberwocky.spi.*;
import java.io.*;
import java.util.Properties;
import javax.xml.bind.*;

/**
 *
 * @author project
 */
@HandlesTypes({Message.class, IQ.class, Presence.class })
public class JabberwockyWebInitializer implements ServletContainerInitializer {
    
    private static final Logger logger = Logger.getLogger(JabberwockyWebInitializer.class.getName());

    @Override
    public void onStartup(Set<Class<?>> handlers, ServletContext ctx) throws ServletException {

		boolean toStart = handlers.size() > 0;
		ctx.setAttribute(Constants.XMPP_COMPONENT_TO_START, toStart);

        if (!toStart) {
            if (logger.isLoggable(Level.INFO))
                logger.log(Level.INFO, "No XMPP message handler found. Not starting component");
            return;
        }        
        
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "Reading xep-0114.xml");                
        
        SubdomainConfiguration config = readConfig(ctx);
        config.setProperties(loadAndMergeDefaults(Constants.XMPP_DEFAULTS
                , config.getProperties()));
        
		ApplicationProperty prop = config.getProperties().get(XMPP_COMPONENT);
		if (null == prop) {
			logger.log(Level.SEVERE, "Cannot get XMPP component class. Set {0}", XMPP_COMPONENT);
			throw new ServletException("Cannot get XMPP component class. Set " + XMPP_COMPONENT);
		}

        String componetnClassName = prop.getValue();
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "Instantiating {0}", componetnClassName);
        
        XMPPComponent xmppComponent = instantiateComponent(componetnClassName);
        JabberwockyComponentConnection connection = null;
        
        try {
            xmppComponent.initialize(handlers, config);
        } catch (XMPPComponentException ex) {
            logger.log(Level.SEVERE, "Error during XMPPComponent initialization", ex);
            throw new ServletException("Error during XMPPComponent initialization", ex);
        }
        
        try {
            connection = new JabberwockyComponentConnection(config);
            connection.connect();
        } catch (XMPPComponentException ex) {
            logger.log(Level.SEVERE, "Connection problem", ex);
            throw new ServletException("Connection problem", ex);
        }
        
        ctx.setAttribute(Constants.XMPP_COMPONENT_OBJECT, xmppComponent);
        ctx.setAttribute(Constants.XMPP_COMPONENT_CONFIGURATION, config);     
        ctx.setAttribute(Constants.XMPP_COMPONENT_CONNECTION, connection);
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
    
    private ApplicationPropertyBag loadAndMergeDefaults(String name, ApplicationPropertyBag toMerge) {
        ApplicationPropertyBag bag = new ApplicationPropertyBag();
        if (logger.isLoggable(Level.FINER))
            logger.log(Level.FINER, "Loading defaults.properties");
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(name)) {
            Properties prop = new Properties();
            prop.load(is);    
            for (String k: prop.stringPropertyNames()) 
                bag.add(new ApplicationProperty(k, prop.getProperty(k)));   
            bag.merge(toMerge);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Cannot load defaults.properties", ex);
        }
        return (bag);
    }
    
}
