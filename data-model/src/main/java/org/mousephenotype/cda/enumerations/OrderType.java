package org.mousephenotype.cda.enumerations;

public enum OrderType {
		
	targeting_vector("Targeting Vector"),
		es_cell("Es Cell"),
		mouse("Mouse");

		private final String name;

		OrderType(String name) {
			this.name = name;
		}


		public String getName(){
			return this.name;
		}
	
}
