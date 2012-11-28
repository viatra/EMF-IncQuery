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

package org.eclipse.viatra2.emf.incquery.testing.ui;

import org.eclipse.incquery.patternlanguage.ui.EMFPatternLanguageExecutableExtensionFactory;
import org.osgi.framework.Bundle;

public class IncQueryLanguageExecutableExtensionFactory extends
		EMFPatternLanguageExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return IncQueryTestingUIPlugin.getDefault().getBundle();
	}

}
