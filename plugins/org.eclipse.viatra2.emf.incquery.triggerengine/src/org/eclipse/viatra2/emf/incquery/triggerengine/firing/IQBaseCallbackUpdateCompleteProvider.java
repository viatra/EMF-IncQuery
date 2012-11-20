/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.triggerengine.firing;

import org.eclipse.viatra2.emf.incquery.base.api.NavigationHelper;

/**
 * @author Abel Hegedus
 *
 */
public class IQBaseCallbackUpdateCompleteProvider extends UpdateCompleteProvider {

    private Runnable matchsetProcessor;
    private NavigationHelper helper;

    /**
     * 
     */
    public IQBaseCallbackUpdateCompleteProvider(NavigationHelper helper) {
        super();
        this.matchsetProcessor = new MatchSetProcessor();

        this.helper = helper;

        if (helper != null) {
            helper.getAfterUpdateCallbacks().add(matchsetProcessor);
        }
    }

    private class MatchSetProcessor implements Runnable {
        @Override
        public void run() {
            updateCompleted();
        }
    }

    @Override
    public void dispose() {
        if (helper != null) {
            helper.getAfterUpdateCallbacks().remove(matchsetProcessor);
        }
        super.dispose();
    }

}
