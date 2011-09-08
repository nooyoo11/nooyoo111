package com.adobe.epubcheck.ops;

import java.util.HashSet;

import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.opf.XRefChecker;
import com.adobe.epubcheck.util.EpubTypeAttributes;
import com.adobe.epubcheck.util.HandlerUtil;
import com.adobe.epubcheck.xml.XMLElement;

public class OPSHandler30 extends OPSHandler {

	String path;

	XRefChecker xrefChecker;

	Report report;

	int line;

	int column;

	HashSet<String> prefixSet;

	public OPSHandler30(String path, XRefChecker xrefChecker, Report report) {
		super(path, xrefChecker, report);
		this.path = path;
		this.xrefChecker = xrefChecker;
		this.report = report;
		prefixSet = new HashSet<String>();
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
		if (name.equals("html"))
			HandlerUtil.processPrefixes(
					e.getAttributeNS("http://www.idpf.org/2007/ops", "prefix"),
					prefixSet, report, path, line, column);
		checkType(e.getAttributeNS("http://www.idpf.org/2007/ops", "type"));
	}
}
