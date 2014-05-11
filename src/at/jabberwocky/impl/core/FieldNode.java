/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.impl.core;

import at.jabberwocky.impl.core.util.ReflectionHelper;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author projects
 */
public class FieldNode {

    private final Field fieldHandle;
    private final Annotation[] fieldAnnotations;

    public FieldNode(Field f, Annotation[] a) {
        fieldHandle = f;
        fieldAnnotations = a;
    }

    public Field field() {
        return (fieldHandle);
    }

    public Annotation[] fieldAnnotations() {
        return (fieldAnnotations);
    }

    public <A extends Annotation> A getFieldAnnotation(Class<A> annot) {
        return (fieldHandle.getAnnotation(annot));
    }

    @Override
    public String toString() {
        return "FieldNode{" + "fieldHandle=" + fieldHandle.getName()
                + ", fieldAnnotations=" + fieldAnnotations + '}';
    }

    public static FieldNode[] create(final Class<?> c) {
        List<FieldNode> nodes = new LinkedList<>();
        Annotation an;
        for (Field f : c.getDeclaredFields()) {
            List<Annotation> a = ReflectionHelper.allCDIAnnotations(f.getAnnotations());
            if (a.size() > 0) {
                nodes.add(new FieldNode(f, a.toArray(new Annotation[]{})));
            }
        }
        return (nodes.toArray(new FieldNode[nodes.size()]));
    }
}
