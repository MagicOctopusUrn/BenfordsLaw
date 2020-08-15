package org.cc.categorization;

import java.lang.reflect.Method;

public class Alteration {
	private final String name;
	
	private final String description;
	
	private final double magnitude;
	
	private final Method method;
	
	public Alteration (String name, String description, double magnitude, Method method) {
		this.name = name;
		this.description = description.trim().replaceAll("[\t|\r|\n]","");
		this.magnitude = magnitude;
		this.method = method;
	}

	public String getName() {
		return name;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public Method getMethod() {
		return method;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		long temp;
		temp = Double.doubleToLongBits(magnitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Alteration other = (Alteration) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (Double.doubleToLongBits(magnitude) != Double.doubleToLongBits(other.magnitude))
			return false;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Alteration [name=" + name + ", description=" + description + ", magnitude=" + magnitude + ", method="
				+ method + "]";
	}
}
