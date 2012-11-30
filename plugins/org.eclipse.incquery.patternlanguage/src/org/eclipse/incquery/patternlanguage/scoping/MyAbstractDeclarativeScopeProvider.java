/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.scoping;

import java.lang.reflect.Method;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.scoping.impl.AbstractScopeProvider;
import org.eclipse.xtext.util.PolymorphicDispatcher;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * <p>
 * An extended abstract declarative scope provider to facilitate the reusing of abstract declarative scope providers
 * together with XBase scope provider.
 * </p>
 * <p>
 * See <a
 * href="http://www.eclipse.org/forums/index.php/mv/msg/219841/699521/#msg_699521">http://www.eclipse.org/forums/index
 * .php/mv/msg/219841/699521/#msg_699521</a> for details.
 * </p>
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public class MyAbstractDeclarativeScopeProvider extends AbstractScopeProvider {

    public static final String NAMED_DELEGATE = "org.eclipse.xtext.scoping.impl.MyAbstractDeclarativeScopeProvider.delegate";
    public static final String NAMED_ERROR_HANDLER = "org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider.errorHandler";

    public final Logger logger = Logger.getLogger(getClass());

    @Inject
    @Named(NAMED_DELEGATE)
    private IScopeProvider delegate;

    protected IScope delegateGetScope(EObject context, EReference reference) {
        return getDelegate().getScope(context, reference);
    }

    public void setDelegate(IScopeProvider delegate) {
        this.delegate = delegate;
    }

    public IScopeProvider getDelegate() {
        return delegate;
    }

    @Inject(optional = true)
    @Named(NAMED_ERROR_HANDLER)
    private PolymorphicDispatcher.ErrorHandler<IScope> errorHandler = new PolymorphicDispatcher.NullErrorHandler<IScope>();

    protected Predicate<Method> getPredicate(EObject context, EClass type) {
        String methodName = "scope_" + type.getName();
        return PolymorphicDispatcher.Predicates.forName(methodName, 2);
    }

    protected Predicate<Method> getPredicate(EObject context, EReference reference) {
        String methodName = "scope_" + reference.getEContainingClass().getName() + "_" + reference.getName();
        return PolymorphicDispatcher.Predicates.forName(methodName, 2);
    }

    public IScope getScope(EObject context, EReference reference) {
        IScope scope = polymorphicFindScopeForReferenceName(context, reference);
        if (scope == null) {
            scope = polymorphicFindScopeForClassName(context, reference);
            if (scope == null) {
                scope = delegateGetScope(context, reference);
            }
        }
        return scope;
    }

    protected IScope polymorphicFindScopeForClassName(EObject context, EReference reference) {
        IScope scope = null;
        PolymorphicDispatcher<IScope> dispatcher = new PolymorphicDispatcher<IScope>(Collections.singletonList(this),
                getPredicate(context, reference.getEReferenceType()), errorHandler) {
            @Override
            protected IScope handleNoSuchMethod(Object... params) {
                if (PolymorphicDispatcher.NullErrorHandler.class.equals(errorHandler.getClass())) {
                    return null;
                }
                return super.handleNoSuchMethod(params);
            }
        };
        EObject current = context;
        while (scope == null && current != null) {
            scope = dispatcher.invoke(current, reference);
            current = current.eContainer();
        }
        current = context;
        while (scope == null && current != null) {
            scope = dispatcher.invoke(current, reference.getEReferenceType());
            if (scope != null) {
                logger.warn("scope_<EClass>(EObject,EClass) is deprecated. Use scope_<EClass>(EObject,EReference) instead.");
            }
            current = current.eContainer();
        }
        return scope;
    }

    protected IScope polymorphicFindScopeForReferenceName(EObject context, EReference reference) {
        Predicate<Method> predicate = getPredicate(context, reference);
        PolymorphicDispatcher<IScope> dispatcher = new PolymorphicDispatcher<IScope>(Collections.singletonList(this),
                predicate, errorHandler) {
            @Override
            protected IScope handleNoSuchMethod(Object... params) {
                if (PolymorphicDispatcher.NullErrorHandler.class.equals(errorHandler.getClass())) {
                    return null;
                }
                return super.handleNoSuchMethod(params);
            }
        };
        EObject current = context;
        IScope scope = null;
        while (scope == null && current != null) {
            scope = dispatcher.invoke(current, reference);
            current = current.eContainer();
        }
        return scope;
    }

    public void setErrorHandler(PolymorphicDispatcher.ErrorHandler<IScope> errorHandler) {
        this.errorHandler = errorHandler;
    }

    public PolymorphicDispatcher.ErrorHandler<IScope> getErrorHandler() {
        return errorHandler;
    }

}
