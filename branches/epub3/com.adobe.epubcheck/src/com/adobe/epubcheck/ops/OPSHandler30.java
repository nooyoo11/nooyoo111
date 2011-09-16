package com.adobe.epubcheck.ops;

import java.util.HashSet;
import java.util.Iterator;

import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.opf.OPFChecker;
import com.adobe.epubcheck.opf.OPFChecker30;
import com.adobe.epubcheck.opf.XRefChecker;
import com.adobe.epubcheck.util.EpubTypeAttributes;
import com.adobe.epubcheck.util.HandlerUtil;
import com.adobe.epubcheck.util.PathUtil;
import com.adobe.epubcheck.xml.XMLElement;
import com.adobe.epubcheck.xml.XMLParser;

public class OPSHandler30 extends OPSHandler {

	String properties;

	HashSet<String> prefixSet;

	HashSet<String> propertiesSet;

	String mimeType;

	boolean video = false;

	boolean audio = false;

	boolean hasValidFallback = false;

	public OPSHandler30(String path, String mimeType, String properties,
			XRefChecker xrefChecker, XMLParser parser, Report report) {
		super(path, xrefChecker, parser, report);
		this.mimeType = mimeType;
		this.properties = properties;
		prefixSet = new HashSet<String>();
		propertiesSet = new HashSet<String>();
	}

	boolean checkPrefix(String prefix) {
		prefix = prefix.trim();
		if (!prefixSet.contains(prefix)) {
			report.error(path, parser.getLineNumber(),
					parser.getColumnNumber(), "Undecleared prefix: " + prefix);
			return false;
		}
		return true;
	}

	private void checkType(String type) {
		if (type == null)
			return;
		type = type.replaceAll("[\\s]+", " ");

		String typeArray[] = type.split(" ");
		for (int i = 0; i < typeArray.length; i++)
			if (typeArray[i].contains(":"))
				checkPrefix(typeArray[i]
						.substring(0, typeArray[i].indexOf(':')));
			else if (!EpubTypeAttributes.EpubTypeSet.contains(typeArray[i]))
				report.error(path, parser.getLineNumber(),
						parser.getColumnNumber(), "Undefined epub:type: "
								+ typeArray[i]);
	}

	@Override
	public void characters(char[] chars, int arg1, int arg2) {
		super.characters(chars, arg1, arg2);
		String str = new String(chars, arg1, arg2);
		str = str.trim();
		if (!str.equals("") && (audio || video))
			hasValidFallback = true;
	}

	public void startElement() {
		super.startElement();
		XMLElement e = parser.getCurrentElement();
		String name = e.getName();

		processSrc(e.getName(), e.getAttribute("src"));

		if (name.equals("html"))
			HandlerUtil.processPrefixes(
					e.getAttributeNS("http://www.idpf.org/2007/ops", "prefix"),
					prefixSet, report, path, parser.getLineNumber(),
					parser.getColumnNumber());
		else if (name.equals("object"))
			processObject(e);
		else if (name.equals("math"))
			propertiesSet.add("mathml");
		else if (!mimeType.equals("image/svg+xml") && name.equals("svg"))
			propertiesSet.add("svg");
		else if (name.equals("script"))
			propertiesSet.add("scripted");
		else if (name.equals("switch"))
			propertiesSet.add("switch");
		else if (name.equals("audio"))
			processAudio(e);
		else if (name.equals("video"))
			processVideo(e);

		checkType(e.getAttributeNS("http://www.idpf.org/2007/ops", "type"));
	}

	private void processAudio(XMLElement e) {
		audio = true;
	}

	private void processVideo(XMLElement e) {
		video = true;

		String posterSrc = e.getAttribute("poster");

		String posterMimeType = null;
		if (xrefChecker != null && posterSrc != null)
			posterMimeType = xrefChecker.getMimeType(PathUtil
					.resolveRelativeReference(path, posterSrc));

		if (posterMimeType != null
				&& !OPFChecker.isBlessedImageType(posterMimeType))
			report.error(path, parser.getLineNumber(),
					parser.getColumnNumber(),
					"Video poster must have core media image type!");

		if (posterSrc != null) {
			hasValidFallback = true;
			processSrc(e.getName(), posterSrc);
		}

	}

	private void processSrc(String name, String src) {
		if (src == null || xrefChecker == null)
			return;

		if (src.startsWith("http://"))
			propertiesSet.add("remote-resources");
		else
			src = PathUtil.resolveRelativeReference(path, src);

		xrefChecker.registerReference(path, parser.getLineNumber(),
				parser.getColumnNumber(), src, XRefChecker.RT_GENERIC);

		String srcMimeType = xrefChecker.getMimeType(src);

		if (srcMimeType == null)
			return;

		if (!mimeType.equals("image/svg+xml")
				&& srcMimeType.equals("image/svg+xml"))
			propertiesSet.add("svg");

		if ((name.equals("source") || name.equals("audio")) && audio
				&& OPFChecker30.isBlessedAudioType(srcMimeType))
			hasValidFallback = true;

	}

	private void processObject(XMLElement e) {
		String type = e.getAttribute("type");

		if (!mimeType.equals("image/svg+xml") && type.equals("image/svg+xml"))
			propertiesSet.add("svg");

	}

	@Override
	public void endElement() {
		super.endElement();
		XMLElement e = parser.getCurrentElement();
		String name = e.getName();
		if (name.equals("html") || name.equals("svg"))
			checkProperties();
		else if (name.equals("video"))
			checkFallback("Video");
		else if (name.equals("audio"))
			checkFallback("Audio");

	}

	/*
	 * This function checks fallbacks for video and audio resources
	 */
	private void checkFallback(String elementType) {
		if (hasValidFallback)
			hasValidFallback = false;
		else
			report.error(path, parser.getLineNumber(),
					parser.getColumnNumber(), elementType
							+ " element doesn't provide fallback!");
	}

	private void checkProperties() {
		if (properties != null && properties.equals("singleFileValidation"))
			return;
		if (properties != null)
			properties = properties.replaceAll("nav", "");
		Iterator<String> propertyIterator = propertiesSet.iterator();
		while (propertyIterator.hasNext()) {
			String prop = propertyIterator.next();
			if (properties != null && properties.contains(prop))
				properties = properties.replaceAll(prop, "");
			else
				report.error(path, 0, 0,
						"This file should declare in opf the property: " + prop);
		}
		if (properties != null)
			properties = properties.trim();
		if (properties != null && !properties.equals(""))
			report.error(path, 0, 0,
					"This file should not declare in opf the properties: "
							+ properties);

	}
}
