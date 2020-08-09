package org.cc.benford;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.cc.image.HSLColor;

public class UnknownImage {
	public static void main(String[] args) throws IOException {
		File file = new File(
				"C:\\Users\\anony\\Desktop\\BenfordTestImages\\SingleTest\\test.png");
		UnknownImage bi = new UnknownImage(file);
		for (int i : bi.getPixelRGB()) {
			if (i != UnknownImage.MAX_PIXEL_VALUE && i != UnknownImage.MIN_PIXEL_VALUE) {
				System.out.println(i);
			}
		}
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
	private final static int MAX_PIXEL_VALUE = Integer.valueOf("FFFFFF", 16);

	private final static int MIN_PIXEL_VALUE = Integer.valueOf("000000", 16);
	
	private final File file;

	private final int[] pixelRGB;

	private final int[] benfordCounts;
	
	private final int deadPixelCount;

	private final double[] benfordFrequencies;

	public UnknownImage(File inputImage) throws IOException {
		this.file = inputImage;
		BufferedImage image = ImageIO.read(this.file);
		this.pixelRGB = new int[image.getWidth() * image.getHeight()];
		populateRGBPixels(image);
		this.deadPixelCount = calculateDeadPixelCount();
		this.benfordCounts = new int[9];
		populateBenfordCounts();
		this.benfordFrequencies = new double[9];
		populateBenfordFrequencies();
	}

	private void populateBenfordFrequencies() {
		for (int i = 0; i < this.benfordCounts.length; i++) {
			this.benfordFrequencies[i] = (100.0 * this.benfordCounts[i])
					/ (this.pixelRGB.length - this.deadPixelCount);
		}
	}
	
	private int calculateDeadPixelCount() {
		int count = 0;
		for (int x : this.pixelRGB) {
			if (x == UnknownImage.MAX_PIXEL_VALUE || x == UnknownImage.MIN_PIXEL_VALUE) {
				count++;
			}
		}
		return count;
	}

	private void populateBenfordCounts() {
		for (int x : this.pixelRGB) {
			if (x != UnknownImage.MAX_PIXEL_VALUE && x != UnknownImage.MIN_PIXEL_VALUE) {
				int digit = leadingDigit(x); // TODO Make recursive to iterate through all digits.
				if (digit > 0) {
					benfordCounts[digit - 1]++;
				}
			}
		}
	}

	private void populateRGBPixels(BufferedImage image) {
		int index = 0;
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int rgb = image.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = rgb & 0xFF;
				HSLColor hsl = new HSLColor(new Color(r,g,b));
				/*--
				 * Shitshow doesnt adhere to benfords regularly due to organization of 
				 * color into 3 spectrums and prioritizing one over the other in numbering.
				 * Trying to use HSL to match human vision better and possibly just multiply
				 * the h s and l values to hash it instead.
				String hexRGB = String.format("%02x", r)
						+ String.format("%02x", g)
						+ String.format("%02x", b);
				int hexRgb = Integer.valueOf(hexRGB, 16);
				 */
				if (r == g && g == b && b == 0) {
					this.pixelRGB[index + y] = UnknownImage.MIN_PIXEL_VALUE;
				} else if (r == g && g == b && b == 255) {
					this.pixelRGB[index + y] = UnknownImage.MAX_PIXEL_VALUE;
				} else {
					this.pixelRGB[index + y] = (int)(hsl.getHSL()[0] * hsl.getHSL()[1] * hsl.getHSL()[2]) / 3;
				}
			}
			index += image.getHeight();
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

	public BufferedImage getImage() throws IOException {
		return ImageIO.read(this.file);
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
		try {
			return new ChiSquareTest().chiSquareTestDataSetsComparison(observed, observed2) * 100.0;
		} catch (ZeroException ze) {
			return 0.0;
		}
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
