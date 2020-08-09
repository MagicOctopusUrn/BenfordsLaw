package org.cc.benford;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BenfordFactoryThread extends Thread {
	private final int threadNumber;
	
	private final List<File> imageList;
	
	public BenfordFactoryThread(int threadNumber, List<File> imageList) {
		this.threadNumber = threadNumber;
		this.imageList = new ArrayList<File>(imageList);
	}
	
	@Override
	public void start() {
		
	}
}
