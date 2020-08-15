package org.cc.stocks;

import com.google.gson.annotations.SerializedName;

public class StockExchange {
	private final String name;
	
	private final String acronym;
	
	private final String mic;
	
	private final String country;
	
	@SerializedName("country_code") 
	private final String countryCode;
	
	private final String city;
	
	private final String website;

	public StockExchange(String name, String acronym, String mic, String country, String countryCode, String city,
			String website) {
		super();
		this.name = name;
		this.acronym = acronym;
		this.mic = mic;
		this.country = country;
		this.countryCode = countryCode;
		this.city = city;
		this.website = website;
	}

	public String getName() {
		return name;
	}

	public String getAcronym() {
		return acronym;
	}

	public String getMic() {
		return mic;
	}

	public String getCountry() {
		return country;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getCity() {
		return city;
	}

	public String getWebsite() {
		return website;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acronym == null) ? 0 : acronym.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((countryCode == null) ? 0 : countryCode.hashCode());
		result = prime * result + ((mic == null) ? 0 : mic.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((website == null) ? 0 : website.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StockExchange other = (StockExchange) obj;
		if (acronym == null) {
			if (other.acronym != null)
				return false;
		} else if (!acronym.equals(other.acronym))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (countryCode == null) {
			if (other.countryCode != null)
				return false;
		} else if (!countryCode.equals(other.countryCode))
			return false;
		if (mic == null) {
			if (other.mic != null)
				return false;
		} else if (!mic.equals(other.mic))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (website == null) {
			if (other.website != null)
				return false;
		} else if (!website.equals(other.website))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StockExchange [name=" + name + ", acronym=" + acronym + ", mic=" + mic + ", country=" + country
				+ ", countryCode=" + countryCode + ", city=" + city + ", website=" + website + "]";
	}
}
