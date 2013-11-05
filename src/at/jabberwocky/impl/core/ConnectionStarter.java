/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.jabberwocky.impl.core;

import at.jabberwocky.api.ComponentLifecycleEvent;
import at.jabberwocky.api.Configurables;
import at.jabberwocky.impl.core.io.JabberwockyComponentConnection;
import at.jabberwocky.impl.core.util.CDIUtilities;
import at.jabberwocky.spi.SubdomainConfiguration;
import at.jabberwocky.spi.XMPPComponent;
import at.jabberwocky.spi.XMPPComponentException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;

/**
 *
 * @author project
 */
@Singleton
public class ConnectionStarter {

	private static final Logger logger = Logger.getLogger(ConnectionStarter.class.getName());

	@Resource private TimerService timer;

	private XMPPComponent xmppComponent = null;
	private SubdomainConfiguration subdomainConfig = null;
	private ServletContext servletContext = null;

	public void scheduleToStart(long delay, XMPPComponent comp
			, SubdomainConfiguration config, ServletContext ctx) {

		if (null != config)
			return;

		xmppComponent = comp;
		subdomainConfig = config;
		servletContext = ctx;

		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Starting connection in {0} ms", delay);

		timer.createTimer(delay, null);
	}

	@Timeout
	public void startIt(Timer timer) {

		JabberwockyComponentConnection connection;
        ManagedExecutorService executor;
        String name = subdomainConfig.getProperties()
				.get(Configurables.EXECUTOR_SERVICE).getValue();

		if (logger.isLoggable(Level.INFO))
			logger.log(Level.INFO, "Connecting to {0}", subdomainConfig.getDomain());

		BeanManager bm = CDI.current().getBeanManager();

        //Fire preConnect        
		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Fire PreConnect event");
		CDIUtilities.fire(xmppComponent, subdomainConfig
				, ComponentLifecycleEvent.Phase.PreConnect, bm);

		try {
            connection = new JabberwockyComponentConnection(subdomainConfig);
            connection.connect();
        } catch (XMPPComponentException ex) {
            logger.log(Level.SEVERE, "Connection problem. Stopped", ex);
			return;
        }

        servletContext.setAttribute(Constants.XMPP_COMPONENT_CONNECTION, connection);

        //Fire postConnect
		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Fire PostConnect event");
		CDIUtilities.fire(xmppComponent, subdomainConfig
				, ComponentLifecycleEvent.Phase.PostDisconnect, bm);

		//Start receiving packets
		if (logger.isLoggable(Level.INFO))
			logger.log(Level.INFO, "Starting XMPPComponent: {0}"
					, subdomainConfig.getProperties().get(Configurables.XMPP_COMPONENT));

        try {
            executor = (ManagedExecutorService) InitialContext.doLookup(name);
        } catch (NamingException ex) {
            logger.log(Level.SEVERE, "Cannot get executor service: {0} ", name);
            return;
        }                
		connection.start(executor, xmppComponent);
	}
	
}
