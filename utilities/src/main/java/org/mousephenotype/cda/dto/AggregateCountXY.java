package org.mousephenotype.cda.dto;

/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

public class AggregateCountXY {

	private int aggregateCount;
	private String xValue;
	private String xName;
	private String xAttribute;
	private String yValue;
	private String yName;
	private String yAttribute;

	/**
	 * @param aggregateCount
	 * @param xValue
	 * @param xName
	 * @param xAttribute
	 * @param yValue
	 * @param yName
	 * @param yAttribute
	 */
	public AggregateCountXY(int aggregateCount, String xValue, String xName, String xAttribute, String yValue, String yName, String yAttribute) {
		this.aggregateCount = aggregateCount;
		this.xValue = xValue;
		this.xName = xName;
		this.xAttribute = xAttribute;
		this.yValue = yValue;
		this.yName = yName;
		this.yAttribute = yAttribute;
	}

	/**
	 * @return the aggregateCount
	 */
	public int getAggregateCount() {
		return aggregateCount;
	}

	/**
	 * @param aggregateCount the aggregateCount to set
	 */
	public void setAggregateCount(int aggregateCount) {
		this.aggregateCount = aggregateCount;
	}

	/**
	 * @return the xValue
	 */
	public String getxValue() {
		return xValue;
	}

	/**
	 * @param xValue the xValue to set
	 */
	public void setxValue(String xValue) {
		this.xValue = xValue;
	}

	/**
	 * @return the xName
	 */
	public String getxName() {
		return xName;
	}

	/**
	 * @param xName the xName to set
	 */
	public void setxName(String xName) {
		this.xName = xName;
	}

	/**
	 * @return the xAttribute
	 */
	public String getxAttribute() {
		return xAttribute;
	}

	/**
	 * @param xAttribute the xAttribute to set
	 */
	public void setxAttribute(String xAttribute) {
		this.xAttribute = xAttribute;
	}

	/**
	 * @return the yValue
	 */
	public String getyValue() {
		return yValue;
	}

	/**
	 * @param yValue the yValue to set
	 */
	public void setyValue(String yValue) {
		this.yValue = yValue;
	}

	/**
	 * @return the yName
	 */
	public String getyName() {
		return yName;
	}

	/**
	 * @param yName the yName to set
	 */
	public void setyName(String yName) {
		this.yName = yName;
	}

	/**
	 * @return the yAttribute
	 */
	public String getyAttribute() {
		return yAttribute;
	}

	/**
	 * @param yAttribute the yAttribute to set
	 */
	public void setyAttribute(String yAttribute) {
		this.yAttribute = yAttribute;
	}
}