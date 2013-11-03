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
import at.jabberwocky.api.ExternalComponentEvent;
import at.jabberwocky.impl.core.Constants;
import at.jabberwocky.impl.core.io.JabberwockyComponentConnection;
import static at.jabberwocky.impl.core.util.Utility.*;
import at.jabberwocky.spi.SubdomainConfiguration;
import at.jabberwocky.spi.XMPPComponent;
import at.jabberwocky.spi.XMPPComponentException;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import org.xmpp.packet.JID;

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

        if (null == xmppComponent) {
            logger.log(Level.SEVERE, "Cannot locate XMPPComponet.");
            return;
        }

        SubdomainConfiguration config = xmppComponent.getConfiguration();
        String name = config.getProperties().get(EXECUTOR_SERVICE).getValue();

        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "Verifying executor service: {0}", name);        

        try {
            executor = (ManagedExecutorService) InitialContext.doLookup(name);
        } catch (NamingException ex) {
            logger.log(Level.SEVERE, "Cannot get executor service: {0} ", name);
            return;
        }                
        
        //Fire preConnect        
		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Fire PreConnect event");
		fire(xmppComponent, config, ExternalComponentEvent.Phase.PreConnect);
        
		JabberwockyComponentConnection connection = new JabberwockyComponentConnection(config);
		        
        try {
            connection = new JabberwockyComponentConnection(config);
            connection.connect();
        } catch (XMPPComponentException ex) {
            logger.log(Level.SEVERE, "Connection problem. Stopped", ex);
			return;
        }

        sce.getServletContext().setAttribute(Constants.XMPP_COMPONENT_CONNECTION, connection);

        //Fire postConnect
		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Fire PostConnect event");
		fire(xmppComponent, config, ExternalComponentEvent.Phase.PostDisconnect);

		//Start receiving packets
		if (logger.isLoggable(Level.INFO))
			logger.log(Level.INFO, "Starting component");
		connection.start(executor, xmppComponent);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
        logger.log(Level.INFO, "Destroying Jabberwocky context");
        
		ServletContext ctx = sce.getServletContext();
        XMPPComponent xmppComponent = (XMPPComponent)ctx.getAttribute(
				Constants.XMPP_COMPONENT_OBJECT);
		JabberwockyComponentConnection connection = (JabberwockyComponentConnection)
				ctx.getAttribute(Constants.XMPP_COMPONENT_CONNECTION);
        SubdomainConfiguration config = xmppComponent.getConfiguration();
        String name = config.getProperties().get(EXECUTOR_SERVICE).getValue();

		if (logger.isLoggable(Level.INFO))
			logger.log(Level.INFO, "Stop dispatching packets to XMPPComponent");
		connection.stopReceiving();

		//Fire PreDisconnect
		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Fire PreDisconnect event");
		fire(xmppComponent, config, ExternalComponentEvent.Phase.PreDisconnect);

        //Shutdown XMPP listener thread
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "Closing connection to {0}", config.getDomain());
		connection.close();

		//Fire PostDisconnect
		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Fire PreDisconnect event");
		fire(xmppComponent, config, ExternalComponentEvent.Phase.PostDisconnect);
        
		//Do I need to shutdown the service ?
        //Shutdown executor - only shutdown if it is not default service
        if (!(isNullOrEmpty(name) || Constants.DEFAULT_SERVICE.endsWith(name))) {
            if (logger.isLoggable(Level.INFO)) {
                logger.log(Level.INFO, "Shutting down executor: {0}", name);
            }
            try {
                ManagedExecutorService executor = 
						(ManagedExecutorService) InitialContext.doLookup(name);
                executor.shutdownNow();
            } catch (NamingException ex) {
                logger.log(Level.WARNING, "Cannot get executor service: {0}", name);
            }
        }
    }

	private void fire(XMPPComponent comp, SubdomainConfiguration config
			, ExternalComponentEvent.Phase p) {

		BeanManager bm = CDI.current().getBeanManager();

		ExternalComponentEvent evt = new ExternalComponentEvent(comp);

		evt.setComponentJID(new JID(config.getName() + "." + config.getDomain()));
		evt.setConfiguration(config);
		evt.setContext(null);
		evt.setPhase(p);
	}

}
