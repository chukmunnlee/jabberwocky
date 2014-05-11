/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.spi;

import javax.xml.bind.annotation.*;

/**
 *
 * @author project
 */
@XmlRootElement(name = "subdomain")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class SubdomainConfiguration {

    private String name;
    private String domain;
    private String secret;
    private int port = 5275;
    private ApplicationPropertyBag properties = new ApplicationPropertyBag();

    @XmlAttribute(required = true)
    public String getName() {
        return (name);
    }

    public void setName(String n) {
        name = n;
    }

    public int getPort() {
        return (port);
    }

    public void setPort(int p) {
        port = p;
    }

    @XmlElement(required = true)
    public String getDomain() {
        return (domain);
    }

    public void setDomain(String d) {
        domain = d;
    }

    @XmlElement(name = "shared-secret", required = true)
    public String getSharedSecret() {
        return (secret);
    }

    public void setSharedSecret(String s) {
        secret = s;
    }

    public ApplicationPropertyBag getProperties() {
        return (properties);
    }

    public void setProperties(ApplicationPropertyBag prop) {
        properties = prop;
    }

    @Override
    public String toString() {
        return "SubdomainConfiguration{" + "name=" + name + ", domain=" + domain + ", secret=" + secret + ", port=" + port + '}';
    }

}
