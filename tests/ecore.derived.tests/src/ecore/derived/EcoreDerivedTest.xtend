package ecore.derived

import com.google.inject.Inject
import org.junit.runner.RunWith
import org.eclipse.viatra2.emf.incquery.testing.core.injector.EMFPatternLanguageInjectorProvider
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.viatra2.emf.incquery.testing.core.TestExecutor
import org.eclipse.viatra2.emf.incquery.testing.core.ModelLoadHelper
import org.eclipse.viatra2.emf.incquery.testing.core.SnapshotHelper
import org.junit.Test
import org.eclipse.viatra2.emf.incquery.testing.core.base.CommonStaticQueryTester
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra2.emf.incquery.base.api.IncQueryBaseFactory
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.EClass

import static org.junit.Assert.*
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EcorePackage$Literals
import java.util.Collections
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.common.notify.Notifier

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(EMFPatternLanguageInjectorProvider))
class EcoreDerivedTest extends CommonStaticQueryTester {
	
	@Inject extension TestExecutor
	@Inject extension ModelLoadHelper
	@Inject extension SnapshotHelper
	
	def ecoreBaseTest(Notifier input, String patternFQN, EClass source, EStructuralFeature target){
		val matcher = queryInputXMI.initializeMatcherFromModel(input, patternFQN)
		val navigationHelper = IncQueryBaseFactory::instance.createNavigationHelper(input, false, null)
		navigationHelper.registerEClasses(Collections::singleton(source))
		navigationHelper.getAllInstances(source).forEach[
			val partial = matcher.newMatch(it, null)
			val matchValues = matcher.getAllValues("Target",partial)
			val values = it.eGet(target)
			if(values instanceof EList){
				assertTrue(matchValues.equals((values as EList).toSet))
			} else if(values != null){
				assertTrue(matchValues.equals(newHashSet(values)))
			} else {
				assertTrue(matchValues.empty)
			}
		]
	}

	def inputModel(){
		val ecore = EPackageRegistryImpl::INSTANCE.getEPackage("http://www.eclipse.org/emf/2002/Ecore")
		val rs = new ResourceSetImpl
		rs.resources.add(ecore.eResource)
		rs
	}

	def eClassFeaturesBaseTest(EStructuralFeature target, String patternName){
		val source = EcorePackage$Literals::ECLASS
		val patternFQN = "org.eclipse.viatra2.emf.incquery.ecore.eclass."+patternName
		inputModel.ecoreBaseTest(patternFQN, source, target)
	}
	
	@Test
	def eAttributesTest(){
		eClassFeaturesBaseTest(EcorePackage$Literals::ECLASS__EATTRIBUTES, "eAttributes")
	}
	
	@Test
	def eReferencesTest(){
		eClassFeaturesBaseTest(EcorePackage$Literals::ECLASS__EREFERENCES, "eReferences")
	}
	
	@Test
	def eSuperTypesTest(){
		eClassFeaturesBaseTest(EcorePackage$Literals::ECLASS__ESUPER_TYPES, "eSuperTypes")
	}
	
	@Test
	def eAllGenericSuperTypes(){
		eClassFeaturesBaseTest(EcorePackage$Literals::ECLASS__EALL_GENERIC_SUPER_TYPES, "eAllGenericSuperTypes")
	}
	
	@Test
	def eAllSuperTypesTest(){
		eClassFeaturesBaseTest(EcorePackage$Literals::ECLASS__EALL_SUPER_TYPES, "eAllSuperTypes")
	}
	
	@Test
	def eAllStructuralFeaturesTest(){
		eClassFeaturesBaseTest(EcorePackage$Literals::ECLASS__EALL_STRUCTURAL_FEATURES, "eAllStructuralFeatures")
	}
	
	@Test
	def eAllAttributesTest(){
		eClassFeaturesBaseTest(EcorePackage$Literals::ECLASS__EALL_ATTRIBUTES, "eAllAttributes")
	}

	@Test
	def eAllReferencesTest(){
		eClassFeaturesBaseTest(EcorePackage$Literals::ECLASS__EALL_REFERENCES, "eAllReferences")
	}
	
	@Test
	def eAllContainmentsTest(){
		eClassFeaturesBaseTest(EcorePackage$Literals::ECLASS__EALL_CONTAINMENTS, "eAllContainments")
	}
	
	@Test
	def eAllOperationsTest(){
		eClassFeaturesBaseTest(EcorePackage$Literals::ECLASS__EALL_OPERATIONS, "eAllOperations")
	}
	
	@Test
	def eIDAttributeTest(){
		eClassFeaturesBaseTest(EcorePackage$Literals::ECLASS__EID_ATTRIBUTE, "eIDAttribute")
	}
	
	@Test
	def eAttributeTypeTest(){
		val source = EcorePackage$Literals::EATTRIBUTE
		val target = EcorePackage$Literals::EATTRIBUTE__EATTRIBUTE_TYPE
		val patternFQN = "org.eclipse.viatra2.emf.incquery.ecore.eType"
		inputModel.ecoreBaseTest(patternFQN, source, target)
	}
	
	@Test
	def containerTest(){
		val source = EcorePackage$Literals::EREFERENCE
		val target = EcorePackage$Literals::EREFERENCE__CONTAINER
		val patternFQN = "org.eclipse.viatra2.emf.incquery.ecore.container"
		inputModel.ecoreBaseTest(patternFQN, source, target)
	}
	
	@Test
	def eReferenceTypeTest(){
		val source = EcorePackage$Literals::EREFERENCE
		val target = EcorePackage$Literals::EREFERENCE__EREFERENCE_TYPE
		val patternFQN = "org.eclipse.viatra2.emf.incquery.ecore.eReferenceType"
		inputModel.ecoreBaseTest(patternFQN, source, target)
	}
	
	@Test
	def manyTest(){
		val source = EcorePackage$Literals::ETYPED_ELEMENT
		val target = EcorePackage$Literals::ETYPED_ELEMENT__MANY
		val patternFQN = "org.eclipse.viatra2.emf.incquery.ecore.many"
		inputModel.ecoreBaseTest(patternFQN, source, target)
	}
	
	@Test
	def requiredTest(){
		val source = EcorePackage$Literals::ETYPED_ELEMENT
		val target = EcorePackage$Literals::ETYPED_ELEMENT__REQUIRED
		val patternFQN = "org.eclipse.viatra2.emf.incquery.ecore.required"
		inputModel.ecoreBaseTest(patternFQN, source, target)
	}
	
	override snapshotURI() {
		throw new UnsupportedOperationException("Auto-generated function stub")
	}
	
	override queryInputXMIURI() {
		"org.eclipse.viatra2.emf.incquery.ecore.derived/queries/globalEiqModel.xmi"
	}
	
}