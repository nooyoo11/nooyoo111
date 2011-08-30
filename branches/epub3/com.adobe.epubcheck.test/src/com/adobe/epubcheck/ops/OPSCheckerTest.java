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

package com.adobe.epubcheck.ops;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.adobe.epubcheck.opf.DocumentValidator;
import com.adobe.epubcheck.util.FileResourceProvider;
import com.adobe.epubcheck.util.GenericResourceProvider;
import com.adobe.epubcheck.util.URLResourceProvider;
import com.adobe.epubcheck.util.ValidationReport;

public class OPSCheckerTest {

	private String path = "testdocs/";

	private ValidationReport testReport;

	private DocumentValidator opsChecker;

	private GenericResourceProvider resourceProvider;

	private boolean verbose;

	/*
	 * TEST DEBUG FUNCTION
	 */
	public void testValidateDocument(String fileName, String mimeType,
			int errors, int warnings, float version, boolean verbose) {
		if (verbose)
			this.verbose = verbose;
		testValidateDocument(fileName, mimeType, errors, warnings, version);

	}

	public void testValidateDocument(String fileName, String mimeType,
			int errors, int warnings, float version) {
		testReport = new ValidationReport(fileName);
		String relativePath = null;
		if (version == 2)
			relativePath = "20/single/";
		else if (version == 3)
			relativePath = "30/single/";

		if (fileName.startsWith("http://") || fileName.startsWith("https://"))
			resourceProvider = new URLResourceProvider(fileName);
		else
			resourceProvider = new FileResourceProvider(path + relativePath
					+ fileName);

		opsChecker = new OPSChecker(path + relativePath + fileName, mimeType,
				resourceProvider, testReport, version);

		opsChecker.validate();

		if (verbose) {
			verbose = false;
			System.out.println(testReport);
		}

		assertEquals(errors, testReport.getErrorCount());
		assertEquals(warnings, testReport.getWarningCount());
	}

	
	// XXX The mimeType of the nav document should be nav; this way it can be
	// tested as a nav file
	@Test
	public void testValidateDocumentValidMinimalNav() {
		testValidateDocument("nav/valid/minimal.xhtml", "nav", 0, 0, 3);
	}

	@Test
	public void testValidateDocumentValidNav001() {
		testValidateDocument("nav/valid/nav001.xhtml", "nav", 0, 0, 3);
	}

	@Test
	public void testValidateDocumentNoTocNav() {
		testValidateDocument("nav/invalid/noTocNav.xhtml", "nav", 3, 0, 3);
	}

	@Test
	public void testValidateDocumentNoTocNavFromURL() {
		testValidateDocument("http://www.interq.ro/bgd/noTocNav.xhtml", "nav",
				3, 0, 3);
	}

	@Test
	public void testValidateDocumentHText() {
		testValidateDocument("nav/invalid/h-text.xhtml", "nav", 8, 0, 3);
	}

	@Test
	public void testValidateDocumenNavLabels001() {
		testValidateDocument("nav/invalid/nav-labels-001.xhtml", "nav", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentNavLabels002() {
		testValidateDocument("nav/invalid/nav-labels-001.xhtml", "nav", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentNavLandmarks001() {
		testValidateDocument("nav/invalid/nav-landmarks-001.xhtml", "nav", 1,
				0, 3);
	}

	@Test
	public void testValidateDocumentNavNoPagelist001() {
		testValidateDocument("nav/invalid/nav-pagelist-001.xhtml", "nav", 1, 0,
				3);
	}

	@Test
	public void testValidateDocumentNavNoToc() {
		testValidateDocument("nav/invalid/nav-no-toc.xhtml", "nav", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentNavReqHeading() {
		testValidateDocument("nav/invalid/req-heading.xhtml", "nav", 1, 0, 3);
	}

}
