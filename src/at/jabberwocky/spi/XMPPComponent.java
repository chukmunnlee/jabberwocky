/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.spi;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import org.xmpp.packet.Packet;

/**
 *
 * @author project
 */
public interface XMPPComponent {    
    
    public void initialize(Set<Class<?>> handlers, SubdomainConfiguration config)
            throws XMPPComponentException;
    
    public SubdomainConfiguration getConfiguration();
    
    public void start() throws XMPPComponentException;
    
    public List<Packet> processPacket(Packet packet) throws XMPPComponentException;
}
