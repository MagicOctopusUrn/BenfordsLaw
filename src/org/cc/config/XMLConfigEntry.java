package org.cc.config;

import org.jdom2.Element;

public abstract class XMLConfigEntry<T> {
	private final int id;
	
	private final String label;
	
	private final T data;
	
	public XMLConfigEntry(Element e) {
		int id = -1;
		try {
			id = Integer.parseInt(e.getAttributeValue("id"));
		} catch (Exception ex) {
			System.err.println("[FATAL]\tInvalid integer as ID for node:\t'" 
				+ e.getAttributeValue("id") + "'");
			ex.printStackTrace();
			System.exit(-1);
		}
		this.id = id;
		this.label = e.getAttributeValue("label");
		this.data = loadData(e);
	}

	public XMLConfigEntry(int id, String label, T data) {
		super();
		this.id = id;
		this.label = label;
		this.data = data;
	}
	
	protected abstract T loadData(Element e);

	public int getId() {
		return this.id;
	}

	public String getLabel() {
		return this.label;
	}
	
	public T getData() {
		return this.data;
	}
}
