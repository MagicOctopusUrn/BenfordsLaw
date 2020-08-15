package org.cc.stocks;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jfree.chart.ChartUtilities;

public class Stock {
	public static void main(String[] args) throws IOException, URISyntaxException {
		String symbol = "EDPFY";
		double[] durationYears = {10, 7, 5, 3, 2, 1,9.0/12.0,6.0/12.0,3.0/12.0,1.0/12.0};
		for (double durationYear : durationYears) {
			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime();
			cal.add(Calendar.MONTH, -1 * (int)(durationYear * 12));
			Date from = cal.getTime();
			File chartFolder = new File(StockChart.CHART_EXPORT_LOCATION + File.separator + symbol);
			if (!chartFolder.exists()) {
				chartFolder.mkdirs();
			}
			ChartUtilities.saveChartAsPNG(new File(chartFolder.getAbsolutePath() + File.separator + (int)(durationYear * 12) + "-Month.png"), 
				new StockChart(StockAPI.getStock(symbol, from, today), from, today).getChartEOD(), (int)Math.max(1920, 1920 * durationYear), 
					(int)Math.max(1080, 1080 * durationYear));
		}
	}
	
	private final String symbol;
	
	private final Map<Date, StockEOD> eodMap;
	
	private volatile Date earliestDateCache;
	
	public Stock(String symbol, Map<Date, StockEOD> eodMap) {
		super();
		this.symbol = symbol;
		this.eodMap = new HashMap<Date, StockEOD>(eodMap);
		this.earliestDateCache = null;
	}
	
	public Stock(String symbol) {
		this.symbol = symbol;
		this.eodMap = new HashMap<Date, StockEOD>(0);
		this.earliestDateCache = null;
	}

	public void addStockEODData(Date date, StockEOD eod) {
		this.eodMap.put(date, eod);
		if (this.earliestDateCache == null || this.earliestDateCache.after(date))
			this.earliestDateCache = date;
	}

	public String getSymbol() {
		return symbol;
	}

	public Map<Date, StockEOD> getEodMap() {
		return eodMap;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Stock other = (Stock) obj;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Stock [symbol=" + symbol + ", eodMap=" + eodMap + "]";
	}

	public Date getEarliestEODDate() {
		if (this.earliestDateCache == null) {
			Date earliestDate = null;
			for (StockEOD eod : this.eodMap.values()) {
				if (earliestDate == null || eod.getDate().before(earliestDate)) {
					earliestDate = eod.getDate();
				}
			}
			return earliestDate;
		} else {
			return this.earliestDateCache;
		}
	}
	
	public void clearEarliestDateCache() {
		this.earliestDateCache = null;
	}
}

