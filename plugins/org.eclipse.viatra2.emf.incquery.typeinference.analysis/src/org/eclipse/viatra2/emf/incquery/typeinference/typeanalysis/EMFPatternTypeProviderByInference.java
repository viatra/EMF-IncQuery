package org.eclipse.viatra2.emf.incquery.typeinference.typeanalysis;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.tooling.generator.types.GenModelBasedTypeProvider;
import org.eclipse.viatra2.emf.incquery.typeinference.queryanalysis.QueryAnalysisProviderOnPattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.common.types.JvmTypeReference;

import com.google.inject.Singleton;

@Singleton
public class EMFPatternTypeProviderByInference extends
		GenModelBasedTypeProvider {

	PatternTypeProvider typeProvider = new PatternTypeProvider();
	
	
	
	@Override
	public synchronized boolean canResolveEasily(Variable variable) {
		return this.typeProvider.getQueryAnalysis(QueryAnalysisProviderOnPattern.getPatternModel(variable)).isCacheValid();
	}
	
	@Override
	public synchronized boolean canResolveEasily(PatternBody body, Variable variable) {
		return canResolveEasily(variable);
	}

	@Override
	public synchronized JvmTypeReference resolve(PatternBody body, Variable variable) {
		TypeAnalysis typeAnalysis = this.typeProvider.getQueryAnalysis(QueryAnalysisProviderOnPattern.getPatternModel(variable));
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
		TypeAnalysis typeAnalysis = this.typeProvider.getQueryAnalysis(QueryAnalysisProviderOnPattern.getPatternModel(variable));
		EClassifier type = null;
		PatternBody possibleBody = QueryAnalysisProviderOnPattern.getPatternBody(variable); 
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
		TypeAnalysis typeAnalysis = this.typeProvider.getQueryAnalysis(QueryAnalysisProviderOnPattern.getPatternModel(variable));
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
		TypeAnalysis typeAnalysis = this.typeProvider.getQueryAnalysis(QueryAnalysisProviderOnPattern.getPatternModel(variable));
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
