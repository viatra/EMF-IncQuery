/*******************************************************************************
 * Copyright (c) 2010-2012, Csicsely Attila, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Csicsely Attila - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.tooling.generator.mavenbuilder;

import org.eclipse.xtext.builder.IXtextBuilderParticipant;
import org.eclipse.xtext.builder.impl.RegistryBuilderParticipant;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Csicsely Attila
 *
 */
@Singleton
public class MavenBuilderRegistryParticipant extends RegistryBuilderParticipant {

	@Inject
	IXtextBuilderParticipant participant;
	
	/* (non-Javadoc)
	 * @see org.eclipse.xtext.builder.impl.RegistryBuilderParticipant#getParticipants()
	 */
	@Override
	public ImmutableList<IXtextBuilderParticipant> getParticipants() {
		return ImmutableList.of(participant);
	}

}
