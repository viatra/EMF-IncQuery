/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.databinding.runtime.collection;

import org.eclipse.incquery.emf.incquery.base.itc.alg.incscc.Direction;
import org.eclipse.incquery.runtime.api.IMatcherFactory;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.IncQueryMatcher;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.ActivationState;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.Agenda;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.IAgenda;
import org.eclipse.viatra2.emf.incquery.triggerengine.api.IRule;
import org.eclipse.viatra2.emf.incquery.triggerengine.firing.AutomaticFiringStrategy;

/**
 * Utility class to prepare a rule in an agenda for an observable collection.
 * For use cases, see {@link ObservablePatternMatchSet} and {@link ObservablePatternMatchList}.
 * 
 * @author Abel Hegedus
 *
 */
public class ObservableCollectionHelper {

    /**
     * Constructor hidden for utility class
     */
    private ObservableCollectionHelper() {
        // TODO Auto-generated constructor stub
    }
    
    /**
     * Creates the rule used for updating the results in the given agenda. 
     * 
     * @param observableCollection the observable collection to handle
     * @param factory the {@link IMatcherFactory} used to create the rule
     * @param agenda an existing {@link Agenda} where the rule is created
     */
    public static <Match extends IPatternMatch, Matcher extends IncQueryMatcher<Match>> void createRuleInAgenda(IObservablePatternMatchCollection<Match> observableCollection, IMatcherFactory<Matcher> factory, IAgenda agenda) {
        IRule<Match> rule = agenda.createRule(factory, false, true);
        ObservableCollectionProcessor<Match> insertProc = new ObservableCollectionProcessor<Match>(Direction.INSERT, observableCollection);
        ObservableCollectionProcessor<Match> deleteProc = new ObservableCollectionProcessor<Match>(Direction.DELETE, observableCollection);
        rule.setStateChangeProcessor(ActivationState.APPEARED, insertProc);
        rule.setStateChangeProcessor(ActivationState.DISAPPEARED, deleteProc);
        AutomaticFiringStrategy firingStrategy = new AutomaticFiringStrategy(agenda.newActivationMonitor(true));
        agenda.addUpdateCompleteListener(firingStrategy, true);
    }

}
