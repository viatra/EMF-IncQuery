/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.core.codegen.internal;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra2.emf.incquery.codegen.patternmatcher.EMFCodegenBuildable;
import org.eclipse.viatra2.emf.incquery.codegen.patternmatcher.EMFPatternMatcherBuilderContext;
import org.eclipse.viatra2.emf.incquery.core.codegen.CodeGenerationException;
import org.eclipse.viatra2.emf.incquery.core.codegen.util.CodegenSupport;
import org.eclipse.viatra2.emf.incquery.core.project.IncQueryNature;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.EcoreModel;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncQueryGenmodel;
import org.eclipse.viatra2.framework.IFramework;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.adapters.CodegenRecorderGTASMBuildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.CodegenRecordingCoordinator;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherBuilderContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.simple.SimpleReteBuilder;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.definitions.Machine;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPattern;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPatternBody;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTRule;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.PatternContainer;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.PatternVariable;

/**
 * @author Bergmann Gábor
 *
 */
public class PatternBuilderSourceGenerator {
	static final String patternBuilderTemplateFile = "PatternBuilder.java.template";
	static final String classNameTemplate = "PatternBuilderFor#pattern-name#";
	//static final String patternBuildersPackage = "patternBuilders";

	String patternBuilderTemplate;
	private final IProject project;
	private final IncQueryGenmodel iqGen;

	/**
	 * @param project
	 * @param iqGen
	 * @throws CodeGenerationException if unable to load template files
	 */
	public PatternBuilderSourceGenerator(IProject project, IncQueryGenmodel iqGen) throws CodeGenerationException {
		super();
		this.project = project;
		this.iqGen = iqGen;
		this.patternBuilderTemplate = CodegenSupport.loadTemplate(patternBuilderTemplateFile);
	}

	/**
	 * Emits generated source code for pattern builder classes for the given patterns.
	 *
	 *
	 * @pre in the current version the parent folder of packageNameRoot must already exist
	 *
	 * @param framework
	 * @param patterns a set of GTPatterns to generate the pattern builder classes for
	 * // @param pathRoot a Java package path to contain packages generated from the pattern container
	 * // @param packageNameRoot the Java package name to contain packages generated from the pattern container
	 * @param monitor optional progress monitor for the file operations
	 * @return mapping of patterns to the qualified name of the generated classes (hint: reuse in plugin.xml)
	 * @throws CodeGenerationException
	 */
	public Map<GTPattern, String> generatePatternBuilders(
			IFramework framework,
			Set<GTPattern> patterns,
			// IPath pathRoot,
			// String packageNameRoot,
			IProgressMonitor monitor) throws CodeGenerationException
	{
		Map<GTPattern, String> results = new HashMap<GTPattern, String>();

		IPath pathRoot = project.getFolder(IncQueryNature.GENERATED_BUILDERS_DIR).getFullPath();
		String packageNameRoot = IncQueryNature.GENERATED_BUILDERS_PACKAGEROOT;


		IPatternMatcherBuilderContext<GTPattern> context = new EMFPatternMatcherBuilderContext<GTPattern>(framework, collectEPackages());
		CodegenRecordingCoordinator<GTPattern> coordinator = new CodegenRecordingCoordinator<GTPattern>(context,
				"Stub<Address<? extends Supplier>>", "Address<? extends Receiver>", "ReteContainerBuildable<String>");
		CodegenRecorderGTASMBuildable buildable = new EMFCodegenBuildable(coordinator, null, "\t\t", "buildable", "");
		IRetePatternBuilder<GTPattern, String, String> builder = new SimpleReteBuilder<String, String>(buildable, context);

		for (GTPattern gtPattern : patterns) {
			try {
				String collector = builder.construct(gtPattern);
				coordinator.emitPatternBuilderLine(gtPattern, "\t\t", "return " + collector + ";");
			} catch (RetePatternBuildException e) {
				throw new CodeGenerationException(
						"Error generating pattern matcher code for pattern "
						+ gtPattern.getFqn() + ": " + e.getMessage(), e);
			}
			String generatedClass = generatePatternBuilder(gtPattern, pathRoot, packageNameRoot, coordinator, monitor);
			results.put(gtPattern, generatedClass);
		}

		return results;
	}

	/**
	 * @return (nsUri, EPackage) map of each EPackage referenced by the .incquery generator model
	 */
	private Map<String, EPackage> collectEPackages() {
		Map<String, EPackage> result = new HashMap<String, EPackage>();
		if (iqGen != null) {
			for (EcoreModel ecoreReference : iqGen.getEcoreModel()) {
				GenModel emfGenModel = ecoreReference.getModels();
				if (emfGenModel != null) {
					for (GenPackage genPackage : emfGenModel.getGenPackages()) {
						EPackage ecorePackage = genPackage.getEcorePackage();
						if (ecorePackage != null) {
							collectEPackage(ecorePackage, result);
						}
					}
				}
			}

		}
		return result;
	}

	/**
	 * Recorsively collects an EPackage tree into an (nsUri, EPackage) map.
	 * @param accumulator (nsUri, EPackage) map of each EPackages
	 * @param ecorePackage EPackage to add, along with contained packages
	 */
	private void collectEPackage(EPackage ecorePackage, Map<String, EPackage> result) {
		result.put(ecorePackage.getNsURI(), ecorePackage);
		for (EPackage subPackage : ecorePackage.getESubpackages()) {
			collectEPackage(subPackage, result);
		}
	}

	/**
	 * Emits a pattern builder class for a pattern.
	 *
	 * @pre pattern was already built using the CodegenRecorder infrastructure, using the given coordinator
	 * @pre in the current version the parent folder of packageNameRoot must already exist
	 *
	 * @param pattern a GTPattern to generate a pattern builder file for
	 * @param pathRoot a Java package path to contain packages generated from the pattern container
	 * @param packageNameRoot the Java package name to contain packages generated from the pattern container
	 * @param coordinator the codegen build coordinator that saved the build instructions
	 * @param monitor optional progress monitor for the file operations
	 * @return qualified name of the generated class (hint: reuse in plugin.xml)
	 * @throws CodeGenerationException
	 */
	public String generatePatternBuilder(
			GTPattern pattern,
			IPath pathRoot,
			String packageNameRoot,
			CodegenRecordingCoordinator<GTPattern> coordinator,
			IProgressMonitor monitor) throws CodeGenerationException
	{
		Map<String, String> substitutions = new HashMap<String, String>();
		substitutions.put("pattern-name", pattern.getName());
		substitutions.put("pattern-fqn", pattern.getFqn());
		substitutions.put("module-file", getMachineOfPattern(pattern).getModule().getFileName());

		String className = CodegenSupport.processTemplate(classNameTemplate, substitutions);
		substitutions.put("java-class", className);


		CodegenSupport.PackageLocationFinder packageLocation =
			new CodegenSupport.PackageLocationFinder(pattern, pathRoot, packageNameRoot, monitor);

		substitutions.put("java-package", packageLocation.getJavaPackageName());
		substitutions.put("build-block", coordinator.getFinishedBuilderCode(pattern));
		substitutions.put("posmap-block", genPosMapper(pattern));
		String generatedCode = CodegenSupport.processTemplate(patternBuilderTemplate, substitutions);

		IFile file = packageLocation.getFolder().getFile(className + ".java");
//		PipedInputStream in = new PipedInputStream();
//		final PrintStream out = new PrintStream(new PipedOutputStream(in));
//		new Thread(){ @Override
//		public void run() {
//			out.println("<?xml version='1.0' encoding='UTF-8'?>");
//			out.close();
//		}}.start();

		try {
			CodegenSupport.printStringToFile(monitor, generatedCode, file);
		} catch (UnsupportedEncodingException e) {
			throw new CodeGenerationException("Error writing generated pattern matcher builder to file.", e);
		} catch (CoreException e) {
			throw new CodeGenerationException(
					"Error writing source code file for generated pattern matcher builder of "
					+ pattern.getFqn(), e);
		}

		return packageLocation.getJavaPackageName() + '.' + className;
	}

	/**
	 * @param pattern
	 * @return
	 */
	private Machine getMachineOfPattern(GTPattern pattern) {
		Machine namespace = pattern.getNamespace();
		if (namespace != null) return namespace;
		PatternContainer container = pattern.getContainer();
		if (container instanceof GTRule) return ((GTRule)container).getNamespace();
		else return getMachineOfPattern(((GTPatternBody)container).getHeader());
	}

	static String genPosMapper(GTPattern pattern) {
		StringBuilder sb = new StringBuilder();
		int pos = 0;
		for(PatternVariable var: pattern.getSymParameters()) {
			sb.append("\t\t\tposMapping.put(\"" + var.getName() + "\", " + pos++ + ");\n");
		}
		return sb.toString();
	}

}
