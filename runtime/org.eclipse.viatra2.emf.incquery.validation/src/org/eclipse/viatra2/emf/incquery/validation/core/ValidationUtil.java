package org.eclipse.viatra2.emf.incquery.validation.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;

public class ValidationUtil {

	static Map<IFile, Set<ValidationProblem>> file2problemListMap = 
		new HashMap<IFile, Set<ValidationProblem>>();
	static Map<Constraint, Map<IPatternSignature, ValidationProblem>> constraint2sigAndVPMap = new HashMap<Constraint, Map<IPatternSignature,ValidationProblem>>();
	private static Map<Notifier,Collection<Validator>> activeRoots = 
		new HashMap<Notifier,Collection<Validator>>();  
	
	/*public static Set<ValidationProblem> getProblems(IFile f) {
		return problems.get(f).keySet();
	}*/
	
	/*static void putProblem(IFile f, ValidationProblem vp) throws CoreException {
		System.out.println("PUT: " + f + " - " + vp.affectedElements.toString());
		IMarker marker = vp.createMarker(f);
		Map<ValidationProblem, IMarker> _problems = problems.get(f);
		if (_problems == null) {
			_problems = new HashMap<ValidationProblem, IMarker>();
			problems.put(f, _problems);
		}
		_problems.put(vp, marker);
	}
	
	static void removeProblem(IFile f, ValidationProblem vp) throws CoreException {
		System.out.println("REMOVE: " + f + " - " + vp.affectedElements.toString());
		Map<ValidationProblem, IMarker> _problems = problems.get(f);
		if (_problems != null) {
			vp.dispose();
			_problems.remove(vp);
		}
	}
	
	static boolean knownProblem(IFile f, ValidationProblem _vp) {
		Map<ValidationProblem, IMarker> _problems = problems.get(f);
		if (_problems!=null) {
			return _problems.containsKey(_vp);
		}
		return false;
	}*/
	
	
	/**
	 * Put a problem with the given constraint and signature in the list of current problems for the given file
	 * 
	 * @param <Signature> The concrete type of the passes signature
	 * @param f The file where the validation is performed
	 * @param constraint The constraint that represents the checked pattern
	 * @param affectedElements The concrete signature instance representing the problem
	 * @throws CoreException
	 */
	static <Signature extends IPatternSignature> void putProblem(IFile f, Constraint<Signature> constraint, Signature affectedElements) throws CoreException {
		ValidationProblem<Signature> vp = new ValidationProblem<Signature>(constraint, affectedElements);
		IMarker marker = vp.createMarker(f);
		// add validation problem to the list corresponding to the file
		if(!file2problemListMap.containsKey(f)){
			file2problemListMap.put(f, new HashSet<ValidationProblem>());
		}
		file2problemListMap.get(f).add(vp);
		// add validation problem to the map of constraints to signatures and validation problems
		if (!constraint2sigAndVPMap.containsKey(constraint)) {
			Map<IPatternSignature, ValidationProblem> sig2VP = new HashMap<IPatternSignature, ValidationProblem>();
			constraint2sigAndVPMap.put(constraint, sig2VP);
		} else if(constraint2sigAndVPMap.get(constraint).containsKey(affectedElements)){
			System.err.println("The impossible has happened (putProblem called without knownProblem)");
		}
		constraint2sigAndVPMap.get(constraint).put(affectedElements, vp);
	}
	
	/**
	 *  Remove an existing problem marker for a given file, constraint and signature
	 *  
	 * @param <Signature> The concrete type of the passes signature
	 * @param f The file where the validation is performed
	 * @param constraint The constraint that represents the checked pattern
	 * @param affectedElements The concrete signature instance representing the problem
	 * @throws CoreException
	 */
	static <Signature extends IPatternSignature> void removeProblem(IFile f, Constraint<Signature> constraint, Signature affectedElements) throws CoreException {
		if (constraint2sigAndVPMap.containsKey(constraint)) {
			ValidationProblem<Signature> vp = constraint2sigAndVPMap.get(constraint).get(affectedElements);
			if(vp != null){
				vp.dispose();
				constraint2sigAndVPMap.get(constraint).remove(affectedElements);
				file2problemListMap.get(f).remove(vp);
			}
		}
	}
	
	/**
	 * Checks whether a problem for a constraint exists in a given file
	 * 
	 * @param <Signature> The concrete type of the passes signature
	 * @param f The file where the validation is performed
	 * @param constraint The constraint that represents the checked pattern
	 * @param affectedElements The concrete signature instance representing the problem
	 * @return
	 */
	static <Signature extends IPatternSignature> boolean knownProblem(IFile f, Constraint<Signature> constraint, Signature affectedElements) {
		if (constraint2sigAndVPMap.containsKey(constraint)) {
			return constraint2sigAndVPMap.get(constraint).containsKey(affectedElements);
		}
		return false;
	}
		
	
	/**
	 * Initializes all registered validators on the specified EMF root (unless already initialized). 
	 * @param emfRoot the EMF root where the violations will be searched
	 * @param file the markers will be reported against this file
	 * @return true if the initializaton was performed, or false if validators had previously been initialized for the same EMF root.  
	 * @throws IncQueryRuntimeException
	 */
	public static boolean initValidators(Notifier emfRoot, IFile file) throws IncQueryRuntimeException {
		Collection<Validator> validators = activeRoots.get(emfRoot);
		if (validators!=null) return false;
		validators = new HashSet<Validator>();
		for (Constraint<?> c: getConstraints()) {
			Validator validator = new Validator(c, emfRoot, file);
			validators.add(validator);
			validator.startMonitoring();
		}
		activeRoots.put(emfRoot, validators);
		return true;
	}
	
	public static boolean closeValidators(Notifier emfRoot, IFile file) {
		if (activeRoots.remove(emfRoot) != null) {
			Set<ValidationProblem> problemSet = new HashSet<ValidationProblem>(file2problemListMap.get(file));
			if (problemSet != null) {
				for (ValidationProblem problem : problemSet) {
					try {
						removeProblem(file, problem.kind, problem.affectedElements);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
			//EngineManager.getInstance().killEngine(emfRoot);
			return true;
		}
		else return false;
	}
	
	private static Collection<Constraint<?>> getConstraints() {
		Vector<Constraint<?>> v = new Vector<Constraint<?>>();
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint("org.eclipse.viatra2.emf.incquery.validation.constraint");
		for (IExtension e : ep.getExtensions()) {
			for (IConfigurationElement ce : e.getConfigurationElements()) {
				if (!ce.getName().equalsIgnoreCase("constraint"))
					continue;
				try {
					Object o = ce.createExecutableExtension("class");
					if (o instanceof Constraint<?>) {
						v.add((Constraint<?>) o);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return v;
	}
	
}
