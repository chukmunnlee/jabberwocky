/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.impl.core.util;

import at.jabberwocky.api.annotation.Order;
import at.jabberwocky.spi.ApplicationProperty;
import at.jabberwocky.spi.ApplicationPropertyBag;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author project
 */
public class Utility {
    
    private static final Logger logger = Logger.getLogger(Utility.class.getName());

	public static boolean isNullOrEmpty(String s) {
		return ((null == s) || (s.trim().length() <= 0));
	}

	public static int orderIt(Order meOrd, Order youOrd, int meAnnotCount, int youAnnotCount, String meName, String youName) {
		if ((null != meOrd) || (null != youOrd)) {
			if (null != meOrd) {
				if (null != youOrd) {
					if (meOrd.value() < youOrd.value())
						return (1);
					else if (meOrd.value() > youOrd.value())
						return (-1);
					else
						return (0);
				} else
					return (1);
			} else if (null != youOrd)
				return (-1);
		}

		if (meAnnotCount < youAnnotCount)
			return (-1);
		else if (meAnnotCount > youAnnotCount)
			return (1);

		return (meName.compareTo(youName) * -1);
	}
    
    public static int property(ApplicationPropertyBag props, String name, int defaultValue) {
        ApplicationProperty prop = props.get(name);
        if (null == prop)
            return (defaultValue);
        try {
            return (Integer.parseInt(prop.getValue()));
        } catch (NumberFormatException ex) {            
            logger.log(Level.WARNING, "Invalid value {0} for property {1}", new Object[]{prop.getValue(), name});            
            return (defaultValue);
        }
    }

}
