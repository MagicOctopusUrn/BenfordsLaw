package org.cc.benford;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class ThreadedBenfordFactory {
	{
		if (!ThreadedBenfordFactory.BENFORD_MATCHES.exists()) {
			ThreadedBenfordFactory.BENFORD_MATCHES.mkdirs();
		}
		if (!ThreadedBenfordFactory.BENFORD_NONMATCHES.exists()) {
			ThreadedBenfordFactory.BENFORD_NONMATCHES.mkdirs();
		}
	}
	
	public static final int NUM_THREADS = 100;
	
	private static final Double CHI_MATCH_THRESHOLD = 5.0;
	
	/**
	 * The expected Benford distribution of any truly random multi-variable integers that span
	 * multiple powers of 10. Any large sample of cumulative integers free of outside influence should be
	 * distributed according to this sequence.
	 */
	public static double[] EXPECTED_BENFORD_FREUQNECIES = {30.1, 17.6, 12.5, 9.7, 7.9, 6.7, 5.8, 5.1, 4.6};
	
	public static final File BENFORD_INPUT_FOLDER = new File("C:\\Users\\anony\\Desktop\\BenfordTestImages\\all_images_raw_2");
	
	public static final File BENFORD_MATCHES = new File(ThreadedBenfordFactory.BENFORD_INPUT_FOLDER + File.separator + "matches_" + ThreadedBenfordFactory.CHI_MATCH_THRESHOLD);
	
	public static final File BENFORD_NONMATCHES = new File(ThreadedBenfordFactory.BENFORD_INPUT_FOLDER + File.separator + "nonmatches_" + ThreadedBenfordFactory.CHI_MATCH_THRESHOLD);
	
	
	public static void main(String[] args) throws IOException {
		
		
		System.out.println("Processing all images from folder:\t" + ThreadedBenfordFactory.BENFORD_INPUT_FOLDER.getName());

		System.out.printf("%5s|%60s|%6s|%6s|%6s|%6s|%6s|%6s|%6s|%6s|%6s|%6s|%14s|\n",
				"#", "File Name", "f[0]","f[1]","f[2]","f[3]","f[4]","f[5]","f[6]","f[7]","f[8]",
				"S-Dev", "Chi^2");
		Map<UnknownImage, Double> sortedImages = new HashMap<UnknownImage, Double>(0);
		int i = 0;
		for (File inputImage : 
			ThreadedBenfordFactory.BENFORD_INPUT_FOLDER.listFiles()) {
			if (inputImage.isFile()) {
				UnknownImage image = new UnknownImage(inputImage);
				double chiSquareBenford = image.calculateBenfordChiSquare();
				sortedImages.put(image, chiSquareBenford);
				double[] f = image.getBenfordFrequencies();
				System.out.printf("%5d|%60s|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6s%8s|\n",
						i++, image.getFile().getName(),	f[0],f[1],f[2],f[3],f[4],f[5],f[6],f[7],f[8],
						image.calculateBenfordDeviation(), 
						((chiSquareBenford > ThreadedBenfordFactory.CHI_MATCH_THRESHOLD) ? "[Pass]" : "[Fail]"),
						String.format("%.2f",chiSquareBenford) + "%");
				if (chiSquareBenford > ThreadedBenfordFactory.CHI_MATCH_THRESHOLD) {
					FileUtils.copyFile(image.getFile(), new File(ThreadedBenfordFactory.BENFORD_MATCHES.getAbsolutePath() 
							+ File.separator + i + " - " + String.format("%.5f",chiSquareBenford) + "(" + image.getFile().getName() + ")."
							+ FilenameUtils.getExtension(image.getFile().getName())), true);
				}  else {
					FileUtils.copyFile(image.getFile(), new File(ThreadedBenfordFactory.BENFORD_NONMATCHES.getAbsolutePath() 
							+ File.separator + i + " - " + String.format("%.5f",chiSquareBenford) + "(" + image.getFile().getName() + ")." 
							+ FilenameUtils.getExtension(image.getFile().getName())), true);
				}
			}
		}
		
		System.out.println("Processing completed, sorting and printing results...");
		
		List<Entry<UnknownImage, Double>> benfordResults = new ArrayList<Entry<UnknownImage, Double>>(sortedImages.entrySet()); 
		Collections.sort(benfordResults, new Comparator<Entry<UnknownImage, Double>>() {
			@Override
			public int compare(Entry<UnknownImage, Double> a, Entry<UnknownImage, Double> b) {
				return b.getValue().compareTo(a.getValue());
			}
		});
		for (Entry<UnknownImage, Double> result : benfordResults) {
			UnknownImage benfordImage = result.getKey();
			Double chiSquare = result.getKey().calculateBenfordChiSquare();
			System.out.println("Input Image:\t" + benfordImage.getFile().getName() + " matches the Benford distribution " + chiSquare + "% of the way.");
			if (chiSquare > ThreadedBenfordFactory.CHI_MATCH_THRESHOLD) {
				System.out.println("[P(original) = " + (100.0-chiSquare) + "% < " + ThreadedBenfordFactory.CHI_MATCH_THRESHOLD + "%]:\tImage has likely not been tampered with from the original state.");
			}  else {
				System.out.println("[P(original) = " + (100.0-chiSquare) + "% > " + ThreadedBenfordFactory.CHI_MATCH_THRESHOLD + "%]:\tImage has been tampered with, compressed or was digitally generated.\r\n"
						+ "It is also possible the image contains not enough discrete and different data-points.\r\n"
						+ "E.G. A shot of darkness will not pass, there must be a range of colors to define randomness.");
			}
			for (int x = 0; x < 9; x++) {
				String bar = "";
				for (int y = 0; y < benfordImage.getBenfordFrequencies()[x]; y++) {
					bar += "X";
				}
				System.out.printf("[%10d | %4.1f%%]\t%1d:\t%s\n", benfordImage.getBenfordCounts()[x], benfordImage.getBenfordFrequencies()[x], x, bar);
			}
		}
	}
}
