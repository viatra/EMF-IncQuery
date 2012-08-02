package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.tooling.generator.types.GenModelBasedTypeProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.common.types.JvmTypeReference;

import com.google.inject.Singleton;

@Singleton
public class EMFPatternTypeProviderByInference extends GenModelBasedTypeProvider{

	private static Map<String, TypeAnalysis> map = new HashMap<String, TypeAnalysis>();
	
	private TypeAnalysis getTypeAnalysis(EObject object)
	{
		do
		{
			object = object.eContainer();
		}
		while(!(object instanceof PatternModel));
		PatternModel patternModel = (PatternModel) object;
		System.out.println("----- Needed: TypeAnalysis for "+patternModel.getPackageName());
		//  Test
		System.out.println("----- Map: ");
		for(Entry<String, TypeAnalysis> e : map.entrySet())
		{
			System.out.println("-----     "+e.getKey() +" -> "+e.getValue().hashCode());
		}
		// /Test
		if(map.get(patternModel.getPackageName())!=null)
		{
			TypeAnalysis typeAnalysis = map.get(patternModel.getPackageName());
			System.out.println("----- Giving: existing TypeAnalysis("+typeAnalysis.hashCode()+") for "+patternModel.getPackageName() + " (#TA = "+TypeAnalysis.instances+")");
			return typeAnalysis;
		}
		else
		{
			System.out.println("----- Trying to create new TypeAnalysis...");
			TypeAnalysis typeAnalysis = null;
			try {
				typeAnalysis = new TypeAnalysis(patternModel);
			} catch (TypeAnalysisException e) {
				System.out.println("----- But it failed.");
				return null;
			}
			map.put(patternModel.getPackageName(), typeAnalysis);
			System.out.println("----- Created and giving: TypeAnalysis("+typeAnalysis.hashCode()+") for "+patternModel.getPackageName() + " (#TA = "+TypeAnalysis.instances+")");
			return typeAnalysis;
		}
	}
	
	@Override
	public JvmTypeReference resolve(PatternBody body, Variable variable)	{
		TypeAnalysis typeAnalysis = this.getTypeAnalysis(variable);
		return null;
		/*EClassifier type = null;
		try {
			type = typeAnalysis.getTypeOfVariableInBody(body, variable);
		} catch (TypeAnalysisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if(type==null) return null;
		else return this.typeReference(type.getInstanceClass(), variable);*/
	}
	
	@Override
	public JvmTypeReference resolve(Variable variable) { 
		TypeAnalysis typeAnalysis = this.getTypeAnalysis(variable);
		return null;
		/*
		EClassifier type = null;
		try {
			type = typeAnalysis.getTypeOfParameter(variable);
		} catch (TypeAnalysisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if(type==null) return null;
		else return this.typeReference(type.getInstanceClass(), variable);*/
	}
}
