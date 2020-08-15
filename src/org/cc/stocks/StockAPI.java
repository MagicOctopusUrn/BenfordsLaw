package org.cc.stocks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.net.URIBuilder;
import org.jfree.chart.ChartUtilities;

import com.google.gson.Gson;
import com.google.gson.JsonArray; 
import com.google.gson.JsonParser;

public class StockAPI {
	public static void main(String[] args) throws IOException, URISyntaxException {
		for (Entry<String, StockTicker> e : StockAPI.getTickers().entrySet()) {
			System.out.println(e.getKey() + "\t" + e.getValue());
		}
		double[] durationYears = {10, 7, 5, 3, 2, 1,9.0/12.0,6.0/12.0,3.0/12.0,1.0/12.0};
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.MONTH, -1 * (int)(durationYears[0] * 12));
		Date from = cal.getTime();
		Map<String, Stock> stockData = StockAPI.getStocks(from, today);
		for (double durationYear : durationYears) {
			cal = Calendar.getInstance();
			today = cal.getTime();
			cal.add(Calendar.MONTH, -1 * (int)(durationYear * 12));
			cal.add(Calendar.DAY_OF_MONTH, 2);
			from = cal.getTime();
			for (Entry<String, Stock> e : stockData.entrySet()) {
				System.out.println(e.getKey() + " has been successfully loaded...");
				Stock s = e.getValue();
				System.out.println(s.getEarliestEODDate());
				File chartFolder = new File(StockChart.CHART_EXPORT_LOCATION + File.separator + (int)(durationYear * 12) + "-Month");
				if (!chartFolder.exists()) {
					chartFolder.mkdirs();
				}
				if (s.getEarliestEODDate() != null && (s.getEarliestEODDate().before(from) || s.getEarliestEODDate().equals(from))) {
					System.out.println("Generating " + (int)(durationYear * 12) + " month chart...");
					ChartUtilities.saveChartAsPNG(new File(chartFolder.getAbsolutePath() + File.separator + e.getKey() + ".png"), 
						new StockChart(e.getValue(), from, today).getChartEOD(), (int)Math.max(1920, 1920 * durationYear), (int)Math.max(1080, 1080 * durationYear));
				} else if (s.getEarliestEODDate() != null) {
					System.out.println("Not enough data for " + (int)(durationYear * 12) + " month chart...");
				} else {
					System.out.println("No EOD data was ever added for this ticker...");
				}
			}
		}
	}
	
	private final static File CACHE_FOLDER = new File("cache/stocks");
	
	private final static File TICKER_CACHE = new File (StockAPI.CACHE_FOLDER.getAbsolutePath() + File.separator + "tickers.dat");

	private final static File STOCK_EOD_CACHE = new File (StockAPI.CACHE_FOLDER.getAbsolutePath() + File.separator + "eod");
	
	private final static String STOCK_API_BASE_URL = "http://api.marketstack.com/v1";
	
	private final static String ACCESS_KEY = "<API_KEY_HERE>";
	
	private final static String EOD_ENDPOINT = "eod";
	
	private final static String INTRADAY_ENDPOINT = "intraday";
	
	private final static String TICKERS_ENDPOINT = "tickers";
	
	private final static int TICKER_LIMIT = 1000;

	private static final boolean RUN_CACHE = true;

	private static final boolean FORCE_RECACHE = false;
	
	public static Map<String, StockTicker> getTickers() throws IOException, URISyntaxException {
		Map<String, StockTicker> tickers;
		if (StockAPI.TICKER_CACHE.exists() && !StockAPI.FORCE_RECACHE) {
			tickers = loadTickersFromCache();
		} else {
			tickers = loadTickersFromMarketStack();
			if (StockAPI.RUN_CACHE || StockAPI.FORCE_RECACHE)
				cacheTickers(tickers);
		}
		return tickers;
	}

	private static Map<String, StockTicker> loadTickersFromMarketStack() throws URISyntaxException, IOException {
		Map<String, StockTicker> tickers = new HashMap<String, StockTicker>(0);
		String endPointURL = StockAPI.STOCK_API_BASE_URL + "/" + StockAPI.TICKERS_ENDPOINT;
		URIBuilder builder = new URIBuilder(endPointURL);
		builder.setParameter("access_key", StockAPI.ACCESS_KEY).setParameter("limit", ""+StockAPI.TICKER_LIMIT)
			.setParameter("sort","DESC");
		URI uri = builder.build();
		HttpGet get = new HttpGet(uri);
		String jsonArray = IOUtils.toString(HttpClients.createDefault().execute(get).getEntity().getContent(), 
				StandardCharsets.UTF_8.name());
		Gson gson = new Gson();
		JsonArray array = JsonParser.parseString(jsonArray).getAsJsonObject().getAsJsonArray("data").getAsJsonArray();
		for (int i = 0; i < array.size(); i++) {
			StockTicker ticker = gson.fromJson(array.get(i).getAsJsonObject(), StockTicker.class);
			if (ticker != null && !ticker.getSymbol().contains("/")) {
				tickers.put(ticker.getSymbol(), ticker);
			}
		}
		return tickers;
	}

	private static Map<String, StockTicker> loadTickersFromCache() throws IOException {
		Map<String, StockTicker> tickers = new HashMap<String, StockTicker>(0);
		String json = FileUtils.readFileToString(StockAPI.TICKER_CACHE, "UTF-8");
		Gson gson = new Gson();
		JsonArray array = JsonParser.parseString(json).getAsJsonArray();
		for (int i = 0; i < array.size(); i++) {
			StockTicker ticker = gson.fromJson(array.get(i).getAsJsonObject(), StockTicker.class);
			if (ticker != null) {
				tickers.put(ticker.getSymbol(), ticker);
			}
		}
		return tickers;
	}
	
	private static void cacheTickers(Map<String, StockTicker> tickers) throws FileNotFoundException {
		if (TICKER_CACHE.exists())
			TICKER_CACHE.delete();
		PrintWriter out = new PrintWriter(StockAPI.TICKER_CACHE);
		out.write(new Gson().toJson(tickers.values()));
		out.close();
	}

	
	public static Map<String, Stock> getStocks(Date from, Date to) throws IOException, URISyntaxException {
		Map<String, Stock> stocks = new HashMap<String, Stock>(0);
		for (StockTicker ticker : StockAPI.getTickers().values()) {
			File cacheFile = new File(StockAPI.STOCK_EOD_CACHE.getAbsolutePath() + File.separator + ticker.getSymbol() + ".dat");
			if (cacheFile.exists() && !StockAPI.FORCE_RECACHE) {
				Stock stock = loadStockFromCache(ticker.getSymbol(), from, to);
				stocks.put(stock.getSymbol(), stock);
			} else {
				Stock stock = loadStockFromMarketStack(ticker.getSymbol(), from, to);
				if (stock != null) {
					stocks.put(stock.getSymbol(), stock);
					if (StockAPI.RUN_CACHE || StockAPI.FORCE_RECACHE)
						cacheStock(stock);
				}
			}
		}
		return stocks;
	}
	
	public static Stock getStock(String symbol, Date from, Date to) throws IOException, URISyntaxException {
		File cacheFile = new File(StockAPI.STOCK_EOD_CACHE.getAbsolutePath() + File.separator + symbol + ".dat");
		Stock stock;
		if (cacheFile.exists() && !StockAPI.FORCE_RECACHE) {
			stock = loadStockFromCache(symbol, from, to);
		} else {
			stock = loadStockFromMarketStack(symbol, from, to);
			if (stock != null) {
				if (StockAPI.RUN_CACHE || StockAPI.FORCE_RECACHE)
					cacheStock(stock);
			}
		}
		return stock;
	}

	private static Stock loadStockFromMarketStack(String ticker, Date from, Date to) throws URISyntaxException, IOException {
		Stock stock = new Stock(ticker);
		try {
			String endPointURL = StockAPI.STOCK_API_BASE_URL + "/" + StockAPI.TICKERS_ENDPOINT + "/" + ticker + "/" + StockAPI.EOD_ENDPOINT;
			URIBuilder builder = new URIBuilder(endPointURL);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			builder.setParameter("access_key", StockAPI.ACCESS_KEY).setParameter("limit", "10000")
				.setParameter("symbols", URLEncoder.encode(ticker, StandardCharsets.UTF_8))
				.setParameter("date_from", sdf.format(from)).setParameter("date_to",sdf.format(to));
			URI uri = builder.build();
			HttpGet get = new HttpGet(uri);
			String jsonArray = IOUtils.toString(HttpClients.createDefault().execute(get).getEntity().getContent(), 
					StandardCharsets.UTF_8.name());
			JsonArray array = JsonParser.parseString(jsonArray).getAsJsonObject().getAsJsonObject("data").getAsJsonArray("eod");
			for (int i = 0; i < array.size(); i++) {
				StockEOD eod = new Gson().fromJson(array.get(i).getAsJsonObject(), StockEOD.class);
				if (ticker != null) {
					stock.addStockEODData(eod.getDate(), eod);
				}
			}
			return stock;
		} catch (Exception e) {
			System.err.println("Could not load stock:\t" + ticker);
			e.printStackTrace();
			return null;
		}
	}

	private static Stock loadStockFromCache(String ticker, Date from, Date to) throws IOException {
		File cacheFile = new File(StockAPI.STOCK_EOD_CACHE.getAbsolutePath() + File.separator + ticker + ".dat");
		if (cacheFile.exists()) {
			Stock stock = new Stock(ticker);
			String json = FileUtils.readFileToString(cacheFile, "UTF-8");
			JsonArray array = JsonParser.parseString(json).getAsJsonArray();
			for (int i = 0; i < array.size(); i++) {
				StockEOD eod = new Gson().fromJson(array.get(i).getAsJsonObject(), StockEOD.class);
				if (eod != null && eod.getDate().after(from) && eod.getDate().before(to)) {
					stock.addStockEODData(eod.getDate(), eod);
				}
			}
			return stock;
		} else {
			return null;
		}
	}
	
	private static void cacheStock(Stock stock) throws FileNotFoundException {
		File cacheFile = new File(StockAPI.STOCK_EOD_CACHE.getAbsolutePath() + File.separator + stock.getSymbol() + ".dat");
		if (cacheFile.exists())
			cacheFile.delete();
		PrintWriter out = new PrintWriter(cacheFile);
		out.write(new Gson().toJson(stock.getEodMap().values()));
		out.close();
	}
}
