package org.eclipse.incquery.runtime.internal.boundary;
///*******************************************************************************
// * Copyright (c) 2004-2012 Gabor Bergmann and Daniel Varro
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *    Gabor Bergmann - initial API and implementation
// *******************************************************************************/
//
//package org.eclipse.viatra2.emf.incquery.runtime.internal.boundary;
//
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Set;
//
//import org.eclipse.emf.ecore.EObject;
//import org.eclipse.emf.ecore.EStructuralFeature;
//import org.eclipse.emf.ecore.EStructuralFeature.Setting;
//import org.eclipse.viatra2.emf.incquery.base.api.FeatureListener;
//import org.eclipse.viatra2.emf.incquery.base.api.NavigationHelper;
//import org.eclipse.viatra2.emf.incquery.base.exception.IncQueryBaseException;
//import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.Disconnectable;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.ReteBoundary;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.IdentityIndexer;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.NullIndexer;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.ProjectionIndexer;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.SpecializedProjectionIndexer;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.StandardNode;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.FlatTuple;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask;
//import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.util.Options;
//import org.eclipse.incquery.patternlanguage.PatternLanguage.Pattern;
//
///**
// * Input node relying on the NavigationUtil base index.
// * Represents the set of instance edges corresponding to a given {@link EStructuralFeature}.
// * 
// * @author Bergmann Gábor
// *
// */
//public class EStructuralFeatureBinaryInputNode extends StandardNode implements Disconnectable {
//
//	private EStructuralFeature feature;
//	private IncQueryEngine engine;
//	private NavigationHelper baseIndex;
//	private ReteEngine<Pattern> reteEngine;
//	private ReteBoundary<Pattern> boundary;
//		
//	static final TupleMask nullMask = TupleMask.linear(0, 2); 
//	static final TupleMask sourceKnown = TupleMask.selectSingle(0, 2); 
//	static final TupleMask targetKnown = TupleMask.selectSingle(1, 2); 
//	static final TupleMask identityMask = TupleMask.identity(2); 
//	
//	private NullIndexer nullIndexer;
//	private IdentityIndexer identityIndexer;
//	private ProjectionIndexer sourceIndexer;
//	private ProjectionIndexer targetIndexer;
//	// TODO more indexers
//	
//	private FeatureListener listener = new FeatureListener() {
//		
//		@Override
//		public void featureInserted(EObject host, EStructuralFeature feature, Object value) {
//			final Tuple tuple = makeTuple(host, value);
//			propagate(Direction.INSERT, tuple);
//		}
//		
//		@Override
//		public void featureDeleted(EObject host, EStructuralFeature feature, Object value) {
//			final Tuple tuple = makeTuple(host, value);
//			propagate(Direction.REVOKE, tuple);
//		}
//	};
//	
//	
//	
//	/**
//	 * @param engine
//	 * @param reteContainer
//	 * @param feature
//	 * @throws IncQueryBaseException 
//	 */
//	public EStructuralFeatureBinaryInputNode(IncQueryEngine engine, ReteContainer reteContainer, EStructuralFeature feature) throws IncQueryBaseException {
//		super(reteContainer);
//		this.engine = engine;
//		this.baseIndex = engine.getBaseIndex();
//		this.reteEngine = engine.getReteEngine();
//		this.boundary = reteEngine.getBoundary();
//		this.feature = feature;
//		setTag(feature.getName());
//		
//		baseIndex.registerFeatureListener(Collections.singleton(feature), listener);
//		reteEngine.addDisconnectable(this);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier#pullInto(java.util.Collection)
//	 */
//	@Override
//	public void pullInto(Collection<Tuple> collector) {
////		Collection<Setting> allSettings =  baseIndex.getAllValuesOfFeature(feature);
////		for (Setting setting : allSettings) {
////			collector.add(makeTuple(setting.getEObject(), setting.get(true)));
////		}
//		collector.addAll(tuples());
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.Disconnectable#disconnect()
//	 */
//	@Override
//	public void disconnect() {
//		baseIndex.unregisterFeatureListener(Collections.singleton(feature), listener);
//	}
//	
//	protected Tuple makeTuple(EObject source, Object target) {
//		return new FlatTuple(boundary.wrapElement(source), boundary.wrapElement(target));
//	}
//	protected Tuple makeTupleSingle(Object element) {
//		return new FlatTuple(boundary.wrapElement(element));
//	}
//	
//	protected void propagate(Direction direction, final Tuple tuple) {
//		propagateUpdate(direction, tuple);
//		if (identityIndexer != null) identityIndexer.propagate(direction, tuple);
//		if (nullIndexer != null) nullIndexer.propagate(direction, tuple);
//	}
//
//	protected Collection<Tuple> tuples() {
//		final Collection<Tuple> result = new HashSet<Tuple>();
//		final Set<Setting> settings = baseIndex.getOccurrencesOfFeature(feature);
//		if (settings != null) for (Setting setting : settings) {
//			result.add(makeTuple(setting.getEObject(), setting.get(true)));
//		}
//		return result;
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.StandardNode#constructIndex(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask)
//	 */
//	@Override
//	public ProjectionIndexer constructIndex(TupleMask mask) {
//		if (Options.employTrivialIndexers) {
//			if (nullMask.equals(mask)) return getNullIndexer();
//			if (identityMask.equals(mask)) return getIdentityIndexer();
//			if (sourceKnown.equals(mask)) return getSourceIndexer();
//			if (targetKnown.equals(mask)) return getTargetIndexer();
//		}
//		return super.constructIndex(mask);
//	}
//	
//	/**
//	 * @return the nullIndexer
//	 */
//	public NullIndexer getNullIndexer() {
//		if (nullIndexer == null) {
//			nullIndexer= new NullIndexer(reteContainer, 2, this, this) {				
//				@Override
//				protected Collection<Tuple> getTuples() {
//					return tuples();
//				}
//				
//				/* (non-Javadoc)
//				 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.NullIndexer#isEmpty()
//				 */
//				@Override
//				protected boolean isEmpty() {
//					final Set<? extends Object> values = baseIndex.getValuesOfFeature(feature);
//					return values == null || values.isEmpty();
//				}
//				
//				/* (non-Javadoc)
//				 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.NullIndexer#isSingleElement()
//				 */
//				@Override
//				protected boolean isSingleElement() {
//					final Set<Setting> settings = baseIndex.getOccurrencesOfFeature(feature);
//					return settings != null && settings.size()==1;
//				}
//			};
//		}
//		return nullIndexer;
//	}
//	
//	/**
//	 * @return the identityIndexer
//	 */
//	public IdentityIndexer getIdentityIndexer() {
//		if (identityIndexer == null) {
//			identityIndexer = new IdentityIndexer(reteContainer, 2, this, this) {			
//				@Override
//				protected Collection<Tuple> getTuples() {
//					return tuples();
//				}
//				@Override
//				protected boolean contains(Tuple signature) {
//					return signature.getSize() == 2 && (baseIndex.findByFeatureValue(signature.get(1), feature).contains(signature.get(0)));
//				}
//			};
//		}
//		return identityIndexer;
//	}
//
//	/**
//	 * @return the sourceIndexer
//	 */
//	public ProjectionIndexer getSourceIndexer() {
//		if (sourceIndexer == null) {
//			sourceIndexer = new SpecializedProjectionIndexer(reteContainer, sourceKnown, this, this) {
//				
//				@Override
//				public Iterator<Tuple> iterator() {
//					return tuples().iterator();
//				}
//				
//				@Override
//				public Collection<Tuple> get(Tuple signature) {
//					if (signature.getSize() == 1) {
//						final Object object = signature.get(0);
//						if (object instanceof EObject) {
//							try {
//								final EObject source = (EObject) object;
//								if (feature.isMany()) {
//									final Collection<? extends Object> values = (Collection<?>) source.eGet(feature);
//									if (!values.isEmpty()) {
//										Collection<Tuple> result = new HashSet<Tuple>();
//										for (Object value : values) {
//											result.add(makeTuple(source, value));
//										}
//									}
//								} else {
//									final Object value = source.eGet(feature);
//									if (value!=null) 
//										return Collections.singleton(makeTuple(source, value));
//								}
//							} catch (Exception ex) {
//								engine.getLogger().logError(					
//										"EMF-IncQuery encountered an error in processing the EMF model. ",
//									ex); 
//								return null;
//							}
//							
//						}
//					}
//					return null;
//				}
//				
//				@Override
//				public Collection<Tuple> getSignatures() {
//					final Collection<EObject> holders = baseIndex.getHoldersOfFeature(feature);
//					if (holders == null || holders.size() == 0) return null;
//					Collection<Tuple> result = new HashSet<Tuple>();
//					for (EObject holder : holders) {
//						result.add(makeTupleSingle(holder));
//					}
//					return result;
//				}
//			};
//		}
//		return sourceIndexer;
//	}
//
//	/**
//	 * @return the targetIndexer
//	 */
//	public ProjectionIndexer getTargetIndexer() {
//		if (targetIndexer == null) {
//			targetIndexer = new SpecializedProjectionIndexer(reteContainer, targetKnown, this, this) {
//				
//				@Override
//				public Iterator<Tuple> iterator() {
//					return tuples().iterator();
//				}
//				
//				@Override
//				public Collection<Tuple> get(Tuple signature) {
//					if (signature.getSize() == 1) {
//						final Object value = signature.get(0);
//						
//						Collection<EObject> holders = baseIndex.findByFeatureValue(value, feature);
//						if (holders == null || holders.size() == 0) return null;
//						
//						Collection<Tuple> result = new HashSet<Tuple>();				
//						for (EObject source : holders) {
//							result.add(makeTuple(source, value));
//						}
//						return result;
//
//					}
//					return null;
//				}
//				
//				@Override
//				public Collection<Tuple> getSignatures() {
//					final Collection<? extends Object> values = baseIndex.getValuesOfFeature(feature);
//					if (values == null || values.size() == 0) return null;
//					Collection<Tuple> result = new HashSet<Tuple>();
//					for (Object value : values) {
//						result.add(makeTupleSingle(value));
//					}
//					return result;
//				}
//			};
//		}
//		return targetIndexer;
//	}
//
//	
//}
