/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.test;

import at.jabberwocky.impl.core.*;
import at.jabberwocky.spi.*;
import java.util.logging.*;
import org.junit.*;

import static org.junit.Assert.*;

/**
 *
 * @author projects
 */
public class ComponentConnectionTest {

    private SubdomainConfiguration config;

    @Before
    public void setup() {
        config = new SubdomainConfiguration();
        config.setDomain("batcomputer");
        config.setName("jabberwocky");
        config.setSharedSecret("jabberwocky");
    }

    @Test
    public void shouldConnect() throws XMPPComponentException {        
        JabberwockyComponentConnection connection = new JabberwockyComponentConnection(config);
        
        Logger logger = LogManager.getLogManager()
                .getLogger(JabberwockyComponentConnection.class.getName());
        
        logger.setLevel(Level.ALL);
        connection.connect();
        try {
            Thread.sleep(30 * 1000);
        } catch (InterruptedException ex) { }
        connection.close();
    }
}
