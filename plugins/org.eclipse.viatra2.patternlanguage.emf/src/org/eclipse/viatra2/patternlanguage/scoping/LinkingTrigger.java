package org.eclipse.viatra2.patternlanguage.scoping;

import org.eclipse.xtext.resource.DerivedStateAwareResource;
import org.eclipse.xtext.resource.IDerivedStateComputer;
import org.eclipse.xtext.xbase.resource.XbaseResource;

/**
 * Triggers the ecore inference as soon as someone wants to access the contents
 * of a {@link GrammarResource}.
 */
public class LinkingTrigger implements IDerivedStateComputer {

	@Override
	public void installDerivedState(DerivedStateAwareResource resource, boolean preLinkingPhase) {
		if (preLinkingPhase)
			return;
		XbaseResource castedResource = (XbaseResource)resource;
		castedResource.resolveLazyCrossReferences(null);
	}

	@Override
	public void discardDerivedState(DerivedStateAwareResource resource) {
	}
	
}