/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.tooling.generator.derived

import org.eclipse.emf.codegen.ecore.genmodel.GenClass
import org.eclipse.emf.codegen.ecore.genmodel.GenFeature
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern

import static extension org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper.*
import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler$FeatureKind

class DerivedFeatureSourceCodeUtil {
	
	//@Inject extension JvmTypesBuilder
	def methodBody(GenClass source, GenFeature feature,
		Pattern pattern, String sourceParamName, String targetParamName, FeatureKind kind, boolean keepCache){
		switch(kind){
			case FeatureKind::SINGLE_REFERENCE:
				singleRefGetMethod(source,feature,pattern,sourceParamName,targetParamName,keepCache)
			case FeatureKind::MANY_REFERENCE:
				manyRefGetMethod(source,feature,pattern,sourceParamName,targetParamName,keepCache)
			case FeatureKind::COUNTER:
				counterGetMethod(source,feature,pattern,sourceParamName,targetParamName)
			case FeatureKind::SUM:
				sumGetMethod(source,feature,pattern,sourceParamName,targetParamName)			
			case FeatureKind::ITERATION:
				iterationGetMethod(source,feature,pattern,sourceParamName,targetParamName)
		}
	}

	def singleRefGetMethod(GenClass source, GenFeature feature,
		Pattern pattern, String sourceParamName, String targetParamName, boolean keepCache)'''
		import org.eclipse.emf.common.util.EList;
		import org.eclipse.emf.ecore.EClass;
		import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler;
		import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler.FeatureKind;
		import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHelper;
		
		public class DummyClass {
			public void DummyMethod() {
				if («feature.name»Handler != null) {
					return («feature.ecoreFeature.EType.name») «feature.name»Handler.getSingleReferenceValue();
				} else {
					«feature.name»Handler = IncqueryFeatureHelper.createHandler(this,
							«source.genPackage.packageClassName».Literals.«source.getFeatureID(feature)»,
							 "«pattern.fullyQualifiedName»", "«sourceParamName»", "«targetParamName»",
							FeatureKind.SINGLE_REFERENCE,«keepCache»);
					if («feature.name»Handler != null) {
						return («feature.ecoreFeature.EType.name») «feature.name»Handler.getSingleReferenceValue();
					}
				}
				return null;
			}
		}
	'''
	
	def manyRefGetMethod(GenClass source, GenFeature feature,
		Pattern pattern, String sourceParamName, String targetParamName, boolean keepCache)'''
		import org.eclipse.emf.common.util.EList;
		import org.eclipse.emf.ecore.EClass;
		import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler;
		import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler.FeatureKind;
		import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHelper;
		
		public class DummyClass {
			public void DummyMethod() {
				if(«feature.name»Handler == null) {
					«feature.name»Handler = IncqueryFeatureHelper.createHandler(this,
						«source.genPackage.packageClassName».Literals.«source.getFeatureID(feature)»,
						 "«pattern.fullyQualifiedName»", "«sourceParamName»", "«targetParamName»",
						FeatureKind.MANY_REFERENCE,«keepCache»);
				}
				return IncqueryFeatureHelper.getManyReferenceValueForHandler(«feature.name»Handler, this,
				 «source.genPackage.packageClassName».Literals.«source.getFeatureID(feature)»);
			}
		}
	'''
	
	def counterGetMethod(GenClass source, GenFeature feature,
		Pattern pattern, String sourceParamName, String targetParamName)'''
		import org.eclipse.emf.common.util.EList;
		import org.eclipse.emf.ecore.EClass;
		import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler;
		import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler.FeatureKind;
		import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHelper;
		
		public class DummyClass {
			public void DummyMethod() {
				if («feature.name»Handler != null) {
					return «feature.name»Handler.getIntValue();
				} else {
					«feature.name»Handler = IncqueryFeatureHelper.createHandler(this,
							«source.genPackage.packageClassName».Literals.«source.getFeatureID(feature)»,
							 "«pattern.fullyQualifiedName»", "«sourceParamName»", null,
							FeatureKind.COUNTER);
					if («feature.name»Handler != null) {
						return «feature.name»Handler.getIntValue();
					}
				}
				return 0;
			}
		}
	'''
	
	def sumGetMethod(GenClass source, GenFeature feature,
		Pattern pattern, String sourceParamName, String targetParamName)'''
		import org.eclipse.emf.common.util.EList;
		import org.eclipse.emf.ecore.EClass;
		import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler;
		import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler.FeatureKind;
		import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHelper;
		
		public class DummyClass {
			public void DummyMethod() {
				if («feature.name»Handler != null) {
					return «feature.name»Handler.getIntValue();
				} else {
					«feature.name»Handler = IncqueryFeatureHelper.createHandler(this,
							«source.genPackage.packageClassName».Literals.«source.getFeatureID(feature)»,
							 "«pattern.fullyQualifiedName»", "«sourceParamName»", "«targetParamName»",
							FeatureKind.SUM);
					if («feature.name»Handler != null) {
						return «feature.name»Handler.getIntValue();
					}
				}
				return 0;
			}
		}
	'''
	
	def iterationGetMethod(GenClass source, GenFeature feature,
		Pattern pattern, String sourceParamName, String targetParamName)'''
		import org.eclipse.emf.common.util.EList;
		import org.eclipse.emf.ecore.EClass;
		import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler;
		import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHandler.FeatureKind;
		import org.eclipse.viatra2.emf.incquery.runtime.derived.IncqueryFeatureHelper;
		
		public class DummyClass {
			public void DummyMethod() {
				if («feature.name»Handler != null) {
					return («feature.ecoreFeature.EType.name») «feature.name»Handler.getValueIteration();
				} else {
					«feature.name»Handler = IncqueryFeatureHelper.createHandler(this,
							«source.genPackage.packageClassName».Literals.«source.getFeatureID(feature)»,
							 "«pattern.fullyQualifiedName»", "«sourceParamName»", "«targetParamName»",
							FeatureKind.ITERATION);
					if («feature.name»Handler != null) {
						return («feature.ecoreFeature.EType.name») «feature.name»Handler.getValueIteration();
					}
				}
				return null;
			}
		}
	'''
	
	def defaultMethod(boolean manyFeature){
		if(manyFeature){
			defaultListGetMethod
		} else {
			defaultSingleGetMethod
		}
	}
	
	def defaultSingleGetMethod()'''
		import java.lang.UnsupportedOperationException;
		
		public class DummyClass {
			public void DummyMethod() {
				// TODO: implement this method to return the 'X' reference
				// -> do not perform proxy resolution
				// Ensure that you remove @generated or mark it @generated NOT
				throw new UnsupportedOperationException();
			}
		}
	'''
	
	def defaultListGetMethod()'''
		import java.lang.UnsupportedOperationException;
		
		public class DummyClass {
			public void DummyMethod() {
				// TODO: implement this method to return the 'X' reference list
				// Ensure that you remove @generated or mark it @generated NOT
				// The list is expected to implement org.eclipse.emf.ecore.util.InternalEList and org.eclipse.emf.ecore.EStructuralFeature.Setting
				// so it's likely that an appropriate subclass of org.eclipse.emf.ecore.util.EcoreEList should be used.
				throw new UnsupportedOperationException();
			}
		}
	'''
}