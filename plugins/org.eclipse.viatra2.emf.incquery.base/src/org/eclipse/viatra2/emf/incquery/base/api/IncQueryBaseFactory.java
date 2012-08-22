/*******************************************************************************
 * Copyright (c) 2010-2012, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.base.api;




import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.viatra2.emf.incquery.base.core.NavigationHelperImpl;
import org.eclipse.viatra2.emf.incquery.base.core.TransitiveClosureHelperImpl;
import org.eclipse.viatra2.emf.incquery.base.exception.IncQueryBaseException;

/**
 * Factory class for the utils in the library:
 * - NavigationHelper (automatic and manual)
 * - TransitiveClosureUtil
 * 
 * @author Tamas Szabo
 *
 */
public class IncQueryBaseFactory {
	
	private static IncQueryBaseFactory instance;
	
	/**
	 * Get the singleton instance of IncQueryBaseFactory.
	 * 
	 * @return the singleton instance
	 */
	public synchronized static IncQueryBaseFactory getInstance() {
		if (instance == null) {
			instance = new IncQueryBaseFactory();
		}
		
		return instance;
	}
	
	protected IncQueryBaseFactory() {
		super();
	}
		
	/**
	 * The method creates a {@link NavigationHelper} index for the given EMF model root. <p>
	 * A NavigationHelper in wildcard mode will process and index all EStructuralFeatures, EClasses and EDatatypes. 
	 * If wildcard mode is off, the client will have to manually register the interesting aspects of the model.
	 * @see NavigationHelper
	 *  
	 * @param emfRoot the root of the EMF tree to be indexed. Recommended: Resource or ResourceSet.
	 * @param wildcardMode true if all aspects of the EMF model should be indexed automatically, false if manual registration of interesting aspects is desirable
	 * @param logger the log output where errors will be logged if encountered during the operation of the NavigationHelper; if null, the default logger for {@link NavigationHelper} is used.
	 * @return the NavigationHelper instance
	 * @throws IncQueryBaseException 
	 */
	public NavigationHelper createNavigationHelper(Notifier emfRoot, boolean wildcardMode, Logger logger) throws IncQueryBaseException {
		if (logger == null) logger = Logger.getLogger(NavigationHelper.class);
		return new NavigationHelperImpl(emfRoot, wildcardMode, logger);
	}
	
	
	/**
	 * The method creates a TransitiveClosureHelper instance for the given EMF model root.
	 * 
	 * <p> One must specify the set of EReferences that will be considered as edged. The set can contain multiple elements; this way one can query forward and backward reachability information along heterogenous paths.
	 * 
	 * @param emfRoot the root of the EMF tree to be processed. Recommended: Resource or ResourceSet.
	 * @param referencesToObserve the set of references to observe
	 * @return the TransitiveClosureHelper instance
	 * @throws IncQueryBaseException
	 */
	public TransitiveClosureHelper createTransitiveClosureHelper(Notifier emfRoot, Set<EReference> referencesToObserve) throws IncQueryBaseException {
		return new TransitiveClosureHelperImpl(emfRoot, referencesToObserve);
	}
	
}
