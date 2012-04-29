package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.RuntimeMatcherRegistrator;

import com.google.inject.Injector;

class DeltaVisitor implements IResourceDeltaVisitor {
	private final Injector injector;

	public DeltaVisitor(Injector injector) {
		this.injector = injector;
	}

	public boolean visit(IResourceDelta delta) {
		IResource res = delta.getResource();
		
		if (res != null && res instanceof IFile && delta.getKind() == IResourceDelta.CHANGED) {
			//System.out.println("File changed");
			IFile file = (IFile) res;
			if (PatternRegistry.getInstance().getFiles().contains(file)) {
				RuntimeMatcherRegistrator job = new RuntimeMatcherRegistrator((IFile) file, injector);
				Display.getDefault().syncExec(job);
			}
			return false;
		}
		return true;
	}
}
