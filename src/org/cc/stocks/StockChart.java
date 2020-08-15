package org.cc.stocks;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.RectangularShape;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SegmentedTimeline;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.GradientXYBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.DefaultOHLCDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.RectangleEdge;

public class StockChart {
	public final static String CHART_EXPORT_LOCATION = "cache/stocks/charts";
	
	private final Stock stock;
	
	private final JFreeChart chartEOD;
	
	private final Date from, to;
	
	public StockChart (Stock stock, Date from, Date to) throws IOException {
		this.stock = stock;
		this.from = from;
		this.to = to;
		this.chartEOD = generateEODChart();
	}

	private JFreeChart generateEODChart() throws IOException {		
		final OHLCDataItem[] items = createOHLCSeries();
		
		final OHLCDataset ohlcDataset = new DefaultOHLCDataset("EOD Data", items);
		
		DateAxis domainAxis = new DateAxis("Date");
        //domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
        domainAxis.setRange(this.from, this.to);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        domainAxis.setDateFormatOverride(sdf);

        //Build Candlestick Chart based on stock price OHLC
        NumberAxis  priceAxis = new NumberAxis("Price");
        CandlestickRenderer priceRenderer = new CandlestickRenderer() {
        	private static final long serialVersionUID = 2130244244401724808L;

			@Override
        	public Paint getItemPaint(int series, int item) {
				return items[item].getOpen().doubleValue() > items[item].getClose().doubleValue() ? Color.RED : Color.GREEN;
        	}
        };
        XYPlot pricePlot = new XYPlot(ohlcDataset, domainAxis, priceAxis, priceRenderer);
        priceRenderer.setSeriesPaint(0, Color.BLACK);
        priceRenderer.setDrawVolume(true);
        priceRenderer.setVolumePaint(Color.BLUE);
        priceAxis.setAutoRangeIncludesZero(false);

        //Build Bar Chart for volume by wrapping price dataset with an IntervalXYDataset
        IntervalXYDataset volumeDataset = getVolumeDataset(ohlcDataset, 24 * 60 * 60 * 1000); // Each bar is 24 hours wide.
        NumberAxis volumeAxis = new NumberAxis("Volume");
        XYBarRenderer volumeRenderer = new XYBarRenderer() {
        	private static final long serialVersionUID = 2130244244401724808L;

			@Override
        	public Paint getItemPaint(int series, int item) {
				return items[item].getOpen().doubleValue() > items[item].getClose().doubleValue() ? Color.RED : Color.GREEN;
        	}
        };
        StandardXYBarPainter barPainter = new StandardXYBarPainter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintBarShadow(Graphics2D g2, XYBarRenderer renderer, int row,
        		    int column, RectangularShape bar, RectangleEdge base,
        		    boolean pegShadow) {}
        };
        volumeRenderer.setBarPainter(barPainter);
        
        XYPlot volumePlot = new XYPlot(volumeDataset, domainAxis, volumeAxis, volumeRenderer);
        volumeRenderer.setSeriesPaint(0, Color.BLUE);

        //Build Combined Plot
        CombinedDomainXYPlot mainPlot = new CombinedDomainXYPlot(domainAxis);
        mainPlot.add(pricePlot, 4);
        mainPlot.add(volumePlot, 1);

        /*


        plot.setDataset(0, ohlcDataset);
        plot.setRenderer(0, ohlcRenderer);
        */

        // Create Category XY plot (not used if I'm using OHLC)
        /*--
		TimeSeriesCollection eodData = new TimeSeriesCollection();
		
		eodData.addSeries(createLowSeries(24 * 60 * 60 * 1000));
		eodData.addSeries(createHighSeries(24 * 60 * 60 * 1000));
		eodData.addSeries(createOpenSeries(24 * 60 * 60 * 1000));
		eodData.addSeries(createCloseSeries(24 * 60 * 60 * 1000));
		
		XYSplineRenderer xyRenderer = new XYSplineRenderer();
        
        pricePlot.setDataset(1, eodData);
        pricePlot.setRenderer(1, xyRenderer);
		*/
        
        return new JFreeChart(this.stock.getSymbol(), mainPlot);
	}
	
    private TimeSeries createLowSeries(final long barWidthInMilliseconds) {
    	TimeSeries series = new TimeSeries("Low");
    	for (Entry<Date, StockEOD> eod : this.stock.getEodMap().entrySet()) {
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(eod.getKey());
    		cal.add(Calendar.MILLISECOND, (int)barWidthInMilliseconds);
    		series.add(new Day(cal.getTime()), eod.getValue().getLow());
    	}
        return series;
    }
	
    private TimeSeries createHighSeries(final long barWidthInMilliseconds) {
    	TimeSeries series = new TimeSeries("High");
    	for (Entry<Date, StockEOD> eod : this.stock.getEodMap().entrySet()) {
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(eod.getKey());
    		cal.add(Calendar.MILLISECOND, (int)barWidthInMilliseconds);
    		series.add(new Day(cal.getTime()), eod.getValue().getHigh());
    	}
        return series;
    }
	
    private TimeSeries createOpenSeries(final long barWidthInMilliseconds) {
    	TimeSeries series = new TimeSeries("Open");
    	for (Entry<Date, StockEOD> eod : this.stock.getEodMap().entrySet()) {
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(eod.getKey());
    		cal.add(Calendar.MILLISECOND, (int)barWidthInMilliseconds);
    		series.add(new Day(cal.getTime()), eod.getValue().getOpen());
    	}
        return series;
    }
	
    private TimeSeries createCloseSeries(final long barWidthInMilliseconds) {
    	TimeSeries series = new TimeSeries("Close");
    	for (Entry<Date, StockEOD> eod : this.stock.getEodMap().entrySet()) {
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(eod.getKey());
    		cal.add(Calendar.MILLISECOND, (int)barWidthInMilliseconds);
    		series.add(new Day(cal.getTime()), eod.getValue().getClose());
    	}
        return series;
    }
    
    private OHLCDataItem[] createOHLCSeries() {
    	OHLCDataItem[] eodData = new OHLCDataItem[this.stock.getEodMap().entrySet().size()];
        int i = 0;
    	for (Entry<Date, StockEOD> eod : this.stock.getEodMap().entrySet()) {
    		double open = eod.getValue().getOpen();
    		double close = eod.getValue().getClose();
    		double high = eod.getValue().getHigh();
    		double low = eod.getValue().getLow();
    		double volume = eod.getValue().getVolume();
    		eodData[i++] = new OHLCDataItem(eod.getKey(), open, high, low, close, volume);
    	}
        return eodData;
    }

    protected static IntervalXYDataset getVolumeDataset(final OHLCDataset priceDataset, final long barWidthInMilliseconds) {
        return new AbstractIntervalXYDataset(){
			private static final long serialVersionUID = -4596214797116122869L;
			
			public int getSeriesCount() {
                return priceDataset.getSeriesCount();
            }
            public Comparable getSeriesKey(int series) {
                return priceDataset.getSeriesKey(series) + "-Volume";
            }
            public int getItemCount(int series) {
                return priceDataset.getItemCount(series);
            }
            public Number getX(int series, int item) {
                return priceDataset.getX(series, item);
            }
            public Number getY(int series, int item) {
                return priceDataset.getVolume(series,  item);
            }
            public Number getStartX(int series, int item) {
                return priceDataset.getX(series, item).doubleValue() - barWidthInMilliseconds/2;
            }
            public Number getEndX(int series, int item) {
                return priceDataset.getX(series, item).doubleValue() + barWidthInMilliseconds/2;
            }
            public Number getStartY(int series, int item) {
                return 0.0;
            }
            public Number getEndY(int series, int item) {
                return priceDataset.getVolume(series,  item);
            }
        };
    }

	public Stock getStock() {
		return stock;
	}

	public JFreeChart getChartEOD() {
		return chartEOD;
	}
}
