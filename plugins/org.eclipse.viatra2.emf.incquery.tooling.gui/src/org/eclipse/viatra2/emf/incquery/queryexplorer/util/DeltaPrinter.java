package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;

class DeltaPrinter implements IResourceDeltaVisitor {
	public boolean visit(IResourceDelta delta) {
		IResource res = delta.getResource();
		switch (delta.getKind()) {
		case IResourceDelta.ADDED:
			System.out.print("Resource ");
			System.out.print(res.getFullPath());
			System.out.println(" was added.");
			break;
		case IResourceDelta.REMOVED:
			System.out.print("Resource ");
			System.out.print(res.getFullPath());
			System.out.println(" was removed.");
			break;
		case IResourceDelta.CHANGED:
			System.out.print("Resource ");
			System.out.print(res.getFullPath());
			System.out.println(" has changed.");
			break;
		}
		return true; // visit the children
	}
}
