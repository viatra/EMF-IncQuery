package org.eclipse.viatra2.emf.incquery.validation.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;

public class ValidationUtil {

	static Map<IFile, Map<ValidationProblem, IMarker>> problems = 
		new HashMap<IFile, Map<ValidationProblem, IMarker>>();
	private static Map<Notifier,Collection<Validator>> activeRoots = 
		new HashMap<Notifier,Collection<Validator>>();  
	
	public static Set<ValidationProblem> getProblems(IFile f) {
		return problems.get(f).keySet();
	}
	
	static void putProblem(IFile f, ValidationProblem vp) throws CoreException {
		IMarker marker = vp.createMarker(f);
		Map<ValidationProblem, IMarker> _problems = problems.get(f);
		if (_problems == null) {
			_problems = new HashMap<ValidationProblem, IMarker>();
			problems.put(f, _problems);
		}
		_problems.put(vp, marker);
	}
	
	static void removeProblem(IFile f, ValidationProblem vp) throws CoreException {
		Map<ValidationProblem, IMarker> _problems = problems.get(f);
		if (_problems != null) {
			vp.dispose();
			IMarker marker = _problems.remove(vp);
			if (marker != null) marker.delete();
		}
	}
	
	static boolean knownProblem(IFile f, ValidationProblem _vp) {
		Map<ValidationProblem, IMarker> _problems = problems.get(f);
		if (_problems!=null) {
			return _problems.containsKey(_vp);
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
			Map<ValidationProblem, IMarker> problemMap = problems.get(file);
			if (problemMap != null) {
				for (ValidationProblem problem : new HashSet<ValidationProblem>(problemMap.keySet())) {
					try {
						removeProblem(file, problem);
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
