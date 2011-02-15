/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.compiled.emf.patternmatcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.viatra2.buffers.BufferStore;
import org.eclipse.viatra2.framework.IFramework;
import org.eclipse.viatra2.frameworkgui.actions.AbstractFrameworkGUIAction;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.gt.GTPattern;
import org.eclipse.viatra2.logger.Logger;


// TODO discard class
public class ModelspaceCodegenGUIAction extends AbstractFrameworkGUIAction {
	public static final String ID = "org.eclipse.viatra2.compiled.emf.patternmatcher.ModelspaceCodegenGUIAction";
	public ModelspaceCodegenGUIAction() {
		this.setText("Generate EMF code");
		this.setEnabled(true);
		setId(ID);

	}
	
	@Override
	public void run() {
		super.run();
		
		super.refreshSelection();
		// for (String frameworkID: FrameworkManager.getInstance().getAllFrameWorks()) {
		// genCode(FrameworkManager.getInstance().getFramework(frameworkID), System.out);
		//genCode(iViatraFramework,null);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		//System.out.print(code);
		
		
		//final CodeOutputPlugin codeout = iViatraFramework.getCodeOutput();
		try {
			genCode(iViatraFramework, new PrintStream(baos));
			String code = baos.toString();

			Writer w = BufferStore.getBuffer(iViatraFramework.getTopmodel(), "core://GeneratedBuilder.java", false);
			iViatraFramework.getTopmodel().getTransactionManager().beginTransaction();
			try {
				w.write(code);
			} finally {
				w.close();
				iViatraFramework.getTopmodel().getTransactionManager().commitTransaction();
			}
		} catch (IOException ex) {
			final String msg = "Error during code generation: " + ex.getMessage();
			iViatraFramework.getLogger().message(Logger.FATAL, msg, ex);
			final Shell shell = Display.getDefault().getShells()[0];
			Display.getDefault().syncExec(new Runnable(){
				public void run() {
					MessageDialog.openInformation(shell, "VIATRA2 R3", msg + " (see the Error Log view for details)");							
					}	
				});
		} catch (java.lang.RuntimeException ex) {
			final String msg = "Error during code generation: " + ex.getMessage();
			iViatraFramework.getLogger().message(Logger.FATAL, msg, ex);
			final Shell shell = Display.getDefault().getShells()[0];
			Display.getDefault().syncExec(new Runnable(){
				public void run() {
					MessageDialog.openInformation(shell, "VIATRA2 R3", msg + " (see the Error Log view for details)");							
					}	
				});	
		} catch (URISyntaxException ex) {
			final String msg = "Error during code generation: " + ex.getMessage();
			iViatraFramework.getLogger().message(Logger.FATAL, msg, ex);
			final Shell shell = Display.getDefault().getShells()[0];
			Display.getDefault().syncExec(new Runnable(){
				public void run() {
					MessageDialog.openInformation(shell, "VIATRA2 R3", msg + " (see the Error Log view for details)");							
					}	
				});	
		}

	}
	
	void genCode(IFramework framework, PrintStream codeStream) {
//		IPatternMatcherBuilderContext<GTPattern> context = new EMFPatternMatcherBuilderContext<GTPattern>(framework);
//		//ReteEngine<GTPattern> engine = new ReteEngine<GTPattern>(context);
//		CodegenRecordingCoordinator<GTPattern> coordinator = new CodegenRecordingCoordinator<GTPattern>(context, 
//				"Stub<Address<? extends Supplier>>", "Address<? extends Receiver>", "ReteContainerBuildable<String>");
//		CodegenRecorderGTASMBuildable buildable = new EMFCodegenBuildable(coordinator, null, "\t\t", "buildable", "");
//		IRetePatternBuilder<GTPattern, String, String> builder = new SimpleReteBuilder<String, String>(buildable, context);
//		//engine.setBuilder(builder);
//
//		codeStream.println("package emf.transformation;");		
//		codeStream.println();		
//		codeStream.println("import java.util.HashMap;");		
//		codeStream.println();		
//		codeStream.println("import org.eclipse.emf.ecore.EClass;");	
//		codeStream.println("import org.eclipse.emf.ecore.EPackage;");	
//		codeStream.println("import org.eclipse.viatra2.compiled.emf.runtime.exception.ViatraCompiledRuntimeException;");	
//		codeStream.println("import org.eclipse.viatra2.compiled.emf.runtime.term.VPMTermEvaluator;");	
//		codeStream.println("import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.boundary.AbstractEvaluator;");	
//		codeStream.println("import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder;");	
//		codeStream.println("import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.ReteContainerBuildable;");	
//		codeStream.println("import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;");	
//		codeStream.println("import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.Stub;");	
//		codeStream.println("import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;");	
//		codeStream.println("import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;");	
//		codeStream.println("import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;");	
//		codeStream.println("import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote.Address;");	
//		codeStream.println("import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.FlatTuple;");	
//		codeStream.println("import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.Tuple;");	
//		codeStream.println("import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.tuple.TupleMask;");	
//		codeStream.println();		
//		codeStream.println("public class GeneratedBuilder implements IRetePatternBuilder<String, Address<? extends Supplier>, Address<? extends Receiver>>"); 
//		codeStream.println("{");
//		codeStream.println("\tReteContainerBuildable<String> buildable;");
//		codeStream.println("\tIPatternMatcherContext<String> context;");
//		codeStream.println("\tHashMap<String, Address<? extends Receiver>> collectors;");
//		codeStream.println("\tHashMap<String, HashMap<Object, Integer>> posMappings;");
//		codeStream.println();		
//
//		codeStream.println("\tpublic GeneratedBuilder(ReteContainerBuildable<String> buildable, IPatternMatcherContext<String> context)");
//		codeStream.println("\t{");
//		codeStream.println("\t\tsuper();");
//		codeStream.println("\t\tthis.buildable = buildable;");
//		codeStream.println("\t\tthis.context = context;");
//		codeStream.println("\t\tthis.collectors = new HashMap<String, Address<? extends Receiver>>();");
//		codeStream.println("\t\tthis.posMappings = new HashMap<String, HashMap<Object, Integer>>();");
//		codeStream.println("\t}");
//		codeStream.println("\tpublic Address<? extends Receiver> construct(String gtPattern) throws RetePatternBuildException {");
//		codeStream.println("\t\treturn collectors.get(gtPattern);");
//		codeStream.println("\t}");
//		codeStream.println("\tpublic HashMap<Object, Integer> getPosMapping(String gtPattern) {");
//		codeStream.println("\t\treturn posMappings.get(gtPattern);");
//		codeStream.println("\t}");
//		codeStream.println("\tpublic void refresh() {");
//		codeStream.println("\t\tthrow new UnsupportedOperationException();	");	
//		codeStream.println("\t}");
//	
//		codeStream.println();		
//		
//		
//		
//		codeStream.println("\tpublic void build() throws RetePatternBuildException {");
//		for (Object machine : framework.getMachines()) {
//			Machine gtasmMachine = (Machine) machine;
//			
//			// process annotation
//			RuntimeAnnotation codegenAnnotation = null;
//			String mode = "default";
//			
//			for (Object annotation : gtasmMachine.getRuntimeAnnotations()) {
//				RuntimeAnnotation annot = (RuntimeAnnotation) annotation;
//				if (annot.getAnnotationName().toLowerCase().equals("@CodeGeneration".toLowerCase())) {
//					codegenAnnotation = annot;
//					break;
//				}
//			}
//			if (codegenAnnotation != null)
//			for (Object element : codegenAnnotation.getElements()) {
//				RuntimeAnnotationElement rel = (RuntimeAnnotationElement) element;
//				if (rel.getKey().toLowerCase().equals("mode")) {
//					mode = rel.getValue().toLowerCase();
//				}
//			}
//			
//			// process patterns in machine
//			if (! mode.equals("ignore"))
//			{
//				// TODO traverse tree and get unnamed patterns, preconditions (?), etc.
//				for (Object patternObj : gtasmMachine.getGtPatternDefinitions()) {
//					//engine.accessMatcher((GTPattern)pattern);
//					genPatternCode((GTPattern) patternObj, framework, codeStream, builder);
//				}
//			}
//		}
//		
//		while (!coordinator.isComplete()) {
//			GTPattern unbuilt = coordinator.nextUnbuilt();
//			genPatternCode(unbuilt, framework, codeStream, builder);
//		}
//		codeStream.println("\t}");
//		codeStream.println("\tpublic void init() throws RetePatternBuildException {");
//		buildable.printInitializer("collectors", "posMappings");
//		codeStream.println("\t}");	
//		StringBuilder sb;
//		coordinator.printMembers(sb, "\t");
//		codeStream.print(sb.toString());
//		codeStream.println("}");	
	}

	private void genPatternCode(GTPattern pattern, IFramework framework, PrintStream codeStream,
			IRetePatternBuilder<GTPattern, String, String> builder) {
		try {
			codeStream.println();
			codeStream.println("\t\t// " + pattern.getName());
			builder.construct(pattern);
		} catch (RetePatternBuildException e) {
			framework.getLogger().message(Logger.ERROR, "Error generating pattern matcher code: " + e.getMessage(), e);
		}
	}
	


}
