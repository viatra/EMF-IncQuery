///*******************************************************************************
// * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *    Gabor Bergmann - initial API and implementation
// *******************************************************************************/
//
//package org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.psystem;
//
///**
// * A constraint that can incorporate other constraints (probably of the same type) and enforce them together
// * @author Bergmann Gábor
// *
// */
//public interface IFoldablePConstraint extends PConstraint {
//	/**
//	 * Attempts to incorporate other into self.
//	 * Does NOT call other.registerIncorporatationInto(this);
//	 * 
//	 * @param other another valid IFoldablePConstraint
//	 * @return true if other constraint was compatible and could be incorporated in this one.
//	 * 
//	 */
//	public boolean incorporate(IFoldablePConstraint other);
//	
//	/**
//	 * Registers that this constraint was incorporated into another.
//	 * NOT called by incorporator.incorporate(this);
//	 * 
//	 * @param incorporator
//	 */
//	public void registerIncorporatationInto(IFoldablePConstraint incorporator);
//	
//	/**
//	 * @return the pConstraint self was incorporated into, or null if there was no such.
//	 */
//	public IFoldablePConstraint getIncorporator();
//}
