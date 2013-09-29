/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.jabberwocky.impl.core;

import java.util.Map;

/**
 *
 * @author projects
 */
public interface NodeVisitor {
    public Map<String, Object> visit(ClassNode classNode);
    public Map<String, Object> visit(MethodNode methodNode);

}
