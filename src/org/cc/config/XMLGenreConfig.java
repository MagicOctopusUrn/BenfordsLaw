package org.cc.config;

import org.cc.categorization.Genre;
import org.w3c.dom.Node;

public class XMLGenreConfig extends XMLConfigEntry<Genre> {
	public XMLGenreConfig(Node n) {
		super(n);
	}

	@Override
	protected Genre loadData(Node n) {
		return null;
	}
}
