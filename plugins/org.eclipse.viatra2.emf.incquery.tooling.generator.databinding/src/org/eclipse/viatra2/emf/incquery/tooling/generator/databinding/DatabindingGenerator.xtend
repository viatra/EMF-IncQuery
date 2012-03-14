package org.eclipse.viatra2.emf.incquery.tooling.generator.databinding

import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment
import org.eclipse.xtext.generator.IFileSystemAccess
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import static extension org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper.*
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.impl.StringValueImpl

class DatabindingGenerator implements IGenerationFragment {
	
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil

	override generateFiles(Pattern pattern, IFileSystemAccess fsa) {
		fsa.generateFile(pattern.packagePath + "/databinding/" + pattern.name.toFirstUpper + "DatabindingAdapter.java", pattern.patternHandler)
	}
	
	override getProjectDependencies() {
		newArrayList("org.eclipse.core.databinding.property", "org.eclipse.emf.databinding", "org.eclipse.core.databinding.observable", "org.eclipse.viatra2.emf.incquery.databinding.runtime")
	}
	
	override getProjectPostfix() {
		"databinding"
	}
	
	override extensionContribution(Pattern pattern, ExtensionGenerator exGen) {
		newArrayList(
		exGen.contribExtension(pattern.getFullyQualifiedName + "Command", "org.eclipse.ui.commands") [
			exGen.contribElement(it, "command") [
				exGen.contribAttribute(it, "commandId", pattern.getFullyQualifiedName + "CommandId")
				exGen.contribAttribute(it, "style", "push")
			]
		]
		)
	}
	
	def patternHandler(Pattern pattern) '''
		package «pattern.name».databinding;
		
		import java.util.HashMap;
		import java.util.Map;

		import org.eclipse.core.databinding.observable.value.IObservableValue;
		import org.eclipse.emf.databinding.EMFProperties;
		import org.eclipse.emf.ecore.EObject;
		import org.eclipse.emf.ecore.EStructuralFeature;
		import org.eclipse.viatra2.emf.incquery.databinding.runtime.DatabindingAdapter;

		import «pattern.packageName + "." + pattern.matchClassName»;

		public class «pattern.name.toFirstUpper»DatabindingAdapter extends DatabindingAdapter<«pattern.matchClassName»> {

			private Map<String, String> parameterMap;
		
			public TestDatabindingAdapter() {
				parameterMap = new HashMap<String, String>();
				«FOR annotation : pattern.annotations»
				«IF annotation.name.matches("ObservableValue")»
				«var name = ""»
				«var expression = ""»
					«FOR annotationParameter : annotation.parameters»
						«IF annotationParameter.name.matches("name")»
							« name = (annotationParameter.value as StringValueImpl).value»	
						«ENDIF»
						«IF annotationParameter.name.matches("expression")»
							«expression = (annotationParameter.value as StringValueImpl).value»	
						«ENDIF»
					«ENDFOR»
				parameterMap.put("«name»","«expression»");
				«ENDIF»
				«ENDFOR»
			}

			@Override
			public String[] getParameterNames() {
				return parameterMap.keySet().toArray(new String[parameterMap.keySet().size()]);
			}

			@Override
			public IObservableValue getObservableParameter(«pattern.matchClassName» match, String parameterName) {
				if (parameterMap.size() > 0) {
					String expression = parameterMap.get(parameterName);
					String[] tokens = expression.split("\\.");
					
					Object o = match.get(tokens[0]);
					if (o != null && o instanceof EObject) {
						EObject eObj = (EObject) o;
						EStructuralFeature feature = eObj.eClass().getEStructuralFeature(tokens[1]);
						if (feature != null) {
							return EMFProperties.value(feature).observe(eObj);
						}
					}
				}
				return null;
			}
		}
	'''

}