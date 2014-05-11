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
@XmlRootElement(name = "property")
public class ApplicationProperty {

    private String name = "";
    private String value = "";

    public ApplicationProperty() {
    }

    public ApplicationProperty(String n, String v) {
        name = n;
        value = v;
    }

    @XmlAttribute(name = "name", required = true)
    public String getName() {
        return (name);
    }

    public void setName(String n) {
        name = n;
    }

    @XmlAttribute(name = "value", required = true)
    public String getValue() {
        return (value);
    }

    public void setValue(String v) {
        value = v;
    }

    @Override
    public String toString() {
        return ("name = " + name + ",value = " + value);
    }
}
