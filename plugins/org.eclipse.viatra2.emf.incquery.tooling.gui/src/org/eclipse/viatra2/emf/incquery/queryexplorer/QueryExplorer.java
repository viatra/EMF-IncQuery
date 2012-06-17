/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.queryexplorer;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail.TableViewerUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.flyout.FlyoutControlComposite;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.flyout.FlyoutPreferences;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.flyout.IFlyoutPreferences;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherContentProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherLabelProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRoot;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatch;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.ObservablePatternMatcher;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternComposite;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternsViewerContentProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternsViewerLabelProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.CheckStateListener;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DoubleClickListener;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.ModelEditorPartListener;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.QueryExplorerPerspectiveAdapter;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.ResourceChangeListener;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * MatchSetViewer is used to display the match sets for those matchers which are annotated with PatternUI. 
 * 
 * @author Tamas Szabo
 *
 */
public class QueryExplorer extends ViewPart {

	public static final String ID = "org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer";
	private TableViewer tableViewer;
	private CheckboxTreeViewer patternsViewer;
	
	//matcher tree viewer
	private TreeViewer matcherTreeViewer;
	private MatcherContentProvider matcherContentProvider;
	private MatcherLabelProvider matcherLabelProvider;
	private MatcherTreeViewerRoot matcherTreeViewerRoot;
	
	//observable view
	private ModelEditorPartListener modelPartListener;
	private PatternComposite patternsViewerInput;
		
	private QueryExplorerPerspectiveAdapter perspectiveAdapter;
	
	private FlyoutControlComposite patternsViewerFlyout;
	private FlyoutControlComposite detailsViewFlyout;
	
	@Inject
	Injector injector;
	
	@Inject
	TableViewerUtil tableViewerUtil;
	
	public QueryExplorer() {
		matcherContentProvider = new MatcherContentProvider();
		matcherLabelProvider = new MatcherLabelProvider();
		matcherTreeViewerRoot = new MatcherTreeViewerRoot();
		modelPartListener = new ModelEditorPartListener();
		perspectiveAdapter = new QueryExplorerPerspectiveAdapter();
		patternsViewerInput = new PatternComposite("", null, false);
	}
	
	public MatcherTreeViewerRoot getMatcherTreeViewerRoot() {
		return matcherTreeViewerRoot;
	}
	
	public static QueryExplorer getInstance() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null) {
			IViewPart form = page.findView(ID);
	    	return (QueryExplorer) form;
		}
		return null;
	}
	
	public TreeViewer getMatcherTreeViewer() {
		return matcherTreeViewer;
	}
	
	public void clearTableViewer() {
		if (tableViewer.getContentProvider() != null) {
			tableViewer.setInput(null);
		}
	}
	
	public void createPartControl(Composite parent) {
		getViewSite().getWorkbenchWindow().addPerspectiveListener(perspectiveAdapter);
		
		IFlyoutPreferences rightFlyoutPreferences = new FlyoutPreferences(IFlyoutPreferences.DOCK_EAST, IFlyoutPreferences.STATE_OPEN, 300);
		detailsViewFlyout = new FlyoutControlComposite(parent, SWT.NONE, rightFlyoutPreferences);
		detailsViewFlyout.setTitleText("Details / Filters");
		detailsViewFlyout.setValidDockLocations(IFlyoutPreferences.DOCK_EAST);
		
		IFlyoutPreferences leftFlyoutPreferences = new FlyoutPreferences(IFlyoutPreferences.DOCK_WEST, IFlyoutPreferences.STATE_COLLAPSED, 100);
		patternsViewerFlyout = new FlyoutControlComposite(detailsViewFlyout.getClientParent(), SWT.NONE, leftFlyoutPreferences);
		patternsViewerFlyout.setTitleText("Pattern registry");
		patternsViewerFlyout.setValidDockLocations(IFlyoutPreferences.DOCK_WEST);
				
		matcherTreeViewer = new TreeViewer(patternsViewerFlyout.getClientParent());
		tableViewer = new TableViewer(detailsViewFlyout.getFlyoutParent(), SWT.FULL_SELECTION);
		
		//matcherTreeViewer configuration
		matcherTreeViewer.setContentProvider(matcherContentProvider);
		matcherTreeViewer.setLabelProvider(matcherLabelProvider);
		matcherTreeViewer.setInput(matcherTreeViewerRoot);
		
		IObservableValue selection = ViewersObservables.observeSingleSelection(matcherTreeViewer);
		selection.addValueChangeListener(new MatcherTreeViewerSelectionChangeListener());
		matcherTreeViewer.addDoubleClickListener(new DoubleClickListener());
		
		//patternsViewer configuration		
		patternsViewer = new CheckboxTreeViewer(patternsViewerFlyout.getFlyoutParent(), SWT.CHECK | SWT.BORDER | SWT.MULTI);
		patternsViewer.addCheckStateListener(new CheckStateListener());
		patternsViewer.setContentProvider(new PatternsViewerContentProvider());
		patternsViewer.setLabelProvider(new PatternsViewerLabelProvider());
		patternsViewer.setInput(patternsViewerInput);
		
		// Create menu manager.
        MenuManager matcherTreeViewerMenuManager = new MenuManager();
        matcherTreeViewerMenuManager.setRemoveAllWhenShown(true);
        matcherTreeViewerMenuManager.addMenuListener(new IMenuListener() {
        	public void menuAboutToShow(IMenuManager mgr) {
        		fillContextMenu(mgr);
            }
        });   
        // Create menu for tree viewer
        Menu matcherTreeViewerMenu = matcherTreeViewerMenuManager.createContextMenu(matcherTreeViewer.getControl());
		matcherTreeViewer.getControl().setMenu(matcherTreeViewerMenu);
		getSite().registerContextMenu("org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer.treeViewerMenu", matcherTreeViewerMenuManager, matcherTreeViewer);
		
        MenuManager patternsViewerMenuManager = new MenuManager();
        patternsViewerMenuManager.setRemoveAllWhenShown(true);
        patternsViewerMenuManager.addMenuListener(new IMenuListener() {
        	public void menuAboutToShow(IMenuManager mgr) {
        		fillContextMenu(mgr);
            }
        });
		// Create menu for patterns viewer
		Menu patternsViewerMenu = patternsViewerMenuManager.createContextMenu(patternsViewer.getControl());
		patternsViewer.getControl().setMenu(patternsViewerMenu);
		getSite().registerContextMenu("org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer.patternsViewerMenu", patternsViewerMenuManager, patternsViewer);
		
		//tableView configuration
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		//tableViewer.setContentProvider(new ObservableListContentProvider());
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		tableViewer.getControl().setLayoutData(gridData);
		
		getSite().setSelectionProvider(matcherTreeViewer);
		
		initFileListener();
	}

	private void fillContextMenu(IMenuManager mgr) {
        mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	public void setFocus() {
		matcherTreeViewer.getControl().setFocus();
	}
	
	private class MatcherTreeViewerSelectionChangeListener implements IValueChangeListener {
		
		@Override
		public void handleValueChange(ValueChangeEvent event) {
			Object value = event.getObservableValue().getValue();
			
			if (value instanceof ObservablePatternMatcher) {
				ObservablePatternMatcher observableMatcher = (ObservablePatternMatcher) value;	
				tableViewerUtil.prepareTableViewerForMatcherConfiguration(observableMatcher, tableViewer);
			}
			else if (value instanceof ObservablePatternMatch) {
				ObservablePatternMatch match = (ObservablePatternMatch) value;
				tableViewerUtil.prepareTableViewerForObservableInput(match, tableViewer);
			}
			else {
				tableViewerUtil.clearTableViewerColumns(tableViewer);
				clearTableViewer();
			}
		}
		
	}
	
	private void initFileListener() {
		IResourceChangeListener listener = new ResourceChangeListener(injector);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.PRE_BUILD);
	}
	
	public ModelEditorPartListener getModelPartListener() {
		return modelPartListener;
	}
	
	public PatternComposite getPatternsViewerInput() {
		return patternsViewerInput;
	}
	
	public CheckboxTreeViewer getPatternsViewer() {
		return patternsViewer;
	}
	
	public FlyoutControlComposite getPatternsViewerFlyout() {
		return patternsViewerFlyout;
	}
}
