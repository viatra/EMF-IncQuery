/*******************************************************************************
 * Copyright (c) 2010-2012, szabo.tamas.ext, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Szabo Tamas - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * An empty implementation of the {@link IPartListener} interface.
 */
public class BasePartListener implements IPartListener {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
    public void partActivated(IWorkbenchPart part) {
        // empty method
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
        // empty method
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
    public void partClosed(IWorkbenchPart part) {
        // empty method
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
    public void partDeactivated(IWorkbenchPart part) {
        // empty method
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     */
    @Override
    public void partOpened(IWorkbenchPart part) {
        // empty method
    }

}
