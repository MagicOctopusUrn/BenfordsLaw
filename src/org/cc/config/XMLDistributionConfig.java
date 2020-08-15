package org.cc.config;

import java.util.HashMap;
import java.util.Map;

import org.cc.categorization.Distribution;
import org.jdom2.Element;

public class XMLDistributionConfig extends XMLConfigEntry<Distribution> {
	private final static String ALLOWED_DEVIANCE_TAG = "AllowedDeviance";
	
	private final static String EXPECTED_TAG = "Expected";
	
	public XMLDistributionConfig(Element e) {
		super(e);
	}

	@Override
	protected Distribution loadData(Element e) {
		double allowedCumulativeDeviance = 0.0;
		Map<Integer,Double> expectedDistribution = null;
		if (e.getChild("AllowedDeviance") != null) {
			allowedCumulativeDeviance = loadAllowedCumulativeDeviance(e.getChild(XMLDistributionConfig.ALLOWED_DEVIANCE_TAG));
		} else {
			System.err.println("[WARN]\tMissing allowed deviance for " + this.getLabel());
		}
		if (e.getChild("Expected") != null) {
			expectedDistribution = loadExpectedDistribution(e.getChild(XMLDistributionConfig.EXPECTED_TAG));
		} else {
			System.err.println("[WARN]\tMissing expected distribution for " + this.getLabel());
		}
		
		if (expectedDistribution != null) {
			return new Distribution(super.getLabel(), expectedDistribution, allowedCumulativeDeviance);
		} else {
		 	return null;
		}
	}
	
	private Map<Integer, Double> loadExpectedDistribution(Element e) {
		Map<Integer, Double> returnMap = new HashMap<Integer, Double>(0);
		for (Element child : e.getChildren()) {
			try {
				if (child.getAttribute("key") != null && child.getAttribute("value") != null) {
					Integer key = child.getAttribute("key").getIntValue();
					Double value = child.getAttribute("value").getDoubleValue();
					returnMap.put(key, value);
				} else {
					System.err.println("[WARN]\tMissing key/value for " + this.getLabel());
				}
			} catch (Exception ex) {
				System.err.println("[FATAL]\tInvalid format of decimal or key in distribuiton " + this.getLabel());
				ex.printStackTrace();
				System.exit(-1);
			}
		}
		return returnMap;
	}

	private double loadAllowedCumulativeDeviance(Element e) {
		Double allowedCumulativeDeviance = 0.0;
		try {
			allowedCumulativeDeviance = Double.parseDouble(e.getValue());
		} catch (Exception ex) {
			System.err.println("[WARN]\tInvalid format of decimal for deviance in distribuiton " + this.getLabel());
			ex.printStackTrace();
		}
		return allowedCumulativeDeviance;
	}
}
