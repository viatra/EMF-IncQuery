package org.eclipse.viatra2.patternlanguage.core;

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
			                        return PatternBodyVariableCollector.class;
			                    }
			                });
			            }
			        }.createInjectorAndDoEMFRegistration();
			    }
}
