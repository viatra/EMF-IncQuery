package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra2.emf.incquery.tooling.generator.types.GenModelBasedTypeProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.common.types.JvmTypeReference;

import com.google.inject.Singleton;

@Singleton
public class EMFPatternTypeProviderByInference extends
		GenModelBasedTypeProvider {

	private static ConcurrentMap<String, TypeAnalysis> map = new ConcurrentHashMap<String, TypeAnalysis>();
	
	public static PatternModel getPatternModel(EObject object)
	{
		do {
			object = object.eContainer();
		} while (!(object instanceof PatternModel));
		//test
		Resource resource = ((PatternModel) object).eResource();
		System.out.println("PM: "+object +"\n\t"+resource);
		for(EObject element : resource.getContents())
			System.out.println("\t\t"+element);
		//test
		return (PatternModel) object;
	}
	
	private String getUri(PatternModel patternModel)
	{
		return patternModel.eResource().getURI().toString();
	}
	
	private synchronized TypeAnalysis getTypeAnalysis(EObject object) {
		PatternModel patternModel = getPatternModel(object);
		
		System.out.println("Thread: " + Thread.currentThread().getId());
		
		if (map.get(getUri(patternModel)) != null) {
			TypeAnalysis typeAnalysis = map.get(getUri(patternModel));
			System.out.println("----- Giving: existing TypeAnalysis("
					+ typeAnalysis.hashCode() + ") for "
					+ patternModel.getPackageName());
			return typeAnalysis;
		} else {
			System.out.println("----- Trying to create new TypeAnalysis...");
			TypeAnalysis typeAnalysis = null;
			try {
				typeAnalysis = new TypeAnalysis(patternModel);
			} catch (TypeAnalysisException e) {
				System.out.println("----- But it failed.");
				return null;
			}
			map.put(getUri(patternModel), typeAnalysis);
			System.out.println("----- Created and giving: TypeAnalysis("
					+ typeAnalysis.hashCode() + ") for "
					+ patternModel.getPackageName());
			return typeAnalysis;
		}
		 
	}
	
	@Override
	public synchronized boolean canResolveEasily(Variable variable) {
		return getTypeAnalysis(variable).isCacheValid();
	}

	@Override
	public synchronized JvmTypeReference resolve(PatternBody body, Variable variable) {
		TypeAnalysis typeAnalysis = this.getTypeAnalysis(variable);
		EClassifier type = null;
		try {
			type = typeAnalysis.getTypeOfVariableInBody(body, variable);
		} catch (TypeAnalysisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(">>> Inferred: body #"
				+ (((Pattern) (body.eContainer())).getBodies().indexOf(body) +1)
				+ " variable " + variable + " > " + (type!=null?type.getName():"null"));

		if (type == null)
			return null;
		else return this.typeReference(type.getInstanceClass(), variable);
	}
	
	@Override
	public synchronized boolean canResolveEasily(PatternBody body, Variable variable) {
		return getTypeAnalysis(variable).isCacheValid();
	}

	@Override
	public synchronized JvmTypeReference resolve(Variable variable) {
		TypeAnalysis typeAnalysis = this.getTypeAnalysis(variable);
		EClassifier type = null;
		try {
			type = typeAnalysis.getTypeOfParameter(variable);
		} catch (TypeAnalysisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(">>> Inferred: parameter " + variable + " > " + (type!=null?type.getName():"null"));

		if (type == null)
			return null;
		else
			return this.typeReference(type.getInstanceClass(), variable);
	}
}
