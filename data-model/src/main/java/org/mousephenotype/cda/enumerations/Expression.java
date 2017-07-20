package org.mousephenotype.cda.enumerations;

public enum Expression {
	EXPRESSION("expression"), NO_EXPRESSION("no expression"), AMBIGUOUS("ambiguous"), TISSUE_NOT_AVAILABLE("tissue not available");//tissue not available;
	
	private final String displayName;

	Expression(String displayName) {
		this.displayName = displayName;
	}
}
