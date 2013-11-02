/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.impl.core.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Qualifier;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author project
 */
public class ReflectionHelper {

	public static boolean isJAXB(Class<?> type) {
		return (type.isAnnotationPresent(XmlRootElement.class));
	}

	public static List<Annotation> allCDIAnnotations(Annotation... annots) {
		List<Annotation> result = new ArrayList<>();
		for (Annotation a : annots)
			if (a.annotationType().isAnnotationPresent(Qualifier.class))
				result.add(a);
		return (result);
	}
}
