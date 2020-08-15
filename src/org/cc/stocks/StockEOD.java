package org.cc.stocks;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class StockEOD {
	private final double open;
	
	private final double high;
	
	private final double low;
	
	private final double close;
	
	private final double volume;
	
	@SerializedName("adj_high") 
	private final double adjHigh;
	
	@SerializedName("adj_low") 
	private final double adjLow;
	
	@SerializedName("adj_close") 
	private final double adjClose;
	
	@SerializedName("adj_open") 
	private final double adjOpen;
	
	@SerializedName("adj_volume") 
	private final double adjVolume;
	
	private final String symbol;
	
	private final String exchange;
	
	private final Date date;

	public StockEOD(double open, double high, double low, double close, double volume, double adjHigh, double adjLow,
			double adjClose, double adjOpen, double adjVolume, String symbol, String exchange, Date date) {
		super();
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		this.adjHigh = adjHigh;
		this.adjLow = adjLow;
		this.adjClose = adjClose;
		this.adjOpen = adjOpen;
		this.adjVolume = adjVolume;
		this.symbol = symbol;
		this.exchange = exchange;
		this.date = date;
	}

	public double getOpen() {
		return open;
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}

	public double getClose() {
		return close;
	}

	public double getVolume() {
		return volume;
	}

	public double getAdjOpen() {
		return adjOpen;
	}

	public double getAdjHigh() {
		return adjHigh;
	}

	public double getAdjLow() {
		return adjLow;
	}

	public double getAdjClose() {
		return adjClose;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getExchange() {
		return exchange;
	}

	public Date getDate() {
		return date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(adjClose);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(adjHigh);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(adjLow);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(adjOpen);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(close);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((exchange == null) ? 0 : exchange.hashCode());
		temp = Double.doubleToLongBits(high);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(low);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(open);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		temp = Double.doubleToLongBits(volume);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		StockEOD other = (StockEOD) obj;
		if (Double.doubleToLongBits(adjClose) != Double.doubleToLongBits(other.adjClose))
			return false;
		if (Double.doubleToLongBits(adjHigh) != Double.doubleToLongBits(other.adjHigh))
			return false;
		if (Double.doubleToLongBits(adjLow) != Double.doubleToLongBits(other.adjLow))
			return false;
		if (Double.doubleToLongBits(adjOpen) != Double.doubleToLongBits(other.adjOpen))
			return false;
		if (Double.doubleToLongBits(close) != Double.doubleToLongBits(other.close))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (exchange == null) {
			if (other.exchange != null)
				return false;
		} else if (!exchange.equals(other.exchange))
			return false;
		if (Double.doubleToLongBits(high) != Double.doubleToLongBits(other.high))
			return false;
		if (Double.doubleToLongBits(low) != Double.doubleToLongBits(other.low))
			return false;
		if (Double.doubleToLongBits(open) != Double.doubleToLongBits(other.open))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		if (Double.doubleToLongBits(volume) != Double.doubleToLongBits(other.volume))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "StockEOD [open=" + open + ", high=" + high + ", low=" + low + ", close=" + close + ", volume=" + volume
				+ ", adjOpen=" + adjOpen + ", adjHigh=" + adjHigh + ", adjLow=" + adjLow + ", adjClose=" + adjClose
				+ ", symbol=" + symbol + ", exchange=" + exchange + ", date=" + date + "]";
	}
}
