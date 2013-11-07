/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.jabberwocky.impl.web;

import at.jabberwocky.api.ComponentLifecycleEvent;
import at.jabberwocky.api.Configurables;
import static at.jabberwocky.api.Configurables.EXECUTOR_SERVICE;
import at.jabberwocky.impl.core.Constants;
import at.jabberwocky.impl.core.io.JabberwockyComponentConnection;
import at.jabberwocky.impl.core.util.CDIUtilities;
import static at.jabberwocky.impl.core.util.Utility.isNullOrEmpty;
import at.jabberwocky.spi.SubdomainConfiguration;
import at.jabberwocky.spi.XMPPComponent;
import at.jabberwocky.spi.XMPPComponentException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author project
 */
@WebServlet(urlPatterns = {"/jabberwocky", "/jabberwocky/*"},
		loadOnStartup = 1
)
public class JabberwockyServlet extends HttpServlet {

	private static final Logger logger = Logger.getLogger(JabberwockyServlet.class.getName());

	@Override
	public void init() throws ServletException {

		ServletContext ctx = getServletContext();

		AtomicBoolean startupLock = (AtomicBoolean)ctx.getAttribute(Constants.XMPP_CONNECTION_LOCK);

		if (!startupLock.compareAndSet(false, true))
			return;

		if (logger.isLoggable(Level.INFO))
			logger.log(Level.INFO, "Starting Jabberwocky connection");

        XMPPComponent xmppComponent = (XMPPComponent)getServletContext()
                .getAttribute(Constants.XMPP_COMPONENT_OBJECT);

        if (null == xmppComponent) {
            logger.log(Level.SEVERE, "Cannot locate XMPPComponent.");
            return;
        }

        SubdomainConfiguration config = xmppComponent.getConfiguration();

		JabberwockyComponentConnection connection;
        ManagedExecutorService executor;
        String name = config.getProperties()
				.get(Configurables.EXECUTOR_SERVICE).getValue();

		if (logger.isLoggable(Level.INFO))
			logger.log(Level.INFO, "Connecting to {0}", config.getDomain());

		BeanManager bm = CDI.current().getBeanManager();

        //Fire preConnect        
		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Fire PreConnect event");
		CDIUtilities.fire(xmppComponent, config
				, ComponentLifecycleEvent.Phase.PreConnect, bm);

		try {
            connection = new JabberwockyComponentConnection(config);
            connection.connect();
        } catch (XMPPComponentException ex) {
            logger.log(Level.SEVERE, "Connection problem. Stopped", ex);
			return;
        }

        ctx.setAttribute(Constants.XMPP_COMPONENT_CONNECTION, connection);

        //Fire postConnect
		if (logger.isLoggable(Level.FINE))
			logger.log(Level.FINE, "Fire PostConnect event");
		CDIUtilities.fire(xmppComponent, config
				, ComponentLifecycleEvent.Phase.PostConnect, bm);

		//Start receiving packets
		if (logger.isLoggable(Level.INFO))
			logger.log(Level.INFO, "Starting XMPPComponent: {0}"
					, config.getProperties().get(Configurables.XMPP_COMPONENT));

        try {
            executor = (ManagedExecutorService) InitialContext.doLookup(name);
        } catch (NamingException ex) {
            logger.log(Level.SEVERE, "Cannot get executor service: {0} ", name);
            return;
        }                

		connection.start(executor, xmppComponent);		
	}

	@Override
	public void destroy() {

		boolean toStart = (Boolean)getServletContext().getAttribute(
				Constants.XMPP_COMPONENT_TO_START);
		if (!toStart)
			return;
        
        logger.log(Level.INFO, "Destroying Jabberwocky context");
        
		ServletContext ctx = getServletContext();
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

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {

		resp.setStatus(HttpServletResponse.SC_OK);
	}

	

}
