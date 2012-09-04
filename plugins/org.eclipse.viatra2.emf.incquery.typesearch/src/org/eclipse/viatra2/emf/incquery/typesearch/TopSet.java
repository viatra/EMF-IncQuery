package org.eclipse.viatra2.emf.incquery.typesearch;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;

public class TopSet {
	protected boolean unsatisfiable;
	protected boolean tooGeneral;
	Set<EClass> topClassPossibilities;
	EClassifier classifierPossibility;
	
	TypeHiearchy hiearchy;
	
	public TopSet(TypeHiearchy hiearchy, Set<EClassifier> firstConstraints) {
		this.hiearchy = hiearchy;
		this.tooGeneral = firstConstraints.isEmpty();
		this.unsatisfiable = false;
		this.firstTypeConstrains(firstConstraints);
	}

	private void firstTypeConstrains(Set<EClassifier> firstConstraints) {
		Set<EClass> firstClassConstrains = new HashSet<EClass>();
		//Fast check to different classifier and class
		// And put the EClasses in a set for later use
		for(EClassifier c : firstConstraints) {
			if(!(c instanceof EClass)) {
				if(classifierPossibility==null) classifierPossibility = c;
				else {
					this.unsatisfiable = true;
					return;
				}
			}
			else firstClassConstrains.add((EClass) c);
		}
		//Every constrains are EClass
		if(classifierPossibility == null)
			this.firstClassConstraint(firstClassConstrains);
	}
	
	public static <E> E getOne(Collection<E> collection)
	{
		for(E element : collection) return element;
		return null;
	}
	
	private void firstClassConstraint(Set<EClass> firstClassConstraints) {
		if(firstClassConstraints.isEmpty()) this.topClassPossibilities = null;
		else {
			EClass aConstraint = getOne(firstClassConstraints);
			this.topClassPossibilities = getTopPossibilities(aConstraint, firstClassConstraints);
			if(this.topClassPossibilities == null) this.topClassPossibilities = new HashSet<EClass>();
			minimaliseTopClassPossibilities();	
		}
	}
	
	// Returns the set of top possibilities, or null if it isn't any.
	private Set<EClass> getTopPossibilities(EClass visited, Set<EClass> classConstraints) {
		if(this.hiearchy.getSupertypes(visited).containsAll(classConstraints)) {
			Set<EClass> ret = new HashSet<EClass>();
			ret.add(visited);
			return ret;
		}
		else {
			Set<EClass> ret = null;
			for(EClass subclass : this.hiearchy.getDirectSubtypes(visited)){
				Set<EClass> retFromSubclass = getTopPossibilities(subclass, classConstraints);
				if(retFromSubclass != null) {
					if(ret == null) ret = new HashSet<EClass>();
					ret.addAll(retFromSubclass);
				}
			}
			return ret;
		}
	}
	
		private Set<EClass> getTopPossibilities(EClass possibility, EClass newClassConstraint) {
			if(this.hiearchy.getSupertypes(possibility).contains(newClassConstraint)) {
				Set<EClass> ret = new HashSet<EClass>();
				ret.add(possibility);
				return ret;
			}
			else {
				Set<EClass> ret = null;
				for(EClass subclass : this.hiearchy.getDirectSubtypes(possibility)){
					Set<EClass> retFromSubclass = getTopPossibilities(subclass, newClassConstraint);
					if(retFromSubclass != null) {
						if(ret == null) ret = new HashSet<EClass>();
						ret.addAll(retFromSubclass);
					}
				}
				return ret;
			}
		}
	
	private void minimaliseTopClassPossibilities() {
		Set<EClass> toRemove = null;
		for(EClass possibility : this.topClassPossibilities) {
			for(EClass otherPossibility : this.topClassPossibilities) {
				if(this.hiearchy.getSupertypes(possibility).contains(otherPossibility)) {
					if(toRemove == null) toRemove = new HashSet<EClass>();
					toRemove.add(possibility);
				}
			}
		}
		if(toRemove!=null)
			this.topClassPossibilities.removeAll(toRemove);
	}
	
	//private void minimaliseClassPossibilies
	public void addTypeConstraint(EClassifier type) {
		if (type instanceof EClass) {
			EClass newClassConstraint = (EClass) type;
			Set<EClass> newTopPossibilities = new HashSet<EClass>();
			for(EClass possibility : this.topClassPossibilities) {
				newTopPossibilities.addAll(this.getTopPossibilities(possibility, newClassConstraint));
			}
			this.minimaliseTopClassPossibilities();
		}
		else if(type != this.classifierPossibility) this.unsatisfiable = true;			
	}
	
	public boolean isUnsatisfiable() {
		return this.unsatisfiable ||
			(this.topClassPossibilities!=null && this.topClassPossibilities.isEmpty());
	}
	public boolean isTooGeneral() {
		return this.tooGeneral;
	}
}
