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

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.viatra2.emf.incquery.base.core.NavigationHelperImpl;
import org.eclipse.viatra2.emf.incquery.base.core.NavigationHelperType;
import org.eclipse.viatra2.emf.incquery.base.core.ParameterizedNavigationHelperImpl;
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
	 * The method creates a NavigationHelper instance for the given Notifier instance. 
	 * This type of NavigationHelper will process all the notifications about EStructuralFeatures and EClasses.
	 *  
	 * @param notifier the Notifier instance
	 * @return the NavigationHelper instance
	 * @throws IncQueryBaseException 
	 */
	public NavigationHelper createNavigationHelper(Notifier notifier) throws IncQueryBaseException {
		return new NavigationHelperImpl(notifier, NavigationHelperType.ALL);
	}
	
	/**
	 * The method creates a ParameterizedNavigationHelper instance for the given Notifier instance.
	 * After instantiation one can register and unregister set of EStructuralFeatures and EClasses 
	 * whose notifications will be processed by the ParameterizedNavigationHelper instance.
	 * 
	 * @param notifier the Notifier instance
	 * @return the ParameterizedNavigationHelper instance
	 * @throws IncQueryBaseException 
	 */
	public ParameterizedNavigationHelper createManualNavigationHelper(Notifier notifier) throws IncQueryBaseException {
		return new ParameterizedNavigationHelperImpl(notifier);
	}
	
	/**
	 * The method creates a TransitiveClosureHelper instance for the given Notifier instance.
	 * One must set the set of EReferences whose notifications will be observed by the Util.
	 * The set can contain multiple elements; this way one can query forward and backward reachability informations along heterogenous paths.
	 * 
	 * @param notifier the Notifier instance
	 * @param referencesToObserv the setof references to observ
	 * @return the TransitiveClosureHelper instance
	 * @throws IncQueryBaseException
	 */
	public TransitiveClosureHelper createTransitiveClosureHelper(Notifier notifier, Set<EReference> referencesToObserv) throws IncQueryBaseException {
		return new TransitiveClosureHelperImpl(notifier, referencesToObserv);
	}
	
}
