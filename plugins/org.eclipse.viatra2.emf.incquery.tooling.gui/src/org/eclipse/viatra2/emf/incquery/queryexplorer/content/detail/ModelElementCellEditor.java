/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tamas Szabo - extended functionality
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcher;

import com.google.inject.Inject;

public class ModelElementCellEditor extends CellEditor {

    private Composite editor;
    private Control contents;
    private Text inputText;
    private Button dialogButton;
    private Button clearButton;
	private KeyListener keyListener;
    private Object value = null;
    private Notifier root;
    private Table table;
    private ObservablePatternMatcher observableMatcher;
    
    @Inject
    TableViewerUtil tableViewerUtil;
        
    public ModelElementCellEditor(Table table, ObservablePatternMatcher observableMatcher) {
        super(table, SWT.NONE);
        this.root = observableMatcher.getParent().getNotifier();
        this.table = table;
        this.observableMatcher = observableMatcher;
    }

    private class DialogCellLayout extends Layout {
    	
        public void layout(Composite editor, boolean force) {
            Rectangle bounds = editor.getClientArea();
            Point dialogButtonSize = dialogButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
            Point clearButtonSize = clearButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
            if (contents != null) {
				contents.setBounds(0, 0, bounds.width - dialogButtonSize.x - clearButtonSize.x, bounds.height);
			}
            
            clearButton.setBounds(bounds.width - dialogButtonSize.x - clearButtonSize.x, 0, clearButtonSize.x, bounds.height);
            dialogButton.setBounds(bounds.width - dialogButtonSize.x, 0, dialogButtonSize.x, bounds.height);
        }

        public Point computeSize(Composite editor, int wHint, int hHint, boolean force) {
            if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
				return new Point(wHint, hHint);
			}
            Point contentsSize = contents.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
            Point buttonSize = dialogButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
            // Just return the button width to ensure the button is not clipped
            // if the label is long.
            // The label will just use whatever extra width there is
            Point result = new Point(buttonSize.x, Math.max(contentsSize.y, buttonSize.y));
            return result;
        }
    }

    private Button createDialogButton(Composite parent) {
        Button result = new Button(parent, SWT.DOWN);
        result.setText("...");
        return result;
    }
    
    private Button createClearButton(Composite parent) {
    	Button result = new Button(parent, SWT.DOWN);
        result.setText("X");
        return result;
    }

    private Control createContents(Composite cell) {
        inputText = new Text(cell, SWT.LEFT);
        inputText.setEditable(true);
        inputText.setFont(cell.getFont());
        inputText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        return inputText;
    }

    protected Control createControl(Composite parent) {

        Font font = parent.getFont();
        Color bg = parent.getBackground();

        editor = new Composite(parent, getStyle());
        editor.setFont(font);
        editor.setBackground(bg);
        editor.setLayout(new DialogCellLayout());

        contents = createContents(editor);
        updateContents(value);

        clearButton = createClearButton(editor);
        clearButton.setFont(font);
        
        dialogButton = createDialogButton(editor);
        dialogButton.setFont(font);

        dialogButton.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.character == '\u001b') { // Escape
                    fireCancelEditor();
                }
            }
        });
        
        dialogButton.addSelectionListener(new SelectionAdapter() {
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            public void widgetSelected(SelectionEvent event) {
            	TableItem selection = table.getSelection()[0];
            	MatcherConfiguration conf = (MatcherConfiguration) selection.getData();
            	if (!tableViewerUtil.isPrimitiveType(conf.getClazz())) {                    
            		Object newValue = openDialogBox(editor, conf.getClazz());

                	if (newValue != null) {
                        boolean newValidState = isCorrect(newValue);
                        if (newValidState) {
                            markDirty();
                            doSetValue(newValue);
                            conf.setFilter(newValue);
                            observableMatcher.setFilter(valuesOfTableItems(table));
                        } else {
                            // try to insert the current value into the error message.
                            setErrorMessage(MessageFormat.format(getErrorMessage(),
                                    new Object[] { newValue.toString() }));
                        }
                        fireApplyEditorValue();
                    }
            	}
            }
        });
        
        clearButton.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		TableItem selection = table.getSelection()[0];
            	MatcherConfiguration conf = (MatcherConfiguration) selection.getData();
        		inputText.setText("");
        		value = "";
        		conf.setFilter("");
        		observableMatcher.setFilter(valuesOfTableItems(table));
        	}
		});

        setValueValid(true);

        return editor;
    }

    public void deactivate() {
    	if (inputText != null && !inputText.isDisposed()) {
    		inputText.removeKeyListener(getTextKeyListener());
    	}
    	
		super.deactivate();
	}

    protected Object doGetValue() {
        return value;
    }

    protected void doSetFocus() {
    	TableItem selection = table.getSelection()[0];
    	MatcherConfiguration conf = (MatcherConfiguration) selection.getData();
    	
    	if (!tableViewerUtil.isPrimitiveType(conf.getClazz())) {
    		inputText.setEditable(false);
    	}
    	else {
    		inputText.setEditable(true);
    	}
    	
        inputText.setFocus();
        inputText.addKeyListener(getTextKeyListener());
        inputText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    }

    private KeyListener getTextKeyListener() {
    	if (keyListener == null) {
    		keyListener = new KeyListener() {
				
				@Override
				public void keyReleased(KeyEvent e) {
					
					String newValue = inputText.getText();
					TableItem ti = table.getSelection()[0];
					MatcherConfiguration conf = (MatcherConfiguration) ti.getData();
					
					if (tableViewerUtil.isValidValue(conf.getClazz(), newValue)) {
						inputText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
						conf.setFilter(inputText.getText());
						value = inputText.getText();
						//set restriction for observable matcher
						observableMatcher.setFilter(valuesOfTableItems(table));
					}
					else {
						inputText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
					}
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
					
				}
			};
    	}
    	
    	return keyListener;
	}

    protected void doSetValue(Object value) {
        this.value = value;
        updateContents(value);
    }

    protected Text getDefaultText() {
        return inputText;
    }

    protected Object openDialogBox(Control cellEditorWindow, String restriction) {
    	ElementListSelectionDialog listDialog = 
    			new ElementListSelectionDialog(
    					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
    					new ModelElementListDialogLabelProvider()
    			);
    	listDialog.setTitle("Model element selection");
    	listDialog.setMessage("Select a model element (* = any string, ? = any char):");
    	Object[] input = getElements(this.root, restriction);
    	listDialog.setElements(input);
    	listDialog.open();
    	Object[] result = listDialog.getResult();
    	if (result != null && result.length > 0) {
    		return result[0];
    	}
        return null;
    }

    protected void updateContents(Object value) {
        if (inputText == null) {
			return;
		}

        String text = "";//$NON-NLS-1$
        if (value != null) {
			text = value.toString();
		}
        inputText.setText(text);
    }
    
    private Object[] valuesOfTableItems(Table table) {
    	Object[] result = new Object[table.getItems().length];
    	
    	for (int i = 0;i<table.getItems().length;i++) {
    		MatcherConfiguration mc = (MatcherConfiguration) table.getItem(i).getData();
    		result[i] = tableViewerUtil.createValue(mc.getClazz(), mc.getFilter());
    	}
    	
    	return result;
    }
    
    private Object[] getElements(Object inputElement, String restrictionFqn) {
		List<Object> result = new ArrayList<Object>();
		TreeIterator<EObject> iterator = null;
		EObject obj = null;
		
		if (root instanceof EObject) {
			iterator = ((EObject) root).eAllContents();
			
			while (iterator.hasNext()) {
				obj = iterator.next();
				if (isOfType(obj.getClass(), restrictionFqn)) {
					result.add(obj);
				}
			}
		}
		else if (root instanceof Resource) {
			iterator = ((Resource) root).getAllContents();
			
			while (iterator.hasNext()) {
				obj = iterator.next();
				if (isOfType(obj.getClass(), restrictionFqn)) {
					result.add(obj);
				}
			}
		}
		else if (root instanceof ResourceSet) {
			for (Resource res : ((ResourceSet) root).getResources()) {
				iterator = res.getAllContents();
				while (iterator.hasNext()) {
					obj = iterator.next();
					if (isOfType(obj.getClass(), restrictionFqn)) {
						result.add(obj);
					}
				}
			}
		}
		
		return result.toArray();
    }
    
	private boolean isOfType(Class<?> clazz, String restrictionFqn) {
    	List<String> classes = collectAllInterfaces(clazz);
    	classes.add("java.lang.Object");
    	return classes.contains(restrictionFqn);
    }
    
    @SuppressWarnings("rawtypes")
	private List<String> collectAllInterfaces(Class clazz) {
    	List<String> result = new ArrayList<String>();
    	Class[] interfaces = clazz.getInterfaces();
    	
    	for (Class i : interfaces) {
    		result.add(i.getName());
    		result.addAll(collectAllInterfaces(i));
    	}

    	return result;
    }
}
