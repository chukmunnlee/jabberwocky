/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.impl.core;

import at.jabberwocky.impl.core.util.Utility;
import at.jabberwocky.api.annotation.Order;
import java.lang.annotation.Annotation;

import java.util.*;
import javax.enterprise.context.NormalScope;
import javax.inject.Scope;



/**
 *
 * @author projects
 */
public class ClassNode implements Comparable<ClassNode>, Iterable<MethodNode> {

    private final Class<?> handler;
    private final Annotation[] annotations;    
    private final boolean cdi;

    private MethodNode[] methods;
    private FieldNode[] fields;
    
    public ClassNode(Class<?> h, Annotation[] a) {
        handler = h;
        annotations = a;
        methods = new MethodNode[0];   
        cdi = (handler.isAnnotationPresent(NormalScope.class) 
				|| handler.isAnnotationPresent(Scope.class));
    }
    
    public boolean isCDIManaged() {
        return (cdi);
    }

    public Annotation[] annotations() {
        return (annotations);
    }

    public int annotationCount() {
        return (annotations.length);
    }

    public void setMethods(MethodNode[] m) {
        methods = m;
    }

    public MethodNode[] getMethods() {
        return (methods);
    }

    public void setFields(FieldNode[] f) {
        fields = f;
    }
    public FieldNode[] getFields() {
        return (fields);
    }

    public Object instantiate() throws InstantiationException, IllegalAccessException {
        return (handler.newInstance());
    }

    public String className() {
        return (handler.getName());
    }
    
    public Class<?> getJavaClass() {
        return (handler);
    }

    public boolean matches(Class<? extends Annotation>... toCompare) {

        for (Class<? extends Annotation> c : toCompare) {            
            for (Annotation m : annotations) {
                if (m.annotationType() != c)
                    return (false);                
            }
        }

        return (true);
    }

    public <A extends Annotation> A getAnnotation(Class<A> annot) {
        return (handler.getAnnotation(annot));
    }

    public Map<String, Object> accept(NodeVisitor v) {
        return (v.visit(this));
    }

    public static ClassNode create(Class<?> c, Class<? extends Annotation> mainAnnot
            , Class<? extends Annotation>[] msgSpecific) {
        
        Set<Annotation> annotSet = new HashSet<>();        
        if (!c.isAnnotationPresent(mainAnnot))
            return (null);
        
        annotSet.add(c.getAnnotation(mainAnnot));

		//Common annotations
		for (Class<? extends Annotation> a: Constants.COMMON_ANNOTATIONS)
			if (c.isAnnotationPresent(a))
				annotSet.add(c.getAnnotation(a));
                
		//Handler specific
        for (Class<? extends Annotation> a : msgSpecific) 
            if (c.isAnnotationPresent(a))
                annotSet.add(c.getAnnotation(a));         

        ClassNode cn = new ClassNode(c, annotSet.toArray(new Annotation[]{}));
        //cn.setFields(FieldNode.create(c, Inject.class));

        return (cn);
    }

    @Override
    public int compareTo(ClassNode o) {
        return (Utility.orderIt(handler.getAnnotation(Order.class)
                , o.handler.getAnnotation(Order.class)
                , annotations.length, o.annotationCount()
                , handler.getClass().getName()
                , o.className()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Annotation a : annotations) {
            sb.append(a.toString()).append(", ");
        }
        String s = sb.toString();
        return "ClassNode{" + "handler=" + handler.getName() + ", annotations="
                + s.substring(0, s.length() - 2) + '}';
    }

    private class MethodNodeIterator implements Iterator<MethodNode> {

        private int idx = 0;

        @Override
        public boolean hasNext() {
            return (idx < methods.length);
        }

        @Override
        public MethodNode next() {
            return (methods[idx++]);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }        
    }

    @Override
    public Iterator<MethodNode> iterator() {
        return (new MethodNodeIterator());
    }
}
