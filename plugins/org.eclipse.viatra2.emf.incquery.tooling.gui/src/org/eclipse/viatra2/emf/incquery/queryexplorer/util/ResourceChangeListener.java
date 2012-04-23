package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;

import com.google.inject.Injector;

public class ResourceChangeListener implements IResourceChangeListener {
	private final Injector injector;

	public ResourceChangeListener(Injector injector) {
		this.injector = injector;
	}

	public void resourceChanged(IResourceChangeEvent event) {	
		if (event.getType() == IResourceChangeEvent.PRE_BUILD) {
			try {
				event.getDelta().accept(new DeltaVisitor(injector));
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
}