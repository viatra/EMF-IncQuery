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
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

public class ModelElementCellEditor extends CellEditor {

    private Composite editor;
    private Control contents;
    private Text inputText;
    private Button button;
	private KeyListener keyListener;
    private Object value = null;
    private Notifier root;
    private Table table;
    
    public ModelElementCellEditor(Table table, Notifier root) {
        super(table, SWT.NONE);
        this.root = root;
        this.table = table;
    }

    private class DialogCellLayout extends Layout {
        public void layout(Composite editor, boolean force) {
            Rectangle bounds = editor.getClientArea();
            Point size = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
            if (contents != null) {
				contents.setBounds(0, 0, bounds.width - size.x, bounds.height);
			}
            button.setBounds(bounds.width - size.x, 0, size.x, bounds.height);
        }

        public Point computeSize(Composite editor, int wHint, int hHint,
                boolean force) {
            if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
				return new Point(wHint, hHint);
			}
            Point contentsSize = contents.computeSize(SWT.DEFAULT, SWT.DEFAULT,
                    force);
            Point buttonSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT,
                    force);
            // Just return the button width to ensure the button is not clipped
            // if the label is long.
            // The label will just use whatever extra width there is
            Point result = new Point(buttonSize.x, Math.max(contentsSize.y,
                    buttonSize.y));
            return result;
        }
    }

    private Button createButton(Composite parent) {
        Button result = new Button(parent, SWT.DOWN);
        result.setText("..."); //$NON-NLS-1$
        return result;
    }

    private Control createContents(Composite cell) {
        inputText = new Text(cell, SWT.LEFT);
        inputText.setEditable(true);
        inputText.setFont(cell.getFont());
        inputText.setBackground(cell.getBackground());
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

        button = createButton(editor);
        button.setFont(font);

        button.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.character == '\u001b') { // Escape
                    fireCancelEditor();
                }
            }
        });
        
        button.addSelectionListener(new SelectionAdapter() {
            /* (non-Javadoc)
             * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            public void widgetSelected(SelectionEvent event) {
            	
            	TableItem selection = table.getSelection()[0];
            	MatcherConfiguration conf = (MatcherConfiguration) selection.getData();
            	if (!TableViewerUtil.isPrimitiveType(conf.getClazz())) {                    
                	button.setToolTipText("");
            		Object newValue = openDialogBox(editor);

                	if (newValue != null) {
                        boolean newValidState = isCorrect(newValue);
                        if (newValidState) {
                            markDirty();
                            doSetValue(newValue);
                        } else {
                            // try to insert the current value into the error message.
                            setErrorMessage(MessageFormat.format(getErrorMessage(),
                                    new Object[] { newValue.toString() }));
                        }
                        fireApplyEditorValue();
                    }
            	}
            	else {
            		button.setToolTipText("No selection - input value!");
            	}
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
        inputText.setFocus();
        inputText.addKeyListener(getTextKeyListener());
    }

    private KeyListener getTextKeyListener() {
    	if (keyListener == null) {
    		keyListener = new KeyListener() {
				
				@Override
				public void keyReleased(KeyEvent e) {
					TableItem ti = table.getSelection()[0];
					MatcherConfiguration conf = (MatcherConfiguration) ti.getData();
					conf.setValue(inputText.getText());
					value = inputText.getText();
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

    protected Object openDialogBox(Control cellEditorWindow) {
    	ElementListSelectionDialog listDialog = 
    			new ElementListSelectionDialog(
    					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
    					new ModelElementListDialogLabelProvider()
    			);
    	listDialog.setTitle("Model element selection");
    	listDialog.setMessage("Select a model element (* = any string, ? = any char):");
    	Object[] input = getElements(this.root);
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
    
    private Object[] getElements(Object inputElement) {
		List<Object> result = new ArrayList<Object>();
		TreeIterator<EObject> iterator = null;
		
		if (root instanceof EObject) {
			iterator = ((EObject) root).eAllContents();
			
			while (iterator.hasNext()) {
				result.add(iterator.next());
			}
		}
		else if (root instanceof Resource) {
			iterator = ((Resource) root).getAllContents();
			
			while (iterator.hasNext()) {
				result.add(iterator.next());
			}
		}
		else if (root instanceof ResourceSet) {
			for (Resource res : ((ResourceSet) root).getResources()) {
				iterator = res.getAllContents();
				while (iterator.hasNext()) {
					result.add(iterator.next());
				}
			}
		}
		
		return result.toArray();
    }
}
