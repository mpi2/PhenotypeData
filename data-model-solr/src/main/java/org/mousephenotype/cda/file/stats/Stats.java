package org.mousephenotype.cda.file.stats;

public class Stats {
	
	public Stats(String summary, Result result) {
		
	}
	
	Result result;
	
	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	private String headerInfo;
	public String getHeaderInfo() {
		return headerInfo;
	}

	public void setHeaderInfo(String headerInfo) {
		this.headerInfo = headerInfo;
	}

}
