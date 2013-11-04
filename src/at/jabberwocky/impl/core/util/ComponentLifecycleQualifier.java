/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.jabberwocky.impl.core.util;

import at.jabberwocky.api.ComponentLifecycleEvent;
import at.jabberwocky.api.annotation.ComponentLifecycle;
import javax.enterprise.util.AnnotationLiteral;

/**
 *
 * @author projects
 */
public class ComponentLifecycleQualifier extends AnnotationLiteral<ComponentLifecycle> 
	implements ComponentLifecycle {

	private final ComponentLifecycleEvent.Phase phase;

	public ComponentLifecycleQualifier(ComponentLifecycleEvent.Phase p) {
		phase = p;
	}

	@Override
	public ComponentLifecycleEvent.Phase value() {
		return (phase);
	}
	
}
