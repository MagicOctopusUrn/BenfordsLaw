module BenfordsLaw {
	requires java.desktop;
	requires commons.math3;
	requires org.apache.commons.io;
	requires org.bytedeco.javacv;
	requires org.bytedeco.opencv;
	requires java.xml;
	requires jdom;
	requires java.base;
	requires com.google.gson;
	requires httpcore5;
	requires httpclient5;
	requires jfreechart;
	requires jcommon;
	exports org.cc.stocks;
	opens org.cc.stocks;
}