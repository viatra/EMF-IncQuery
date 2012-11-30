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

package org.eclipse.incquery.tooling.ui.queryexplorer.content.detail;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.incquery.databinding.runtime.adapter.DatabindingAdapter;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.Variable;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.ObservablePatternMatch;
import org.eclipse.incquery.tooling.ui.queryexplorer.content.matcher.ObservablePatternMatcher;
import org.eclipse.incquery.tooling.ui.queryexplorer.util.DatabindingUtil;
import org.eclipse.incquery.tooling.ui.queryexplorer.util.PatternRegistry;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.xbase.typing.ITypeProvider;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@SuppressWarnings("restriction")
@Singleton
public class TableViewerUtil {

    @Inject
    private ITypeProvider typeProvider;

    @Inject
    private Injector injector;

    private final Set<String> primitiveTypes;

    protected TableViewerUtil() {
        primitiveTypes = new HashSet<String>();
        primitiveTypes.add(Boolean.class.getName());
        primitiveTypes.add(Character.class.getName());
        primitiveTypes.add(Byte.class.getName());
        primitiveTypes.add(Short.class.getName());
        primitiveTypes.add(Integer.class.getName());
        primitiveTypes.add(Long.class.getName());
        primitiveTypes.add(Float.class.getName());
        primitiveTypes.add(Double.class.getName());
        primitiveTypes.add(Void.class.getName());
        primitiveTypes.add(String.class.getName());
    }

    public boolean isPrimitiveType(String fqn) {
        return primitiveTypes.contains(fqn);
    }

    public void prepareTableViewerForObservableInput(final ObservablePatternMatch match, TableViewer viewer) {
        clearTableViewerColumns(viewer);
        String[] titles = { "Parameter", "Value" };
        createColumns(viewer, titles);
        viewer.setUseHashlookup(true);
        viewer.setColumnProperties(titles);
        viewer.setContentProvider(new ObservableListContentProvider());
        viewer.setLabelProvider(new DetailElementLabelProvider());
        viewer.setCellModifier(new DetailElementCellModifier());
        viewer.setComparator(new ViewerComparator(new DetailComparator(match.getPatternMatch().parameterNames())));

        DatabindingAdapter<IPatternMatch> databindableMatcher = DatabindingUtil.getDatabindingAdapter(match
                .getPatternMatch().patternName(), match.getParent().isGenerated());

        if (databindableMatcher == null) {
            viewer.setInput(null);
        } else {
            DetailObserver observer = new DetailObserver(databindableMatcher, match);
            viewer.setInput(observer);
        }
    }

    public void prepareTableViewerForMatcherConfiguration(ObservablePatternMatcher observableMatcher, TableViewer viewer) {
        clearTableViewerColumns(viewer);
        String[] titles = { "Parameter", "Filter", "Class" };
        createColumns(viewer, titles);
        viewer.setUseHashlookup(true);
        viewer.setColumnProperties(titles);
        viewer.setContentProvider(new MatcherConfigurationContentProvider());
        viewer.setLabelProvider(new MatcherConfigurationLabelProvider());
        viewer.setCellModifier(new MatcherConfigurationCellModifier(viewer));
        viewer.setComparator(new ViewerComparator(new DetailComparator(observableMatcher.getMatcher()
                .getParameterNames())));

        Table table = viewer.getTable();
        CellEditor[] editors = new CellEditor[titles.length];

        editors[0] = new TextCellEditor(table);
        ModelElementCellEditor cellEditor = new ModelElementCellEditor(table, observableMatcher);
        injector.injectMembers(cellEditor);
        editors[1] = cellEditor;
        editors[2] = new TextCellEditor(table);

        viewer.setCellEditors(editors);

        Pattern pattern = PatternRegistry.getInstance().getPatternByFqn(observableMatcher.getPatternName());
        Object[] filter = observableMatcher.getFilter();
        MatcherConfiguration[] input = new MatcherConfiguration[pattern.getParameters().size()];
        if (filter != null) {
            for (int i = 0; i < pattern.getParameters().size(); i++) {
                Variable var = pattern.getParameters().get(i);
                String name = var.getName();
                JvmTypeReference ref = typeProvider.getTypeForIdentifiable(var);
                String clazz = (ref == null) ? "" : ref.getType().getQualifiedName();
                input[i] = new MatcherConfiguration(name, clazz, filter[i]);
            }
            viewer.setInput(input);
        }
    }

    public void clearTableViewerColumns(TableViewer viewer) {

        if (viewer.getContentProvider() != null) {
            viewer.setInput(null);
        }
        while (viewer.getTable().getColumnCount() > 0) {
            viewer.getTable().getColumns()[0].dispose();
        }

        viewer.refresh();
    }

    private void createColumns(TableViewer viewer, String[] titles) {
        for (int i = 0; i < titles.length; i++) {
            createTableViewerColumn(viewer, titles[i], i);
        }
    }

    private TableViewerColumn createTableViewerColumn(TableViewer viewer, String title, int index) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE, index);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setResizable(true);
        column.setMoveable(true);
        column.setWidth(150);
        return viewerColumn;
    }

    public Object createValue(String classFqn, Object value) {
        if (!(value instanceof String)) {
            return value;
        } else {
            classFqn = classFqn.toLowerCase();
            String strValue = value.toString();

            if (strValue.matches("")) {
                return null;
            } else if (Boolean.class.getName().toLowerCase().matches(classFqn)) {
                return Boolean.valueOf(strValue.toLowerCase());
            } else if (Character.class.getName().toLowerCase().matches(classFqn)) {
                return Character.valueOf(strValue.charAt(0));
            } else if (Byte.class.getName().toLowerCase().matches(classFqn)) {
                return Byte.valueOf(strValue);
            } else if (Short.class.getName().toLowerCase().matches(classFqn)) {
                return Short.valueOf(strValue);
            } else if (Integer.class.getName().toLowerCase().matches(classFqn)) {
                return Integer.valueOf(strValue);
            } else if (Long.class.getName().toLowerCase().matches(classFqn)) {
                return Long.valueOf(strValue);
            } else if (Float.class.getName().toLowerCase().matches(classFqn)) {
                return Float.valueOf(strValue);
            } else if (Double.class.getName().toLowerCase().matches(classFqn)) {
                return Double.valueOf(strValue);
            } else if (String.class.getName().toLowerCase().matches(classFqn)) {
                return value;
            } else {
                return null;
            }
        }
    }

    public boolean isValidValue(String classFqn, String value) {
        classFqn = classFqn.toLowerCase();

        if (Boolean.class.getName().toLowerCase().matches(classFqn)) {
            if (value.toLowerCase().matches("true") || value.toLowerCase().matches("false")) {
                return true;
            } else {
                return false;
            }
        } else if (Character.class.getName().toLowerCase().matches(classFqn)) {
            return true;
        } else if (Byte.class.getName().toLowerCase().matches(classFqn)
                || Short.class.getName().toLowerCase().matches(classFqn)
                || Integer.class.getName().toLowerCase().matches(classFqn)
                || Long.class.getName().toLowerCase().matches(classFqn)) {
            return value.matches("[0-9]*");
        } else if (Float.class.getName().toLowerCase().matches(classFqn)
                || Double.class.getName().toLowerCase().matches(classFqn)) {
            return value.matches("[0-9]*\\.?[0-9]*");
        } else if (String.class.getName().toLowerCase().matches(classFqn)) {
            return true;
        } else {
            return true;
        }
    }
}
