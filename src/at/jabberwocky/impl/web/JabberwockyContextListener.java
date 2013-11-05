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
import at.jabberwocky.api.ComponentLifecycleEvent;
import at.jabberwocky.impl.core.ConnectionStarter;
import at.jabberwocky.impl.core.Constants;
import at.jabberwocky.impl.core.io.JabberwockyComponentConnection;
import at.jabberwocky.impl.core.util.CDIUtilities;
import static at.jabberwocky.impl.core.util.Utility.*;
import at.jabberwocky.spi.SubdomainConfiguration;
import at.jabberwocky.spi.XMPPComponent;
import javax.ejb.EJB;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

/**
 *
 * @author project
 */
@WebListener
public class JabberwockyContextListener implements ServletContextListener {   

    private static final Logger logger = Logger.getLogger(
            JabberwockyContextListener.class.getName());

	@EJB private ConnectionStarter connectionStarter;

    @Override
    public void contextInitialized(ServletContextEvent sce) {

		boolean toStart = (Boolean)sce.getServletContext().getAttribute(
				Constants.XMPP_COMPONENT_TO_START);
		if (!toStart)
			return;

        logger.log(Level.INFO, "Initializing Jabberwocky context");                

        XMPPComponent xmppComponent = (XMPPComponent)sce.getServletContext()
                .getAttribute(Constants.XMPP_COMPONENT_OBJECT);

        if (null == xmppComponent) {
            logger.log(Level.SEVERE, "Cannot locate XMPPComponent.");
            return;
        }

        SubdomainConfiguration config = xmppComponent.getConfiguration();
		long delay = Long.parseLong(
				config.getProperties().get(CONNECTION_START_DELAY).getValue());

		connectionStarter.scheduleToStart(delay, xmppComponent
				, config, sce.getServletContext());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

		boolean toStart = (Boolean)sce.getServletContext().getAttribute(
				Constants.XMPP_COMPONENT_TO_START);
		if (!toStart)
			return;
        
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
		BeanManager bm = CDI.current().getBeanManager();
		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Fire PreDisconnect event");
		CDIUtilities.fire(xmppComponent, config
				, ComponentLifecycleEvent.Phase.PreDisconnect, bm);

        //Shutdown XMPP listener thread
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "Closing connection to {0}", config.getDomain());
		connection.close();

		//Fire PostDisconnect
		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Fire PreDisconnect event");
		CDIUtilities.fire(xmppComponent, config
				, ComponentLifecycleEvent.Phase.PostDisconnect, bm);
        
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

}
