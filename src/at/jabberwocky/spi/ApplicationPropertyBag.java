/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.spi;

import java.util.*;
import javax.xml.bind.annotation.*;

/**
 *
 * @author project
 */
@XmlRootElement(name = "properties")
public class ApplicationPropertyBag implements Iterable<ApplicationProperty> {

    private List<ApplicationProperty> properties = new LinkedList<>();

    @XmlElement(name = "property")
    public List<ApplicationProperty> getProperties() {
        return (properties);
    }

    public void setProperties(List<ApplicationProperty> props) {        
        properties = props;
    }

    public void add(String key, String value) {
        add(new ApplicationProperty(key, value));
    }

    public void add(ApplicationProperty prop) {
        String k = prop.getName().trim();
        String v = prop.getValue();
        for (ApplicationProperty p : properties)
            if (k.equals(p.getName().trim())) {
                p.setValue(v);
                return;
            }
        properties.add(prop);
    }

    public void remove(String k) {
        String key = k.trim();
        for (ApplicationProperty p : properties)
            if (key.equals(p.getName().trim())) {
                properties.remove(p);
                return;
            }
    }

    public void remove(ApplicationProperty p) {
        remove(p.getName());
    }

    public ApplicationProperty get(String k) {
        String n = k.trim();
        for (ApplicationProperty p : properties)
            if (n.equals(p.getName().trim()))
                return (p);
        return (null);
    }

    @Override
    public Iterator<ApplicationProperty> iterator() {
        return (properties.listIterator());
    }
}
