/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.jabberwocky.impl.core;

import java.util.HashMap;
import java.util.Map;
import org.xmpp.packet.Packet;

/**
 *
 * @author project
 */
public class CurrentPacketContext {
    
    private final ThreadLocal<Map<String, Object>> perThread;
    private static final CurrentPacketContext instance;
    
    static {
        instance = new CurrentPacketContext();
    }
    
    private CurrentPacketContext() {
        perThread = new ThreadLocal<>();
    }
    
    public Map<String, Object> start(Packet pkt) {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put(Constants.KEY_PACKET, pkt);
        perThread.set(ctx);
        return (ctx);
    }
    
    public Map<String, Object> get() {
        return (perThread.get());
    }
    
    public Object get(final String key) {
        return (perThread.get().get(key));
    }
    
    public void put(final String key, final Object val) {
        perThread.get().put(key, val);
    }
    
    public void end() {        
        perThread.remove();
    }
    
    public static CurrentPacketContext getInstance() {
        return (instance);
    }
}
