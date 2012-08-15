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

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * @author Csicsely Attila
 *
 */
public class BuilderApplication implements IApplication {
	private static String projectParam = "-p";
	private static String cleanParam = "-c";

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object start(IApplicationContext context) throws Exception {
		
		Map<String, Object> arguments = context.getArguments();
		String[] args = (String[]) arguments.get("application.args");
		String projectArg = null;
		boolean cleanArg = false;
		
		if (args == null || args.length == 0) {
			return IApplication.EXIT_OK;
		}
		
		int i = 0;
		while (i < args.length) {
			if (args[i].equals(projectParam)) {
				projectArg = args[i + 1];
				i += 2;
				continue;
			}
			if (args[i].equals(cleanParam)) {
				cleanArg = true;
				i++;
				continue;
			} else {
				i++;
				continue;
			}
		}
		
		if (projectArg == null) {
			  System.out.println("Project parameter not set");
				return IApplication.EXIT_OK;
			}
		
		try{		
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectArg);
			String builderName = "org.eclipse.xtext.ui.shared.xtextBuilder";
			if(cleanArg){
				System.out.println("Start clean building " + projectArg + " project" );					
				long start = System.nanoTime();				
				project.build(IncrementalProjectBuilder.CLEAN_BUILD, builderName, null, new NullProgressMonitor());				
				long stop = System.nanoTime();			
				System.out.println("Building time: " + (stop - start) / 1000000 + " ms");
			}else{
				System.out.println("Start building " + projectArg + " project" );					
				long start = System.nanoTime();					
				project.build(IncrementalProjectBuilder.FULL_BUILD, builderName, null, new NullProgressMonitor());				
				long stop = System.nanoTime();			
				System.out.println("Building time: " + (stop - start) / 1000000 + " ms");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return IApplication.EXIT_OK;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
