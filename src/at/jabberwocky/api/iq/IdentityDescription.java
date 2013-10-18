/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.jabberwocky.api.iq;

/**
 *
 * @author project
 */
import at.jabberwocky.impl.core.util.Utility;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author projects
 */
@XmlRootElement(name="identity")
public class IdentityDescription {
    
    private String category;
    private String type;
    private String name;

    public IdentityDescription() {
    }
    
    public IdentityDescription(String category, String type) {
        this.category = category;
        this.type = type;
    }

    public IdentityDescription(String category, String type, String name) {
        this.category = category;
        this.type = type;
        this.name = name;
    }

    @XmlAttribute
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {        
        return "Identity{" + create() + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IdentityDescription))
            return (false);
        return (toString().equals(obj.toString()));
    }

    @Override
    public int hashCode() {
        return (toString().hashCode());
    }        
    
    private String create() {
        StringBuilder sb = new StringBuilder();
        if (!Utility.isNullOrEmpty(category.toString()))
            sb.append("category=").append(category.toString());
        if (!Utility.isNullOrEmpty(type))
            sb.append(", type=").append(type);
        if (!Utility.isNullOrEmpty(name))
            sb.append(", name=").append(name);
        return (sb.toString());
    }  
    
}
