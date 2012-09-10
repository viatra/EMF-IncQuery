package org.eclipse.viatra2.emf.incquery.triggerengine.specific;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.emf.incquery.triggerengine.Activation;

public class RecordingActivation<MatchType extends IPatternMatch> extends Activation<MatchType> {

	private RecordingRule<MatchType> rule;
	
	public RecordingActivation(RecordingRule<MatchType> rule, MatchType patternMatch, boolean allowMultipleFiring) {
		super(rule, patternMatch, allowMultipleFiring);
		this.rule = rule;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		else if (obj == null || this.getClass() != obj.getClass()) {
			return false;
		}
		
		RecordingActivation<MatchType> oA = (RecordingActivation<MatchType>) obj;
		if (oA.patternMatch.equals(this.patternMatch) && oA.rule.equals(this.rule)) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + rule.hashCode();
		result = prime * result + patternMatch.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "[Rule: "+rule+"][Match: "+patternMatch+"][State: "+state+"][Fired: "+fired+"]";
	}

	
	/**
	 * Fires the activation and records the EMF model manipulations within a {@link RecordingCommand}.
	 * This way the model manipulations can be re- or undone.
	 * 
	 * Note that, if the {@link RecordingRule} is not registered on a {@link TransactionalEditingDomain} 
	 * then the model manipulation will not be recorded and null will be returned. 
	 * 
	 * @return the {@link RecordingCommand} which recorded the model manipulations during firing or null if recording was not possible
	 */
	public RecordingCommand fireWithRecording() {
		TransactionalEditingDomain domain = this.rule.getEditingDomain();
		if (domain == null) {
			fire();
			return null;
		}
		else {
			final RecordingCommand command = new RecordingCommand(domain) {
				@Override
				protected void doExecute() {
					fire();
				}
			};
			domain.getCommandStack().execute(command);
						
			return command;
		}
	}
}
