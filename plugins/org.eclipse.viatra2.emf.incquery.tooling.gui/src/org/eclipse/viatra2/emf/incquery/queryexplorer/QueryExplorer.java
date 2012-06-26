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

import java.util.List;

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
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
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
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternComponent;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternComposite;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternsViewerHierarchicalContentProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternsViewerHierarchicalLabelProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.CheckStateListener;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DatabindingUtil;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.DoubleClickListener;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.ModelEditorPartListener;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.PatternRegistry;
import org.eclipse.viatra2.emf.incquery.queryexplorer.util.ResourceChangeListener;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Query Explorer view implementation. 
 * 
 * @author Tamas Szabo
 *
 */
public class QueryExplorer extends ViewPart {

	public static final String ID = "org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer";
	
	private TableViewer detailsTableViewer;
	private CheckboxTreeViewer patternsTreeViewer;
	private TreeViewer matcherTreeViewer;
	
	private MatcherContentProvider matcherContentProvider;
	private MatcherLabelProvider matcherLabelProvider;
	private MatcherTreeViewerRoot matcherTreeViewerRoot;
	
	private ModelEditorPartListener modelPartListener;
	private PatternComposite patternsViewerInput;
	
	private FlyoutControlComposite patternsViewerFlyout;
	private FlyoutControlComposite detailsViewerFlyout;
	
	private IFlyoutPreferences detailsViewerFlyoutPreferences;
	private IFlyoutPreferences patternsViewerFlyoutPreferences;
	
	@Inject
	Injector injector;
	
	@Inject
	TableViewerUtil tableViewerUtil;
	
	public QueryExplorer() {
		matcherContentProvider = new MatcherContentProvider();
		matcherLabelProvider = new MatcherLabelProvider();
		matcherTreeViewerRoot = new MatcherTreeViewerRoot();
		modelPartListener = new ModelEditorPartListener();
		patternsViewerInput = new PatternComposite("", null);
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
	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		int detailsState = IFlyoutPreferences.STATE_OPEN;
		int patternsState = IFlyoutPreferences.STATE_COLLAPSED;
		if (memento != null) {
			if (memento.getInteger("detailsViewFlyoutState") != null) {
				detailsState = memento.getInteger("detailsViewFlyoutState");
			}
			if (memento.getInteger("patternsViewerFlyoutState") != null) {
				patternsState = memento.getInteger("detailsViewFlyoutState");
			}
		}
		detailsViewerFlyoutPreferences = new FlyoutPreferences(IFlyoutPreferences.DOCK_EAST, detailsState, 300);
		patternsViewerFlyoutPreferences = new FlyoutPreferences(IFlyoutPreferences.DOCK_WEST, patternsState, 100);
	}
	
	public void clearTableViewer() {
		if (detailsTableViewer.getContentProvider() != null) {
			detailsTableViewer.setInput(null);
		}
	}
	
	public void createPartControl(Composite parent) {
		detailsViewerFlyout = new FlyoutControlComposite(parent, SWT.NONE, detailsViewerFlyoutPreferences);
		detailsViewerFlyout.setTitleText("Details / Filters");
		detailsViewerFlyout.setValidDockLocations(IFlyoutPreferences.DOCK_EAST);
		
		patternsViewerFlyout = new FlyoutControlComposite(detailsViewerFlyout.getClientParent(), SWT.NONE, patternsViewerFlyoutPreferences);
		patternsViewerFlyout.setTitleText("Pattern registry");
		patternsViewerFlyout.setValidDockLocations(IFlyoutPreferences.DOCK_WEST);
		
		matcherTreeViewer = new TreeViewer(patternsViewerFlyout.getClientParent());
		detailsTableViewer = new TableViewer(detailsViewerFlyout.getFlyoutParent(), SWT.FULL_SELECTION);
		
		//matcherTreeViewer configuration
		matcherTreeViewer.setContentProvider(matcherContentProvider);
		matcherTreeViewer.setLabelProvider(matcherLabelProvider);
		matcherTreeViewer.setInput(matcherTreeViewerRoot);
		matcherTreeViewer.setComparator(null);
		IObservableValue selection = ViewersObservables.observeSingleSelection(matcherTreeViewer);
		selection.addValueChangeListener(new MatcherTreeViewerSelectionChangeListener());
		matcherTreeViewer.addDoubleClickListener(new DoubleClickListener());
		
		//patternsViewer configuration		
		patternsTreeViewer = new CheckboxTreeViewer(patternsViewerFlyout.getFlyoutParent(), SWT.CHECK | SWT.BORDER | SWT.MULTI);
		patternsTreeViewer.addCheckStateListener(new CheckStateListener());
		patternsTreeViewer.setContentProvider(new PatternsViewerHierarchicalContentProvider());
		patternsTreeViewer.setLabelProvider(new PatternsViewerHierarchicalLabelProvider());
		patternsTreeViewer.setInput(patternsViewerInput);
		
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
		Menu patternsViewerMenu = patternsViewerMenuManager.createContextMenu(patternsTreeViewer.getControl());
		patternsTreeViewer.getControl().setMenu(patternsViewerMenu);
		getSite().registerContextMenu("org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer.patternsViewerMenu", patternsViewerMenuManager, patternsTreeViewer);
		
		//tableView configuration
		Table table = detailsTableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		//tableViewer.setContentProvider(new ObservableListContentProvider());
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		detailsTableViewer.getControl().setLayoutData(gridData);
		
		//Focus listening and selection providing
		getSite().setSelectionProvider(matcherTreeViewer);

		initFileListener();
		initPatternsViewerWithGeneratedPatterns();
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
				tableViewerUtil.prepareTableViewerForMatcherConfiguration(observableMatcher, detailsTableViewer);
				String patternFqn = CorePatternLanguageHelper.getFullyQualifiedName(observableMatcher.getMatcher().getPattern());
				List<PatternComponent> components = patternsViewerInput.find(patternFqn);
				if (components != null) {
					patternsTreeViewer.setSelection(new TreeSelection(new TreePath(components.toArray())));
				}
			}
			else if (value instanceof ObservablePatternMatch) {
				ObservablePatternMatch match = (ObservablePatternMatch) value;
				tableViewerUtil.prepareTableViewerForObservableInput(match, detailsTableViewer);
			}
			else {
				tableViewerUtil.clearTableViewerColumns(detailsTableViewer);
				clearTableViewer();
			}
		}
	}
	
	private void initPatternsViewerWithGeneratedPatterns() {
		for (Pattern pattern : DatabindingUtil.generatedPatterns) {
			String patternFqn = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
			PatternRegistry.getInstance().addGeneratedPattern(pattern, patternFqn);
			patternsViewerInput.addComponent(patternFqn);
		}
		
		patternsTreeViewer.refresh();
		patternsViewerInput.updateSelection(patternsTreeViewer);
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
		return patternsTreeViewer;
	}
	
	public FlyoutControlComposite getPatternsViewerFlyout() {
		return patternsViewerFlyout;
	}
	
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putInteger("detailsViewFlyoutState", detailsViewerFlyout.getPreferences().getState());
		memento.putInteger("patternsViewerFlyoutState", patternsViewerFlyout.getPreferences().getState());
	}
}
