/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.jabberwocky.impl.core.io;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xmpp.packet.Packet;

/**
 *
 * @author projects
 */
public class PacketQueue {

	private static final Logger logger = Logger.getLogger(PacketQueue.class.getName());

	private final BlockingQueue<Packet> queue;
	private final Semaphore semaphore;

	private String name;

	public PacketQueue(int size, String name) {
		queue = new ArrayBlockingQueue<Packet>(size, true);
		semaphore = new Semaphore(0);
		this.name = name;
	}

	public void write(Packet p) {
		try {
			queue.put(p);
			semaphore.release();
		} catch (InterruptedException ex) {
			logger.log(Level.INFO, "Ignore: writing to queue interrupted", ex);
		}
	}

	public int size() {
		return (queue.size());
	}

	//Unsynchronized
	public Collection<Packet> drain() {
		Collection<Packet> coll = new HashSet<Packet>();
		queue.drainTo(coll);
		return (coll);
	}

	public Packet read() {
		try {
			semaphore.acquire();
			return (queue.poll());
		} catch (InterruptedException ex) {
			logger.log(Level.INFO, "Ignore: reading from queue interrupted", ex);
		}
		return (null);
	}
}
