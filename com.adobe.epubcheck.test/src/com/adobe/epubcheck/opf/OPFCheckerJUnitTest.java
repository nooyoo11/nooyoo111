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

package com.adobe.epubcheck.opf;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;

import com.adobe.epubcheck.opf.OPFChecker;
import com.adobe.epubcheck.report.CustomTestReport;

public class OPFCheckerJUnitTest {

	private String path = "testdocs/general/OPF/validateDocument/";

	private CustomTestReport testReport;

	private OPFChecker opfChecker;

	public void testValidateDocument(String fileName, int errors, int warnings,
			float version) {
		String relativePath = null;
		if (version == 2)
			relativePath = "2.0/";
		else if (version == 3)
			relativePath = "3.0/";

		InputStream is = null, is2 = null;
		try {
			is = new FileInputStream(new File(path + relativePath + fileName));
			is2 = new FileInputStream(new File(path + relativePath + fileName));
		} catch (FileNotFoundException e) {
			new RuntimeException(e);
		}
		testReport = new CustomTestReport(fileName);

		opfChecker = new OPFChecker(null, testReport, path + relativePath
				+ fileName, null);

		opfChecker.validateDocument(null, is, is2, testReport, version);

		// * For test debugging:
		/*
		 * System.out.println("Test: " + fileName + " errors: " +
		 * testReport.errorCount + " warnings: " + testReport.warningCount);
		 * System.out.println("errors:\n " + testReport.errorBuffer);
		 * System.out.println("warnings:\n " + testReport.warningBuffer);
		 */
		assertEquals(errors, testReport.errorCount);
		assertEquals(warnings, testReport.warningCount);
	}

	@Test
	public void testValidateDocumentValidOPF() {
		testValidateDocument("valid.opf", 0, 0, 3);
	}

	@Test
	public void testValidateDocumentNoPackageElement() {
		testValidateDocument("noPackageElement.opf", 8, 0, 3);
	}

	@Test
	public void testValidateDocumentNoMetadataElement() {
		testValidateDocument("noMetadataElement.opf", 4, 0, 3);
	}

	@Test
	public void testValidateDocumentNoNav() {
		testValidateDocument("noNav.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentNoProfileAttribute() {
		testValidateDocument("noProfileAttribute.opf", 1, 0, 3);
	}

	@Test
	// TODO change sch schema to pass this test
	public void testValidateDocumentInvalidMetaAbout() {
		testValidateDocument("invalidMetaAbout.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentNoDcNamespace() {
		testValidateDocument("noDcNamespace.opf", 4, 0, 3);
	}

	@Test
	public void testValidateDocumentValidOPFVersion2() {
		testValidateDocument("valid.opf", 0, 0, 2);
	}
}
