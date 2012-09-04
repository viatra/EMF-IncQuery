package org.eclipse.viatra2.emf.incquery.typesearch;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;

public class TypeHiearchy {
	Map<EClass, TypeHiearchyEntry> typeHiearchy;
	
	public TypeHiearchy(EList<PackageImport> packages) {
		this.typeHiearchy = new HashMap<EClass, TypeHiearchyEntry>();
		
		for (PackageImport package_ : packages) {
			for (EClassifier classifier : package_.getEPackage().getEClassifiers()) {
				if (classifier instanceof EClass) {
					EClass eclass = (EClass) classifier;
					typeHiearchy.put(eclass, new TypeHiearchyEntry(eclass));
				}
			}
		}
		boolean doDirectSubtypes = true;
		boolean doPropegate;
		do {
			doPropegate = propegate(doDirectSubtypes);
			doDirectSubtypes = false;
		} while(doPropegate);
	}
	
	private boolean propegate(boolean doDirectSubtypes)	{
		boolean hasChanged = false;
		for(Entry<EClass, TypeHiearchyEntry> entry : typeHiearchy.entrySet()){
			EClass key = entry.getKey();
			for(EClass supertype : key.getEAllSuperTypes()) {
				Set<EClass> oldSupertypes = this.typeHiearchy.get(key).getSuperclasses();
				int oldCount = oldSupertypes.size();
				oldSupertypes.addAll(this.typeHiearchy.get(supertype).getSuperclasses());
				if(oldSupertypes.size()>oldCount) hasChanged=true;
				
				if(doDirectSubtypes) {
					this.typeHiearchy.get(supertype).getDirectSubclasses().add(key);
				}
			}
		}
		return hasChanged;
	}
	
	public void print()
	{
		System.out.println("!!!Type Hiearchy: ");
		for(Entry<EClass, TypeHiearchyEntry> e : this.typeHiearchy.entrySet())
		{
			System.out.println(e.getKey().getName()+": ");
			System.out.println("- Supertypes:");
			for(EClass s: e.getValue().getSuperclasses())
				System.out.println("\t"+s.getName());
		}
	}
	
	public Set<EClass> getSupertypes(EClass type){
		return this.typeHiearchy.get(type).getSuperclasses();
	}
	
	public Set<EClass> getDirectSubtypes(EClass type){
		return this.typeHiearchy.get(type).getDirectSubclasses();
	}
}
