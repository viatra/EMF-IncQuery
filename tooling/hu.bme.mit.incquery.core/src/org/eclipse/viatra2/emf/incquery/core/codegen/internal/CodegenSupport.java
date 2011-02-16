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


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.viatra2.emf.incquery.core.IncQueryPlugin;
import org.eclipse.viatra2.emf.incquery.core.codegen.CodeGenerationException;
import org.eclipse.viatra2.gtasmmodel.gtasm.metamodel.asm.core.GTASMElement;
import org.osgi.framework.Bundle;

/**
 * @author Bergmann Gábor
 *
 */
public class CodegenSupport {
	static final String pathToTemplatesDir = "templates/";
	static final String charset = "UTF-8";

	
	/**
	 * Yet Another Very Simple Template Engine. 
	 * Takes a template string as input, and replaces #key# occurrences with value occurrences
	 * for each (key, value) pair in substitutions.
	 * @param template the template
	 * @param substitutions
	 * @return
	 */
	static String processTemplate(String template, Map<String, String> substitutions) {
		for(Map.Entry<String, String> substitution : substitutions.entrySet()) {
			String from = java.util.regex.Pattern.quote("#"+substitution.getKey()+"#");
			String to = java.util.regex.Pattern.quote(substitution.getValue());
			to = to.substring(2, to.length()-2); // skip \Q in the beginning and \E at the and that would otherwise be printed verbatim to the output
			to = to.replaceAll("\\\\", "\\\\\\\\");
			template = template.replaceAll(from, to);
		}
		return template;
	}
	
	/**
	 * Loads and returns the given template from the templates/ dir in this bundle
	 * @param templateFileName
	 * @return
	 * @throws CodeGenerationException if template fill is missing
	 */
	static String loadTemplate(String templateFileName) throws CodeGenerationException {
		Bundle thisBundle = IncQueryPlugin.plugin.context.getBundle();
		InputStream patternBuilderTemplateStream;
		try {
			Path pathToTemplate = new Path(pathToTemplatesDir + templateFileName);
			patternBuilderTemplateStream = FileLocator.openStream(thisBundle, pathToTemplate, false);
			Scanner sc = new Scanner(patternBuilderTemplateStream);
			StringBuilder sb = new StringBuilder();
			while(sc.hasNextLine()) {
				String line = sc.nextLine();
				sb.append(line + '\n');
			}
			return sb.toString();
//			if (patternBuilderTemplateSegments.size() != 3) 
//				throw new CodeGenerationException("Corrupt template file");
		} catch (IOException e) {
			throw new CodeGenerationException("Error while accessing template file", e);
		}
	}

	/**
	 * @param monitor
	 * @param contents
	 * @param file
	 * @throws UnsupportedEncodingException
	 * @throws CoreException
	 */
	public static void printStringToFile(IProgressMonitor monitor, String contents,
			IFile file) throws UnsupportedEncodingException, CoreException {
		InputStream stringStream = new ByteArrayInputStream(contents.getBytes(charset));
		if (file.exists()) file.setContents(stringStream, true, true, monitor);
			else file.create(stringStream, true, monitor);
		file.setDerived(true, monitor);
		file.setCharset(charset, monitor);
	}
	
	/**
	 * Given a base package name and its pre-existing path and a GTASMElement (e.g. a PatternContainer), navigates 
	 * to the subpackage folder that corresponds to the namespace of the GTASMElement. 
	 * Benefits:
	 *  - ensures that the package forder exists
	 *  - returns an IFolder as well as a Java package name String
	 *  - forms package names so that they are lowercase and not reserved Java identifiers
	 *  
	 * Usage:
	 * CodegenSupport.PackageLocationFinder packageLocation = 
	 * 	new CodegenSupport.PackageLocationFinder(pattern.getContainer(), pathRoot, packageNameRoot, monitor);
	 * packageLocation.getJavaPackageName()...;
	 * packageLocation.getFolder()...;
	 * 
	 * @author Bergmann Gábor
	 *
	 */
	public static class PackageLocationFinder {
		private IFolder folder;
		private String javaPackageName;
		
		private IProgressMonitor monitor;

		public IFolder getFolder() {
			return folder;
		}

		public String getJavaPackageName() {
			return javaPackageName;
		}
		
		/**
		 * @pre either pathRoot or at least its parent folder already exists.
		 * @param element a GTASM element whose namespace is to be mapped to Java packages
		 * @param pathRoot a Java package path to contain packages generated from the pattern container 
		 * @param packageNameRoot the Java package name to contain packages generated from the pattern container 
		 * @param monitor optional progress monitor for the file operations
		 * @throws CodeGenerationException if there is some problem creating the packages,
		 */
		public PackageLocationFinder(
				GTASMElement element,
				IPath pathRoot, 
				String packageNameRoot,
				IProgressMonitor monitor) throws CodeGenerationException {
			super();
			this.monitor = monitor;
			folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(pathRoot);
			assureExists();
			StringBuilder packageName = new StringBuilder(packageNameRoot); // + "." + patternBuildersPackage);
			String[] nameSegments = element.getFqn().split("\\.");
			for (int i=0; i<nameSegments.length -1; ++i) // skips the last segment, only generates package for container element
				if (!nameSegments[i].isEmpty()) { 
					String packageLocalName = Character.toLowerCase(nameSegments[i].charAt(0))
											+ nameSegments[i].substring(1)
											//+ '_'
											;
					packageName.append('.');
					packageName.append(packageLocalName);
					folder = folder.getFolder(packageLocalName);
					assureExists();
				}
			this.javaPackageName = packageName.toString();
		}

		private void assureExists() throws CodeGenerationException
		{
			if (!folder.exists())
			{
				try {
					folder.create(false, false, monitor);
				} catch (CoreException e) {
					throw new CodeGenerationException("Error creating package folder " 
							+ folder.getFullPath().toOSString()
							+ " during code generation.", e);
				}
			}
		}

	}
}
