package org.eclipse.viatra2.emf.incquery.tooling.generator.databinding

import com.google.inject.Inject
import org.eclipse.viatra2.emf.incquery.tooling.generator.ExtensionGenerator
import org.eclipse.viatra2.emf.incquery.tooling.generator.fragments.IGenerationFragment
import org.eclipse.viatra2.emf.incquery.tooling.generator.util.EMFPatternLanguageJvmModelInferrerUtil
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.impl.StringValueImpl
import org.eclipse.xtext.generator.IFileSystemAccess

import static extension org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper.*
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation

class DatabindingGenerator implements IGenerationFragment {
	
	@Inject extension EMFPatternLanguageJvmModelInferrerUtil

	override generateFiles(Pattern pattern, IFileSystemAccess fsa) {
		fsa.generateFile(pattern.packagePath + "/databinding/" + pattern.name.toFirstUpper + "DatabindingAdapter.java", pattern.patternHandler)
	}
	
	override getProjectDependencies() {
		newArrayList("org.eclipse.core.databinding.property", 
		"org.eclipse.emf.databinding", 
		"org.eclipse.core.databinding.observable", 
		"org.eclipse.viatra2.emf.incquery.databinding.runtime",
		"org.eclipse.viatra2.emf.incquery.runtime")
	}
	
	override getProjectPostfix() {
		"databinding"
	}
	
	override extensionContribution(Pattern pattern, ExtensionGenerator exGen) {
		var tmp = ""
		
		for (a : pattern.annotations) {
			if (a.name.matches("PatternUI")) {
				for (ap : a.parameters) {
					if (ap.name.matches("message")) {
						tmp = (ap.value as StringValueImpl).value
					}
				}
			}
		}
		
		val message = tmp;
		
		newArrayList(
		exGen.contribExtension("", "org.eclipse.viatra2.emf.incquery.databinding.runtime.databinding") [
			exGen.contribElement(it, "databinding") [
				exGen.contribAttribute(it, "class", pattern.packagePath+".databinding."+pattern.name.toFirstUpper+"DatabindingAdapter")
				exGen.contribAttribute(it, "patternName", pattern.fullyQualifiedName)
				exGen.contribAttribute(it, "message", message)
				exGen.contribAttribute(it, "matcherFactoryClass", pattern.packagePath+"."+pattern.matcherFactoryClassName)
			]
		]
		)
	}
	
	def getElementOfObservableValue(Annotation a, String literal) {
		for (ap : a.parameters) {
			if (ap.name.matches(literal)) {
				return (ap.value as StringValueImpl).value
			}
		}
		
		return null
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
		
			public «pattern.name.toFirstUpper»DatabindingAdapter() {
				parameterMap = new HashMap<String, String>();
				«FOR annotation : pattern.annotations»
				«IF annotation.name.matches("ObservableValue")»
					«val name = getElementOfObservableValue(annotation, "name")»
					«val expression = getElementOfObservableValue(annotation, "expression")»
					«IF (name != null) && (expression != null)»
						parameterMap.put("«name»","«expression»");
					«ENDIF»
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