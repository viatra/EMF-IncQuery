package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
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
		return (PatternModel) object;
	}
	
	public static PatternBody getPatternBody(EObject object)
	{
		while(!(object instanceof PatternBody))
		{
			EObject object2 = object.eContainer();
			if(object2 == null) return null;
			else object = object2;
		}
		return (PatternBody) object;
	}
	
	private String getUri(PatternModel patternModel)
	{
		return patternModel.eResource().getURI().toString();
	}
	
	private synchronized TypeAnalysis getTypeAnalysis(EObject object) {
		PatternModel patternModel = getPatternModel(object);
		
		if (map.get(getUri(patternModel)) != null) {
			TypeAnalysis typeAnalysis = map.get(getUri(patternModel));
			return typeAnalysis;
		} else {
			TypeAnalysis typeAnalysis = null;
			try {
				typeAnalysis = new TypeAnalysis(patternModel);
			} catch (TypeAnalysisException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			map.put(getUri(patternModel), typeAnalysis);
			return typeAnalysis;
		}
	}
	
	@Override
	public synchronized boolean canResolveEasily(Variable variable) {
		return getTypeAnalysis(variable).isCacheValid();
	}
	
	@Override
	public synchronized boolean canResolveEasily(PatternBody body, Variable variable) {
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
	public synchronized JvmTypeReference resolve(Variable variable) {
		TypeAnalysis typeAnalysis = this.getTypeAnalysis(variable);
		EClassifier type = null;
		PatternBody possibleBody = getPatternBody(variable); 
		try {
			if(possibleBody!=null)
			{
				return resolve(possibleBody,variable);
			}
			else
			{
				type = typeAnalysis.getTypeOfParameter(variable);
				System.out.println(">>> Inferred: parameter " + variable + " > " + (type!=null?type.getName():"null"));
				if (type == null)
					return null;
				else
					return this.typeReference(type.getInstanceClass(), variable);
			}
		} catch (TypeAnalysisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public Boolean isUnsatisfiableVariable(PatternBody body, Variable variable)
	{
		TypeAnalysis typeAnalysis = this.getTypeAnalysis(variable);
		Boolean ret = null;
		try {
			ret = typeAnalysis.isUnsatisfiableTypeOfVariableInBody(body, variable);
		} catch (TypeAnalysisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public Boolean isTooGeneralVariable(PatternBody body, Variable variable)
	{
		TypeAnalysis typeAnalysis = this.getTypeAnalysis(variable);
		Boolean ret = null;
		try {
			ret = typeAnalysis.isTooGeneralTypeOfVariableInBody(body, variable);
		} catch (TypeAnalysisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
}
