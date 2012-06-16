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

package org.eclipse.viatra2.emf.incquery.tooling.generator

import org.eclipse.pde.core.plugin.IExtensionsModelFactory
import org.eclipse.pde.core.plugin.IPluginExtension
import org.eclipse.pde.core.plugin.IPluginElement
import org.eclipse.pde.core.plugin.IPluginObject
import org.eclipse.core.resources.IProject
import org.eclipse.core.resources.IFile
import org.eclipse.pde.internal.core.project.PDEProject
import org.eclipse.pde.internal.core.plugin.WorkspacePluginModel
import org.eclipse.pde.core.plugin.IPluginParent

class ExtensionGenerator {
	
	IExtensionsModelFactory factory
	
	def setProject(IProject project) {
		val IFile plugin = PDEProject::getPluginXml(project)
		val fModel = new WorkspacePluginModel(plugin, true)
		factory = fModel.factory
	}
	
	def contribExtension(String id, String point, (IPluginExtension) => void initializer) {
		val ex = factory.createExtension
		ex.id = id
		ex.point = point
		ex.init(initializer)
	}
	
	def contribElement(IPluginObject parent, String name, (IPluginElement) => void initializer) {
		val el = factory.createElement(parent)
		el.name = name
		if (parent instanceof IPluginParent) {
			(parent as IPluginParent).add(el)
		}
		el.init(initializer)
	}
	
	def contribAttribute(IPluginElement element, String name, String value) {
		element.setAttribute(name, value)
	}
	
	def private <T> T init (T obj, (T)=>void init) {
		init.apply(obj)
		return obj
	}
}