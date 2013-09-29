/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.jabberwocky.impl.core;

import at.jabberwocky.api.annotation.Order;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 *
 * @author projects
 */
public class MethodNode implements Comparable<MethodNode> {

    private final Method methodHandle;
    private final Annotation[] methodAnnotations;
    private Class[] paramType;
    private Annotation[] paramAnnotation;

    public MethodNode(Method m, Annotation[] a) {
        methodHandle = m;
        methodAnnotations = a;
        paramType = new Class[0];
        paramAnnotation = new Annotation[0];
    }
/*
    public void parametersInfo(Annotation[] a, Class[] t) {
        paramType = t;
        paramAnnotation = a;
    } */

    public String methodName() {
        return (methodHandle.getName());
    }

    public Method method() {
        return (methodHandle);
    }

    public Annotation[] methodAnnotations() {
        return (methodAnnotations);
    }

    public <A extends Annotation> A getMethodAnnotation(Class<A> annot) {
        return (methodHandle.getAnnotation(annot));
    }

    public Class[] parameterType() {
        return (paramType);
    }

    public Annotation[] parameterAnnotations() {
        return (paramAnnotation);
    }

    public static MethodNode[] create(final Class<?> c, final Class<? extends Annotation>... superSet) {

        MethodNode result = null;
        List<MethodNode> nodes = new LinkedList<MethodNode>();
        
        //I could extend a class
        for (Method m : c.getDeclaredMethods()) {
            List<Annotation> methodAnnot = new LinkedList<Annotation>();
            //Run through all the methods            
            for (Class ac : superSet) {                
                Annotation an = m.getAnnotation(ac);
                if (null != an)
                    methodAnnot.add(an);                                    
            }

            if (methodAnnot.size() <= 0)
                continue;

            //Parameters
/*            
            boolean hasAnnotatedParam = false;
            Class paramType[] = m.getParameterTypes();
            Annotation paramAnnot[] = new Annotation[paramType.length];
            Annotation[][] declAnnot = m.getParameterAnnotations();
            for (int i = 0; i < declAnnot.length; i++) {
                for (int j = 0; j < declAnnot[i].length; j++) {
                    if (declAnnot[i][j].annotationType() == Bind.class) {
                        hasAnnotatedParam = true;
                        paramAnnot[i] = declAnnot[i][j];
                    }
                }
            }       */

            result = new MethodNode(m, methodAnnot.toArray(new Annotation[]{}));
            //if (hasAnnotatedParam)
                //result.parametersInfo(paramAnnot, paramType);
            nodes.add(result);            
        }

        Collections.sort(nodes);
        Collections.reverse(nodes);

        return (nodes.toArray(new MethodNode[]{}));
    }

    public Map<String, Object> accept(NodeVisitor v) {
        return (v.visit(this));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        System.out.println("---------> method node = " + methodHandle.getName());
        System.out.println("----------> param annotation = " + paramAnnotation.length);
        for (int i = 0; i < paramAnnotation.length; i++) {
            System.out.println("\t-----> " + paramAnnotation[i]);
            System.out.println("\t=====> " + paramType[i]);
            sb.append("[").append(paramAnnotation[i].toString()).append(":").append(paramType[i].toString()).append("], ");
        }
        
        String s = sb.toString();
        if (sb.length() > 0)
            return ("MethodNode{" + "method=" + methodHandle.getName() + ", parameters"
                    + "(" + paramAnnotation.length + ")=" + s.substring(0, s.length() - 2) + '}');
        return ("MethodNode{" + "method=" + methodHandle.getName() + ", parameters(0)}");
    }

    @Override
    public int compareTo(MethodNode o) {
        return (Utility.orderIt(methodHandle.getAnnotation(Order.class)
                , o.methodHandle.getAnnotation(Order.class)
                , methodAnnotations.length, o.methodAnnotations.length
                , methodHandle.getName(), o.method().getName()));
    }
}
