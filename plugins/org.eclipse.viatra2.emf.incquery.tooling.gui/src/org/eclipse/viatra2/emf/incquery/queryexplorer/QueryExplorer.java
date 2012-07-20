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
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;
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
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternsViewerFlatContentProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternsViewerFlatLabelProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternsViewerHierarchicalContentProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternsViewerHierarchicalLabelProvider;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.patternsviewer.PatternsViewerInput;
import org.eclipse.viatra2.emf.incquery.queryexplorer.preference.PreferenceConstants;
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

	private static final String PACKAGE_PRESENTATION_STATE = "packagePresentationState";
	private static final String PATTERNS_VIEWER_FLYOUT_STATE = "patternsViewerFlyoutState";
	private static final String DETAILS_VIEW_FLYOUT_STATE = "detailsViewFlyoutState";

	public static final String ID = "org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer";
	
	private TableViewer detailsTableViewer;
	private CheckboxTreeViewer patternsTreeViewer;
	private TreeViewer matcherTreeViewer;
	
	private MatcherContentProvider matcherContentProvider;
	private MatcherLabelProvider matcherLabelProvider;
	private MatcherTreeViewerRoot matcherTreeViewerRoot;
	
	private ModelEditorPartListener modelPartListener;
	public static PatternsViewerInput patternsViewerInput;
	
	private FlyoutControlComposite patternsViewerFlyout;
	private FlyoutControlComposite detailsViewerFlyout;
	
	private IFlyoutPreferences detailsViewerFlyoutPreferences;
	private IFlyoutPreferences patternsViewerFlyoutPreferences;
	
	private PatternsViewerFlatContentProvider flatCP;
	private PatternsViewerFlatLabelProvider flatLP;
	private PatternsViewerHierarchicalContentProvider hierarchicalCP;
	private PatternsViewerHierarchicalLabelProvider hierarchicalLP;
	
	@Inject
	private Injector injector;
	
	@Inject
	private TableViewerUtil tableViewerUtil;
	
	private String mementoPackagePresentation = "flat";
	
	public QueryExplorer() {
		matcherContentProvider = new MatcherContentProvider();
		matcherLabelProvider = new MatcherLabelProvider();
		matcherTreeViewerRoot = new MatcherTreeViewerRoot();
		modelPartListener = new ModelEditorPartListener();
		patternsViewerInput = new PatternsViewerInput();
		flatCP = new PatternsViewerFlatContentProvider();
		hierarchicalCP = new PatternsViewerHierarchicalContentProvider();
		hierarchicalLP = new PatternsViewerHierarchicalLabelProvider(patternsViewerInput);
		flatLP = new PatternsViewerFlatLabelProvider(patternsViewerInput);
	}
	
	public MatcherTreeViewerRoot getMatcherTreeViewerRoot() {
		return matcherTreeViewerRoot;
	}
	
	public static QueryExplorer getInstance() {
		//In Juno activeWorkbenchWindow will be null when Eclipse is closing
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (activeWorkbenchWindow != null && activeWorkbenchWindow.getActivePage() != null) {
			return (QueryExplorer) activeWorkbenchWindow.getActivePage().findView(ID);
		}
		return null;
	}
	
	public TreeViewer getMatcherTreeViewer() {
		return matcherTreeViewer;
	}
	
	public PatternsViewerFlatContentProvider getFlatContentProvider() {
		return flatCP;
	}
	
	public PatternsViewerFlatLabelProvider getFlatLabelProvider() {
		return flatLP;
	}
	
	public PatternsViewerHierarchicalContentProvider getHierarchicalContentProvider() {
		return hierarchicalCP;
	}
	
	public PatternsViewerHierarchicalLabelProvider getHierarchicalLabelProvider() {
		return hierarchicalLP;
	}
	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		int detailsState = IFlyoutPreferences.STATE_OPEN;
		int patternsState = IFlyoutPreferences.STATE_COLLAPSED;
		if (memento != null) {
			if (memento.getInteger(DETAILS_VIEW_FLYOUT_STATE) != null) {
				detailsState = memento.getInteger(DETAILS_VIEW_FLYOUT_STATE);
			}
			if (memento.getInteger(PATTERNS_VIEWER_FLYOUT_STATE) != null) {
				patternsState = memento.getInteger(DETAILS_VIEW_FLYOUT_STATE);
			}
			if (memento.getString(PACKAGE_PRESENTATION_STATE) != null) {
				mementoPackagePresentation = memento.getString(PACKAGE_PRESENTATION_STATE);
			}
		}
		detailsViewerFlyoutPreferences = new FlyoutPreferences(IFlyoutPreferences.DOCK_EAST, detailsState, 300);
		patternsViewerFlyoutPreferences = new FlyoutPreferences(IFlyoutPreferences.DOCK_WEST, patternsState, 100);
		
		IPreferenceStore preferenceStore = IncQueryGUIPlugin.getDefault().getPreferenceStore();
		if (preferenceStore.contains(PreferenceConstants.WILDCARD_MODE)) {
			preferenceStore.setValue(PreferenceConstants.WILDCARD_MODE, true);
		}
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
//		patternsTreeViewer.setContentProvider(flatCP);
//		patternsTreeViewer.setLabelProvider(flatLP);
		setPackagePresentation(mementoPackagePresentation, false);
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
				if (observableMatcher.getMatcher() != null) {
					tableViewerUtil.prepareTableViewerForMatcherConfiguration(observableMatcher, detailsTableViewer);
					String patternFqn = CorePatternLanguageHelper.getFullyQualifiedName(observableMatcher.getMatcher().getPattern());
					Pattern pattern = PatternRegistry.getInstance().getPatternByFqn(patternFqn);
					List<PatternComponent> components = null;
					if (PatternRegistry.getInstance().isGenerated(pattern)) {
						components = patternsViewerInput.getGeneratedPatternsRoot().find(patternFqn);
						components.add(0, patternsViewerInput.getGeneratedPatternsRoot());
					}
					else {
						components = patternsViewerInput.getGenericPatternsRoot().find(patternFqn);
						components.add(0, patternsViewerInput.getGenericPatternsRoot());
					}
					
					if (components != null) {
						patternsTreeViewer.setSelection(new TreeSelection(new TreePath(components.toArray())));
					}
				}
				else {
					clearTableViewer();
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
		for (Pattern pattern : DatabindingUtil.getGeneratedPatterns()) {
			String patternFqn = CorePatternLanguageHelper.getFullyQualifiedName(pattern);
			PatternRegistry.getInstance().addGeneratedPattern(pattern, patternFqn);
			PatternRegistry.getInstance().addActivePattern(pattern);
			patternsViewerInput.getGeneratedPatternsRoot().addComponent(patternFqn);
		}
		
		patternsTreeViewer.refresh();
		patternsViewerInput.getGeneratedPatternsRoot().updateSelection(patternsTreeViewer);
	}
	
	private void initFileListener() {
		IResourceChangeListener listener = new ResourceChangeListener(injector);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.PRE_BUILD);
	}
	
	public ModelEditorPartListener getModelPartListener() {
		return modelPartListener;
	}
	
	public PatternsViewerInput getPatternsViewerInput() {
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
		memento.putInteger(DETAILS_VIEW_FLYOUT_STATE, detailsViewerFlyout.getPreferences().getState());
		memento.putInteger(PATTERNS_VIEWER_FLYOUT_STATE, patternsViewerFlyout.getPreferences().getState());
		memento.putString(PACKAGE_PRESENTATION_STATE, (patternsTreeViewer.getContentProvider() == flatCP) ? "flat" : "hierarchical");
	}
	
	public void setPackagePresentation(String command, boolean update) {
		
		if (command.contains("flat")) {
			patternsTreeViewer.setContentProvider(flatCP);
			patternsTreeViewer.setLabelProvider(flatLP);
		}
		else {
			patternsTreeViewer.setContentProvider(hierarchicalCP);
			patternsTreeViewer.setLabelProvider(hierarchicalLP);
		}
		
		if (update) {
			patternsViewerInput.getGeneratedPatternsRoot().updateSelection(patternsTreeViewer);
			patternsViewerInput.getGenericPatternsRoot().updateSelection(patternsTreeViewer);
		}
	}
}
