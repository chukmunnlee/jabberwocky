/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.impl.core;

import at.jabberwocky.api.Configurables;
import at.jabberwocky.api.annotation.IQ;
import at.jabberwocky.api.annotation.Message;
import at.jabberwocky.api.annotation.Presence;
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

import static at.jabberwocky.impl.core.Constants.*;
import java.lang.annotation.Annotation;
import java.util.Collections;

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

		ClassNode nh;
		for (Class<?> c : handlers) {

			nh = null;

			if (null != (nh = isHandlerType(Message.class, c, MESSAGE_ANNOTATIONS))) {
				nh.setMethods(MethodNode.create(c, MESSAGE_ANNOTATIONS));
				nh.setFields(FieldNode.create(c));
				messageHandlers.add(nh);
				continue;
			}

			if (null != (nh = isHandlerType(IQ.class, c, IQ_ANNOTATIONS))) {
				nh.setMethods(MethodNode.create(c, IQ_ANNOTATIONS));
				nh.setFields(FieldNode.create(c));
				iqHandlers.add(nh);
				continue;
			}

			if (null != (nh = isHandlerType(Presence.class, c, PRESENCE_ANNOTATIONS))) {
				nh.setMethods(MethodNode.create(c, PRESENCE_ANNOTATIONS));
				nh.setFields(FieldNode.create(c));
				presenceHandlers.add(nh);
				continue;
			}			

			logger.log(Level.WARNING, "Unknown handler type: {0}", c.getName());
		}

		Collections.sort(messageHandlers);
		Collections.reverse(messageHandlers);
		Collections.sort(iqHandlers);
		Collections.reverse(iqHandlers);
		Collections.sort(presenceHandlers);
		Collections.reverse(presenceHandlers);

		if (logger.isLoggable(Level.INFO)) {
			logger.log(Level.INFO, "Message handlers");
			for (ClassNode cn : messageHandlers)
				logger.log(Level.INFO, "  Message handler {0}", cn);

			logger.log(Level.INFO, "IQ handlers");
			for (ClassNode cn : iqHandlers)
				logger.log(Level.INFO, "  IQ handler {0}", cn);

			logger.log(Level.INFO, "Presence handlers");
			for (ClassNode cn : presenceHandlers)
				logger.log(Level.INFO, "  Presence handler {0}", cn);	
		}
	}            

	@Override
	public void preConnect() {

        if (logger.isLoggable(Level.FINE))
            logger.log(Level.FINE, "preConnect: Jabberwocky XMPPComponent");

        //Configure the disco#info 
        discoInfoIdentityCategory = Utility.property(config.getProperties()
                , Configurables.COMPONENT_CATEGORY, "");
        discoInfoIdentityCategoryType = Utility.property(config.getProperties()
                , Configurables.COMPONENT_TYPE, "");				
		discoInfoIdentityName = Utility.property(config.getProperties()
                , Configurables.COMPONENT_NAME, "Jabberwocky");				        
	}

	@Override
	public void postConnect() {

        if (logger.isLoggable(Level.FINE))
            logger.log(Level.FINE, "postConnect: Jabberwocky XMPPComponent");
	}

	@Override
	public void preDisconnect() {

        if (logger.isLoggable(Level.FINE))
            logger.log(Level.FINE, "preDisconnect: Jabberwocky XMPPComponent");
	}

	@Override
	public void postDisconnect() {

        if (logger.isLoggable(Level.FINE))
            logger.log(Level.FINE, "preDisconnect: Jabberwocky XMPPComponent");
	}
	
    @Override
    public List<Packet> processPacket(Packet packet) throws XMPPComponentException {
        
        List<Packet> result = null;
        
        if (logger.isLoggable(Level.FINE))
            logger.log(Level.FINE, "Incoming: {0}", packet.toString());
        
        //Process packet
		System.out.println("--------> in processPacket: ");
		System.out.println("\t" + packet);
        
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Outgoing:");
            for (Packet p: result)
                logger.log(Level.FINE, "   {0}", p.toString());
        }            
        
        return (result);
    }

    @Override
    public SubdomainConfiguration getConfiguration() {
        return (config);
    }        

	private ClassNode isHandlerType(Class<? extends Annotation> annot, Class c
			, Class[] handlerAnnotation) {
		ClassNode nh = ClassNode.create(c, annot, handlerAnnotation);
		if (null == nh)
			return (null);

		nh.setMethods(MethodNode.create(c, handlerAnnotation));
		return (nh);
	}

}
