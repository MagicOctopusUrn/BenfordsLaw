package org.cc.config;

import org.cc.categorization.Genre;
import org.jdom2.Element;

public class XMLGenreConfig extends XMLConfigEntry<Genre> {
	private final static String NAME_TAG = "Name";
	
	private final static String DESCRIPTION_TAG = "Description";
	
	public XMLGenreConfig(Element child) {
		super(child);
	}
	
	@Override
	protected Genre loadData(Element e) {
		String name = e.getChildText(XMLGenreConfig.NAME_TAG);
		String description = e.getChildText(XMLGenreConfig.DESCRIPTION_TAG);
		return new Genre(name, description);
	}
}
