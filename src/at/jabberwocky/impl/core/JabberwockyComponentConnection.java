/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.impl.core;

import at.jabberwocky.impl.core.util.*;
import at.jabberwocky.spi.*;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import org.dom4j.io.*;
import org.xmlpull.v1.*;
import org.xmpp.packet.*;

/**
 *
 * @author projects
 */
public class JabberwockyComponentConnection implements Runnable {

    private static final Logger logger = Logger.getLogger(
            JabberwockyComponentConnection.class.getName());

    private final SubdomainConfiguration config;

    private Socket connection = null;
    private Reader reader = null;
    private Writer writer = null;
    private XmlPullParser xmlParser = null;
    private XMLWriter xmlWriter = null;
    
    private JID subdomainJID = null;
    private JID domainJID = null;
    
    private String connectionId = null;
    private String errorTag = null;

    public JabberwockyComponentConnection(SubdomainConfiguration config) {
        this.config = config;
    }

    public void connect() throws XMPPComponentException {
        
        if (logger.isLoggable(Level.FINER))
            logger.log(Level.FINER, "Starting connection");
        
        try {
            connection = new Socket(config.getDomain(), config.getPort());
            connection.setSoTimeout(0);
        } catch (IOException ex) {
            connection = null;
            logger.log(Level.SEVERE, "Cannnot connect to {0}:{1}", 
                    new Object[]{config.getDomain(), config.getPort()});
            throw new XMPPComponentException(
                    "Creating socket connection to " + config.getDomain(), ex);
        }
        
        if (logger.isLoggable(Level.FINER))
            logger.log(Level.FINER, "Initializing XML parser");
        
        try {
            reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "UTF-8"));
            writer = new BufferedWriter(
                    new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Cannot create reader/writer", ex);
            close();
            throw new XMPPComponentException("Cannot create reader/writer", ex);
        }
        
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xmlParser = factory.newPullParser();
            xmlParser.setInput(reader);
            xmlWriter = new XMLWriter(writer);
        } catch (XmlPullParserException ex) {
            logger.log(Level.SEVERE, "Cannot initialze XML parser", ex);
            close();
            throw new XMPPComponentException("Cannot initialize XML parser", ex);            
        }
        
        subdomainJID = new JID(config.getName() + "." + config.getDomain());      
        domainJID = new JID(config.getDomain());
        
        try {
            if (!performHandshake(config.getName(), config.getSharedSecret())) {
                logger.log(Level.SEVERE, "Handshake error: {0}", errorTag);
                throw new XMPPComponentException("Handshake error: " + errorTag);
            }
            if (logger.isLoggable(Level.FINER))
                logger.log(Level.FINER, "Handshake successful");
        } catch (IOException | XmlPullParserException ex) {
            logger.log(Level.SEVERE, "Error during performHandshake()", ex);
            throw new XMPPComponentException("Error during performHandshake()", ex);
        }
    }
    
    private boolean performHandshake(String subdomain, String secretKey) 
            throws IOException, XmlPullParserException {
        
		StringBuilder stream = new StringBuilder();

		logger.log(Level.OFF, "Opening stream to {0}", subdomain);
		stream.append("<stream:stream");
		stream.append(" xmlns=\"jabber:component:accept\"");
		stream.append(" xmlns:stream=\"http://etherx.jabber.org/streams\"");
		stream.append(" to=\"").append(subdomain).append("\">");
		writer.write(stream.toString());
		writer.flush();

		//Jump to the return stream
		for (int evtType = xmlParser.getEventType(); evtType != XmlPullParser.START_TAG;)
			evtType = xmlParser.next();

		connectionId = xmlParser.getAttributeValue("", "id");

		logger.log(Level.OFF, "Sending handshake");
		stream = new StringBuilder();
		stream.append("<handshake>");
		stream.append(StringUtils.hash(connectionId + secretKey));
		stream.append("</handshake>");
		writer.write(stream.toString());
		writer.flush();

		xmlParser.next();

		for (int evtType = xmlParser.getEventType(); evtType != XmlPullParser.START_TAG;)
			evtType = xmlParser.next();

		if ("handshake".equals(xmlParser.getName())) {
			logger.log(Level.OFF, "Connection established");
			return (true);
		}

		errorTag = xmlParser.getName();
		if ("not-authorized".equals(errorTag))
			logger.log(Level.SEVERE, "Authentication failure");
		else if ("error".equals(errorTag))
			logger.log(Level.SEVERE, "Error: {0}", errorTag);
		else
			logger.log(Level.SEVERE, "Unexpected tag: {0}", errorTag);

		return (false);
    }
    
    public JID getSubdomain() {
        return (subdomainJID);
    }
    
    public JID getDomain() {
        return (domainJID);
    }
    
    public String getConnectionId() {
        return (connectionId);
    }
    
    public String getError() {
        return (errorTag);
    }
    
    public void close() {
        if (null != connection) 
            try {
                connection.close();
            } catch (IOException ex) { /* ignore */ }
    }

    @Override
    public void run() {        
    }        

}
