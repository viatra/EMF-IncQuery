/*******************************************************************************
 * Copyright (c) 2004-2012 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.runtime.internal.boundary;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.base.api.InstanceListener;
import org.eclipse.viatra2.emf.incquery.base.api.NavigationHelper;
import org.eclipse.viatra2.emf.incquery.base.exception.IncQueryBaseException;
import org.eclipse.viatra2.emf.incquery.runtime.api.IncQueryEngine;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.Disconnectable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.ReteBoundary;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.IdentityIndexer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.NullIndexer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.ProjectionIndexer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.StandardNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.FlatTuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.util.Options;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

/**
 * Input node relying on the NavigationUtil base index.
 * Represents the set of (direct or indirect) instances of a given {@link EClass}.
 * 
 * @author Bergmann Gábor
 *
 */
public class EClassUnaryInputNode extends StandardNode implements Disconnectable {
	
	private EClass clazz;
	private IncQueryEngine engine;
	private NavigationHelper baseIndex;
	private ReteEngine<Pattern> reteEngine;
	private ReteBoundary<Pattern> boundary;
	
	static final TupleMask nullMask = TupleMask.linear(0, 1); 
	static final TupleMask identityMask = TupleMask.identity(1); 
	
	private NullIndexer nullIndexer;
	private IdentityIndexer identityIndexer;
	
	private InstanceListener listener = new InstanceListener() {		
		@Override
		public void instanceInserted(EClass clazz, EObject instance) {
			final Tuple tuple = makeTuple(instance);
			propagate(Direction.INSERT, tuple);
		}
		
		@Override
		public void instanceDeleted(EClass clazz, EObject instance) {
			final Tuple tuple = makeTuple(instance);
			propagate(Direction.REVOKE, tuple);
		}
	};
	
	public EClassUnaryInputNode(IncQueryEngine engine, ReteContainer reteContainer, EClass clazz) throws IncQueryBaseException {
		super(reteContainer);
		this.engine = engine;
		this.baseIndex = engine.getBaseIndex();
		this.reteEngine = engine.getReteEngine();
		this.boundary = reteEngine.getBoundary();
		this.clazz = clazz;
		setTag(clazz.getName());
						
		baseIndex.registerInstanceListener(Collections.singleton(clazz), listener);
		reteEngine.addDisconnectable(this);
	}



	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier#pullInto(java.util.Collection)
	 */
	@Override
	public void pullInto(Collection<Tuple> collector) {
//		final Set<EObject> allInstances = baseIndex.getAllInstances(clazz);
//		for (EObject instance : allInstances) {
//			collector.add(makeTuple(instance));
//		}
		collector.addAll(tuples());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.Disconnectable#disconnect()
	 */
	@Override
	public void disconnect() {
		baseIndex.unregisterInstanceListener(listener);
	}

	protected Tuple makeTuple(EObject instance) {
		return new FlatTuple(boundary.wrapElement(instance));
	}
	
	protected void propagate(Direction direction, final Tuple tuple) {
		propagateUpdate(direction, tuple);
		if (identityIndexer != null) identityIndexer.propagate(direction, tuple);
		if (nullIndexer != null) nullIndexer.propagate(direction, tuple);
	}
	
	protected Collection<Tuple> tuples() {
		final Collection<Tuple> result = new HashSet<Tuple>();
		final Set<EObject> allInstances = baseIndex.getAllInstances(clazz);
		if (allInstances != null) for (EObject instance : allInstances) {
			result.add(makeTuple(instance));
		}
		return result;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.StandardNode#constructIndex(org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask)
	 */
	@Override
	public ProjectionIndexer constructIndex(TupleMask mask) {
		if (Options.employTrivialIndexers) {
			if (nullMask.equals(mask)) return getNullIndexer();
			if (identityMask.equals(mask)) return getIdentityIndexer();
		}
		return super.constructIndex(mask);
	}
	
	/**
	 * @return the nullIndexer
	 */
	public NullIndexer getNullIndexer() {
		if (nullIndexer == null) {
			nullIndexer= new NullIndexer(reteContainer, 1, this, this) {				
				@Override
				protected Collection<Tuple> getTuples() {
					return tuples();
				}
				
				/* (non-Javadoc)
				 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.NullIndexer#isEmpty()
				 */
				@Override
				protected boolean isEmpty() {
					final Set<EObject> allInstances = baseIndex.getAllInstances(clazz);
					return allInstances == null || allInstances.isEmpty();
				}
				
				/* (non-Javadoc)
				 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.NullIndexer#isSingleElement()
				 */
				@Override
				protected boolean isSingleElement() {
					final Set<EObject> allInstances = baseIndex.getAllInstances(clazz);
					return allInstances != null && allInstances.size()==1;
				}
			};
		}
		return nullIndexer;
	}
	
	/**
	 * @return the identityIndexer
	 */
	public IdentityIndexer getIdentityIndexer() {
		if (identityIndexer == null) {
			identityIndexer = new IdentityIndexer(reteContainer, 1, this, this) {			
				@Override
				protected Collection<Tuple> getTuples() {
					return tuples();
				}
				@Override
				protected boolean contains(Tuple signature) {
					try {
						return signature.getSize() == 1 && clazz.isInstance(signature.get(0));
					} catch (Exception ex) {
						engine.getLogger().logError(					
								"EMF-IncQuery encountered an error in processing the EMF model. ",
							ex); 
						return false;
					}
				}
			};
		}
		return identityIndexer;
	}
	
}
