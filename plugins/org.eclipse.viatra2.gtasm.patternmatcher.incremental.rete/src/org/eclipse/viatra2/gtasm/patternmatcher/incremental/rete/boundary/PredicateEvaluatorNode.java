/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.IdentityIndexer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.NullIndexer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.index.ProjectionIndexer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.ReteEngine;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Direction;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.ReteContainer;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote.Address;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.single.SingleInputNode;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMemory;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.util.Options;


/**
 * @author Gabor Bergmann
 * 
 *         Permits the traversal of update notifications if a given function
 *         (with variables mapped to given positions in the Tuple) equals a
 *         right-hand-side (also mapped to a given position). If right-hand-side
 *         is omitted, the function is the predicate itself and should evaluate to true.
 * 
 *         The predicate is reevaluated on the Tuple each time an element affected by
 *         the term experiences a move, name or value change. Furthermore, it is
 *         also reevaluated if any ASMfunctions called at the previous
 *         evaluation are changed at the positions that were used.
 * 
 *         Uses unwrapped tuples. In distributed environments, AbstractPredicateEvaluatorNodes 
 *         should always be built on the head container, because they need access to Viatra.
 * 
 */

public class PredicateEvaluatorNode extends SingleInputNode {

	protected ReteEngine<?> engine;
	protected ReteBoundary<?> boundary;
	protected Integer rhsIndex;
	protected int[] affectedIndices;
	protected Set<Tuple> outgoing;
	protected NullIndexer nullIndexer;
	protected IdentityIndexer identityIndexer;
	protected Map<Object, Collection<Tuple>> elementOccurences;
	protected Map<Tuple, Set<Tuple>> invoker2traces;
	protected Map<Tuple, Set<Tuple>> trace2invokers;
	protected Address<ASMFunctionTraceNotifierNode> asmFunctionTraceNotifier;
	protected Address<ElementChangeNotifierNode> elementChangeNotifier;
	protected AbstractEvaluator evaluator;

	private final int tupleWidth;
	private final TupleMask nullMask;
	private final TupleMask identityMask;
	
	/**
	 * @param rhsIndex
	 *            the index of the element in the Tuple that should equals the
	 *            result of the evaluation; if null, the right-hand-side will be the
	 *            Boolean true.
	 * @param variableIndices
	 *            maps variable names to values.
	 */
	public PredicateEvaluatorNode(ReteEngine<?> engine, ReteContainer container,
			Integer rhsIndex, int[] affectedIndices, int tupleWidth,
			AbstractEvaluator evaluator) {
		super(container);
		this.engine = engine;
		this.boundary = engine.getBoundary();
		this.rhsIndex = rhsIndex;
		this.affectedIndices = affectedIndices;
		this.tupleWidth = tupleWidth;
		this.evaluator = evaluator;
		
		this.elementOccurences = new HashMap<Object, Collection<Tuple>>();
		this.outgoing = new HashSet<Tuple>();
		this.invoker2traces = new HashMap<Tuple, Set<Tuple>>();
		this.trace2invokers = new HashMap<Tuple, Set<Tuple>>();
		// extractASMFunctions();
		this.asmFunctionTraceNotifier = Address
				.of(new ASMFunctionTraceNotifierNode(reteContainer));
		this.elementChangeNotifier = Address.of(new ElementChangeNotifierNode(
				reteContainer));

		nullMask = TupleMask.linear(0, tupleWidth);
		identityMask = TupleMask.identity(tupleWidth);
//		if (Options.employTrivialIndexers) {
//			nullIndexer = new NullIndexer(reteContainer, tupleWidth, outgoing, this, this);
//			reteContainer.getLibrary().registerSpecializedProjectionIndexer(this, nullIndexer);
//			identityIndexer = new IdentityIndexer(reteContainer, tupleWidth, outgoing, this, this);
//			reteContainer.getLibrary().registerSpecializedProjectionIndexer(this, identityIndexer);
//		}

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
	
	
	public void pullInto(Collection<Tuple> collector) {
		for (Tuple ps : outgoing)
			collector.add(boundary.wrapTuple(ps));
	
	}

	public void update(Direction direction, Tuple wrappers) {
		Tuple updateElement = boundary.unwrapTuple(wrappers);
		updateOccurences(direction, updateElement);
		if (direction == Direction.REVOKE) {
			if (outgoing.remove(updateElement)) {
				clearTraces(updateElement);
				propagateUpdate(Direction.REVOKE, wrappers);
			}
		} else /* (direction == Direction.INSERT) */
		{
			check(updateElement);
		}
	}

	protected void notifyASMFunctionValueChanged(Tuple trace) {
		// System.out.println("TEN notified");
		Set<Tuple> invokers = trace2invokers.get(trace);
		if (invokers != null) {
			LinkedList<Tuple> copy = new LinkedList<Tuple>(invokers);
			for (Tuple ps : copy)
				check(ps);
		}
	}

	protected void notifyElementChange(Object element) {
		for (Tuple ps : elementOccurences.get(element))
			check(ps);
	}

	protected void updateOccurences(Direction direction, Tuple ps) {
		for (Integer i : affectedIndices) {
			Object element = ps.get(i);
			//if (element instanceof IModelElement) {
				updateElementOccurence(direction, ps, element);
			//}
		}
	}

	protected void updateElementOccurence(Direction direction, Tuple ps,
			Object element) {
				Collection<Tuple> occurences;
				if (direction == Direction.INSERT) {
					occurences = elementOccurences.get(element);
					boolean change = occurences == null;
					if (change) {
						occurences = new TupleMemory();
						elementOccurences.put(element, occurences);
						engine.getManipulationListener().registerSensitiveTerm(element, this);
					}
					occurences.add(ps);
				} else // REVOKE
				{
					occurences = elementOccurences.get(element);
					occurences.remove(ps);
					boolean change = occurences.isEmpty();
					if (change) {
						elementOccurences.remove(element);
						engine.getManipulationListener().unregisterSensitiveTerm(element,
								this);
					}
				}
			}

	protected void check(Tuple ps) {
		boolean result = evaluateExpression(ps);
		if (result) /* expression evaluates to true */
		{
			if (outgoing.add(ps))
				propagateUpdate(Direction.INSERT, boundary.wrapTuple(ps));
		} else /* expression evaluates to false */
		{
			if (outgoing.remove(ps))
				propagateUpdate(Direction.REVOKE, boundary.wrapTuple(ps));
		}
	}

	protected boolean evaluateExpression(Tuple ps) {
		Object termResult = evaluateTerm(ps);
		Object rightHandSide = (rhsIndex == null) ? true : ps.get(rhsIndex);
	
		return (termResult == null) ? rightHandSide == null : termResult
				.equals(rightHandSide);
	}
	
	public Object evaluateTerm(Tuple ps) {
		// clearing ASMfunction traces
		clearTraces(ps);

		// actual evaluation
		Object result = null;
		try {
			result = evaluator.evaluate(ps);
		} catch (Throwable e) {
			engine.getContext().logWarning( 
					"The incremental pattern matcher encountered an error during check() evaluation over variables "
					+ prettyPrintTuple(ps) 
					+ " Error message: " + e.getMessage()
					+ " (Developer note: " + e.getClass().getSimpleName() + " in " + this + ")"
					, e);
//			engine.logEvaluatorException(e);
			
			result = Boolean.FALSE;
		}

		// saving ASMFunction traces
		saveTraces(ps, evaluator.getTraces());

		return result;
	}
	
	protected String prettyPrintTuple(Tuple ps) {
		return ps.toString();
	}
	
	protected void clearTraces(Tuple invoker) {
		Set<Tuple> traces = invoker2traces.get(invoker);
		if (traces != null) {
			invoker2traces.remove(invoker);
			for (Tuple trace : traces) {
				Set<Tuple> invokers = trace2invokers.get(trace);
				invokers.remove(invoker);
				if (invokers.isEmpty()) {
					trace2invokers.remove(trace);
					engine.geTraceListener().unregisterSensitiveTrace(trace,
							this);
				}
			}
		}
	}

	protected void saveTraces(Tuple invoker, Set<Tuple> traces) {
		if (traces != null && !traces.isEmpty()) {
			invoker2traces.put(invoker, traces);
	
			for (Tuple trace : traces) {
				Set<Tuple> invokers = trace2invokers.get(trace);
				if (invokers == null) {
					invokers = new HashSet<Tuple>();
					trace2invokers.put(trace, invokers);
					engine.geTraceListener().registerSensitiveTrace(trace,
							this);
				}
				invokers.add(invoker);
			}
		}
	}

	@Override
	protected void propagateUpdate(Direction direction, Tuple updateElement) {
		super.propagateUpdate(direction, updateElement);
		if (identityIndexer != null) identityIndexer.propagate(direction, updateElement);
		if (nullIndexer != null) {
			boolean radical = (direction==Direction.REVOKE && outgoing.isEmpty()) 
				|| (direction==Direction.INSERT && outgoing.size()==1);
			nullIndexer.propagate(direction, updateElement, radical);
		}
	}

	/**
	 * @return the asmFunctionTraceNotifier
	 */
	public Address<? extends Receiver> getAsmFunctionTraceNotifier() {
		return asmFunctionTraceNotifier;
	}

	/**
	 * @return the elementChangeNotifier
	 */
	public Address<? extends Receiver> getElementChangeNotifier() {
		return elementChangeNotifier;
	}

	/**
	 * @return the engine
	 */
	public ReteEngine<?> getEngine() {
		return engine;
	}
	
	public NullIndexer getNullIndexer() {
		if (nullIndexer == null) nullIndexer = new NullIndexer(reteContainer, tupleWidth, outgoing, this, this);
		return nullIndexer;
	}

	public IdentityIndexer getIdentityIndexer() {
		if (identityIndexer == null) identityIndexer = new IdentityIndexer(reteContainer, tupleWidth, outgoing, this, this);
		return identityIndexer;
	}
	
	class ASMFunctionTraceNotifierNode extends SingleInputNode {
		public ASMFunctionTraceNotifierNode(ReteContainer reteContainer) {
			super(reteContainer);
		}

		public void pullInto(Collection<Tuple> collector) {
		}

		public void update(Direction direction, Tuple updateElement) {
			notifyASMFunctionValueChanged(updateElement);
		}
	}

	class ElementChangeNotifierNode extends SingleInputNode {
		public ElementChangeNotifierNode(ReteContainer reteContainer) {
			super(reteContainer);
		}

		public void pullInto(Collection<Tuple> collector) {
		}

		public void update(Direction direction, Tuple updateElement) {
			notifyElementChange(updateElement.get(0));
		}
	}

}