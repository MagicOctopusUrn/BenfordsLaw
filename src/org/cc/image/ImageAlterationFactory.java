package org.cc.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.cc.benford.BenfordFactory;
import org.cc.categorization.Alteration;
import org.cc.config.XMLAlterationConfig;
import org.cc.config.XMLConfig;
import org.jdom2.JDOMException;

public class ImageAlterationFactory {
	public static void main(String[] args) throws JDOMException, IOException {
		File inputFolder = new File("C:\\Users\\anony\\Desktop\\BenfordTestImages\\OutputImages\\SingleImageTest\\INPUT");
		File outputFolder = new File("C:\\Users\\anony\\Desktop\\BenfordTestImages\\OutputImages\\SingleImageTest\\");
		System.out.println("Loading config...");
		XMLConfig config = new XMLConfig(XMLConfig.DEFAULT_XML_CONFIG_FILE);
		
		Map<Integer, XMLAlterationConfig> aMap = config.getAlterationConfigurations();
		Alteration[] customAlterationChain = {
				aMap.get(9).getData(),
				aMap.get(304).getData(),
				aMap.get(406).getData()
				
		};
		
		System.out.println("Loading " + inputFolder.getName() + "...");
		for (File inputImage : inputFolder.listFiles()) {
			BufferedImage testImage = ImageIO.read(inputImage);
			ImageAlterationFactory iaf = new ImageAlterationFactory(testImage);
			
			System.out.println("\tLoading " + inputImage.getName() + "...");
			for (XMLAlterationConfig alterationConfig : config.getAlterationConfigurations().values()) {
				Alteration alteration = alterationConfig.getData();
				System.out.println("\t\tApplying " + alteration.getName() + "...");
				
				File alterationOutputFolder = new File(outputFolder.getAbsolutePath() + File.separator + alteration.getName());
				if (!alterationOutputFolder.exists()) {
					alterationOutputFolder.mkdirs();
				}
				String outputFileName = alterationOutputFolder.getAbsolutePath() + File.separator + inputImage.getName() + ".png";
				ImageIO.write(iaf.applyAlteration(alterationConfig.getData()), "PNG", new File(outputFileName));
			}
			
			// Do the custom chain
			System.out.println("\t\tApplying CUSTOM-CHAIN...");
			for (Alteration a : customAlterationChain) {
				System.out.println("\t\t\tApplying " + a.getName() + "...");
				iaf = new ImageAlterationFactory(iaf.applyAlteration(a));
			}
			
			File alterationOutputFolder = new File(outputFolder.getAbsolutePath() + File.separator + "CUSTOM-CHAIN");
			if (!alterationOutputFolder.exists()) {
				alterationOutputFolder.mkdirs();
			}
			String outputFileName = alterationOutputFolder.getAbsolutePath() + File.separator + inputImage.getName() + ".png";
			ImageIO.write(iaf.getImage(), "PNG", new File(outputFileName));
		}

		System.out.printf("%3s|%25s|%6s|%6s|%6s|%6s|%6s|%6s|%6s|%6s|%6s|%6s\n", 
				"ID","Alteration Name","f[0]","f[1]","f[2]","f[3]","f[4]","f[5]","f[6]","f[7]","f[8]","Match");
		for (XMLAlterationConfig alterationConfig : config.getAlterationConfigurations().values()) {
			Alteration alteration = alterationConfig.getData();
			File alterationOutputFolder = new File(outputFolder.getAbsolutePath() + File.separator + alteration.getName());
			double[] f = new double[9];
			for (File image : alterationOutputFolder.listFiles()) {
				UnknownImage ui = new UnknownImage(image);
				for (int i = 0; i < 9; i++) {
					f[i] += ui.getBenfordFrequencies()[i];
				}
			}
			for (int i = 0; i < 9; i++) {
				f[i] /= alterationOutputFolder.listFiles().length;
			}

			System.out.printf("%3d|%25s|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f%%\n", 
					alterationConfig.getId(), alteration.getName(), f[0],f[1],f[2],f[3],f[4],f[5],f[6],f[7],f[8],
					calculateChiSquare(BenfordFactory.EXPECTED_BENFORD_FREUQNECIES, f));
		}
		
		File alterationOutputFolder = new File(outputFolder.getAbsolutePath() + File.separator + "CUSTOM-CHAIN");
		double[] f = new double[9];
		for (File image : alterationOutputFolder.listFiles()) {
			UnknownImage ui = new UnknownImage(image);
			for (int i = 0; i < 9; i++) {
				f[i] += ui.getBenfordFrequencies()[i];
			}
		}
		for (int i = 0; i < 9; i++) {
			f[i] /= alterationOutputFolder.listFiles().length;
		}

		System.out.printf("%3d|%25s|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f%%\n", 
				666, "CUSTOM-CHAIN", f[0],f[1],f[2],f[3],f[4],f[5],f[6],f[7],f[8],
				calculateChiSquare(BenfordFactory.EXPECTED_BENFORD_FREUQNECIES, f));
	}
	
	private final BufferedImage image;

	public ImageAlterationFactory(BufferedImage image) {
		this.image = ImageAlterationFactory.deepCopy(image);
	}

	public BufferedImage applyAlteration(Alteration alteration) {
		try {
			return (BufferedImage) alteration.getMethod().invoke(this, alteration.getMagnitude());
		} catch (Exception ex) {
			System.err.println("[FATAL]\tInvalid method signature provided in configuration file.");
			ex.printStackTrace();
			System.exit(-1);
		}
		return this.image;
	}

	public BufferedImage brighten(double magnitude) {
		BufferedImage saturationCopy = ImageAlterationFactory.deepCopy(this.image);
		for (int x = 0; x < saturationCopy.getWidth(); x++) {
			for (int y = 0; y < saturationCopy.getHeight(); y++) {
				int rgb = this.image.getRGB(x, y);
		        int r = (rgb >> 16) & 0xFF;
		        int g = (rgb >> 8) & 0xFF;
		        int b = (rgb) & 0xFF;

		        HSLColor hsl = new HSLColor(new Color(r, g, b));
		        
				saturationCopy.setRGB(x, y, hsl.adjustTone((float)magnitude).getRGB());
			}
		}
		return saturationCopy;
	}

	public BufferedImage darken(double magnitude) {
		BufferedImage saturationCopy = ImageAlterationFactory.deepCopy(this.image);
		for (int x = 0; x < saturationCopy.getWidth(); x++) {
			for (int y = 0; y < saturationCopy.getHeight(); y++) {
				int rgb = this.image.getRGB(x, y);
		        int r = (rgb >> 16) & 0xFF;
		        int g = (rgb >> 8) & 0xFF;
		        int b = (rgb) & 0xFF;

		        HSLColor hsl = new HSLColor(new Color(r, g, b));
		        
				saturationCopy.setRGB(x, y, hsl.adjustShade((float)magnitude).getRGB());
			}
		}
		return saturationCopy;
	}

	public BufferedImage saturate(double magnitude) {
		BufferedImage saturationCopy = ImageAlterationFactory.deepCopy(this.image);
		for (int x = 0; x < saturationCopy.getWidth(); x++) {
			for (int y = 0; y < saturationCopy.getHeight(); y++) {
				int rgb = this.image.getRGB(x, y);
		        int r = (rgb >> 16) & 0xFF;
		        int g = (rgb >> 8) & 0xFF;
		        int b = (rgb) & 0xFF;

		        HSLColor hsl = new HSLColor(new Color(r, g, b));
		        
				saturationCopy.setRGB(x, y, hsl.saturate((float)magnitude).getRGB());
			}
		}
		return saturationCopy;
	}

	public BufferedImage desaturate(double magnitude) {
		BufferedImage saturationCopy = ImageAlterationFactory.deepCopy(this.image);
		for (int x = 0; x < saturationCopy.getWidth(); x++) {
			for (int y = 0; y < saturationCopy.getHeight(); y++) {
				int rgb = this.image.getRGB(x, y);
		        int r = (rgb >> 16) & 0xFF;
		        int g = (rgb >> 8) & 0xFF;
		        int b = (rgb) & 0xFF;

		        HSLColor hsl = new HSLColor(new Color(r, g, b));
		        
				saturationCopy.setRGB(x, y, hsl.desaturate((float)magnitude).getRGB());
			}
		}
		return saturationCopy;
	}

	public BufferedImage hueOffset(double magnitude) {
		BufferedImage saturationCopy = ImageAlterationFactory.deepCopy(this.image);
		for (int x = 0; x < saturationCopy.getWidth(); x++) {
			for (int y = 0; y < saturationCopy.getHeight(); y++) {
				int rgb = this.image.getRGB(x, y);
		        int r = (rgb >> 16) & 0xFF;
		        int g = (rgb >> 8) & 0xFF;
		        int b = (rgb) & 0xFF;

		        HSLColor hsl = new HSLColor(new Color(r, g, b));
		        
				saturationCopy.setRGB(x, y, hsl.offsetHue((float)magnitude).getRGB());
			}
		}
		return saturationCopy;
	}
	
	public BufferedImage nothing(double magnitude) {
		return this.image;
	}
	
	public BufferedImage getImage() {
		return this.image;
	}

	private static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	private static double calculateChiSquare(double[] a, double[] b) {
		long[] observed = new long[9];
		for (int i = 0; i < a.length; i++) {
			observed[i] = (long)a[i];
		}
		long[] observed2 = new long[9];
		for (int i = 0; i < b.length; i++) {
			observed2[i] = (long)b[i];
		}
		try {
			return new ChiSquareTest().chiSquareTestDataSetsComparison(observed, observed2) * 100.0;
		} catch (ZeroException ze) {
			return 0.0;
		}
	}
}
