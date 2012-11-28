/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.ui.wizards.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.eclipse.ui.internal.misc.StringMatcher;

/**
 * @author Tamas Szabo
 *
 */
@SuppressWarnings("restriction")
public class ElementSelectionDialog extends SelectionStatusDialog {

	private StyledCellLabelProvider labelProvider;
	private IStructuredContentProvider contentProvider;
	private TableViewer tableViewer;
	//private Map<Object, TableItem> elementMap;
	private List<Object> elements;
    private String filter = "";
    private Text filterText;
    private String header;
	private ImportFilter importFilter;
    
	private class ImportFilter extends ViewerFilter {

		private String filterString = "";

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers
		 * .Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			StringMatcher matcher = new StringMatcher("*" + filterString + "*",
					true,
					false);
			if (element instanceof EPackage) {
				return matcher.match(((EPackage) element).getNsURI());
			}
			return true;
		}

	}

	public ElementSelectionDialog(Shell parent,
			StyledCellLabelProvider labelProvider, String header) {
		super(parent);
		this.labelProvider = labelProvider;
		this.contentProvider = new ElementSelectionDialogContentProvider();
		this.elements = new ArrayList<Object>();
		this.header = header;
	}

	public void setElements(Object[] elements) {	
		this.elements.clear();
		for (Object element : elements) {
			this.elements.add(element);
		}
	}

	@Override
	protected void computeResult() {
		TableItem[] selection = this.tableViewer.getTable().getSelection();
    	List<Object> result = new ArrayList<Object>();
    	for (TableItem item : selection) {
    		result.add(item.getData());
    	}
    	setResult(result);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
        Composite contents = (Composite) super.createDialogArea(parent);
        createMessageArea(contents);
        createFilterText(contents);
        createElementTable(contents);
        return contents;
	}
	
	private void createElementTable(Composite parent) {
		this.tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		this.tableViewer.setContentProvider(this.contentProvider);
		importFilter = new ImportFilter();
		this.tableViewer.addFilter(importFilter);
		
		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setWidth(400);
		column.getColumn().setText(this.header);
		column.setLabelProvider(labelProvider);
		
        final Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

        table.addMouseListener(new MouseAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.swt.events.MouseAdapter#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
             */
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                if (table.getSelectionCount() > 0) {
                    okPressed();
                }
            }

        });

		tableViewer.setInput(elements);
	}
	
    protected Text createFilterText(Composite parent) {
        Text text = new Text(parent, SWT.BORDER);

        GridData data = new GridData();
        data.grabExcessVerticalSpace = false;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.BEGINNING;
        text.setLayoutData(data);
        text.setFont(parent.getFont());

        text.setText((filter == null ? "" : filter));

        Listener listener = new Listener() {
            public void handleEvent(Event e) {
				importFilter.filterString = filterText.getText();
				tableViewer.refresh();
				// filterElements(filterText.getText());
            }
        };
        text.addListener(SWT.Modify, listener);

        text.addKeyListener(new KeyListener() {
        	public void keyPressed(KeyEvent e) {
        		if (e.keyCode == SWT.ARROW_DOWN) {
        			tableViewer.getTable().setFocus();
        		}
        	}

        	public void keyReleased(KeyEvent e) {
        	}
        });

        filterText = text;

        return text;
    }
    
    @Override
    public int open() {
        super.open();
        return getReturnCode();
    }

}
