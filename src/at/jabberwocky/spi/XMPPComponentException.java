/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.jabberwocky.spi;

/**
 *
 * @author project
 */
public class XMPPComponentException extends Exception {

    public XMPPComponentException() {
        super();
    }    
    public XMPPComponentException(String msg) {
        super(msg);
    }            
    public XMPPComponentException(Exception cause) {
        super(cause);
    }
    public XMPPComponentException(String msg, Exception cause) {
        super(msg, cause);
    }
    
}
