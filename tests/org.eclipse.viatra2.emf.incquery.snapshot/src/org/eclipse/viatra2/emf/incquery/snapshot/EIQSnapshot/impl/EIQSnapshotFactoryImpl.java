/*******************************************************************************
 * Copyright (c) 2010-2012, Gabor Bergmann, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann, Abel Hegedus - initial API and implementation
 *******************************************************************************/

/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EIQSnapshotFactoryImpl extends EFactoryImpl implements EIQSnapshotFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static EIQSnapshotFactory init() {
		try {
			EIQSnapshotFactory theEIQSnapshotFactory = (EIQSnapshotFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/viatra2/emf/incquery/snapshot"); 
			if (theEIQSnapshotFactory != null) {
				return theEIQSnapshotFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new EIQSnapshotFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EIQSnapshotFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case EIQSnapshotPackage.MATCH_SET_RECORD: return createMatchSetRecord();
			case EIQSnapshotPackage.MATCH_RECORD: return createMatchRecord();
			case EIQSnapshotPackage.EMF_SUBSTITUTION: return createEMFSubstitution();
			case EIQSnapshotPackage.INT_SUBSTITUTION: return createIntSubstitution();
			case EIQSnapshotPackage.LONG_SUBSTITUTION: return createLongSubstitution();
			case EIQSnapshotPackage.DOUBLE_SUBSTITUTION: return createDoubleSubstitution();
			case EIQSnapshotPackage.FLOAT_SUBSTITUTION: return createFloatSubstitution();
			case EIQSnapshotPackage.BOOLEAN_SUBSTITUTION: return createBooleanSubstitution();
			case EIQSnapshotPackage.STRING_SUBSTITUTION: return createStringSubstitution();
			case EIQSnapshotPackage.DATE_SUBSTITUTION: return createDateSubstitution();
			case EIQSnapshotPackage.ENUM_SUBSTITUTION: return createEnumSubstitution();
			case EIQSnapshotPackage.MISCELLANEOUS_SUBSTITUTION: return createMiscellaneousSubstitution();
			case EIQSnapshotPackage.INC_QUERY_SNAPSHOT: return createIncQuerySnapshot();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case EIQSnapshotPackage.INPUT_SPECIFICATION:
				return createInputSpecificationFromString(eDataType, initialValue);
			case EIQSnapshotPackage.RECORD_ROLE:
				return createRecordRoleFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case EIQSnapshotPackage.INPUT_SPECIFICATION:
				return convertInputSpecificationToString(eDataType, instanceValue);
			case EIQSnapshotPackage.RECORD_ROLE:
				return convertRecordRoleToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MatchSetRecord createMatchSetRecord() {
		MatchSetRecordImpl matchSetRecord = new MatchSetRecordImpl();
		return matchSetRecord;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MatchRecord createMatchRecord() {
		MatchRecordImpl matchRecord = new MatchRecordImpl();
		return matchRecord;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EMFSubstitution createEMFSubstitution() {
		EMFSubstitutionImpl emfSubstitution = new EMFSubstitutionImpl();
		return emfSubstitution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IntSubstitution createIntSubstitution() {
		IntSubstitutionImpl intSubstitution = new IntSubstitutionImpl();
		return intSubstitution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LongSubstitution createLongSubstitution() {
		LongSubstitutionImpl longSubstitution = new LongSubstitutionImpl();
		return longSubstitution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DoubleSubstitution createDoubleSubstitution() {
		DoubleSubstitutionImpl doubleSubstitution = new DoubleSubstitutionImpl();
		return doubleSubstitution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FloatSubstitution createFloatSubstitution() {
		FloatSubstitutionImpl floatSubstitution = new FloatSubstitutionImpl();
		return floatSubstitution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BooleanSubstitution createBooleanSubstitution() {
		BooleanSubstitutionImpl booleanSubstitution = new BooleanSubstitutionImpl();
		return booleanSubstitution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public StringSubstitution createStringSubstitution() {
		StringSubstitutionImpl stringSubstitution = new StringSubstitutionImpl();
		return stringSubstitution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DateSubstitution createDateSubstitution() {
		DateSubstitutionImpl dateSubstitution = new DateSubstitutionImpl();
		return dateSubstitution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EnumSubstitution createEnumSubstitution() {
		EnumSubstitutionImpl enumSubstitution = new EnumSubstitutionImpl();
		return enumSubstitution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MiscellaneousSubstitution createMiscellaneousSubstitution() {
		MiscellaneousSubstitutionImpl miscellaneousSubstitution = new MiscellaneousSubstitutionImpl();
		return miscellaneousSubstitution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IncQuerySnapshot createIncQuerySnapshot() {
		IncQuerySnapshotImpl incQuerySnapshot = new IncQuerySnapshotImpl();
		return incQuerySnapshot;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InputSpecification createInputSpecificationFromString(EDataType eDataType, String initialValue) {
		InputSpecification result = InputSpecification.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertInputSpecificationToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RecordRole createRecordRoleFromString(EDataType eDataType, String initialValue) {
		RecordRole result = RecordRole.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertRecordRoleToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EIQSnapshotPackage getEIQSnapshotPackage() {
		return (EIQSnapshotPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static EIQSnapshotPackage getPackage() {
		return EIQSnapshotPackage.eINSTANCE;
	}

} //EIQSnapshotFactoryImpl
