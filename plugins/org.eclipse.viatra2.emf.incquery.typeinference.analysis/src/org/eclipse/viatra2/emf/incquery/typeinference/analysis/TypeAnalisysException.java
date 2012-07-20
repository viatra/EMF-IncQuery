package org.eclipse.viatra2.emf.incquery.typeinference.analysis;

import java.util.Collection;

public class TypeAnalisysException extends Exception {
	private static final long serialVersionUID = -1740770198384457774L;

	private Collection<?> resoultSet;
	
	public TypeAnalisysException(String message) {
		super(message);
	}
	
	public TypeAnalisysException(String message, Collection<?> resultSet) {
		this(message);
		this.resoultSet = resultSet;
	}

	public Collection<?> getResoultSet() {
		return resoultSet;
	}
}
