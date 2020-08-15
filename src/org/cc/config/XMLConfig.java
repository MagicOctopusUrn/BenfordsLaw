package org.cc.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class XMLConfig {
	public static void main(String[] args) throws IOException, JDOMException {
		XMLConfig xml = new XMLConfig(XMLConfig.DEFAULT_XML_CONFIG_FILE);
		for (XMLDistributionConfig xdc : xml.distributionConfigurations.values()) {
			System.out.println(xdc.getData());
		}
		for (XMLAlterationConfig xac : xml.alterationConfigurations.values()) {
			System.out.println(xac.getData());
		}
		for (XMLGenreConfig xgc : xml.genreConfigurations.values()) {
			System.out.println(xgc.getData());
		}
	}
	
	public final static File DEFAULT_XML_CONFIG_FILE = new File("config/Config.xml");
	
	private final static String XML_CONFIG_VERSION = "1.0.0";
	
	private final static String DISTRIBUTION_TAG = "DistributionConfig";
	
	private final static String ALTERATION_TAG = "AlterationConfig";
	
	private final static String GENRE_TAG = "GenreConfig";

	private final Map<Integer, XMLDistributionConfig> distributionConfigurations;

	private final Map<Integer, XMLAlterationConfig> alterationConfigurations;

	private final Map<Integer, XMLGenreConfig> genreConfigurations;
	
	private final File configFile;

	public XMLConfig(File configFile) throws JDOMException, IOException {
		this.configFile = configFile;
		this.distributionConfigurations = new LinkedHashMap<Integer, XMLDistributionConfig>(0);
		this.alterationConfigurations = new LinkedHashMap<Integer, XMLAlterationConfig>(0);
		this.genreConfigurations = new LinkedHashMap<Integer, XMLGenreConfig>(0);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(configFile);
		Element root = doc.getRootElement();
		if (!root.getAttribute("version").getValue().equalsIgnoreCase(XMLConfig.XML_CONFIG_VERSION)) {
			System.err.println("[WARN]\tVersion number mismatch, XML version is " + root.getAttribute("version").getValue() + ", we expected " + XMLConfig.XML_CONFIG_VERSION + ".");
		}
		parseNodes(doc.getRootElement());
	}

	private void parseNodes(Element root) {
		if (root.getChild(XMLConfig.DISTRIBUTION_TAG) != null) {
			parseDistributionNodes(root.getChild(XMLConfig.DISTRIBUTION_TAG));
		} else {
			
		}
		if (root.getChild(XMLConfig.ALTERATION_TAG) != null) {
			parseAlterationNodes(root.getChild(XMLConfig.ALTERATION_TAG));
		} else {
			
		}
		if (root.getChild(XMLConfig.GENRE_TAG) != null) {
			parseGenreNodes(root.getChild(XMLConfig.GENRE_TAG));
		} else {
			
		}
	}

	private void parseGenreNodes(Element e) {
		for (Element child : e.getChildren()) {
			XMLGenreConfig genreConfig = new XMLGenreConfig(child);
			if (!this.genreConfigurations.containsKey(genreConfig.getId()))
				this.genreConfigurations.put(genreConfig.getId(), genreConfig);
			else {
				System.err.println("[FATAL]\tDuplicate integer as ID for genre configuration node:\t'" 
					+ genreConfig.getId() + "'");
				System.exit(-1);
			}
		}
	}

	private void parseAlterationNodes(Element e) {
		for (Element child : e.getChildren()) {
			XMLAlterationConfig alterationConfig = new XMLAlterationConfig(child);
			if (!this.alterationConfigurations.containsKey(alterationConfig.getId()))
				this.alterationConfigurations.put(alterationConfig.getId(), alterationConfig);
			else {
				System.err.println("[FATAL]\tDuplicate integer as ID for alteration configuration node:\t'" 
					+ alterationConfig.getId() + "'");
				System.exit(-1);
			}
		}
	}

	private void parseDistributionNodes(Element e) {
		for (Element child : e.getChildren()) {
			XMLDistributionConfig distributionConfig = new XMLDistributionConfig(child);
			if (!this.distributionConfigurations.containsKey(distributionConfig.getId()))
				this.distributionConfigurations.put(distributionConfig.getId(), distributionConfig);
			else {
				System.err.println("[FATAL]\tDuplicate integer as ID for distribution configuration node:\t'" 
					+ distributionConfig.getId() + "'");
				System.exit(-1);
			}
		}
	}

	public Map<Integer, XMLDistributionConfig> getDistributionConfigurations() {
		return distributionConfigurations;
	}

	public Map<Integer, XMLAlterationConfig> getAlterationConfigurations() {
		return alterationConfigurations;
	}

	public Map<Integer, XMLGenreConfig> getGenreConfigurations() {
		return genreConfigurations;
	}

	public File getConfigFile() {
		return configFile;
	}
}
