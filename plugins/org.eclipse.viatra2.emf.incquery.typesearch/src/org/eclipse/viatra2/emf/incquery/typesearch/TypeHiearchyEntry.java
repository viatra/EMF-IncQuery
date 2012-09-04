package org.eclipse.viatra2.emf.incquery.typesearch;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;

public class TypeHiearchyEntry {
	private Set<EClass> directSubclasses;
	private Set<EClass> superclasses;
	
	public TypeHiearchyEntry(EClass target) {
		this.directSubclasses = new HashSet<EClass>();
		this.superclasses = new HashSet<EClass>();
		this.superclasses.add(target);
	}
	
	public Set<EClass> getDirectSubclasses() {
		return directSubclasses;
	}
	public Set<EClass> getSuperclasses() {
		return superclasses;
	}
}
