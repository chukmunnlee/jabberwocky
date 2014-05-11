/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.api.iq;

import at.jabberwocky.impl.core.util.Utility;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author project
 */
@XmlRootElement(name = "item")
public class ItemDescription {

    private String jid;
    private String node;
    private String name;

    public ItemDescription() {
    }

    public ItemDescription(String jid) {
        this.jid = jid;
    }

    public ItemDescription(String jid, String name) {
        this.jid = jid;
        this.name = name;
    }

    public ItemDescription(String jid, String node, String name) {
        this.jid = jid;
        this.node = node;
        this.name = name;
    }

    @XmlAttribute
    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    @Override
    public String toString() {
        return "Item{" + create() + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemDescription)) {
            return (false);
        }
        return (toString().equals(obj.toString()));
    }

    @Override
    public int hashCode() {
        return (toString().hashCode());
    }

    private String create() {
        StringBuilder sb = new StringBuilder();
        if (!Utility.isNullOrEmpty(jid.toString())) {
            sb.append("jid=").append(jid.toString());
        }
        if (!Utility.isNullOrEmpty(node)) {
            sb.append(", node=").append(node);
        }
        if (!Utility.isNullOrEmpty(name)) {
            sb.append(", name=").append(name);
        }
        return (sb.toString());
    }
}
