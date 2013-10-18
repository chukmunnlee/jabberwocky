/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.impl.core;

import at.jabberwocky.api.Configurables;
import at.jabberwocky.impl.core.io.JabberwockyComponentConnection;
import at.jabberwocky.impl.core.util.Utility;
import at.jabberwocky.spi.SubdomainConfiguration;
import at.jabberwocky.spi.XMPPComponent;
import at.jabberwocky.spi.XMPPComponentException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmpp.packet.Packet;

/**
 *
 * @author project
 */
public class JabberwockyXMPPComponent implements XMPPComponent {

	private static final Logger logger = Logger.getLogger(JabberwockyXMPPComponent.class.getName());

	protected List<ClassNode> messageHandlers = new LinkedList<>();
	protected List<ClassNode> iqHandlers = new LinkedList<>();
	protected List<ClassNode> presenceHandlers = new LinkedList<>();
    
    protected String discoInfoIdentityCategory = null;
	protected String discoInfoIdentityCategoryType = null;
	protected String discoInfoIdentityName = null;
    
    protected String description = null;
	
	protected SubdomainConfiguration config;
	protected JabberwockyComponentConnection connection;
    
    protected ExecutorService executorService;

	@Override
	public void initialize(Set<Class<?>> handlers, SubdomainConfiguration config)
			throws XMPPComponentException {
		
		this.config = config;           
        
        //Configure the disco#info 
        discoInfoIdentityCategory = Utility.property(config.getProperties()
                , Configurables.COMPONENT_CATEGORY, "");
        discoInfoIdentityCategoryType = Utility.property(config.getProperties()
                , Configurables.COMPONENT_TYPE, "");				
		discoInfoIdentityName = Utility.property(config.getProperties()
                , Configurables.COMPONENT_NAME, "Jabberwocky");				        

		for (Class<?> c : handlers) {
			ClassNode nh = null;

			logger.log(Level.WARNING, "Unknown handler type: {0}", c.getName());
		}
	}            

    @Override
    public List<Packet> processPacket(Packet packet) throws XMPPComponentException {
        
        List<Packet> result = null;
        
        if (logger.isLoggable(Level.FINE))
            logger.log(Level.FINE, "Incoming: {0}", packet.toString());
        
        //Process packet
        
        
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Outgoing:");
            for (Packet p: result)
                logger.log(Level.FINE, "   {0}", p.toString());
        }            
        
        return (result);
    }

    @Override
    public void start() {   
        if (logger.isLoggable(Level.FINE))
            logger.log(Level.FINE, "Starting Jabberwocky XMPPComponent");
    }        

    @Override
    public SubdomainConfiguration getConfiguration() {
        return (config);
    }        

}
