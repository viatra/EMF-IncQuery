package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

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
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;

public class TypeAnalysisErrorReason extends QueryAnalysisOnPattern{
	UnsatisfiableTypeConstrainInPatternBodyReason2Matcher unsatisfiableTypeConstrainInPatternBodyReason2Matcher;
	UnsatisfiableTypeConstrainInPatternBodyReason3Matcher unsatisfiableTypeConstrainInPatternBodyReason3Matcher;
	ClassConstrainInPatternBodyMatcher classConstrainInPatternBodyMatcher;
	TooGeneralTypeOfPatternParameterReason2Matcher tooGeneralTypeOfPatternParameterReason2Matcher;
	
	Map<String, List<TypeReason<Object>>> unsatisfiableTypeConstrainInPatternBodyReason2 = new HashMap<String, List<TypeReason<Object>>>();
	Map<String, List<TypeReason<Object>>> unsatisfiableTypeConstrainInPatternBodyReason3 = new HashMap<String, List<TypeReason<Object>>>();
	Map<String, Set<TypeReason<Object>>> classConstrainInPatternBody = new HashMap<String, Set<TypeReason<Object>>>();
	Map<String, List<TypeReason<PatternBody>>> tooGeneralTypeOfPatternParameterReason2 = new HashMap<String, List<TypeReason<PatternBody>>>();
	
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
		
		for(UnsatisfiableTypeConstrainInPatternBodyReason2Match result2 : this.unsatisfiableTypeConstrainInPatternBodyReason2Matcher.getAllMatches())
		{
			String id = this.getVariableInBodyID(result2.getVariable(), result2.getBody());
			if(!this.unsatisfiableTypeConstrainInPatternBodyReason2.containsKey(id)) {
				List<TypeReason<Object>> reasons2 = new LinkedList<TypeReason<Object>>();
				reasons2.add(new TypeReason<Object>(result2.getFrom1(), result2.getType1()));
				reasons2.add(new TypeReason<Object>(result2.getFrom2(), result2.getType2()));
				this.unsatisfiableTypeConstrainInPatternBodyReason2.put(id,reasons2);
			}
		}

		for(UnsatisfiableTypeConstrainInPatternBodyReason3Match result3 : this.unsatisfiableTypeConstrainInPatternBodyReason3Matcher.getAllMatches())
		{
			String id = this.getVariableInBodyID(result3.getVariable(), result3.getBody());
			if(!this.unsatisfiableTypeConstrainInPatternBodyReason3.containsKey(id))
			{
				List<TypeReason<Object>> reasons3 = new LinkedList<TypeReason<Object>>();
				reasons3.add(new TypeReason<Object>(result3.getFrom1(), result3.getType1()));
				reasons3.add(new TypeReason<Object>(result3.getFrom2(), result3.getType2()));
				reasons3.add(new TypeReason<Object>(result3.getFrom3(), result3.getType3()));
				this.unsatisfiableTypeConstrainInPatternBodyReason3.put(id,reasons3);
			}
		}
		
		for(ClassConstrainInPatternBodyMatch result : this.classConstrainInPatternBodyMatcher.getAllMatches())
		{
			String id = this.getVariableInBodyID(result.getVariable(), result.getBody());
			Set<TypeReason<Object>> reasons;
			if(this.classConstrainInPatternBody.containsKey(id))
				reasons = this.classConstrainInPatternBody.get(id);
			else
			{
				reasons = new TreeSet<TypeReason<Object>>(new TypeReasonCompare<Object>());
				this.classConstrainInPatternBody.put(id, reasons);
			}
			reasons.add(new TypeReason<Object>(result.getFrom(), result.getValueOfClass()));
		}
		
		for(TooGeneralTypeOfPatternParameterReason2Match result : this.tooGeneralTypeOfPatternParameterReason2Matcher.getAllMatches())
		{
			String id = this.getParameterID(result.getVariable());
			if(this.tooGeneralTypeOfPatternParameterReason2.containsKey(id))
			{
				List<TypeReason<PatternBody>> reasons = new LinkedList<TypeReason<PatternBody>>();
				reasons.add(new TypeReason<PatternBody>(result.getFrom1(), result.getType1()));
				reasons.add(new TypeReason<PatternBody>(result.getFrom2(), result.getType2()));
				this.tooGeneralTypeOfPatternParameterReason2.put(id, reasons);
			}
		}
	}

	@Override
	protected void releaseMatchers() {
		this.unsatisfiableTypeConstrainInPatternBodyReason2Matcher.getEngine().dispose();
		this.unsatisfiableTypeConstrainInPatternBodyReason3Matcher.getEngine().dispose();
		this.classConstrainInPatternBodyMatcher.getEngine().dispose();
		this.tooGeneralTypeOfPatternParameterReason2Matcher.getEngine().dispose();
	}
	
	public synchronized List<TypeReason<Object>> get2ReasonOftUnsatisfiabilityOfVariableInBody(PatternBody body, Variable variable) throws TypeAnalysisException {
		this.validateCache();
		return this.unsatisfiableTypeConstrainInPatternBodyReason2.get(this.getVariableInBodyID(variable, body));
	}
	
	public synchronized List<TypeReason<Object>> get3ReasonOftUnsatisfiabilityOfVariableInBody(PatternBody body, Variable variable) throws TypeAnalysisException {
		this.validateCache();
		return this.unsatisfiableTypeConstrainInPatternBodyReason3.get(this.getVariableInBodyID(variable, body));
	}
	
	public synchronized Set<TypeReason<Object>> getNReasonOftUnsatisfiabilityOfVariableInBody(PatternBody body, Variable variable) throws TypeAnalysisException {
		this.validateCache();
		return this.classConstrainInPatternBody.get(this.getVariableInBodyID(variable, body));
	}
	
	public synchronized List<TypeReason<PatternBody>> getReasonOfTooGeneralParameter(Variable parameter) throws TypeAnalysisException
	{
		this.validateCache();
		return this.tooGeneralTypeOfPatternParameterReason2.get(this.getParameterID(parameter));
	}
}
