/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.impl.core.util;

import at.jabberwocky.api.ComponentLifecycleEvent;
import at.jabberwocky.spi.SubdomainConfiguration;
import at.jabberwocky.spi.XMPPComponent;
import javax.enterprise.inject.spi.BeanManager;
import org.xmpp.packet.JID;

/**
 *
 * @author project
 */
public class CDIUtilities {

    public static void fire(XMPPComponent comp, SubdomainConfiguration config, ComponentLifecycleEvent.Phase p, BeanManager bm) {

        ComponentLifecycleEvent evt = new ComponentLifecycleEvent(comp);

        evt.setComponentJID(new JID(config.getName() + "." + config.getDomain()));
        evt.setConfiguration(config);
        evt.setContext(null);
        evt.setPhase(p);

        bm.fireEvent(evt, new ComponentLifecycleQualifier(p));
    }

}
