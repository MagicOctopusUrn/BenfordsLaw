package org.cc.categorization;

import java.util.HashMap;
import java.util.Map;

public class Distribution {
	private final String name;
	
	private final Map<Integer, Double> expectedDistribution;
	
	private final double allowedCumulativeDeviance;
	
	public Distribution(String name, Map<Integer, Double> expectedDistribution, 
			double allowedCumulativeDeviance) {
		this.name = name;
		this.expectedDistribution = new HashMap<Integer, Double>(expectedDistribution);
		this.allowedCumulativeDeviance = allowedCumulativeDeviance;
	}
	
	public String getName() {
		return this.name;
	}

	public Map<Integer, Double> getExpectedDistribution() {
		return expectedDistribution;
	}

	public double getAllowedCumulativeDeviance() {
		return allowedCumulativeDeviance;
	}
}
