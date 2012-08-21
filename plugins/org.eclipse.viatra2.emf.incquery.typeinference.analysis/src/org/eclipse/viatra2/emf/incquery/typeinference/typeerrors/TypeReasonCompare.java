package org.eclipse.viatra2.emf.incquery.typeinference.typeerrors;

public class TypeReasonCompare<ReasonType> implements java.util.Comparator<TypeReason<ReasonType>>
{
	@Override
	public int compare(TypeReason<ReasonType> arg0, TypeReason<ReasonType> arg1) {
		return arg0.getType().getName().compareTo(arg1.getType().getName());
	}	
}