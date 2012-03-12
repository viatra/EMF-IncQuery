package org.eclipse.viatra2.emf.incquery.databinding.tooling;

public class DatabindingAdapterData {

	private String databindableMatcherPackage;
	private String patternName;
	private String databindableMatcherName;
	private String message;
	private String matcherFactory;
	private boolean isMessageOnly;

	public DatabindingAdapterData(String databindableMatcherPackage,
			String databindableMatcherName, String patternName, String message,
			boolean isMessageOnly, String matcherFactory) {
		this.databindableMatcherPackage = databindableMatcherPackage;
		this.patternName = patternName;
		this.databindableMatcherName = databindableMatcherName;
		this.message = message;
		this.isMessageOnly = isMessageOnly;
		this.matcherFactory = matcherFactory;
	}

	public String getDatabindableMatcherPackage() {
		return databindableMatcherPackage;
	}

	public void setDatabindableMatcherPackage(String databindableMatcherPackage) {
		this.databindableMatcherPackage = databindableMatcherPackage;
	}

	public String getPatternName() {
		return patternName;
	}

	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}

	public String getDatabindableMatcherName() {
		return databindableMatcherName;
	}

	public void setDatabindableMatcherName(String databindableMatcherName) {
		this.databindableMatcherName = databindableMatcherName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isMessageOnly() {
		return isMessageOnly;
	}

	public void setMessageOnly(boolean isMessageOnly) {
		this.isMessageOnly = isMessageOnly;
	}

	public String getMatcherFactory() {
		return matcherFactory;
	}

	public void setMatcherFactory(String matcherFactory) {
		this.matcherFactory = matcherFactory;
	}
}
