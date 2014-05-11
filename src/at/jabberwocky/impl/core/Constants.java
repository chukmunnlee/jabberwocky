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

    public static final String XMPP_DEFAULTS = "at/jabberwocky/impl/core/defaults.properties";

    public static final String XMPP_COMPONENT_TO_START = "at.jabberwocky.xmppComponent.toStart";

    public static final String XMPP_COMPONENT_OBJECT = "at.jabberwocky.xmppComponent.object";
    public static final String XMPP_COMPONENT_CONFIGURATION = "at.jabberwocky.xmppComponent.config";
    public static final String XMPP_COMPONENT_CONNECTION = "at.jabberwocky.xmppComponent.connection";
    public static final String XMPP_CONNECTION_LOCK = "at.jabberwocky.xmppComponent.connection.lock";

    public static final String NAME = "Jabberwocky XMPP framework";
    public static final String VERSION = "0.1 beta";

    public static final Class[] COMMON_ANNOTATIONS = {
        From.class, To.class
    };

    public static final Class[] MESSAGE_ANNOTATIONS = {
        Body.class
    };

    public static final Class[] IQ_ANNOTATIONS = {};

    public static final Class[] PRESENCE_ANNOTATIONS = {};

    public static final Class[] FIELD_AND_PARAM_ANNOTATIONS = {};
    
    public static final String KEY_PACKET = "packet";
}
