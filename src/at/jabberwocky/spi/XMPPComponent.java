/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.spi;

import java.net.Socket;
import java.util.Set;

/**
 *
 * @author project
 */
public interface XMPPComponent {    
    
    public void initialize(Set<Class<?>> handlers, SubdomainConfiguration config)
            throws XMPPComponentException;
	
	public Socket connect() throws XMPPComponentException;
}
