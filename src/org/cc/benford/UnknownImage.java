package org.cc.benford;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.inference.TestUtils;

public class UnknownImage {
	public static void main(String[] args) throws IOException {
		File file = new File(
				"C:\\Users\\anony\\Desktop\\BenfordTestImages\\ezgif-4-906ffa69f7da-gif-png\\frame_33_delay-0.1s.png");
		UnknownImage bi = new UnknownImage(file);
		System.out.println(bi.calculateBenfordDeviation());
		System.out.println(bi.calculateBenfordChiSquare());
		TTest tTest = new TTest();
		System.out.println("H-tTest:\t" + tTest.homoscedasticTTest(bi.benfordFrequencies, BenfordFactory.EXPECTED_BENFORD_FREUQNECIES));
		System.out.println("P-tTest:\t" + tTest.pairedTTest(bi.benfordFrequencies, BenfordFactory.EXPECTED_BENFORD_FREUQNECIES));
		System.out.println("tTest:\t" + tTest.tTest(bi.benfordFrequencies, BenfordFactory.EXPECTED_BENFORD_FREUQNECIES));
		System.out.println("H-t:\t" + tTest.homoscedasticT(bi.benfordFrequencies, BenfordFactory.EXPECTED_BENFORD_FREUQNECIES));
		System.out.println("P-t:\t" + tTest.pairedT(bi.benfordFrequencies, BenfordFactory.EXPECTED_BENFORD_FREUQNECIES));
		System.out.println("t:\t" + tTest.t(bi.benfordFrequencies, BenfordFactory.EXPECTED_BENFORD_FREUQNECIES));
	}

	private final File file;

	private final BufferedImage image;

	private final int[] pixelRGB;

	private final int[] benfordCounts;

	private final double[] benfordFrequencies;

	public UnknownImage(File inputImage) throws IOException {
		this.file = inputImage;
		this.image = ImageIO.read(this.file);
		this.pixelRGB = new int[this.image.getWidth() * this.image.getHeight()];
		populateRGBPixels();
		this.benfordCounts = new int[9];
		populateBenfordCounts();
		this.benfordFrequencies = new double[9];
		populateBenfordFrequencies();
	}

	private void populateBenfordFrequencies() {
		for (int i = 0; i < this.benfordCounts.length; i++)
			this.benfordFrequencies[i] = (100.0 * benfordCounts[i]) / this.pixelRGB.length;
	}

	private void populateBenfordCounts() {
		for (int x : this.pixelRGB) {
			int digit = leadingDigit(x);
			if (digit > 0)
				benfordCounts[digit - 1]++;
		}
	}

	private void populateRGBPixels() {
		int index = 0;
		for (int x = 0; x < this.image.getWidth(); x++) {
			for (int y = 0; y < this.image.getHeight(); y++) {
				int rgb = this.image.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = rgb & 0xFF;
				String hexRGB = Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b);
				this.pixelRGB[index + y] = Integer.valueOf(hexRGB, 16);
			}
			index += this.image.getHeight();
		}
	}

	// Private Utility Methods
	private static int leadingDigit(int x) {
		while (x >= 10) {
			x = x / 10;
		}
		return x;
	}

	// Getters constant time access.
	public File getFile() {
		return file;
	}

	public BufferedImage getImage() {
		return image;
	}

	public int[] getPixelRGB() {
		return pixelRGB;
	}

	public int[] getBenfordCounts() {
		return benfordCounts;
	}

	public double[] getBenfordFrequencies() {
		return benfordFrequencies;
	}

	// Calculated functions non-constant time access.
	public double calculateBenfordChiSquare() {
		long[] observed = new long[9];
		for (int i = 0; i < this.benfordFrequencies.length; i++) {
			observed[i] = (long)this.benfordFrequencies[i];
		}
		long[] observed2 = new long[9];
		for (int i = 0; i < BenfordFactory.EXPECTED_BENFORD_FREUQNECIES.length; i++) {
			observed2[i] = (long)BenfordFactory.EXPECTED_BENFORD_FREUQNECIES[i];
		}
		return new ChiSquareTest().chiSquareTestDataSetsComparison(observed, observed2) * 100.0;
	}
	
	public double calculateBenfordDeviation() {
		double deviation = 0.0;
		for (int i = 0; i < 9; i++) { 
			deviation += Math.abs(this.benfordFrequencies[i] - BenfordFactory.EXPECTED_BENFORD_FREUQNECIES[i]);
		}
		return deviation;
	}

	// No Setters, Immutable

	// HashCode and other required for efficiency.
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
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
		UnknownImage other = (UnknownImage) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}
}
