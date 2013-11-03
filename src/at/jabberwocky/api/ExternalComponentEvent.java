/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.jabberwocky.api;

import at.jabberwocky.spi.SubdomainConfiguration;
import java.util.Date;
import java.util.EventObject;
import org.xmpp.packet.JID;

/**
 *
 * @author project
 */
public class ExternalComponentEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	public enum Phase { PreConnect, PostConnect, PreDisconnect, PostDisconnect };

	private Phase phase;
	private SubdomainConfiguration config;
	private ComponentContext ctx;
	private JID jid;
	private final long time = (new Date()).getTime();

	public ExternalComponentEvent() {
		super(null);
	}

	public ExternalComponentEvent(Object obj) {
		super (obj);
	}

	public Phase getPhase() {
		return phase;
	}
	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public SubdomainConfiguration getConfiguration() {
		return (config);
	}
	public void setConfiguration(SubdomainConfiguration c) {
		config = c;
	}

	public ComponentContext getContext() {
		return (ctx);
	}
	public void setContext(ComponentContext ctx) {
		this.ctx = ctx;
	}

	public JID getComponentJID() {
		return (jid);
	}
	public void setComponentJID(JID j) {
		jid = j;
	}

	public long getTimestamp() {
		return (time);
	}

}
