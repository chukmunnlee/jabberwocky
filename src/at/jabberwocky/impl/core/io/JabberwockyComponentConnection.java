/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.impl.core.io;

import at.jabberwocky.api.Configurables;
import at.jabberwocky.impl.core.util.*;
import at.jabberwocky.spi.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
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
    
    private XMPPComponent xmppComponent;
    
    private boolean stop = false;

    private Socket connection;
    private Reader reader;
    private Writer writer;
    private XmlPullParser xmlParser;
    private XMLWriter xmlWriter;
    
    private JID subdomainJID;
    private JID domainJID;
    
    private String connectionId = null;
    private String errorTag = null;
        
    private ExecutorService executorService;
    
    private PacketWriter pktWriter;
    private Future<?> writerFuture;
    private PacketQueue outQueue;
    
    private PacketReader pktReader;
    private Future<?> readerFuture;
    private PacketQueue inQueue;
    
    private Future<?> thisFuture;      
    
    private KeepAlive keepAliveThread;
    private Future<?> keepAliveFuture;
    
    private class KeepAlive implements Runnable {
        
        private PacketWriter pktWriter;
        private boolean stop = false;
        private final long sleep;
        
        public KeepAlive(PacketWriter pw, long sleep) {
            pktWriter = pw;
            this.sleep = sleep;
        }
        
        public void stop() { stop = true; }

        @Override
        public void run() {
            if (logger.isLoggable(Level.FINER))
                logger.log(Level.FINER, "Starting keepalive thread");
            while (!stop) {
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException ex) {
                    logger.log(Level.WARNING, "Keep alive thread interrupted. Exiting");
                    return;
                }
                pktWriter.keepAlive();
             }
            if (logger.isLoggable(Level.FINER))
                logger.log(Level.FINER, "Stopping keepalive thread");
        }        
    }

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
        if (null != connection) {
            if (logger.isLoggable(Level.INFO))
                logger.log(Level.INFO, "Terminating all IOs");
            
            stop = true;
            
            keepAliveThread.stop();
            pktReader.stop();
            pktWriter.stop();
            
            keepAliveFuture.cancel(false);
            thisFuture.cancel(false);
            readerFuture.cancel(false);
            writerFuture.cancel(false);
            
            try {
                connection.close();
            } catch (IOException ex) { /* ignore */ }        
        }
    }
    
    public void start(ExecutorService ex, XMPPComponent comp) {
        ApplicationPropertyBag props = config.getProperties();        
        executorService = ex;
        
        xmppComponent = comp;
        
        inQueue = new PacketQueue(Utility.property(props, Configurables.IN_QUEUE, 5)
                , "In queue");
        outQueue = new PacketQueue(Utility.property(props, Configurables.OUT_QUEUE, 5)
                , "Out queue");
        
        pktReader = new PacketReader(xmlParser, reader, inQueue);
        pktWriter = new PacketWriter(xmlWriter, writer, outQueue
                , Utility.property(props, Configurables.KEEP_ALIVE, 20000L));
        
        keepAliveThread = new KeepAlive(pktWriter
                , Utility.property(props, Configurables.KEEP_ALIVE, 20000L));
        
        readerFuture = ex.submit(pktReader);
        writerFuture = ex.submit(pktWriter);
        
        thisFuture = ex.submit(this);          
        keepAliveFuture = ex.submit(keepAliveThread);
    }

    @Override
    public void run() {  
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "Starting packet dispatcher thread");
        
        List<Packet> result;
        
        while (!stop) {
            Packet in = inQueue.read();
            if (null == in)
                continue;
            if (logger.isLoggable(Level.FINER))
                logger.log(Level.FINER, "Routing to component: {0}", in.toString());
            try {
                result = xmppComponent.processPacket(in);
                if ((null != result) || (result.size() > 0))
                    for (Packet p: result) {
                    if (logger.isLoggable(Level.FINER))
                        logger.log(Level.FINER, "Dispatching to out queue: {0}", p.toString());
                    outQueue.write(p);
                }                    
            } catch (XMPPComponentException ex) {
                logger.log(Level.WARNING, "Error processing packet", ex);
            }
        }
    }        

}
