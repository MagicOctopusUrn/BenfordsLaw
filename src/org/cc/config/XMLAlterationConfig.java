package org.cc.config;

import java.lang.reflect.Method;

import org.cc.categorization.Alteration;
import org.cc.image.ImageAlterationFactory;
import org.jdom2.Element;

public class XMLAlterationConfig extends XMLConfigEntry<Alteration> {
	private final static String NAME_TAG = "Name";
	
	private final static String DESCRIPTION_TAG = "Description";
	
	private final static String METHOD_TAG = "Method";
	
	private final static String MAGNITUDE_TAG = "Magnitude";
	
	public XMLAlterationConfig(Element child) {
		super(child);
	}

	@Override
	protected Alteration loadData(Element e) {
		String name = "";
		String description = "";
		Double magnitude = 0.0;
		Method method = null;
		try {
			name = e.getChildText(XMLAlterationConfig.NAME_TAG);
			description = e.getChild(XMLAlterationConfig.DESCRIPTION_TAG).getValue();
			magnitude = Double.parseDouble(e.getChild(XMLAlterationConfig.MAGNITUDE_TAG).getValue());
			String methodString = e.getChildText(XMLAlterationConfig.METHOD_TAG);
			method = ImageAlterationFactory.class.getMethod(methodString, double.class);
			return new Alteration(name, description, magnitude, method);
		} catch (Exception ex) {
			System.err.println("[WARN]\tInvalid alteration configuration for " + this.getLabel());
			ex.printStackTrace();
			return null;
		}
	}
}
