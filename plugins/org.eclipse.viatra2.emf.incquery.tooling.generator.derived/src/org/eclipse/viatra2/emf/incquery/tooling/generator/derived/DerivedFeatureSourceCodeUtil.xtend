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
import org.eclipse.viatra2.emf.incquery.runtime.derived.QueryBasedFeatureKind

import static extension org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper.*

class DerivedFeatureSourceCodeUtil {
	
	//@Inject extension JvmTypesBuilder
	def methodBody(GenClass source, GenFeature feature,
		Pattern pattern, String sourceParamName, String targetParamName, QueryBasedFeatureKind kind, boolean keepCache){
		switch(kind){
			case org::eclipse::viatra2::emf::incquery::runtime::derived::QueryBasedFeatureKind::SINGLE_REFERENCE:
				singleRefGetMethod(source,feature,pattern,sourceParamName,targetParamName,keepCache)
			case org::eclipse::viatra2::emf::incquery::runtime::derived::QueryBasedFeatureKind::MANY_REFERENCE:
				manyRefGetMethod(source,feature,pattern,sourceParamName,targetParamName,keepCache)
			case org::eclipse::viatra2::emf::incquery::runtime::derived::QueryBasedFeatureKind::COUNTER:
				counterGetMethod(source,feature,pattern,sourceParamName,targetParamName)
			case org::eclipse::viatra2::emf::incquery::runtime::derived::QueryBasedFeatureKind::SUM:
				sumGetMethod(source,feature,pattern,sourceParamName,targetParamName)			
			case org::eclipse::viatra2::emf::incquery::runtime::derived::QueryBasedFeatureKind::ITERATION:
				iterationGetMethod(source,feature,pattern,sourceParamName,targetParamName)
		}
	}
	
	def dummyCompUnitHeader()'''
	  import org.eclipse.emf.common.util.EList;
	  import org.eclipse.emf.ecore.EClass;
	  import org.eclipse.viatra2.emf.incquery.runtime.derived.QueryBasedFeatureHandler;
	  import org.eclipse.viatra2.emf.incquery.runtime.derived.QueryBasedFeatureKind;
	  import org.eclipse.viatra2.emf.incquery.runtime.derived.QueryBasedFeatureHelper;
	  
	  public class DummyClass {
	   public void DummyMethod() {
	'''

	def singleRefGetMethod(GenClass source, GenFeature feature,
		Pattern pattern, String sourceParamName, String targetParamName, boolean keepCache)'''
		«dummyCompUnitHeader»
				if («feature.name»Handler == null) {
					«feature.name»Handler = QueryBasedFeatureHelper.getQueryBasedFeatureHandler(this,
						«source.genPackage.packageClassName».Literals.«source.getFeatureID(feature)»,
						"«pattern.fullyQualifiedName»", "«sourceParamName»", "«targetParamName»",
						QueryBasedFeatureKind.SINGLE_REFERENCE,«keepCache», false);
				}
				return («feature.getType(source)») «feature.name»Handler.getSingleReferenceValue(this);
			}
		}
	'''
	/*if («feature.name»Handler != null) {
          return («feature.getType(source)») «feature.name»Handler.getSingleReferenceValue();
        } else {
          «feature.name»Handler = IncqueryFeatureHelper.createHandler(this,
              «source.genPackage.packageClassName».Literals.«source.getFeatureID(feature)»,
               "«pattern.fullyQualifiedName»", "«sourceParamName»", "«targetParamName»",
              FeatureKind.SINGLE_REFERENCE,«keepCache»);
          if («feature.name»Handler != null) {
            return («feature.getType(source)») «feature.name»Handler.getSingleReferenceValue();
          }
        }
        return null; */
	
	def manyRefGetMethod(GenClass source, GenFeature feature,
		Pattern pattern, String sourceParamName, String targetParamName, boolean keepCache)'''
		«dummyCompUnitHeader»
				if(«feature.name»Handler == null) {
					«feature.name»Handler = QueryBasedFeatureHelper.getQueryBasedFeatureHandler(this,
						«source.genPackage.packageClassName».Literals.«source.getFeatureID(feature)»,
						"«pattern.fullyQualifiedName»", "«sourceParamName»", "«targetParamName»",
						QueryBasedFeatureKind.MANY_REFERENCE,«keepCache», false);
				}
				return «feature.name»Handler.getManyReferenceValueAsEList(this);
			}
		}
	'''
	
	def counterGetMethod(GenClass source, GenFeature feature,
		Pattern pattern, String sourceParamName, String targetParamName)'''
		«dummyCompUnitHeader»
				if («feature.name»Handler == null) {
					«feature.name»Handler = QueryBasedFeatureHelper.getQueryBasedFeatureHandler(this,
						«source.genPackage.packageClassName».Literals.«source.getFeatureID(feature)»,
						"«pattern.fullyQualifiedName»", "«sourceParamName»", null,
						QueryBasedFeatureKind.COUNTER, true, false);
				}
				return «feature.name»Handler.getIntValue(this);
			}
		}
	'''
	
	def sumGetMethod(GenClass source, GenFeature feature,
		Pattern pattern, String sourceParamName, String targetParamName)'''
		«dummyCompUnitHeader»
				if («feature.name»Handler == null) {
					«feature.name»Handler = QueryBasedFeatureHelper.getQueryBasedFeatureHandler(this,
						«source.genPackage.packageClassName».Literals.«source.getFeatureID(feature)»,
						"«pattern.fullyQualifiedName»", "«sourceParamName»", "«targetParamName»",
						QueryBasedFeatureKind.SUM, true, false);
				}
				return «feature.name»Handler.getIntValue(this);
			}
		}
	'''
	
	def iterationGetMethod(GenClass source, GenFeature feature,
		Pattern pattern, String sourceParamName, String targetParamName)'''
		«dummyCompUnitHeader»
				if («feature.name»Handler == null) {
					«feature.name»Handler = QueryBasedFeatureHelper.getQueryBasedFeatureHandler(this,
						«source.genPackage.packageClassName».Literals.«source.getFeatureID(feature)»,
						"«pattern.fullyQualifiedName»", "«sourceParamName»", "«targetParamName»",
						QueryBasedFeatureKind.ITERATION, true, false);
				}
				return («feature.getType(source)») «feature.name»Handler.getValueIteration(this);
			}
		}
	'''
	
	def singleRefSetMethod(GenFeature feature, Pattern pattern, String parameter, String setMethod, String setExpression)'''
    «dummyCompUnitHeader»
        if(«parameter» != null){
          «setMethod»(«setExpression»);
        }  else {
          «setMethod»(null);
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