/*******************************************************************************
 * Copyright (c) 2004-2009 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.runtime.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.base.api.NavigationHelper;
import org.eclipse.incquery.runtime.rete.boundary.IManipulationListener;
import org.eclipse.incquery.runtime.rete.boundary.IPredicateTraceListener;
import org.eclipse.incquery.runtime.rete.boundary.PredicateEvaluatorNode;
import org.eclipse.incquery.runtime.rete.matcher.IPatternMatcherRuntimeContext;
import org.eclipse.incquery.runtime.rete.matcher.ReteEngine;
import org.eclipse.incquery.runtime.rete.tuple.Tuple;

/**
 * @author Bergmann Gábor
 * 
 */
public class EMFPatternMatcherRuntimeContext extends EMFPatternMatcherContext implements
        IPatternMatcherRuntimeContext<Pattern> {

    // protected abstract EMFContainmentHierarchyTraversal newTraversal();
    // protected abstract ExtensibleEMFManipulationListener newListener(ReteEngine<PatternDescription> engine);

    // protected Collection<EMFVisitor> waitingVisitors;
    // boolean traversalCoalescing;
    // protected ExtensibleEMFManipulationListener listener;
    private final NavigationHelper baseIndex;
    private BaseIndexListener listener;

    // protected void traverse(EMFVisitor visitor) {
    // try {
    // newTraversal().accept(visitor);
    // } catch (Exception ex) {
    // iqEngine.getLogger().logError(
    // "EMF-IncQuery encountered an error in processing the EMF model. " +
    // "This happened while traversing the model for the initialization of pattern match caches.", ex);
    // }
    // }

    // /**
    // * @param visitor
    // */
    // protected void doVisit(CustomizedEMFVisitor visitor) {
    // if (traversalCoalescing) waitingVisitors.add(visitor);
    // else traverse(visitor);
    // }
    //
    //
    //
    // class CustomizedEMFVisitor extends EMFVisitor {
    // @Override
    // public final void visitNonContainmentReference(EObject source, EReference feature, EObject target) {
    // if (target == null) return; // null-valued attributes / references are simply not stored
    // if (feature.getEOpposite() != null && feature.getEOpposite().isContainment()) return;
    // considerForExpansion(target);
    // doVisitReference(source, feature, target);
    // }
    //
    // @Override
    // public void visitInternalContainment(EObject source,EReference feature, EObject target) {
    // if (target == null) return; // null-valued attributes / references are simply not stored
    // if (feature.getEOpposite() != null) {
    // doVisitReference(target, feature.getEOpposite(), source);
    // }
    // doVisitReference(source, feature, target);
    // }
    // // @Override
    // // public void visitExternalReference(EObject source, EReference feature, EObject target) {
    // // if (target == null) return; // null-valued attributes / references are simply not stored
    // // if (feature.getEOpposite() != null && feature.getEOpposite().isContainment()) return;
    // // doVisitReference(source, feature, target);
    // // }
    // void doVisitReference(EObject source, EReference feature, EObject target) {}
    // }

    // public static class ForResourceSet<PatternDescription> extends
    // EMFPatternMatcherRuntimeContext<PatternDescription> {
    // ResourceSet root;
    // Collection<Resource> additionalResources;
    // public ForResourceSet(ResourceSet root, IncQueryEngine iqEngine) {
    // super(iqEngine);
    // this.root = root;
    // this.additionalResources = new HashSet<Resource>();
    // }
    // @Override
    // protected EMFContainmentHierarchyTraversal newTraversal() {
    // return new EMFContainmentHierarchyTraversal(root, additionalResources);
    // }
    // @Override
    // protected ExtensibleEMFManipulationListener newListener(ReteEngine<PatternDescription> engine) {
    // ExtensibleEMFManipulationListener emfContentTreeViralListener = new EMFContentTreeViralListener(engine, root,
    // this, iqEngine.getLogger());
    // for (Resource resource : additionalResources) {
    // emfContentTreeViralListener.addRoot(resource);
    // }
    // return emfContentTreeViralListener;
    // }
    // @Override
    // public void considerForExpansion(EObject obj) {
    // Resource eResource = obj.eResource();
    // if (eResource != null && eResource.getResourceSet() == null && !additionalResources.contains(eResource)) {
    // additionalResources.add(eResource);
    // listener.addRoot(eResource);
    // }
    // }
    // }
    // public static class ForResource<PatternDescription> extends EMFPatternMatcherRuntimeContext<PatternDescription> {
    // Resource root;
    // public ForResource(Resource root, IncQueryEngine iqEngine) {
    // super(iqEngine);
    // this.root = root;
    // }
    // @Override
    // protected EMFContainmentHierarchyTraversal newTraversal() {
    // return new EMFContainmentHierarchyTraversal(root);
    // }
    // @Override
    // protected ExtensibleEMFManipulationListener newListener(ReteEngine<PatternDescription> engine) {
    // return new EMFContentTreeViralListener(engine, root, this, iqEngine.getLogger());
    // }
    // @Override
    // public void considerForExpansion(EObject obj) {}
    // }
    // public static class ForEObject<PatternDescription> extends EMFPatternMatcherRuntimeContext<PatternDescription> {
    // EObject root;
    // public ForEObject(EObject root, IncQueryEngine iqEngine) {
    // super(iqEngine);
    // this.root = root;
    // }
    // @Override
    // protected EMFContainmentHierarchyTraversal newTraversal() {
    // return new EMFContainmentHierarchyTraversal(root);
    // }
    // @Override
    // protected ExtensibleEMFManipulationListener newListener(ReteEngine<PatternDescription> engine) {
    // return new EMFContentTreeViralListener(engine, root, this, iqEngine.getLogger());
    // }
    // @Override
    // public void considerForExpansion(EObject obj) {}
    // }
    // public static class ForTransactionalEditingDomain<PatternDescription> extends
    // EMFPatternMatcherRuntimeContext<PatternDescription> {
    // TransactionalEditingDomain domain;
    // public ForTransactionalEditingDomain(TransactionalEditingDomain domain) {
    // super();
    // this.domain = domain;
    // }
    // @Override
    // protected EMFContainmentHierarchyTraversal newTraversal() {
    // return new EMFContainmentHierarchyTraversal(domain.getResourceSet());
    // }
    // @Override
    // protected ExtensibleEMFManipulationListener newListener(ReteEngine<PatternDescription> engine) {
    // return new EMFTransactionalEditingDomainListener(engine, domain, this);
    // }
    // @Override
    // public void considerForExpansion(EObject obj) {}
    //
    // }

    /**
     * Notifier must be EObject, Resource or ResourceSet
     * 
     * @param notifier
     */
    public EMFPatternMatcherRuntimeContext(IncQueryEngine iqEngine, NavigationHelper baseIndex) {
        super(iqEngine);
        this.baseIndex = baseIndex;
        // this.waitingVisitors = new ArrayList<EMFVisitor>();
        // this.traversalCoalescing = false;
    }

    @Override
    public <V> V coalesceTraversals(Callable<V> callable) throws InvocationTargetException {
        return baseIndex.coalesceTraversals(callable);
    }

    // @Override
    // public void startCoalescing() {
    // assert(!traversalCoalescing);
    // traversalCoalescing = true;
    // }
    // @Override
    // public void finishCoalescing() {
    // assert(traversalCoalescing);
    // traversalCoalescing = false;
    // if (! waitingVisitors.isEmpty()){
    // ArrayList<EMFVisitor> visitors = new ArrayList<EMFVisitor>(waitingVisitors);
    // waitingVisitors.clear();
    // newTraversal().accept(new MultiplexerVisitor(visitors));
    // }
    // }

    @Override
    public void enumerateAllBinaryEdges(final ModelElementPairCrawler crawler) {
        throw new UnsupportedOperationException();

        // CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
        // @Override
        // public void visitAttribute(EObject source, EAttribute feature, Object target) {
        // if (target != null) // Exclude NULL attribute values from RETE
        // crawler.crawl(source, target);
        // super.visitAttribute(source, feature, target);
        // }
        // @Override
        // public void doVisitReference(EObject source, EReference feature, EObject target) {
        // crawler.crawl(source, target);
        // }
        // };
        // doVisit(visitor);
    }

    @Override
    public void enumerateAllGeneralizations(ModelElementPairCrawler crawler) {
        throw new UnsupportedOperationException();
    }

    @Override
    // Only direct instantiation of unaries is supported now
    public void enumerateAllInstantiations(final ModelElementPairCrawler crawler) {
        throw new UnsupportedOperationException();
        // CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
        // @Override
        // public void visitAttribute(EObject source, EAttribute feature, Object target) {
        // if (target != null) // Exclude NULL attribute values from RETE
        // crawler.crawl(feature.getEAttributeType(), target);
        // }
        // @Override
        // public void visitElement(EObject source) {
        // crawler.crawl(source.eClass(), source);
        // }
        // };
        // doVisit(visitor);
    }

    @Override
    public void enumerateAllTernaryEdges(final ModelElementCrawler crawler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enumerateAllUnaries(final ModelElementCrawler crawler) {
        throw new UnsupportedOperationException();
        // CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
        // @Override
        // public void visitAttribute(EObject source, EAttribute feature, Object target) {
        // if (target != null) // Exclude NULL attribute values from RETE
        // crawler.crawl(target);
        // super.visitAttribute(source, feature, target);
        // }
        // @Override
        // public void visitElement(EObject source) {
        // crawler.crawl(source);
        // super.visitElement(source);
        // }
        // };
        // doVisit(visitor);
    }

    @Override
    public void enumerateAllUnaryContainments(final ModelElementPairCrawler crawler) {
        throw new UnsupportedOperationException();
        // CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
        // // FIXME: containment no longer holds between EObject and its raw attribute values.
        // // @Override
        // // public void visitAttribute(EObject source, EAttribute feature, Object target) {
        // // if (target != null) // Exclude NULL attribute values from RETE
        // // crawler.crawl(source, target);
        // // super.visitAttribute(source, feature, target);
        // // }
        // @Override
        // public void doVisitReference(EObject source, EReference feature, EObject target) {
        // if (feature.isContainment()) crawler.crawl(source, target);
        // }
        // };
        // doVisit(visitor);
    }

    @Override
    public void enumerateDirectBinaryEdgeInstances(Object typeObject, final ModelElementPairCrawler crawler) {
        final EStructuralFeature structural = (EStructuralFeature) typeObject;
        listener.ensure(structural);
        final Collection<EObject> holders = baseIndex.getHoldersOfFeature(structural);
        for (EObject holder : holders) {
            if (structural.isMany()) {
                final Collection<?> values = (Collection<?>) holder.eGet(structural);
                for (Object value : values) {
                    crawler.crawl(holder, value);
                }
            } else {
                final Object value = holder.eGet(structural);
                if (value != null)
                    crawler.crawl(holder, value);
            }
        }
        // CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
        // @Override
        // public void visitAttribute(EObject source, EAttribute feature, Object target) {
        // if (structural.equals(feature) && target != null) // NULL attribute values excluded from RETE
        // crawler.crawl(source, target);
        // super.visitAttribute(source, feature, target);
        // }
        // @Override
        // public void doVisitReference(EObject source, EReference feature, EObject target) {
        // if (structural.equals(feature)) crawler.crawl(source, target);
        // }
        // };
        // doVisit(visitor);
    }

    @Override
    public void enumerateAllBinaryEdgeInstances(Object typeObject, final ModelElementPairCrawler crawler) {
        enumerateDirectBinaryEdgeInstances(typeObject, crawler); // No edge subtyping
    }

    @Override
    public void enumerateDirectTernaryEdgeInstances(Object typeObject, final ModelElementCrawler crawler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enumerateAllTernaryEdgeInstances(Object typeObject, final ModelElementCrawler crawler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void enumerateDirectUnaryInstances(final Object typeObject, final ModelElementCrawler crawler) {
        if (typeObject instanceof EClass) {
            final EClass eClass = (EClass) typeObject;
            listener.ensure(eClass);
            final Collection<EObject> allInstances = baseIndex.getDirectInstances(eClass);
            for (EObject eObject : allInstances) {
                crawler.crawl(eObject);
            }
            // CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
            // @Override
            // public void visitElement(EObject source) {
            // if (source.eClass().equals(typeObject)) crawler.crawl(source);
            // super.visitElement(source);
            // }
            // };
            // doVisit(visitor);
        } else if (typeObject instanceof EDataType) {
            final EDataType eDataType = (EDataType) typeObject;
            listener.ensure(eDataType);
            final Collection<Object> allInstances = baseIndex.getDataTypeInstances(eDataType);
            for (Object value : allInstances) {
                crawler.crawl(value);
            }
            // CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
            // @Override
            // public void visitAttribute(EObject source, EAttribute feature, Object target) {
            // if (target != null && ((EDataType)typeObject).isInstance(target)) // Exclude NULL attribute values from
            // RETE
            // crawler.crawl(target);
            // super.visitAttribute(source, feature, target);
            // }
            // };
            // doVisit(visitor);
        } else
            throw new IllegalArgumentException("typeObject has invalid type " + typeObject.getClass().getName());
    }

    @Override
    public void enumerateAllUnaryInstances(final Object typeObject, final ModelElementCrawler crawler) {
        if (typeObject instanceof EClass) {
            final EClass eClass = (EClass) typeObject;
            listener.ensure(eClass);
            final Collection<EObject> allInstances = baseIndex.getAllInstances(eClass);
            for (EObject eObject : allInstances) {
                crawler.crawl(eObject);
            }
            // CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
            // @Override
            // public void visitElement(EObject source) {
            // if (((EClass)typeObject).isInstance(source)) crawler.crawl(source);
            // super.visitElement(source);
            // }
            // };
            // doVisit(visitor);
        } else if (typeObject instanceof EDataType) {
            final EDataType eDataType = (EDataType) typeObject;
            listener.ensure(eDataType);
            final Collection<Object> allInstances = baseIndex.getDataTypeInstances(eDataType);
            for (Object value : allInstances) {
                crawler.crawl(value);
            }
            // CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
            // @Override
            // public void visitAttribute(EObject source, EAttribute feature, Object target) {
            // if (target != null && ((EDataType)typeObject).isInstance(target)) // Exclude NULL attribute values from
            // RETE
            // crawler.crawl(target);
            // super.visitAttribute(source, feature, target);
            // }
            // };
            // doVisit(visitor);
        } else
            throw new IllegalArgumentException("typeObject has invalid type " + typeObject.getClass().getName());
    }

    @Override
    public void modelReadLock() {
        // TODO runnable? domain.runExclusive(read)

    }

    @Override
    public void modelReadUnLock() {
        // TODO runnable? domain.runExclusive(read)

    }

    // @Override
    // public String retrieveUnaryTypeFQN(Object typeObject) {
    // return contextMapping.retrieveFQN((EClassifier)typeObject);
    // }
    //
    // @Override
    // public String retrieveBinaryEdgeTypeFQN(Object typeObject) {
    // return contextMapping.retrieveFQN((EStructuralFeature)typeObject);
    // }
    //
    // @Override
    // public String retrieveTernaryEdgeTypeFQN(Object typeObject) {
    // throw new UnsupportedOperationException();
    // }

    @Override
    // TODO Transactional?
    public IManipulationListener subscribePatternMatcherForUpdates(ReteEngine<Pattern> engine) {
        if (listener == null)
            listener = new BaseIndexListener(iqEngine, engine, baseIndex);
        return listener;
    }

    @Override
    public Object ternaryEdgeSource(Object relation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object ternaryEdgeTarget(Object relation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPredicateTraceListener subscribePatternMatcherForTraceInfluences(ReteEngine<Pattern> engine) {
        // No ASMFunctions, use DUMMY
        return new IPredicateTraceListener() {
            @Override
            public void registerSensitiveTrace(Tuple trace, PredicateEvaluatorNode node) {
            }

            @Override
            public void unregisterSensitiveTrace(Tuple trace, PredicateEvaluatorNode node) {
            }

            @Override
            public void disconnect() {
            }
        };
    }

}
