package com.adobe.epubcheck.overlay;

import java.util.HashSet;

import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.opf.OPFChecker30;
import com.adobe.epubcheck.opf.XRefChecker;
import com.adobe.epubcheck.util.EpubTypeAttributes;
import com.adobe.epubcheck.util.HandlerUtil;
import com.adobe.epubcheck.util.PathUtil;
import com.adobe.epubcheck.xml.XMLElement;
import com.adobe.epubcheck.xml.XMLHandler;

public class OverlayHandler implements XMLHandler {

	String path;

	XRefChecker xrefChecker;

	Report report;

	HashSet<String> prefixSet;

	int line;

	int column;

	public OverlayHandler(String path, XRefChecker xrefChecker, Report report) {
		this.path = path;
		this.xrefChecker = xrefChecker;
		this.report = report;
		prefixSet = new HashSet<String>();
	}

	public void startElement(XMLElement e, int line, int column) {
		this.line = line;
		this.column = column;
		String name = e.getName();

		if (name.equals("smil"))
			HandlerUtil.processPrefixes(
					e.getAttributeNS("http://www.idpf.org/2007/ops", "prefix"),
					prefixSet, report, path, line, column);
		else if (name.equals("seq"))
			processSeq(e);
		else if (name.equals("text"))
			processSrc(e);
		else if (name.equals("audio"))
			processRef(e.getAttribute("src"), XRefChecker.RT_AUDIO);
		else if (name.equals("body") || name.equals("par"))
			checkType(e.getAttributeNS("http://www.idpf.org/2007/ops", "type"));
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

	private void processSrc(XMLElement e) {
		processRef(e.getAttribute("src"), XRefChecker.RT_HYPERLINK);

	}

	private void processRef(String ref, int type) {
		if (ref != null && xrefChecker != null) {
			ref = PathUtil.resolveRelativeReference(path, ref);
			if (type == XRefChecker.RT_AUDIO) {
				String mimeType = xrefChecker.getMimeType(ref);
				if (mimeType != null
						&& !OPFChecker30.isBlessedAudioType(mimeType))
					report.error(path, line, column,
							"Media Overlay audio refernence " + ref
									+ " to non-standard audio type " + mimeType);
			}
			xrefChecker.registerReference(path, line, column, ref, type);
		}
	}

	private void processSeq(XMLElement e) {
		processRef(e.getAttributeNS("http://www.idpf.org/2007/ops", "textref"),
				XRefChecker.RT_HYPERLINK);
		checkType(e.getAttributeNS("http://www.idpf.org/2007/ops", "type"));
	}

	public void characters(char[] chars, int arg1, int arg2, XMLElement e,
			int line, int column) {
	}

	public void endElement(XMLElement e, int line, int column) {
	}

	public void ignorableWhitespace(char[] chars, int arg1, int arg2,
			XMLElement e, int line, int column) {
	}

	public void processingInstruction(String arg0, String arg1, XMLElement e,
			int line, int column) {
	}

}
