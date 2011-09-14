package com.adobe.epubcheck.ops;

import java.util.HashSet;
import java.util.Iterator;

import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.opf.XRefChecker;
import com.adobe.epubcheck.util.EpubTypeAttributes;
import com.adobe.epubcheck.util.HandlerUtil;
import com.adobe.epubcheck.util.PathUtil;
import com.adobe.epubcheck.xml.XMLElement;

public class OPSHandler30 extends OPSHandler {

	String path;

	XRefChecker xrefChecker;

	Report report;

	int line;

	int column;

	String properties;

	HashSet<String> prefixSet;

	HashSet<String> propertiesSet;

	String mimeType;

	public OPSHandler30(String path, String mimeType, String properties,
			XRefChecker xrefChecker, Report report) {
		super(path, xrefChecker, report);
		this.path = path;
		this.mimeType = mimeType;
		this.properties = properties;
		this.xrefChecker = xrefChecker;
		this.report = report;
		prefixSet = new HashSet<String>();
		propertiesSet = new HashSet<String>();
	}

	boolean checkPrefix(String prefix) {
		prefix = prefix.trim();
		if (!prefixSet.contains(prefix)) {
			report.error(path, line, column, "Undecleared prefix: " + prefix);
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
				report.error(path, line, column, "Undefined epub:type: "
						+ typeArray[i]);
	}

	public void startElement(XMLElement e, int line, int column) {
		super.startElement(e, line, column);

		this.line = line;
		this.column = column;
		String name = e.getName();

		processSrc(e.getAttribute("src"));

		if (name.equals("html"))
			HandlerUtil.processPrefixes(
					e.getAttributeNS("http://www.idpf.org/2007/ops", "prefix"),
					prefixSet, report, path, line, column);
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

		checkType(e.getAttributeNS("http://www.idpf.org/2007/ops", "type"));
	}

	private void processSrc(String src) {
		if (src == null || xrefChecker == null)
			return;

		if (src.startsWith("http://"))
			propertiesSet.add("remote-resources");
		else
			src = PathUtil.resolveRelativeReference(path, src);

		xrefChecker.registerReference(path, line, column, src,
				XRefChecker.RT_GENERIC);

		String srcMimeType = xrefChecker.getMimeType(src);

		if (srcMimeType == null)
			return;

		if (!mimeType.equals("image/svg+xml")
				&& srcMimeType.equals("image/svg+xml"))
			propertiesSet.add("svg");

	}

	private void processObject(XMLElement e) {
		String type = e.getAttribute("type");

		if (!mimeType.equals("image/svg+xml") && type.equals("image/svg+xml"))
			propertiesSet.add("svg");

	}

	@Override
	public void endElement(XMLElement e, int line, int column) {
		String name = e.getName();
		if (name.equals("html") || name.equals("svg"))
			checkProperties();
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
