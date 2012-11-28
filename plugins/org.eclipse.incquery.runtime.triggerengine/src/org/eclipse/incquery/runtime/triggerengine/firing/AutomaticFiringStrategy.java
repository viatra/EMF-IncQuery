package org.eclipse.incquery.runtime.triggerengine.firing;

import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.triggerengine.api.Activation;
import org.eclipse.incquery.runtime.triggerengine.api.ActivationMonitor;

/**
 * This class automatically fires the applicable activations 
 * which are present in the given {@link ActivationMonitor}.
 * It is used by the Validation Framework to automatically 
 * create/update/remove problem markers when it is needed.
 * 
 * @author Tamas Szabo
 *
 */
public class AutomaticFiringStrategy implements IUpdateCompleteListener {

    private final ActivationMonitor monitor;
    
    public AutomaticFiringStrategy(ActivationMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void updateComplete() {
        if(monitor != null) {
            for (Activation<? extends IPatternMatch> a : monitor.getActivations()) {
                a.fire();
            }
            monitor.clear();
        }
    }
    
}
