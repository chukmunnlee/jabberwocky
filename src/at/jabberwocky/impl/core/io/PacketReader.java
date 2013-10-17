/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.jabberwocky.impl.core.io;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmpp.packet.IQ;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;
import org.xmpp.packet.Roster;

/**
 *
 * @author projects
 */
public class PacketReader implements Runnable {

	private static final Logger logger = Logger.getLogger(PacketReader.class.getName());

	private final XmlPullParser parser;
	private final Reader reader;
	private final DocumentFactory docFactory;

	private final PacketQueue queue;
	private final AtomicBoolean stop;

	public PacketReader(XmlPullParser p, Reader r, PacketQueue q)  {
		parser = p;
		reader = r;
		queue = q;
		docFactory = DocumentFactory.getInstance();
		stop = new AtomicBoolean(false);
	}
				
	@Override
	public void run() {
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "Starting PacketReader");
		while (!stop.get())
			try {
				Packet pkt = getPacket();
				if (null == pkt)
					continue;
				queue.write(pkt);
                if (logger.isLoggable(Level.FINE))
                    logger.log(Level.FINE, ">>> Inbound: {0}" + pkt.toString());
			} catch (Throwable ex) {
				logger.log(Level.SEVERE, "PacketReader.run() exception", ex);
			}
        if (logger.isLoggable(Level.INFO))
            logger.log(Level.INFO, "PacketReader stopped");
	}

	public void stop() {
		stop.set(true);
	}

	private Packet getPacket() throws Exception {
		Element doc = parseDocument().getRootElement();
		if (null == doc)
			return (null);

		String tag = doc.getName();
		if ("message".equals(tag))
			return (new Message(doc));

		if ("presence".equals(tag))
			return (new Presence(doc));

		Element query = doc.element("query");
        if (query != null && "jabber:iq:roster".equals(query.getNamespaceURI()))
            return new Roster(doc);

		return new IQ(doc);
	}

	//Copied wholesale from XPPPacketReader.java from Whack
    private Document parseDocument() throws DocumentException, IOException, XmlPullParserException {
        DocumentFactory df = docFactory;
        Document document = df.createDocument();
        Element parent = null;
        XmlPullParser pp = parser;
        int count = 0;
        while (true) {
            int type = -1;
            type = pp.nextToken();
            switch (type) {
                case XmlPullParser.PROCESSING_INSTRUCTION: {
                    String text = pp.getText();
                    int loc = text.indexOf(" ");
                    if (loc >= 0) {
                        document.addProcessingInstruction(text.substring(0, loc), text.substring(loc + 1));
                    }
                    else
                        document.addProcessingInstruction(text, "");
                    break;
                }
                case XmlPullParser.COMMENT: {
                    if (parent != null)
                        parent.addComment(pp.getText());
                    else
                        document.addComment(pp.getText());
                    break;
                }
                case XmlPullParser.CDSECT: {
                    String text = pp.getText();
                    if (parent != null) {
                        parent.addCDATA(text);
                    }
                    else {
                        if (text.trim().length() > 0) {
                            throw new DocumentException("Cannot have text content outside of the root document");
                        }
                    }
                    break;

                }
                case XmlPullParser.ENTITY_REF: {
                    String text = pp.getText();
                    if (parent != null) {
                        parent.addText(text);
                    }
                    else {
                        if (text.trim().length() > 0) {
                            throw new DocumentException("Cannot have an entityref outside of the root document");
                        }
                    }
                    break;
                }
                case XmlPullParser.END_DOCUMENT: {
                    return document;
                }
                case XmlPullParser.START_TAG: {
                    QName qname = (pp.getPrefix() == null) ? df.createQName(pp.getName(), pp.getNamespace()) : df.createQName(pp.getName(), pp.getPrefix(), pp.getNamespace());
                    Element newElement = null;
                    // Do not include the namespace if this is the start tag of a new packet
                    // This avoids including "jabber:client", "jabber:server" or
                    // "jabber:component:accept"
                    if ("jabber:client".equals(qname.getNamespaceURI()) ||
                            "jabber:server".equals(qname.getNamespaceURI()) ||
                            "jabber:component:accept".equals(qname.getNamespaceURI()) ||
                            "http://jabber.org/protocol/httpbind".equals(qname.getNamespaceURI())) {
                        newElement = df.createElement(pp.getName());
                    }
                    else {
                        newElement = df.createElement(qname);
                    }
                    int nsStart = pp.getNamespaceCount(pp.getDepth() - 1);
                    int nsEnd = pp.getNamespaceCount(pp.getDepth());
                    for (int i = nsStart; i < nsEnd; i++)
                        if (pp.getNamespacePrefix(i) != null)
                            newElement.addNamespace(pp.getNamespacePrefix(i), pp.getNamespaceUri(i));
                    for (int i = 0; i < pp.getAttributeCount(); i++) {
                        QName qa = (pp.getAttributePrefix(i) == null) ? df.createQName(pp.getAttributeName(i)) : df.createQName(pp.getAttributeName(i), pp.getAttributePrefix(i), pp.getAttributeNamespace(i));
                        newElement.addAttribute(qa, pp.getAttributeValue(i));
                    }
                    if (parent != null) {
                        parent.add(newElement);
                    }
                    else {
                        document.add(newElement);
                    }
                    parent = newElement;
                    count++;
                    break;
                }
                case XmlPullParser.END_TAG: {
                    if (parent != null) {
                        parent = parent.getParent();
                    }
                    count--;
                    if (count < 1) {
                        return document;
                    }
                    break;
                }
                case XmlPullParser.TEXT: {
                    String text = pp.getText();
                    if (parent != null) {
                        parent.addText(text);
                    }
                    else {
                        if (text.trim().length() > 0) {
                            throw new DocumentException("Cannot have text content outside of the root document");
                        }
                    }
                    break;
                }
                default: 
            }
        }
    }	

}
