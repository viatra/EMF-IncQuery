package org.eclipse.viatra2.emf.incquery.tooling.generator.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;

public class EMFPatternURIHandler extends URIHandlerImpl {

	private final Map<URI, EPackage> uriToEPackageMap = new HashMap<URI, EPackage>();

	public EMFPatternURIHandler(Collection<EPackage> packages) {
		for (EPackage e : packages) {
			if (e.eResource() != null) {
				uriToEPackageMap.put(e.eResource().getURI(), e);
			}
		}
	}
	
	@Override
	public URI deresolve(URI uri) {
		if (uri.isPlatform()) {
			String fragment = uri.fragment();
			URI fragmentRemoved = uri.trimFragment();
			EPackage p = uriToEPackageMap.get(fragmentRemoved);
			if (p != null) {
				URI newURI = URI.createURI(p.getNsURI());
				newURI = newURI.appendFragment(fragment);
				return newURI;
			}
		}
		return super.deresolve(uri);
	}
	
}
