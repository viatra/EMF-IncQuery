/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.EcoreModel;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncQueryGenmodel;
import org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.IncquerygenmodelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Inc Query Genmodel</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.viatra2.emf.incquery.model.incquerygenmodel.impl.IncQueryGenmodelImpl#getEcoreModel <em>Ecore Model</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class IncQueryGenmodelImpl extends EObjectImpl implements IncQueryGenmodel {
	/**
	 * The cached value of the '{@link #getEcoreModel() <em>Ecore Model</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEcoreModel()
	 * @generated
	 * @ordered
	 */
	protected EList<EcoreModel> ecoreModel;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected IncQueryGenmodelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return IncquerygenmodelPackage.Literals.INC_QUERY_GENMODEL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EcoreModel> getEcoreModel() {
		if (ecoreModel == null) {
			ecoreModel = new EObjectContainmentEList<EcoreModel>(EcoreModel.class, this, IncquerygenmodelPackage.INC_QUERY_GENMODEL__ECORE_MODEL);
		}
		return ecoreModel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case IncquerygenmodelPackage.INC_QUERY_GENMODEL__ECORE_MODEL:
				return ((InternalEList<?>)getEcoreModel()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case IncquerygenmodelPackage.INC_QUERY_GENMODEL__ECORE_MODEL:
				return getEcoreModel();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case IncquerygenmodelPackage.INC_QUERY_GENMODEL__ECORE_MODEL:
				getEcoreModel().clear();
				getEcoreModel().addAll((Collection<? extends EcoreModel>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case IncquerygenmodelPackage.INC_QUERY_GENMODEL__ECORE_MODEL:
				getEcoreModel().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case IncquerygenmodelPackage.INC_QUERY_GENMODEL__ECORE_MODEL:
				return ecoreModel != null && !ecoreModel.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //IncQueryGenmodelImpl
