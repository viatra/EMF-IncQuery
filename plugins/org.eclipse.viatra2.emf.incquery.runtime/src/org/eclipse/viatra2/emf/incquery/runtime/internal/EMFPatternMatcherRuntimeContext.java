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

package org.eclipse.viatra2.emf.incquery.runtime.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.IManipulationListener;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.IPredicateTraceListener;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.PredicateEvaluatorNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherRuntimeContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;



/**
 * @author Bergmann Gábor
 *
 */
public abstract class EMFPatternMatcherRuntimeContext<PatternDescription> 
	extends EMFPatternMatcherContext<PatternDescription> 
	implements IPatternMatcherRuntimeContext<PatternDescription>
{
	protected abstract EMFContainmentHierarchyTraversal newTraversal();
	protected abstract ExtensibleEMFManipulationListener newListener(ReteEngine<PatternDescription> engine);
	
	protected Collection<EMFVisitor> waitingVisitors;
	boolean traversalCoalescing;
	protected ExtensibleEMFManipulationListener listener;

	
	/**
	 * @param visitor
	 */
	protected void doVisit(CustomizedEMFVisitor visitor) {
		if (traversalCoalescing) waitingVisitors.add(visitor);
		else newTraversal().accept(visitor);
	}
	
	class CustomizedEMFVisitor extends EMFVisitor {
		@Override
		public final void visitNonContainmentReference(EObject source, EReference feature, EObject target) {
			if (target == null) return; // null-valued attributes / references are simply not stored
			if (feature.getEOpposite() != null && feature.getEOpposite().isContainment()) return;
			considerForExpansion(target);
			doVisitReference(source, feature, target);
		}

		@Override
		public void visitInternalContainment(EObject source,EReference feature, EObject target) {
			if (target == null) return; // null-valued attributes / references are simply not stored
			if (feature.getEOpposite() != null) {
				doVisitReference(target, feature.getEOpposite(), source);			
			}
			doVisitReference(source, feature, target);
		}
//		@Override
//		public void visitExternalReference(EObject source, EReference feature, EObject target) {
//			if (target == null) return; // null-valued attributes / references are simply not stored
//			if (feature.getEOpposite() != null && feature.getEOpposite().isContainment()) return;
//			doVisitReference(source, feature, target);
//		}
		void doVisitReference(EObject source, EReference feature, EObject target) {}
	}
	
	public static class ForResourceSet<PatternDescription> extends EMFPatternMatcherRuntimeContext<PatternDescription> {
		ResourceSet root;
		Collection<Resource> additionalResources;
		public ForResourceSet(ResourceSet root, IncQueryEngine iqEngine) {
			super(iqEngine);
			this.root = root;
			this.additionalResources = new HashSet<Resource>();
		}
		@Override
		protected EMFContainmentHierarchyTraversal newTraversal() {
			return new EMFContainmentHierarchyTraversal(root, additionalResources);
		}
		@Override
		protected ExtensibleEMFManipulationListener newListener(ReteEngine<PatternDescription> engine) {
			ExtensibleEMFManipulationListener emfContentTreeViralListener = new EMFContentTreeViralListener(engine, root, this);
			for (Resource resource : additionalResources) {
				emfContentTreeViralListener.addRoot(resource);
			}
			return emfContentTreeViralListener;
		}	
		@Override
		public void considerForExpansion(EObject obj) {
			Resource eResource = obj.eResource();
			if (eResource != null && eResource.getResourceSet() == null && !additionalResources.contains(eResource)) {
				additionalResources.add(eResource);
				listener.addRoot(eResource);
			}
		}
	}
	public static class ForResource<PatternDescription> extends EMFPatternMatcherRuntimeContext<PatternDescription> {
		Resource root;
		public ForResource(Resource root, IncQueryEngine iqEngine) {
			super(iqEngine);
			this.root = root;
		}
		@Override
		protected EMFContainmentHierarchyTraversal newTraversal() {
			return new EMFContainmentHierarchyTraversal(root);
		}	
		@Override
		protected ExtensibleEMFManipulationListener newListener(ReteEngine<PatternDescription> engine) {
			return new EMFContentTreeViralListener(engine, root, this);
		}	
		@Override
		public void considerForExpansion(EObject obj) {}
	}
	public static class ForEObject<PatternDescription> extends EMFPatternMatcherRuntimeContext<PatternDescription> {
		EObject root;
		public ForEObject(EObject root, IncQueryEngine iqEngine) {
			super(iqEngine);
			this.root = root;
		}
		@Override
		protected EMFContainmentHierarchyTraversal newTraversal() {
			return new EMFContainmentHierarchyTraversal(root);
		}	
		@Override
		protected ExtensibleEMFManipulationListener newListener(ReteEngine<PatternDescription> engine) {
			return new EMFContentTreeViralListener(engine, root, this);
		}	
		@Override
		public void considerForExpansion(EObject obj) {}
	}
//	public static class ForTransactionalEditingDomain<PatternDescription> extends EMFPatternMatcherRuntimeContext<PatternDescription> {
//		TransactionalEditingDomain domain;
//		public ForTransactionalEditingDomain(TransactionalEditingDomain domain) {
//			super();
//			this.domain = domain;
//		}
//		@Override
//		protected EMFContainmentHierarchyTraversal newTraversal() {
//			return new EMFContainmentHierarchyTraversal(domain.getResourceSet());
//		}
//		@Override
//		protected ExtensibleEMFManipulationListener newListener(ReteEngine<PatternDescription> engine) {
//			return new EMFTransactionalEditingDomainListener(engine, domain, this);
//		}
//		@Override
//		public void considerForExpansion(EObject obj) {}
//
//	}
	
	/**
	 * Notifier must be EObject, Resource or ResourceSet
	 * @param notifier
	 */
	protected EMFPatternMatcherRuntimeContext(IncQueryEngine iqEngine) {
		super(iqEngine);
		this.waitingVisitors = new ArrayList<EMFVisitor>();
		this.traversalCoalescing = false;
	}
	
	@Override
	public void startCoalescing() {
		assert(!traversalCoalescing);
		traversalCoalescing = true;
	}
	@Override
	public void finishCoalescing() {
		assert(traversalCoalescing);
		traversalCoalescing = false;
		if (! waitingVisitors.isEmpty()){
			newTraversal().accept(new MultiplexerVisitor(waitingVisitors));
			waitingVisitors.clear();
		}
	}

	@Override
	public void enumerateAllBinaryEdges(final ModelElementPairCrawler crawler) {
		CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
			@Override
			public void visitAttribute(EObject source, EAttribute feature, Object target) {
				if (target != null) // Exclude NULL attribute values from RETE
					crawler.crawl(source, target);
				super.visitAttribute(source, feature, target);
			}
			@Override
			public void doVisitReference(EObject source, EReference feature, EObject target) {
				crawler.crawl(source, target);
			}
		};
		doVisit(visitor);
	}

	@Override
	public void enumerateAllGeneralizations(ModelElementPairCrawler crawler) {
		throw new UnsupportedOperationException();
	}

	@Override
	// Only direct instantiation of unaries is supported now
	public void enumerateAllInstantiations(final ModelElementPairCrawler crawler) {
		CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
			@Override
			public void visitAttribute(EObject source, EAttribute feature, Object target) {
				if (target != null) // Exclude NULL attribute values from RETE
					crawler.crawl(feature.getEAttributeType(), target);
			}
			@Override
			public void visitElement(EObject source) {
				crawler.crawl(source.eClass(), source);
			}
		};
		doVisit(visitor);
	}

	@Override
	public void enumerateAllTernaryEdges(final ModelElementCrawler crawler) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void enumerateAllUnaries(final ModelElementCrawler crawler) {
		CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
			@Override
			public void visitAttribute(EObject source, EAttribute feature, Object target) {
				if (target != null) // Exclude NULL attribute values from RETE
					crawler.crawl(target);
				super.visitAttribute(source, feature, target);
			}
			@Override
			public void visitElement(EObject source) {
				crawler.crawl(source);
				super.visitElement(source);
			}
		};
		doVisit(visitor);
	}

	@Override
	public void enumerateAllUnaryContainments(final ModelElementPairCrawler crawler) {
		CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
			// FIXME: containment no longer holds between EObject and its raw attribute values.
//			@Override
//			public void visitAttribute(EObject source, EAttribute feature, Object target) {
//				if (target != null) // Exclude NULL attribute values from RETE
//					crawler.crawl(source, target);
//				super.visitAttribute(source, feature, target);
//			}
			@Override
			public void doVisitReference(EObject source, EReference feature, EObject target) {
				if (feature.isContainment()) crawler.crawl(source, target);
			}
		};
		doVisit(visitor);
	}

	@Override
	public void enumerateDirectBinaryEdgeInstances(Object typeObject, final ModelElementPairCrawler crawler) {
		final EStructuralFeature structural = (EStructuralFeature) typeObject;
		CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
			@Override
			public void visitAttribute(EObject source, EAttribute feature, Object target) {
				if (structural.equals(feature) && target != null) // NULL attribute values excluded from RETE
					crawler.crawl(source, target);
				super.visitAttribute(source, feature, target);
			}
			@Override
			public void doVisitReference(EObject source, EReference feature, EObject target) {
				if (structural.equals(feature)) crawler.crawl(source, target);
			}
		};
		doVisit(visitor);
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
			CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
				@Override
				public void visitElement(EObject source) {			
					if (source.eClass().equals(typeObject)) crawler.crawl(source);
					super.visitElement(source);
				}
			};
			doVisit(visitor);
		} else if (typeObject instanceof EDataType) {
			CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
				@Override
				public void visitAttribute(EObject source, EAttribute feature, Object target) {
					if (target != null && ((EDataType)typeObject).isInstance(target)) // Exclude NULL attribute values from RETE
						crawler.crawl(target);
					super.visitAttribute(source, feature, target);
				}
			};
			doVisit(visitor);
		} else throw new IllegalArgumentException("typeObject has invalid type " + typeObject.getClass().getName());
	}
	@Override
	public void enumerateAllUnaryInstances(final Object typeObject, final ModelElementCrawler crawler) {
		if (typeObject instanceof EClass) {
			CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
				@Override
				public void visitElement(EObject source) {			
					if (((EClass)typeObject).isInstance(source)) crawler.crawl(source);
					super.visitElement(source);
				}
			};
			doVisit(visitor);
		} else if (typeObject instanceof EDataType) {
			CustomizedEMFVisitor visitor = new CustomizedEMFVisitor() {
				@Override
				public void visitAttribute(EObject source, EAttribute feature, Object target) {
					if (target != null && ((EDataType)typeObject).isInstance(target)) // Exclude NULL attribute values from RETE
						crawler.crawl(target);
					super.visitAttribute(source, feature, target);
				}
			};
			doVisit(visitor);
		} else throw new IllegalArgumentException("typeObject has invalid type " + typeObject.getClass().getName());
	}

	@Override
	public void modelReadLock() {
		// TODO runnable? domain.runExclusive(read)
		
	}

	@Override
	public void modelReadUnLock() {
		// TODO runnable? domain.runExclusive(read)
			
	}

	//	@Override
	//	public String retrieveUnaryTypeFQN(Object typeObject) {
	//		return contextMapping.retrieveFQN((EClassifier)typeObject);
	//	}
	//
	//	@Override
	//	public String retrieveBinaryEdgeTypeFQN(Object typeObject) {
	//		return contextMapping.retrieveFQN((EStructuralFeature)typeObject);
	//	}
	//
	//	@Override
	//	public String retrieveTernaryEdgeTypeFQN(Object typeObject) {
	//		throw new UnsupportedOperationException();
	//	}	
		
	
	@Override
	// TODO Transactional?
	public IManipulationListener subscribePatternMatcherForUpdates(
			ReteEngine<PatternDescription> engine) {
		if (listener == null) listener = newListener(engine);
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

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.IPatternMatcherRuntimeContext#subscribePatternMatcherForTraceInfluences(org.eclipse.viatra2.gtasm.patternmatcher.incremental.ReteEngine)
	 */
	@Override
	public IPredicateTraceListener subscribePatternMatcherForTraceInfluences(ReteEngine<PatternDescription> engine) {
		// No ASMFunctions, use DUMMY
		return new IPredicateTraceListener() {
			@Override
			public void registerSensitiveTrace(Tuple trace,
					PredicateEvaluatorNode node) {
			}
			@Override
			public void unregisterSensitiveTrace(Tuple trace,
					PredicateEvaluatorNode node) {
			}
			@Override
			public void disconnect() {
			}
		};
	}
	
	/**
	 * Consider expanding the notification scope to this object and surroundings.
	 * Hack added primarily to handle EPackage instances referenced by nsUri
	 * @param obj
	 */
	public abstract void considerForExpansion(EObject obj);
}
