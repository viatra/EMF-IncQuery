package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.typeinference.classconstraininpatternbody.ClassConstrainInPatternBodyMatch;
import org.eclipse.viatra2.emf.incquery.typeinference.classconstraininpatternbody.ClassConstrainInPatternBodyMatcher;
import org.eclipse.viatra2.emf.incquery.typeinference.toogeneraltypeofpatternparameterreason2.TooGeneralTypeOfPatternParameterReason2Match;
import org.eclipse.viatra2.emf.incquery.typeinference.toogeneraltypeofpatternparameterreason2.TooGeneralTypeOfPatternParameterReason2Matcher;
import org.eclipse.viatra2.emf.incquery.typeinference.unsatisfiabletypeconstraininpatternbodyreason2.UnsatisfiableTypeConstrainInPatternBodyReason2Match;
import org.eclipse.viatra2.emf.incquery.typeinference.unsatisfiabletypeconstraininpatternbodyreason2.UnsatisfiableTypeConstrainInPatternBodyReason2Matcher;
import org.eclipse.viatra2.emf.incquery.typeinference.unsatisfiabletypeconstraininpatternbodyreason3.UnsatisfiableTypeConstrainInPatternBodyReason3Match;
import org.eclipse.viatra2.emf.incquery.typeinference.unsatisfiabletypeconstraininpatternbodyreason3.UnsatisfiableTypeConstrainInPatternBodyReason3Matcher;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

public class TypeAnalysisErrorReason extends QueryAnalisys{
	UnsatisfiableTypeConstrainInPatternBodyReason2Matcher unsatisfiableTypeConstrainInPatternBodyReason2Matcher;
	UnsatisfiableTypeConstrainInPatternBodyReason3Matcher unsatisfiableTypeConstrainInPatternBodyReason3Matcher;
	ClassConstrainInPatternBodyMatcher classConstrainInPatternBodyMatcher;
	TooGeneralTypeOfPatternParameterReason2Matcher tooGeneralTypeOfPatternParameterReason2Matcher;
	
	Map<VariableInBody,List<TypeReason<Object>>> unsatisfiableTypeConstrainInPatternBodyReason2 = new HashMap<VariableInBody, List<TypeReason<Object>>>();
	Map<VariableInBody,List<TypeReason<Object>>> unsatisfiableTypeConstrainInPatternBodyReason3 = new HashMap<VariableInBody, List<TypeReason<Object>>>();
	Map<VariableInBody,Set<TypeReason<Object>>> classConstrainInPatternBody = new HashMap<VariableInBody, Set<TypeReason<Object>>>();
	Map<Variable,List<TypeReason<PatternBody>>> tooGeneralTypeOfPatternParameterReason2 = new HashMap<Variable, List<TypeReason<PatternBody>>>();
	
	public TypeAnalysisErrorReason(PatternModel patternModel) throws TypeAnalysisException {
		super(patternModel);
		
	}
	
	@Override
	protected void initMatchers() throws TypeAnalysisException {
		try {
			this.unsatisfiableTypeConstrainInPatternBodyReason2Matcher = new UnsatisfiableTypeConstrainInPatternBodyReason2Matcher(resourceSet);
			this.unsatisfiableTypeConstrainInPatternBodyReason3Matcher = new UnsatisfiableTypeConstrainInPatternBodyReason3Matcher(resourceSet);
			this.classConstrainInPatternBodyMatcher = new ClassConstrainInPatternBodyMatcher(resourceSet);
			this.tooGeneralTypeOfPatternParameterReason2Matcher = new TooGeneralTypeOfPatternParameterReason2Matcher(resourceSet);
		} catch (IncQueryException e) {
			throw new TypeAnalysisException("The matchers can not be created.");
		}
	}

	@Override
	protected void getMaches() {
		this.unsatisfiableTypeConstrainInPatternBodyReason2.clear();
		this.unsatisfiableTypeConstrainInPatternBodyReason3.clear();
		this.classConstrainInPatternBody.clear();
		this.tooGeneralTypeOfPatternParameterReason2.clear();
		
		UnsatisfiableTypeConstrainInPatternBodyReason2Match result2 = this.unsatisfiableTypeConstrainInPatternBodyReason2Matcher.getOneArbitraryMatch();
		LinkedList<TypeReason<Object>> reasons2 = new LinkedList<TypeReason<Object>>();
		reasons2.add(new TypeReason<Object>(result2.getFrom1(), result2.getType1()));
		reasons2.add(new TypeReason<Object>(result2.getFrom2(), result2.getType2()));
		this.unsatisfiableTypeConstrainInPatternBodyReason2.put(
			new VariableInBody(result2.getVariable(), result2.getBody()),reasons2);

		UnsatisfiableTypeConstrainInPatternBodyReason3Match result3 = this.unsatisfiableTypeConstrainInPatternBodyReason3Matcher.getOneArbitraryMatch();
		LinkedList<TypeReason<Object>> reasons3 = new LinkedList<TypeReason<Object>>();
		reasons3.add(new TypeReason<Object>(result3.getFrom1(), result3.getType1()));
		reasons3.add(new TypeReason<Object>(result3.getFrom2(), result3.getType2()));
		reasons3.add(new TypeReason<Object>(result3.getFrom3(), result3.getType3()));
		this.unsatisfiableTypeConstrainInPatternBodyReason3.put(
			new VariableInBody(result3.getVariable(), result3.getBody()),reasons3);
		
		for(ClassConstrainInPatternBodyMatch result : this.classConstrainInPatternBodyMatcher.getAllMatches())
		{
			VariableInBody v = new VariableInBody(result.getVariable(), result.getBody());
			Set<TypeReason<Object>> reasonSet;
			if(this.classConstrainInPatternBody.containsKey(v))
				reasonSet = this.classConstrainInPatternBody.get(v);
			else
			{
				reasonSet = new TreeSet<TypeReason<Object>>(new TypeReasonCompare<Object>());
				classConstrainInPatternBody.put(v, reasonSet);
			}
			reasonSet.add(new TypeReason<Object>(result.getFrom(), result.getValueOfClass()));
		}

		//+getReason
		//+minden változóhoz max 1-et kell bechace-elni
	}

	@Override
	protected void releaseMatchers() {
		this.unsatisfiableTypeConstrainInPatternBodyReason2Matcher.getEngine().dispose();
		this.unsatisfiableTypeConstrainInPatternBodyReason3Matcher.getEngine().dispose();
		this.classConstrainInPatternBodyMatcher.getEngine().dispose();
		this.tooGeneralTypeOfPatternParameterReason2Matcher.getEngine().dispose();
	}
	
	public List<TypeReason<Object>> get2ReasonOftUnsatisfiabilityOfVariableInBody(PatternBody body, Variable variable) {
		Collection<UnsatisfiableTypeConstrainInPatternBodyReason2Match> result2 =
			this.unsatisfiableTypeConstrainInPatternBodyReason2Matcher.getAllMatches(null, body, variable, null, null, null, null);
		if(!result2.isEmpty())
		{
			UnsatisfiableTypeConstrainInPatternBodyReason2Match aReason =
				this.getOne(result2);
			List<TypeReason<Object>> reasons = new LinkedList<TypeReason<Object>>();
			reasons.add(new TypeReason<Object>(aReason.getFrom1(), aReason.getType1()));
			reasons.add(new TypeReason<Object>(aReason.getFrom2(), aReason.getType2()));
			return reasons;
		}
		else return null;
	}
	
	public List<TypeReason<Object>> get3ReasonOftUnsatisfiabilityOfVariableInBody(PatternBody body, Variable variable) {
		Collection<UnsatisfiableTypeConstrainInPatternBodyReason3Match> result3 =
				this.unsatisfiableTypeConstrainInPatternBodyReason3Matcher.getAllMatches(null, body, variable, null, null, null, null, null, null);
		if(!result3.isEmpty())
		{
			List<TypeReason<Object>> reasons = new LinkedList<TypeReason<Object>>();
			UnsatisfiableTypeConstrainInPatternBodyReason3Match aReason =
					this.getOne(result3);
			reasons.add(new TypeReason<Object>(aReason.getFrom1(), aReason.getType1()));
			reasons.add(new TypeReason<Object>(aReason.getFrom2(), aReason.getType2()));
			reasons.add(new TypeReason<Object>(aReason.getFrom3(), aReason.getType3()));
			return reasons;
		}
		else return null;
	}
	
	public Set<TypeReason<Object>> getNReasonOftUnsatisfiabilityOfVariableInBody(PatternBody body, Variable variable) throws TypeAnalysisException {
		Collection<ClassConstrainInPatternBodyMatch> resultN =
				this.classConstrainInPatternBodyMatcher.getAllMatches(null,body,variable,null,null);
		if(!resultN.isEmpty())
		{
			Set<TypeReason<Object>> reasonSet = new TreeSet<TypeReason<Object>>(new TypeReasonCompare<Object>());
			for(ClassConstrainInPatternBodyMatch aReason : resultN)
				reasonSet.add(new TypeReason<Object>(aReason.getFrom(), aReason.getValueOfClass()));
			return reasonSet;
		}
		else throw new TypeAnalysisException("There is no reason to " + variable.getName() + " be unsatisfaible by type constraint.");
	}
	
	public List<TypeReason<PatternBody>> getReasonOfTooGeneralParameter(Pattern pattern, Variable parameter)
	{
		TooGeneralTypeOfPatternParameterReason2Match match =
			this.tooGeneralTypeOfPatternParameterReason2Matcher.getOneArbitraryMatch(pattern, parameter, null, null, null, null);
		if(match!=null)
		{
			List<TypeReason<PatternBody>> ret = new LinkedList<TypeReason<PatternBody>>();
			ret.add(new TypeReason<PatternBody>(match.getFrom1(), match.getType1()));
			ret.add(new TypeReason<PatternBody>(match.getFrom2(), match.getType2()));
			return ret;
		}
		else return null;
	}
}
