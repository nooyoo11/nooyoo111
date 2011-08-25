/*
 * Copyright (c) 2007 Adobe Systems Incorporated
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of
 *  this software and associated documentation files (the "Software"), to deal in
 *  the Software without restriction, including without limitation the rights to
 *  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *  the Software, and to permit persons to whom the Software is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.adobe.epubcheck.ops;

import java.io.IOException;

import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.ocf.OCFPackage;
import com.adobe.epubcheck.opf.ContentChecker;
import com.adobe.epubcheck.opf.XRefChecker;
import com.adobe.epubcheck.xml.SchematronXSLT2Validator;
import com.adobe.epubcheck.xml.SvrlParser;
import com.adobe.epubcheck.xml.XMLParser;
import com.adobe.epubcheck.xml.XMLValidator;

public class OPSChecker implements ContentChecker {

	OCFPackage ocf;

	Report report;

	String path;

	String mimeType;

	XRefChecker xrefChecker;

	float version;

	static XMLValidator xhtmlValidator = new XMLValidator("rng/ops20.nvdl");
	static XMLValidator svgValidator = new XMLValidator("rng/svg11.rng");

	static XMLValidator xhtmlValidator30 = new XMLValidator(
			"epub30schemas/epub-xhtml-30.rnc");
	static XMLValidator svgValidator30 = new XMLValidator(
			"epub30schemas/epub-svg-30.rnc");
	static XMLValidator mediaOverlayValidator30 = new XMLValidator(
			"epub30schemas/media-overlay-30.rnc");
	static XMLValidator navValidator30 = new XMLValidator(
			"epub30schemas/epub-nav-30.rnc");

	static String xhtmlSchematronValidator30 = new String(
			"epub30schemas/epub-xhtml-30.sch");
	static String svgSchematronValidator30 = new String(
			"epub30schemas/epub-svg-30.sch");
	static String mediaOverlaySchematronValidator30 = new String(
			"epub30schemas/media-overlay-30.sch");
	static String navSchematronValidator30 = new String(
			"epub30schemas/epub-nav-30.sch");

	public OPSChecker(OCFPackage ocf, Report report, String path,
			String mimeType, XRefChecker xrefChecker, float version) {
		this.ocf = ocf;
		this.report = report;
		this.path = path;
		this.xrefChecker = xrefChecker;
		this.mimeType = mimeType;
		this.version = version;
	}

	public void runChecks() {
		if (!ocf.hasEntry(path))
			report.error(null, 0, "OPS/XHTML file " + path + " is missing");
		else if (!ocf.canDecrypt(path))
			report.error(null, 0, "OPS/XHTML file " + path
					+ " cannot be decrypted");
		else {
			XMLParser opsParser = null;
			try {
				opsParser = new XMLParser(ocf.getInputStream(path), path,
						report);
			} catch (IOException e) {
				report.error(path, -10, e.getMessage());
			}
			OPSHandler opsHandler = new OPSHandler(opsParser, path, xrefChecker);
			opsParser.addXMLHandler(opsHandler);
			if (version == 2.0) {
				if (mimeType.equals("image/svg+xml"))
					opsParser.addValidator(svgValidator);
				else if (mimeType.equals("application/xhtml+xml"))
					opsParser.addValidator(xhtmlValidator);
			} else if (version == 3.0) {
				if (mimeType.equals("image/svg+xml")) {
					opsParser.addValidator(svgValidator30);
					try {
						SchematronXSLT2Validator schematronXSLT2Validator = new SchematronXSLT2Validator(
								ocf.getInputStream(path),
								svgSchematronValidator30, report);
						schematronXSLT2Validator.compile();
						new SvrlParser(path,
								schematronXSLT2Validator.generateSVRL(), report);
					} catch (Throwable t) {
						report.error(
								path,
								-1,
								"Failed performing OPF Schematron tests: "
										+ t.getMessage());
					}
				} else if (mimeType.equals("application/xhtml+xml")) {
					opsParser.addValidator(xhtmlValidator30);
					// there might be smth wrong with the schematron schema for
					// xhtml 5 TODO correct html5.sch
				} else if (mimeType.equals("application/smil+xml")) {
					opsParser.addValidator(mediaOverlayValidator30);
					try {
						SchematronXSLT2Validator schematronXSLT2Validator = new SchematronXSLT2Validator(
								ocf.getInputStream(path),
								mediaOverlaySchematronValidator30, report);
						schematronXSLT2Validator.compile();
						new SvrlParser(path,
								schematronXSLT2Validator.generateSVRL(), report);
					} catch (Throwable t) {
						report.error(
								path,
								-1,
								"Failed performing OPF Schematron tests: "
										+ t.getMessage());
					}
				} else if (mimeType.equals("nav")) {

					opsParser.addValidator(navValidator30);
					try {
						SchematronXSLT2Validator schematronXSLT2Validator = new SchematronXSLT2Validator(
								ocf.getInputStream(path),
								navSchematronValidator30, report);
						schematronXSLT2Validator.compile();
						new SvrlParser(path,
								schematronXSLT2Validator.generateSVRL(), report);

					} catch (Throwable t) {
						report.error(
								path,
								-1,
								"Failed performing OPF Schematron tests: "
										+ t.getMessage());
					}
				}
			}
			opsParser.process();
		}
	}

}
