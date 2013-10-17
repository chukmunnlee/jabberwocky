/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.jabberwocky.impl.core.io;


import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.io.XMLWriter;
import org.xmpp.packet.Packet;

/**
 *
 * @author projects
 */
public class PacketWriter implements Runnable {

	private static final Logger logger = Logger.getLogger(PacketWriter.class.getName());

	private final XMLWriter xmlWriter;
	private final Writer writer;
	private final AtomicBoolean stop;

	private final PacketQueue queue;

	private final Lock lock;

	private long lastWrite = System.currentTimeMillis();

	public PacketWriter(XMLWriter xw, Writer w, PacketQueue q) {
		xmlWriter = xw;
		writer = w;
		queue = q;
		stop = new AtomicBoolean(false);
		lock = new ReentrantLock();
	}

	@Override
	public void run() {
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "Starting PacketWriter");
		while (!stop.get()) {
			Packet pkt = queue.read();
			if (null == pkt)
				continue;
			lock.lock();
			try {
				write(pkt);
			} finally {
				lock.unlock();
			}
		}
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.OFF, "PacketWriter stopped");
	}

	public void stop() {
		stop.set(true);
	}

	public void write(Packet pkt) {
		lock.lock();
		try {
			xmlWriter.write(pkt.getElement());
			xmlWriter.flush();
			lastWrite = System.currentTimeMillis();
            if (logger.isLoggable(Level.FINER))
                logger.log(Level.FINER, "<<< Outbound: {0}" + pkt.toString());
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Cannot send packet: {0}", pkt);
			logger.log(Level.SEVERE, "Send packet exception", ex);
		} finally {
			lock.unlock();
		} 
	}

	public void keepAlive() {
		if ((System.currentTimeMillis() - lastWrite) >= 20000) {
			logger.log(Level.OFF, "Sending keep alive packet");
			directWrite(" ");
		}
	}

	private void directWrite(final String msg) {
		lock.lock();
		try {
			writer.write(msg);
			writer.flush();
		} catch (IOException ex) {
			//TODO do something
		} finally {
			lock.unlock();
		}
	}
}