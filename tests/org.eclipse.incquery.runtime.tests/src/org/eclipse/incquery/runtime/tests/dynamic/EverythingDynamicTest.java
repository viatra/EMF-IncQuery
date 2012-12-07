package org.eclipse.incquery.runtime.tests.dynamic;

import java.util.List;

import junit.framework.Assert;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.ClassType;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EClassifierConstraint;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.EMFPatternLanguageFactory;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PackageImport;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.incquery.patternlanguage.patternLanguage.ParameterRef;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternLanguageFactory;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.patternlanguage.patternLanguage.VariableReference;
import org.eclipse.incquery.runtime.api.GenericPatternMatch;
import org.eclipse.incquery.runtime.api.GenericPatternMatcher;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.junit.Test;

public class EverythingDynamicTest {

    @SuppressWarnings("unchecked")
    @Test
    public void everythingDynamic() {
        // Create the dynamic metamodel
        EcoreFactory theCoreFactory = EcoreFactory.eINSTANCE;
        EcorePackage theCorePackage = EcorePackage.eINSTANCE;

        EClass bookStoreEClass = theCoreFactory.createEClass();
        bookStoreEClass.setName("BookStore");
        EAttribute bookStoreOwner = theCoreFactory.createEAttribute();
        bookStoreOwner.setName("owner");
        bookStoreOwner.setEType(theCorePackage.getEString());
        bookStoreEClass.getEStructuralFeatures().add(bookStoreOwner);

        EClass bookEClass = theCoreFactory.createEClass();
        bookEClass.setName("Book");
        EAttribute bookName = theCoreFactory.createEAttribute();
        bookName.setName("name");
        bookName.setEType(theCorePackage.getEString());
        bookEClass.getEStructuralFeatures().add(bookName);

        EReference bookStore_Books = theCoreFactory.createEReference();
        bookStore_Books.setName("books");
        bookStore_Books.setEType(bookEClass);
        bookStore_Books.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
        bookStore_Books.setContainment(true);
        bookStoreEClass.getEStructuralFeatures().add(bookStore_Books);

        EPackage bookStoreEPackage = theCoreFactory.createEPackage();
        bookStoreEPackage.setName("BookStorePackage");
        bookStoreEPackage.setNsPrefix("bookStore");
        bookStoreEPackage.setNsURI("http:///org.example.incquery.bookstore");
        bookStoreEPackage.getEClassifiers().add(bookStoreEClass);
        bookStoreEPackage.getEClassifiers().add(bookEClass);

        // Create the dynamic instance
        EFactory bookFactoryInstance = bookStoreEPackage.getEFactoryInstance();

        EObject bookObject = bookFactoryInstance.create(bookEClass);
        bookObject.eSet(bookName, "Harry Potter and the Deathly Hallows");

        EObject bookStoreObject = bookFactoryInstance.create(bookStoreEClass);
        bookStoreObject.eSet(bookStoreOwner, "Somebody");
        ((List<EObject>) bookStoreObject.eGet(bookStore_Books)).add(bookObject);

        // Create the dynamic pattern
        PatternModel patternModel = EMFPatternLanguageFactory.eINSTANCE.createPatternModel();
        patternModel.setPackageName("TestPatternPackage");
        PackageImport packageImport = EMFPatternLanguageFactory.eINSTANCE.createPackageImport();
        packageImport.setEPackage(bookStoreEPackage);
        patternModel.getImportPackages().add(packageImport);

        Pattern pattern = PatternLanguageFactory.eINSTANCE.createPattern();
        PatternBody patternBody = PatternLanguageFactory.eINSTANCE.createPatternBody();
        Variable variable = PatternLanguageFactory.eINSTANCE.createVariable();
        variable.setName("X");
        pattern.setName("plainPattern");
        pattern.getBodies().add(patternBody);
        pattern.getParameters().add(variable);

        ParameterRef parameterRef = PatternLanguageFactory.eINSTANCE.createParameterRef();
        parameterRef.setReferredParam(variable);
        parameterRef.setName("X");
        VariableReference variableReference = PatternLanguageFactory.eINSTANCE.createVariableReference();
        variableReference.setVar("X");
        variableReference.setVariable(parameterRef);
        parameterRef.getReferences().add(variableReference);
        patternBody.getVariables().add(parameterRef);

        ClassType classType = EMFPatternLanguageFactory.eINSTANCE.createClassType();
        classType.setClassname(bookEClass);
        EClassifierConstraint classifierConstraint = EMFPatternLanguageFactory.eINSTANCE.createEClassifierConstraint();
        classifierConstraint.setVar(variableReference);
        classifierConstraint.setType(classType);
        patternBody.getConstraints().add(classifierConstraint);

        patternModel.getPatterns().add(pattern);

        // Matching
        List<GenericPatternMatch> matches = null;
        try {
            GenericPatternMatcher matcher = new GenericPatternMatcher(pattern, bookStoreObject);
            matches = (List<GenericPatternMatch>) matcher.getAllMatches();
        } catch (IncQueryException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(matches);
        GenericPatternMatch match = matches.get(0);
        Assert.assertEquals("\"X\"=Harry Potter and the Deathly Hallows", match.prettyPrint());
    }

}
