package org.eclipse.viatra2.emf.incquery.base.itc.igraph;

/**
 * Interface ITcObserver is used to observ the changes in a transitive closure relation; tuple insertion/deleteion.
 * 
 * @author Szabó Tamás
 *
 */
public interface ITcObserver<V> {
	
	/**
	 * Used to notify when a tuple is inserted into the transitive closure relation. 
	 * 
	 * @param source the source of the tuple
	 * @param target the target of the tuple
	 */
	public void tupleInserted(V source, V target);
	
	/**
	 * Used to notify when a tuple is deleted from the transitive closure relation.
	 * 
	 * @param source the source of the tuple
	 * @param target the target of the tuple
	 */
	public void tupleDeleted(V source, V target);
}
