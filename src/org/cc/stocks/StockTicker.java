package org.cc.stocks;

import com.google.gson.annotations.SerializedName;

public class StockTicker {
	private final String name;
	
	private final String symbol;
	
	@SerializedName("has_intraday") 
	private final boolean hasIntraday;
	
	@SerializedName("stock_exchange") 
	private final StockExchange exchange;

	public StockTicker(String name, String symbol, boolean hasIntraday, StockExchange exchange) {
		super();
		this.name = name;
		this.symbol = symbol;
		this.hasIntraday = hasIntraday;
		this.exchange = exchange;
	}

	public String getName() {
		return name;
	}

	public String getSymbol() {
		return symbol;
	}

	public boolean isHasIntraday() {
		return hasIntraday;
	}

	public StockExchange getExchange() {
		return exchange;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exchange == null) ? 0 : exchange.hashCode());
		result = prime * result + (hasIntraday ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
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
		StockTicker other = (StockTicker) obj;
		if (exchange == null) {
			if (other.exchange != null)
				return false;
		} else if (!exchange.equals(other.exchange))
			return false;
		if (hasIntraday != other.hasIntraday)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StockTicker [name=" + name + ", symbol=" + symbol + ", hasIntraday=" + hasIntraday + ", exchange="
				+ exchange + "]";
	}
}
