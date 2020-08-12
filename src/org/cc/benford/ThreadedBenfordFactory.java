package org.cc.benford;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cc.image.UnknownImage;

public class ThreadedBenfordFactory {
	public static final File THREADED_BENFORD_INPUT_FOLDER = new File("C:\\Users\\anony\\Desktop\\BenfordTestImages\\1900TestMaverick");
	
	public static void main(String[] args) {
		ThreadedBenfordFactory tbf = 
				new ThreadedBenfordFactory(ThreadedBenfordFactory.THREADED_BENFORD_INPUT_FOLDER);
		tbf.startThreads();
		tbf.waitForThreads();
		tbf.printResults();
		
	}
	
	private final static Integer NUM_THREADS = 19;
	
	private final File inputFolder;
	
	private final Map<Integer, List<File>> threadFiles;
	
	private final Map<Integer, BenfordFactoryThread> threads;
	
	public ThreadedBenfordFactory (File inputFolder) {
		this.inputFolder = inputFolder;
		this.threadFiles = new HashMap<Integer, List<File>>(0);
		this.threads = new HashMap<Integer, BenfordFactoryThread>(0);
		createBuckets();
	}
	
	public void printResults() {
		System.out.println("Processing completed, sorting and printing results...");
		
		List<UnknownImage> sortedImages = new ArrayList<UnknownImage>(0);
		for (BenfordFactoryThread bft : this.threads.values()) {
			if (bft.getFactory() != null) {
				sortedImages.addAll(bft.getFactory().getUnknownImages());
			} else {
				System.err.println(bft.getThreadNumber() + " died probably cause heap-space and didn't create a factory.");
			}
		}
		
		Collections.sort(sortedImages, new Comparator<UnknownImage>() {
			@Override
			public int compare(UnknownImage a, UnknownImage b) {
				return Double.valueOf(b.calculateBenfordChiSquare()).compareTo(a.calculateBenfordChiSquare());
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

	public void waitForThreads() {
		for (BenfordFactoryThread t : this.threads.values()) {
			while (t.isAlive()) {
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void startThreads() {
		for (int i = 0; i < ThreadedBenfordFactory.NUM_THREADS; i++) {
			BenfordFactoryThread bft = new BenfordFactoryThread(i, this.threadFiles.get(i));
			this.threads.put(i, bft);
			bft.start();
		}
	}

	private void createBuckets() {
		int i = 0;
		for (File f : this.inputFolder.listFiles()) {
			int radix = i++ % ThreadedBenfordFactory.NUM_THREADS;
			if (this.threadFiles.get(radix) == null) {
				this.threadFiles.put(radix, new ArrayList<File>(0));
			}
			this.threadFiles.get(radix).add(f);
		}
	}
}
