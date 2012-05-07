package org.eclipse.viatra2.emf.incquery.base.api;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra2.emf.incquery.base.itc.igraph.ITcDataSource;

/**
 * The class can be used to compute the transitive closure of a given emf model, 
 * where the nodes will be the objects in the model and the edges will be represented by the references between them.
 * One must provide the set of references that the helper should treat as edges when creating an instance with the factory: 
 * only the notifications about these references will be handled.
 * 
 * @author Tamas Szabo
 *
 */
public interface TransitiveClosureHelper extends ITcDataSource<EObject> {

}
