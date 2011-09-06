package com.adobe.epubcheck.overlay;

import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.opf.XRefChecker;
import com.adobe.epubcheck.util.PathUtil;
import com.adobe.epubcheck.xml.XMLElement;
import com.adobe.epubcheck.xml.XMLHandler;

public class OverlayHandler implements XMLHandler {

	String path;

	XRefChecker xrefChecker;

	Report report;

	public OverlayHandler(String path, XRefChecker xrefChecker, Report report) {
		this.path = path;
		this.xrefChecker = xrefChecker;
		this.report = report;
	}

	public void startElement(XMLElement e, int line, int column) {
		String name = e.getName();
		String ref;
		if (name.equals("seq")) {
			ref = e.getAttributeNS("http://www.idpf.org/2007/ops", "textref");
			// TODO check if null ref is reported by schemas!
			if (ref != null) {
				ref = PathUtil.resolveRelativeReference(path, ref);
				xrefChecker.registerReference(path, line, column, ref,
						XRefChecker.RT_HYPERLINK);
			}
		} else if (name.equals("text")) {
			ref = e.getAttribute("src");
			// TODO check if null ref is reported by schemas!
			if (ref != null) {
				ref = PathUtil.resolveRelativeReference(path, ref);
				xrefChecker.registerReference(path, line, column, ref,
						XRefChecker.RT_HYPERLINK);
			}
		}
		else if (name.equals("audio")) {
			ref = e.getAttribute("src");
			// TODO check if null ref is reported by schemas!
			if (ref != null) {
				ref = PathUtil.resolveRelativeReference(path, ref);
				xrefChecker.registerReference(path, line, column, ref,
						XRefChecker.RT_AUDIO);
			}
		}

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
