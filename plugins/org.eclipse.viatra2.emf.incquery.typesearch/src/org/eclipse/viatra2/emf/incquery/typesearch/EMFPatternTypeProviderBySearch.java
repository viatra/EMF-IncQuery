package org.eclipse.viatra2.emf.incquery.typesearch;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.tooling.generator.types.GenModelBasedTypeProvider;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.common.types.JvmTypeReference;

import com.google.inject.Singleton;

@Singleton
public class EMFPatternTypeProviderBySearch extends GenModelBasedTypeProvider{
	
	public static PatternModel getPatternModel(EObject object)
	{
		EObject objectOfPM = object;
		while(!(objectOfPM instanceof PatternModel))
			objectOfPM = objectOfPM.eContainer();
		return (PatternModel) objectOfPM;
	}
	
	@Override
	public JvmTypeReference resolve(Variable variable) {
		PatternModel pm = getPatternModel(variable);
		TypeHiearchy t = new TypeHiearchy(pm.getImportPackages());
		t.print();
		return null;
	}

	@Override
	public JvmTypeReference resolve(PatternBody body, Variable variable) {
		// TODO Auto-generated method stub
		return null;
	}
}
