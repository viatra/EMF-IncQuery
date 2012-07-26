package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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
	
	public TypeAnalysisErrorReason(PatternModel patternModel) throws TypeAnalysisException {
		super(patternModel);
		try {
			this.unsatisfiableTypeConstrainInPatternBodyReason2Matcher = new UnsatisfiableTypeConstrainInPatternBodyReason2Matcher(resourceSet);
			this.unsatisfiableTypeConstrainInPatternBodyReason3Matcher = new UnsatisfiableTypeConstrainInPatternBodyReason3Matcher(resourceSet);
			this.classConstrainInPatternBodyMatcher = new ClassConstrainInPatternBodyMatcher(resourceSet);
			this.tooGeneralTypeOfPatternParameterReason2Matcher = new TooGeneralTypeOfPatternParameterReason2Matcher(resourceSet);
		} catch (IncQueryException e) {
			throw new TypeAnalysisException("The matchers can not be created.");
		}
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
