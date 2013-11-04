/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.test;

import at.jabberwocky.impl.core.io.JabberwockyComponentConnection;
import at.jabberwocky.impl.core.*;
import at.jabberwocky.spi.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;
import org.junit.*;

import static org.junit.Assert.*;
import org.xmpp.packet.Packet;

/**
 *
 * @author projects
 */
public class ComponentConnectionTest implements XMPPComponent {

    private SubdomainConfiguration config;
    private ExecutorService service;

    @Before
    public void setup() {
        config = new SubdomainConfiguration();
        config.setDomain("batcomputer");
        config.setName("jabberwocky");
        config.setSharedSecret("growl");
        
        service = Executors.newFixedThreadPool(3);
    }

    @Test
    public void shouldConnect() throws XMPPComponentException {        
        JabberwockyComponentConnection connection = new JabberwockyComponentConnection(config);
        
        Logger logger = LogManager.getLogManager()
                .getLogger(JabberwockyComponentConnection.class.getName());
        
        logger.setLevel(Level.ALL);
        connection.connect();
        
        connection.start(service,  this);
        
        try {
            Thread.sleep(30 * 1000);
        } catch (InterruptedException ex) { }
        connection.close();
    }

    @Override
    public void initialize(Set<Class<?>> handlers, SubdomainConfiguration config) 
            throws XMPPComponentException {  }

    @Override
    public SubdomainConfiguration getConfiguration() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Packet> processPacket(Packet packet) throws XMPPComponentException {
        System.out.println("------> received packet: " + packet.toString());
        return (new LinkedList<>());
    }

	@Override
	public void preConnect() throws XMPPComponentException { }

	@Override
	public void postConnect() throws XMPPComponentException { }

	@Override
	public void preDisconnect() throws XMPPComponentException { }

	@Override
	public void postDisconnect() throws XMPPComponentException { }
}
