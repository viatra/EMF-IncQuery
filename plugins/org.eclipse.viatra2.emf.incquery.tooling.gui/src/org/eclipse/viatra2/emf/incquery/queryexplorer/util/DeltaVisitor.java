package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.RuntimeMatcherRegistrationJob;

class DeltaVisitor implements IResourceDeltaVisitor {
	public boolean visit(IResourceDelta delta) {
		IResource res = delta.getResource();
		
		if (res != null && res instanceof IFile && delta.getKind() == IResourceDelta.CHANGED) {
			System.out.println("File changed");
			IFile file = (IFile) res;
			if (DatabindingUtil.registeredPatterModels.containsKey(file)) {
				RuntimeMatcherRegistrationJob job = new RuntimeMatcherRegistrationJob((IFile) file);
				Display.getDefault().syncExec(job);
			}
			return false;
		}
		return true;
	}
}
