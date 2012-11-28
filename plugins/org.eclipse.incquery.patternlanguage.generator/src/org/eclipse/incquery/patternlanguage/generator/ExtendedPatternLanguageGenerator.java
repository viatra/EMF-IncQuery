/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.patternlanguage.generator;

import org.eclipse.xtext.XtextRuntimeModule;
import org.eclipse.xtext.XtextStandaloneSetup;
import org.eclipse.xtext.generator.Generator;
import org.eclipse.xtext.xtext.ecoreInference.IXtext2EcorePostProcessor;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ExtendedPatternLanguageGenerator extends Generator {

		    public ExtendedPatternLanguageGenerator() {
			        new XtextStandaloneSetup() {
			            @Override
			            public Injector createInjector() {
			                return Guice.createInjector(new XtextRuntimeModule() {
			                    @Override
			                    public Class<? extends IXtext2EcorePostProcessor> bindIXtext2EcorePostProcessor() {
			                        return BasePatternLanguageGeneratorPostProcessor.class;
			                    }
			                });
			            }
			        }.createInjectorAndDoEMFRegistration();
			    }
}
