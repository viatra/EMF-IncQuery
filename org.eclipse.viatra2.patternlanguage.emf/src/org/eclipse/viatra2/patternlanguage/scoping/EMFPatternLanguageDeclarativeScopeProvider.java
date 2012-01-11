/*******************************************************************************
 * Copyright (c) 2011 Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.patternlanguage.scoping;

import static org.eclipse.emf.ecore.util.EcoreUtil.getRootContainer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageScopeHelper;
import org.eclipse.viatra2.patternlanguage.ResolutionException;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionTail;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Type;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.util.PatternLanguageSwitch;
import org.eclipse.viatra2.patternlanguage.core.scoping.PatternLanguageDeclarativeScopeProvider;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ReferenceType;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.SimpleScope;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

/**
 * <p>An extended abstract declarative scope provider to facilitate the reusing of abstract
 * declarative scope providers together with XBase scope provider.</p>
 * <p>See <a href="http://www.eclipse.org/forums/index.php/mv/msg/219841/699521/#msg_699521">http://www.eclipse.org/forums/index.php/mv/msg/219841/699521/#msg_699521</a> for details.</p>
 * @author Zoltan Ujhelyi
 *
 */
public class EMFPatternLanguageDeclarativeScopeProvider extends
		PatternLanguageDeclarativeScopeProvider {
	
	@Inject
	private IQualifiedNameConverter qualifiedNameConverter;
	
	/**
	 * {@inheritDoc}
	 * Overridden for debugging purposes.
	 */
	@Override
	protected Predicate<Method> getPredicate(EObject context, EClass type) {
		String methodName = "scope_" + type.getName();
		//System.out.println(methodName + " ctx " + context.eClass().getName());
		return PolymorphicDispatcher.Predicates.forName(methodName, 2);
	}

	/**
	 * {@inheritDoc}
	 * Overridden for debugging purposes.
	 */
	@Override
	protected Predicate<Method> getPredicate(EObject context, EReference reference) {
		String methodName = "scope_" + reference.getEContainingClass().getName() + "_" + reference.getName();
		//System.out.println(methodName + " ctx " + context.eClass().getName());
		return PolymorphicDispatcher.Predicates.forName(methodName, 2);
	}
	
	public IScope scope_EPackage(PackageImport ctx, EReference ref){
		IScope current = new SimpleScope(IScope.NULLSCOPE, Iterables.transform(EPackage.Registry.INSTANCE.keySet(), new Function<String, IEObjectDescription>() {
			public IEObjectDescription apply(String from) {
				InternalEObject proxyPackage = (InternalEObject) EcoreFactory.eINSTANCE.createEPackage();
				proxyPackage.eSetProxyURI(URI.createURI(from));
				QualifiedName qualifiedName = qualifiedNameConverter.toQualifiedName(from);
				return EObjectDescription.create(qualifiedName, proxyPackage, Collections.singletonMap("nsURI", "true"));
			}
		}));
		return current;
	}
	
	public IScope scope_EClass(PatternBody ctx, EReference ref) {
		// This is needed for content assist - in that case the ClassType does not exists
		EObject root = getRootContainer(ctx);
		if (root instanceof PatternModel){
			return createReferencedPackagesScope((PatternModel) root);
		} else 
			return IScope.NULLSCOPE;
	}
	
	public IScope scope_EClass(ClassType ctx, EReference ref) {
		EObject root = getRootContainer(ctx);
		if (root instanceof PatternModel){
			return createReferencedPackagesScope((PatternModel) root);
		} else 
			return IScope.NULLSCOPE;
	}
	
	protected IScope createClassifierScope(Iterable<EClassifier> classifiers) {
		return Scopes.scopeFor(classifiers);
	}
	
	protected IScope createReferencedPackagesScope(PatternModel model) {
		final Collection<EClassifier> allClassifiers = new ArrayList<EClassifier>();
		for(PackageImport decl: model.getImportPackages()) {
			if (decl.getEPackage() != null)
				allClassifiers.addAll(decl.getEPackage().getEClassifiers());
		}
		return createClassifierScope(allClassifiers);
	}
	
	public IScope scope_EStructuralFeature(PathExpressionHead ctx, EReference ref) {
		// This is needed for content assist - in that case the ExpressionTail does not exists
		return expressionParentScopeProvider.doSwitch(ctx);
	}
	
	public IScope scope_EStructuralFeature(PathExpressionTail ctx, EReference ref) {
		return expressionParentScopeProvider.doSwitch(ctx.eContainer());
	}
	
	public IScope scope_EEnumLiteral(PathExpressionHead ctx, EReference ref) {
		EEnum type;
		try {
			type = EMFPatternLanguageScopeHelper.calculateEnumerationType(ctx);
		} catch (ResolutionException e) {
			return IScope.NULLSCOPE;
		}
		return calculateEnumLiteralScope(type);
	}
	
	public IScope scope_EEnumLiteral(PathExpressionTail ctx, EReference ref) {
		EEnum type;
		try {
			type = EMFPatternLanguageScopeHelper.calculateEnumerationType(ctx);
		} catch (ResolutionException e) {
			return IScope.NULLSCOPE;
		}
		return calculateEnumLiteralScope(type);
	}
	
	private IScope calculateEnumLiteralScope(EEnum enumeration) {
		EList<EEnumLiteral> literals = enumeration.getELiterals();
		return Scopes.scopeFor(literals);
	}
	
	private ParentScopeProvider expressionParentScopeProvider = new ParentScopeProvider();
	
	class ParentScopeProvider extends PatternLanguageSwitch<IScope> {

		@Override
		public IScope casePathExpressionHead(PathExpressionHead object) {
			return calculateReferences(object.getType());
		}

		@Override
		public IScope casePathExpressionTail(PathExpressionTail object) {
			return calculateReferences(object.getType());
		}
		
		private IScope calculateReferences(Type type) {
			List<EStructuralFeature> targetReferences = Collections.emptyList();
			if (type instanceof ReferenceType) {
				EClassifier referredType = ((ReferenceType) type).getRefname().getEType();
				if (referredType instanceof EClass) {
					targetReferences = ((EClass) referredType).getEAllStructuralFeatures();
				}
			} else if (type instanceof ClassType) {
				targetReferences = ((ClassType) type).getClassname().getEAllStructuralFeatures();
			}
			return Scopes.scopeFor(targetReferences);
		}
	}
}
