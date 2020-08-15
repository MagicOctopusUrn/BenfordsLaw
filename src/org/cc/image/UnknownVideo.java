package org.cc.image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UnknownVideo {
	private final List<UnknownImage> frames;
	
	private final File file;
	
	public UnknownVideo (File inputVideo) { 
		this.file = inputVideo;
		this.frames = new ArrayList<UnknownImage>();
	}
}
