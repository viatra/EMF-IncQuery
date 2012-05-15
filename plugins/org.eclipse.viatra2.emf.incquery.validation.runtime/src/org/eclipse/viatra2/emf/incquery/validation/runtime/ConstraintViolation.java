package org.eclipse.viatra2.emf.incquery.validation.runtime;

import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.viatra2.emf.incquery.databinding.runtime.DatabindingAdapterUtil;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.runtime.api.impl.BasePatternMatch;

public class ConstraintViolation<T extends IPatternMatch> {

	private IMarker marker;
	private T patternMatch;
	private ConstraintAdapter<T> adapter;
	private String message;
	private ParameterValueChangedListener listener;
	private List<IObservableValue> affectedValues;

	public ConstraintViolation(ConstraintAdapter<T> adapter, T patternMatch) {
		this.patternMatch = patternMatch;
		this.adapter = adapter;
		this.message = adapter.getConstraint().getMessage();
		this.listener = new ParameterValueChangedListener();
		affectedValues = DatabindingAdapterUtil.observeFeatures(patternMatch,
				listener, message);
		initMarker();
	}

	private void updateText(String text) throws CoreException {
		marker.setAttribute(IMarker.MESSAGE, text);
	}

	private void initMarker() {
		EObject location = this.adapter.getConstraint().getLocationObject(
				patternMatch);
		URI uri = location.eResource().getURI();
		IResource markerLoc = null;
		String platformString = uri.toPlatformString(true);
		markerLoc = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(platformString);
		try {
			marker = markerLoc
					.createMarker(EValidator.MARKER);
			marker.setAttribute(IMarker.SEVERITY, this.adapter.getConstraint()
					.getSeverity());
			marker.setAttribute(IMarker.TRANSIENT, true);
			String locationString = String.format("%1$s %2$s", location.eClass().getName(), BasePatternMatch.prettyPrintValue(location));
			marker.setAttribute(IMarker.LOCATION, locationString);
			marker.setAttribute(EValidator.URI_ATTRIBUTE,
					EcoreUtil.getURI(location).toString());
			updateText(DatabindingAdapterUtil.getMessage(patternMatch, message));
		} catch (CoreException e) {
			ValidationRuntimeActivator.getDefault().logException(
					"Cannot create EMF-IncQuery Validation Marker", e);
		}
	}

	private class ParameterValueChangedListener implements IValueChangeListener {
		@Override
		public void handleValueChange(ValueChangeEvent event) {
			try {
				updateText(DatabindingAdapterUtil.getMessage(patternMatch,
						message));
			} catch (CoreException e) {
				ValidationRuntimeActivator.getDefault().logException(
						"Cannot update EMF-IncQuery Validation Marker", e);
			}
		}
	}

	public void dispose() {
		for (IObservableValue val : affectedValues) {
			val.removeValueChangeListener(listener);
		}
		if (marker != null) {
			try {
				marker.delete();
			} catch (CoreException e) {
				ValidationRuntimeActivator.getDefault().logException(
						"Cannot remove EMF-IncQuery Validation Marker", e);
			}
		}
	}
}
