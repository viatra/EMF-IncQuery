package org.eclipse.viatra2.emf.incquery.queryexplorer.content.detail;

public class MatcherConfiguration {

	private String parameterName;
	private String clazz;
	private Object filter;

	public MatcherConfiguration(String parameterName, String clazz,	Object filter) {
		super();
		this.parameterName = parameterName;
		this.clazz = clazz;
		this.filter = filter;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public Object getFilter() {
		return filter;
	}

	public void setFilter(Object filter) {
		this.filter = filter;
	}
}
