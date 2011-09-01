/*
 * Copyright (c) 2011 Adobe Systems Incorporated
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

package com.adobe.epubcheck.nav;

import java.io.IOException;

import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.ocf.OCFPackage;
import com.adobe.epubcheck.opf.ContentChecker;
import com.adobe.epubcheck.opf.DocumentValidator;
import com.adobe.epubcheck.opf.XRefChecker;
import com.adobe.epubcheck.util.EPUBVersion;
import com.adobe.epubcheck.util.GenericResourceProvider;
import com.adobe.epubcheck.util.Messages;
import com.adobe.epubcheck.xml.SchematronXSLT2Validator;
import com.adobe.epubcheck.xml.SvrlParser;
import com.adobe.epubcheck.xml.XMLParser;
import com.adobe.epubcheck.xml.XMLValidator;

public class NavChecker implements ContentChecker, DocumentValidator {

	static XMLValidator navValidator30 = new XMLValidator(
			"schema/30/epub-nav-30.rnc");

	static String navSchematronValidator30 = new String(
			"schema/30/epub-nav-30.sch");

	static String xhtmlSchematronValidator30 = new String(
			"schema/30/epub-xhtml-30.sch");

	OCFPackage ocf;

	Report report;

	String path;

	XRefChecker xrefChecker;

	GenericResourceProvider resourceProvider;

	public NavChecker(GenericResourceProvider resourceProvider, Report report,
			String path, EPUBVersion version) {
		if (version == EPUBVersion.VERSION_2)
			report.error(path, 0, 0, Messages.NAV_NOT_SUPPORTED);
		this.report = report;
		this.path = path;
		this.resourceProvider = resourceProvider;
	}

	public NavChecker(OCFPackage ocf, Report report, String path,
			EPUBVersion version) {
		if (version == EPUBVersion.VERSION_2)
			report.error(path, 0, 0, Messages.NAV_NOT_SUPPORTED);
		this.ocf = ocf;
		this.report = report;
		this.path = path;
		this.resourceProvider = ocf;
	}

	public void runChecks() {
		if (!ocf.hasEntry(path))
			report.error(null, 0, 0, "Nav file " + path + " is missing");
		else if (!ocf.canDecrypt(path))
			report.error(null, 0, 0, "Nav file " + path
					+ " cannot be decrypted");
		else {
			validate();
		}
	}

	public boolean validate() {

		try {
			XMLParser navParser = new XMLParser(
					resourceProvider.getInputStream(path), path, report);
			navParser.addValidator(navValidator30);
			navParser.process();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			SchematronXSLT2Validator schematronXSLT2Validator = new SchematronXSLT2Validator(
					resourceProvider.getInputStream(path),
					navSchematronValidator30, report);
			schematronXSLT2Validator.compile();
			schematronXSLT2Validator.execute();
//			new SvrlParser(path, schematronXSLT2Validator.generateSVRL(),
//					report);

			/*
			 * schematronXSLT2Validator = new SchematronXSLT2Validator(
			 * resourceProvider.getInputStream(path),
			 * xhtmlSchematronValidator30, report);
			 * schematronXSLT2Validator.compile(); new SvrlParser(path,
			 * schematronXSLT2Validator.generateSVRL(), report);
			 */
		} catch (Throwable t) {
			report.error(path, -1, 0,
					"Failed performing OPF Schematron tests: " + t.getMessage());
		}
		return false;
	}

}
