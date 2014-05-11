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
@XmlRootElement(name = "feature")
public class FeatureDescription {

    private String var;

    public FeatureDescription() {
    }

    public FeatureDescription(String var) {
        this.var = var;
    }

    @XmlAttribute
    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    @Override
    public String toString() {
        return "Feature{" + create() + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FeatureDescription)) {
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
        if (!Utility.isNullOrEmpty(var.toString())) {
            sb.append("var=").append(var.toString());
        }
        return (sb.toString());
    }

}
