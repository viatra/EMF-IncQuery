/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo, Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.ui.wizards;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.incquery.tooling.core.project.IncQueryNature;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class SelectIncQueryProjectPage extends WizardPage {
    private final static class ProjectColumnLabelProvider extends ColumnLabelProvider {
        private Color disabledColor;

        public ProjectColumnLabelProvider(Color disabledColor) {
            super();
            this.disabledColor = disabledColor;
        }

        @Override
        public String getText(Object element) {
            if (element instanceof IProject) {
                return ((IProject) element).getName();
            }
            return super.getText(element);
        }

        @Override
        public Color getForeground(Object element) {
            if (element instanceof IProject && ((IProject) element).findMember(IncQueryNature.IQGENMODEL) != null) {
                return disabledColor;
            }
            return super.getForeground(element);
        }

        @Override
        public String getToolTipText(Object element) {
            if (element instanceof IProject && ((IProject) element).findMember(IncQueryNature.IQGENMODEL) != null) {
                return String.format("Project %s has already defined an EMF-IncQuery Generator model.",
                        ((IProject) element).getName());
            }
            return super.getToolTipText(element);
        }
    }

    private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
    private IStructuredSelection selection;
    private TableViewer viewer;

    private Logger logger;

    /**
     * Create the wizard.
     */
    public SelectIncQueryProjectPage(String title, IStructuredSelection selection, Logger logger) {
        super("wizardPage");
        this.selection = selection;
        this.logger = logger;
        setTitle(title);
        setDescription("Select an EMF-IncQuery project without an EMF-IncQuery Generator model");
    }

    /**
     * Create contents of the wizard.
     * 
     * @param parent
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        setControl(container);
        container.setLayout(new FillLayout(SWT.HORIZONTAL));

        Table table = formToolkit.createTable(container, SWT.NONE);
        formToolkit.paintBordersFor(table);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        viewer = new TableViewer(table);
        viewer.setContentProvider(new ArrayContentProvider());
        TableViewerColumn column = new TableViewerColumn(viewer, SWT.LEFT);
        column.setLabelProvider(new ProjectColumnLabelProvider(parent.getDisplay().getSystemColor(SWT.COLOR_GRAY)));
        viewer.addFilter(new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof IProject) {
                    try {
                        IProject project = (IProject) element;
                        return project.exists() && project.isOpen() && project.hasNature(IncQueryNature.NATURE_ID);
                    } catch (CoreException e) {
                        // This exception shall not come forth
                        logger.error("Error while filtering project list", e);
                    }
                }
                return false;
            }
        });

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (getContainer().getCurrentPage() != null) {
                    getContainer().updateButtons();
                }
            }
        });

        viewer.setInput(root.getProjects());
        TableLayout layout = new TableLayout();
        layout.addColumnData(new ColumnWeightData(100));
        table.setLayout(layout);

        Iterator<?> it = selection.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof IResource) {
                IProject containerProject = ((IResource) obj).getProject();
                setSelectedProject(viewer, containerProject);
            } else if (obj instanceof IAdaptable) {
                IProject containerProject = ((IResource) ((IAdaptable) obj).getAdapter(IResource.class)).getProject();
                setSelectedProject(viewer, containerProject);
            }
        }

    }

    private void setSelectedProject(TableViewer viewer, IProject containerProject) {
        try {
            if (containerProject.hasNature(IncQueryNature.NATURE_ID)) {
                viewer.setSelection(new StructuredSelection(containerProject));
            }
        } catch (CoreException e) {
            // This exception shall not come forth
            logger.error("Error while selecting project " + containerProject.getName(), e);
        }
    }

    public IProject getSelectedProject() {
        if (!viewer.getSelection().isEmpty()) {
            return (IProject) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
        }
        return null;
    }

    @Override
    public boolean isPageComplete() {
        return !viewer.getSelection().isEmpty()
                && !(getSelectedProject().findMember(IncQueryNature.IQGENMODEL) != null);
    }

}
