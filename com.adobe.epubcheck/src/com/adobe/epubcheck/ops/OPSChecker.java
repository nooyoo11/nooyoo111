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
import java.util.HashMap;

import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.ocf.OCFPackage;
import com.adobe.epubcheck.opf.ContentChecker;
import com.adobe.epubcheck.opf.DocumentValidator;
import com.adobe.epubcheck.opf.XRefChecker;
import com.adobe.epubcheck.xml.SchematronXSLT2Validator;
import com.adobe.epubcheck.xml.SvrlParser;
import com.adobe.epubcheck.xml.XMLParser;
import com.adobe.epubcheck.xml.XMLValidator;
import com.adobe.epubcheck.util.GenericResourceProvider;
import com.adobe.epubcheck.util.OPSType;

public class OPSChecker implements ContentChecker, DocumentValidator {

	class EpubValidator {
		XMLValidator xmlValidator = null;
		String schValidator = null;

		public EpubValidator(XMLValidator xmlValidator, String schValidator) {
			this.xmlValidator = xmlValidator;
			this.schValidator = schValidator;
		}
	}

	OCFPackage ocf;

	Report report;

	String path;

	String mimeType;

	XRefChecker xrefChecker;

	float version;

	private OPSHandler opsHandler = null;

	GenericResourceProvider resourceProvider;

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

	private HashMap epubValidatorMap;

	private void initEpubValidatorMap() {
		HashMap map = new HashMap();
		map.put(new OPSType("application/xhtml+xml", 2), new EpubValidator(
				xhtmlValidator, null));
		// TODO
		map.put(new OPSType("application/xhtml+xml", 3), new EpubValidator(
				xhtmlValidator30, /* xhtmlSchematronValidator30 */null));

		map.put(new OPSType("image/svg+xml", 2), new EpubValidator(
				svgValidator, null));
		map.put(new OPSType("image/svg+xml", 3), new EpubValidator(
				svgValidator30, svgSchematronValidator30));

		map.put(new OPSType("application/smil+xml", 3), new EpubValidator(
				mediaOverlayValidator30, mediaOverlaySchematronValidator30));
		map.put(new OPSType("nav", 3), new EpubValidator(navValidator30,
				navSchematronValidator30));

		epubValidatorMap = map;
	}

	public OPSChecker(OCFPackage ocf, Report report, String path,
			String mimeType, XRefChecker xrefChecker, float version) {
		initEpubValidatorMap();
		this.ocf = ocf;
		this.resourceProvider = ocf;
		this.report = report;
		this.path = path;
		this.xrefChecker = xrefChecker;
		this.mimeType = mimeType;
		this.version = version;
	}

	public OPSChecker(String path, String mimeType,
			GenericResourceProvider resourceProvider, Report report,
			float version) {
		initEpubValidatorMap();
		this.resourceProvider = resourceProvider;
		this.mimeType = mimeType;
		this.report = report;
		this.path = path;
		this.version = version;
	}

	public void runChecks() {
		if (!ocf.hasEntry(path))
			report.error(null, 0, 0, "OPS/XHTML file " + path + " is missing");
		else if (!ocf.canDecrypt(path))
			report.error(null, 0, 0, "OPS/XHTML file " + path
					+ " cannot be decrypted");
		else {
			opsHandler = new OPSHandler(path, xrefChecker, report);
			validate();
		}
	}

	public boolean validate() {
		XMLValidator rngValidator = null;
		String schValidator = null;
		int errorsSoFar = report.getErrorCount();
		int warningsSoFar = report.getWarningCount();
		OPSType type = new OPSType(mimeType, version);
		EpubValidator epubValidator = (EpubValidator) epubValidatorMap
				.get(type);
		if (epubValidator != null) {
			rngValidator = epubValidator.xmlValidator;
			schValidator = epubValidator.schValidator;
		}
		try {
			validateAgainstSchemas(rngValidator, schValidator);
		} catch (IOException e) {
			report.error(path, 0, 0, e.getMessage());
		}
		return errorsSoFar == report.getErrorCount()
				&& warningsSoFar == report.getWarningCount();
	}

	public void validateAgainstSchemas(XMLValidator rngValidator,
			String schValidator) throws IOException {

		XMLParser opsParser = new XMLParser(
				resourceProvider.getInputStream(path), path, report);
		opsParser.addXMLHandler(opsHandler);

		if (rngValidator != null)
			opsParser.addValidator(rngValidator);

		opsParser.process();

		if (schValidator != null)
			try {
				SchematronXSLT2Validator schematronXSLT2Validator = new SchematronXSLT2Validator(
						resourceProvider.getInputStream(path), schValidator,
						report);
				schematronXSLT2Validator.compile();
				new SvrlParser(path, schematronXSLT2Validator.generateSVRL(),
						report);
			} catch (Throwable t) {
				report.error(
						path,
						-1,
						0,
						"Failed performing OPF Schematron tests: "
								+ t.getMessage());
			}
	}
}
