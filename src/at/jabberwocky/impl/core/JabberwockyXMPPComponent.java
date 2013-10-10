/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.impl.core;

import at.jabberwocky.api.annotation.IQ;
import at.jabberwocky.api.annotation.Message;
import at.jabberwocky.api.annotation.Presence;
import at.jabberwocky.spi.SubdomainConfiguration;
import at.jabberwocky.spi.XMPPComponent;
import at.jabberwocky.spi.XMPPComponentException;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author project
 */
public class JabberwockyXMPPComponent implements XMPPComponent {

	private static final Logger logger = Logger.getLogger(JabberwockyXMPPComponent.class.getName());

	protected List<ClassNode> messageHandlers = new LinkedList<ClassNode>();
	protected List<ClassNode> iqHandlers = new LinkedList<ClassNode>();
	protected List<ClassNode> presenceHandlers = new LinkedList<ClassNode>();
	
	protected SubdomainConfiguration config;
	protected JabberwockyComponentConnection connection;

	@Override
	public void initialize(Set<Class<?>> handlers, SubdomainConfiguration config)
			throws XMPPComponentException {
		
		this.config = config;               

		for (Class<?> c : handlers) {
			ClassNode nh = null;

			logger.log(Level.WARNING, "Unknown handler type: {0}", c.getName());
		}
	}

    @Override
    public void connect() throws XMPPComponentException {        
        connection = new JabberwockyComponentConnection(config);        
    }        
		

}
