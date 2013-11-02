/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.jabberwocky.impl.core;

import at.jabberwocky.api.annotation.From;
import at.jabberwocky.api.annotation.To;
import at.jabberwocky.api.annotation.message.Body;

/**
 *
 * @author projects
 */
public class Constants {        
    
    public static final String DEFAULT_SERVICE = "java:comp/DefaultManagedExecutorService";
    
    public static final String XMPP_DEFAULTS = "defaults.properties";
    
    public static final String XMPP_COMPONENT_OBJECT = "at.jabberwocky.xmppComponent.object";
    public static final String XMPP_COMPONENT_CONFIGURATION = "at.jabberwocky.xmppComponent.config";
    public static final String XMPP_COMPONENT_CONNECTION = "at.jabberwocky.xmppComponent.connection";   
    
    public static final String NAME = "Jabberwocky XMPP framework";
	public static final String VERSION = "0.1 beta";

	public static final String NAMESPACE_DISCO_ITEMS = "http://jabber.org/protocol/disco#items";
	public static final String NAMESPACE_DISCO_INFO = "http://jabber.org/protocol/disco#info";
	public static final String NAMESPACE_XMPP_PING = "urn:xmpp:ping";
	public static final String NAMESPACE_LAST_ACTIVITY = "jabber:iq:last";
	public static final String NAMESPACE_ENTITY_TIME = "urn:xmpp:time";	
	public static final String NAMESPACE_VCARD_TEMP = "vcard-temp";
    public static final String NAMESPACE_PERSONAL_EVENTING = "http://jabber.org/protocol/caps";	
    public static final String NAMESPACE_WEB_INTERFACE = "urn:web:interface";

	public static final Class[] COMMON_ANNOTATIONS = {
		From.class, To.class	
	};

	public static final Class[] MESSAGE_ANNOTATIONS = {
		Body.class
	};

	public static final Class[] IQ_ANNOTATIONS = {

	};

	public static final Class[] PRESENCE_ANNOTATIONS = {

	};

	public static final Class[] FIELD_AND_PARAM_ANNOTATIONS = {

	};
}
