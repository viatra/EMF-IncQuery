package org.eclipse.viatra2.emf.incquery.validation.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryRuntimeException;

public class ValidationUtil {

	static Map<IFile, Map<ValidationProblem, IMarker>> problems = 
		new HashMap<IFile, Map<ValidationProblem, IMarker>>();
	
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
		
	
	public static void initValidators(Resource eResource, IFile file) throws IncQueryRuntimeException {
//		new Validator(InheritanceDiamondConstraint.INSTANCE, eResource/*.getResourceSet()*/, file).startMonitoring();
//		new Validator(MessageWithoutAssociationConstraint.INSTANCE, eResource/*.getResourceSet()*/, file).startMonitoring();					
//		new Validator(UnreferencedClassConstraint.INSTANCE, eResource/*.getResourceSet()*/, file).startMonitoring();
//		new Validator(ClassWithIDConstraint.INSTANCE, eResource/*.getResourceSet()*/, file).startMonitoring();
		// instead: use extension point to instantiate constraints
		// TODO
	}
	
}
