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
package org.eclipse.viatra2.emf.incquery.gui.wizards.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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

	private ILabelProvider labelProvider;
	private TableViewer tableViewer;
	private Map<Object, TableItem> elementMap;
	private List<Object> elements;
    private String filter = "";
    private Map<String, Object> labelMap;
    private Text filterText;
    private String header;
    
	public ElementSelectionDialog(Shell parent, ILabelProvider labelProvider, String header) {
		super(parent);
		this.labelProvider = labelProvider;
		this.elementMap = new HashMap<Object, TableItem>();
		this.labelMap = new HashMap<String, Object>();
		this.elements = new ArrayList<Object>();
		this.header = header;
	}

	public void setElements(Object[] elements) {	
		this.elements.clear();
		this.labelMap.clear();
		for (Object element : elements) {
			this.elements.add(element);
			this.labelMap.put(this.labelProvider.getText(element), element);
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
        setSelection(getInitialElementSelections().toArray());
        return contents;
	}
	
	private void createElementTable(Composite parent) {
		this.tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		TableViewerColumn col = new TableViewerColumn(this.tableViewer, SWT.NONE);
		col.getColumn().setWidth(400);
		col.getColumn().setText(this.header);
		
		Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		filterElements("");
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
                filterElements(filterText.getText());
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
    
	private void filterElements(String filter) {
    	StringMatcher matcher = new StringMatcher(filter+"*", true, false);
    	this.tableViewer.getTable().removeAll();
    	
		for (Object element : elements) {
			String label = this.labelProvider.getText(element);
			if (matcher.match(label)) {
				TableItem item = new TableItem(tableViewer.getTable(), SWT.NONE);
				item.setData(element);
				item.setText(label);
				item.setImage(this.labelProvider.getImage(element));
				elementMap.put(element, item);
			}
		}
    }

	private void setSelection(Object[] selection) {
		TableItem[] itemSelection = new TableItem[selection.length];
		int i = 0;
		for (Object label : selection) {
			itemSelection[i++] = elementMap.get(labelMap.get(label));
		}
		this.tableViewer.getTable().setSelection(itemSelection);
	}
}
