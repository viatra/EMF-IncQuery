package org.eclipse.viatra2.emf.incquery.databinding.ui.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.viatra2.emf.incquery.databinding.ui.observable.ViewerRootKey;
import org.eclipse.viatra2.emf.incquery.runtime.api.IMatcherFactory;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternSignature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryMatcher;

public class PatternMemory {

	public static PatternMemory INSTANCE = new PatternMemory();
	
	private Map<IFile, Map<IncQueryMatcher<? extends IPatternSignature>, Set<ViewerRootKey>>> matcherRegistry = 
			new HashMap<IFile, Map<IncQueryMatcher<? extends IPatternSignature>, Set<ViewerRootKey>>>();
	
	@SuppressWarnings("rawtypes")
	private Map<IFile, Set<IMatcherFactory>> factoryRegistry = 
			new HashMap<IFile, Set<IMatcherFactory>>();
	
	@SuppressWarnings("rawtypes")
	public Set<IMatcherFactory> getAllFactories() {
		Set<IMatcherFactory> ret = new HashSet<IMatcherFactory>();
		
		for (Set<IMatcherFactory> e : factoryRegistry.values()) {
			ret.addAll(e);
		}
		
		return ret;
	}
	
	@SuppressWarnings("rawtypes")
	public void registerFactory(IFile file, IMatcherFactory factory) {
		if (factoryRegistry.get(file) == null) {
			Set<IMatcherFactory> setTmp = new HashSet<IMatcherFactory>();
			setTmp.add(factory);
			factoryRegistry.put(file, setTmp);
		}
		else {
			factoryRegistry.get(file).add(factory);
		}
	}
	
	public void unregisterFactories(IFile file) {
		factoryRegistry.remove(file);
	}
	
	public void registerPattern(IFile file, IncQueryMatcher<? extends IPatternSignature> matcher, ViewerRootKey key) {
		if (matcherRegistry.get(file) == null) {
			Map<IncQueryMatcher<? extends IPatternSignature>, Set<ViewerRootKey>> tmp = 
					new HashMap<IncQueryMatcher<? extends IPatternSignature>, Set<ViewerRootKey>>();
			
			Set<ViewerRootKey> vKeys = new HashSet<ViewerRootKey>();
			vKeys.add(key);
			tmp.put(matcher, vKeys);
			matcherRegistry.put(file, tmp);
		}
		else if (matcherRegistry.get(file).get(matcher) == null) {
			Set<ViewerRootKey> vKeys = new HashSet<ViewerRootKey>();
			vKeys.add(key);
			matcherRegistry.get(file).put(matcher, vKeys);
		}
		else {
			matcherRegistry.get(file).get(matcher).add(key);
		}
	}
	
	public void unregisterPattern(IFile file, IncQueryMatcher<? extends IPatternSignature> matcher, ViewerRootKey key) {
		Map<IncQueryMatcher<? extends IPatternSignature>, Set<ViewerRootKey>> mapTmp = matcherRegistry.get(file);
		if (mapTmp != null) {
			Set<ViewerRootKey> setTmp = mapTmp.get(matcher);
			if (setTmp != null) {
				setTmp.remove(key);
			}
			if (setTmp.size() == 0) {
				mapTmp.remove(matcher);
			}
			if (mapTmp.size() == 0) {
				matcherRegistry.remove(file);
			}
		}
	}
	
	public Map<IncQueryMatcher<? extends IPatternSignature>, Set<ViewerRootKey>> getMatchers(IFile file) {
		if (matcherRegistry.get(file) != null) {
			return new HashMap<IncQueryMatcher<? extends IPatternSignature>, Set<ViewerRootKey>>(matcherRegistry.get(file));
		}
		return null;
	}
}
