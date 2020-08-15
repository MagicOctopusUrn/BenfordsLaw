package org.cc.benford;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.cc.image.UnknownImage;

public class BenfordFactory {
	// Static direction creation to ensure it always exists.
	{
		if (!BenfordFactory.BENFORD_MATCHES.exists()) {
			BenfordFactory.BENFORD_MATCHES.mkdirs();
		}
		if (!BenfordFactory.BENFORD_NONMATCHES.exists()) {
			BenfordFactory.BENFORD_NONMATCHES.mkdirs();
		}
	}

	public static void main(String[] args) {
		BenfordFactory benfordFactory = new BenfordFactory(BenfordFactory.BENFORD_INPUT_FOLDER);
		benfordFactory.printResults();
	}
	
	public static final Double CHI_MATCH_THRESHOLD = 95.0;
	
	/**
	 * The expected Benford distribution of any truly random multi-variable integers that span
	 * multiple powers of 10. Any large sample of cumulative integers free of outside influence should be
	 * distributed according to this sequence.
	 */
	// TODO externalize this into XML configuration for multiple separate statistical curves.
	// TODO have a curve for different effects to attempt to to the following:
	//  - profile each filter individually, and come up with a curve to identify them.
	//  - do this across multiple images and get the average result.
	//  - ensure the seed images are a 99.9% or above match to the default benford curve.
	//  - calculate combined probability curves to detect combinations of changes to a single image.
	//  - Output most likely statistical fit to the combination of effects, if nothing fits...
	//  PHASE 2 BEGINS IF NOTHING FITS!
	//    - Have separate training vectors for non natural images, and try to figure out a style maybe.
	//    - Create separate statistical training vectors for different art styles, or try to define my own.
	//    - See if I can make statistical vectors for certain artists as well, versus others.
	//  PHASE 3 BEGINS IF NOTHING FITS!
	//     - Use google image search to find the 10 top similar internet images.
	//     - determine how truly similar they are to the image.
	//     - see if any of them have a higher benford score.
	//     - if they do... maybe we've actually found the original source!
	public static double[] EXPECTED_BENFORD_FREUQNECIES = {30.1, 17.6, 12.5, 9.7, 7.9, 6.7, 5.8, 5.1, 4.6};
	
	public static final File BENFORD_INPUT_FOLDER = new File("C:\\Users\\anony\\Desktop\\BenfordTestImages\\500WallPaperTest");
	
	public static final File BENFORD_OUTPUT_FOLDER = new File("C:\\Users\\anony\\Desktop\\BenfordTestImages\\OutputImages");
	
	public static final File BENFORD_MATCHES = new File(BenfordFactory.BENFORD_OUTPUT_FOLDER + File.separator + "matches_" + BenfordFactory.CHI_MATCH_THRESHOLD);
	
	public static final File BENFORD_NONMATCHES = new File(BenfordFactory.BENFORD_OUTPUT_FOLDER + File.separator + "nonmatches_" + BenfordFactory.CHI_MATCH_THRESHOLD);
	
	private final List<File> inputFiles;
	
	private final List<UnknownImage> unknownImages;
	
	public BenfordFactory (File inputFolder) {
		this(Arrays.asList(inputFolder.listFiles()), -1);
		System.out.println("Processing all images from folder:\t" + inputFolder.getName());
	}
	
	public BenfordFactory (List<File> inputFiles, int t) {
		this.inputFiles = new ArrayList<File>(inputFiles);
		this.unknownImages = new ArrayList<UnknownImage>(0);
		this.populateUnknownImages(t);
	}
	
	private void populateUnknownImages(int t) {
		if (t == -1 || t == 0) {
			System.out.printf("%4s|%5s|%60s|%6s|%6s|%6s|%6s|%6s|%6s|%6s|%6s|%6s|%6s|%14s|\n",
				"T", "#", "File Name", "f[0]","f[1]","f[2]","f[3]","f[4]","f[5]","f[6]","f[7]","f[8]",
				"S-Dev", "Chi^2");
		}
		int i = 0;
		for (File inputImage : this.inputFiles) {
			if (inputImage.isFile()) {
				try {
					String imgurName = inputImage.getName();
					try {
						imgurName = "http://i.imgur.com/" + imgurName.split("\\s")[imgurName.split("\\s").length-1];
					} catch (Exception e) {
						// Ignore, use base file name, this is for Imgur images.
					}
					UnknownImage image = new UnknownImage(inputImage);
					double chiSquareBenford = image.calculateBenfordChiSquare();
					double[] f = image.getBenfordFrequencies();
					System.out.printf("T%3s|%5d|%60s|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6.1f|%6s%8s|\n", t,
							i++, imgurName,	f[0],f[1],f[2],f[3],f[4],f[5],f[6],f[7],f[8],
							image.calculateBenfordDeviation(), 
							((chiSquareBenford > BenfordFactory.CHI_MATCH_THRESHOLD) ? "[Pass]" : "[Fail]"),
							String.format("%.2f",chiSquareBenford) + "%");
					if (chiSquareBenford > BenfordFactory.CHI_MATCH_THRESHOLD) {
						FileUtils.copyFile(image.getFile(), new File(BenfordFactory.BENFORD_MATCHES.getAbsolutePath() 
								+ File.separator + /*String.format("%.5f",chiSquareBenford) + "(" + */image.getFile().getName() + "." 
								+ FilenameUtils.getExtension(image.getFile().getName())), true);
					}  else {
						FileUtils.copyFile(image.getFile(), new File(BenfordFactory.BENFORD_NONMATCHES.getAbsolutePath() 
								+ File.separator + /*String.format("%.5f",chiSquareBenford) + "(" + */image.getFile().getName() + "." 
								+ FilenameUtils.getExtension(image.getFile().getName())), true);
					}
					this.unknownImages.add(image);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void printResults() {
		System.out.println("Processing completed, sorting and printing results...");
		
		List<UnknownImage> sortedImages = new ArrayList<UnknownImage>(this.unknownImages);
		Collections.sort(sortedImages, new Comparator<UnknownImage>() {
			@Override
			public int compare(UnknownImage a, UnknownImage b) {
				return (int)(b.calculateBenfordChiSquare()-a.calculateBenfordChiSquare());
			}
		});
		for (UnknownImage benfordImage : sortedImages) {
			Double chiSquare = benfordImage.calculateBenfordChiSquare();
			System.out.println("Input Image:\t" + benfordImage.getFile().getName() + " matches the Benford distribution " + chiSquare + "% of the way.");
			if (chiSquare > BenfordFactory.CHI_MATCH_THRESHOLD) {
				System.out.println("[P(original) = " + chiSquare + "% > " + BenfordFactory.CHI_MATCH_THRESHOLD + "%]:\tImage has likely not been tampered with from the original state.");
			}  else {
				System.out.println("[P(original) = " + chiSquare + "% < " + BenfordFactory.CHI_MATCH_THRESHOLD + "%]:\tImage has been tampered with, compressed or was digitally generated.\r\n"
						+ "It is also possible the image contains not enough discrete and different data-points.\r\n"
						+ "E.G. A shot of darkness will not pass, there must be a range of colors to define randomness.");
			}
			for (int x = 0; x < 9; x++) {
				String bar = "";
				for (int y = 0; y < benfordImage.getBenfordFrequencies()[x]; y++) {
					bar += "X";
				}
				System.out.printf("[%10d | %4.1f%%]\t%1d:\t%s\n", benfordImage.getBenfordCounts()[x], benfordImage.getBenfordFrequencies()[x], x+1, bar);
			}
		}
	}
	
	// Getters, no setters, immutable for threading.
	public List<File> getInputFiles() {
		return inputFiles;
	}

	public List<UnknownImage> getUnknownImages() {
		return unknownImages;
	}
}
