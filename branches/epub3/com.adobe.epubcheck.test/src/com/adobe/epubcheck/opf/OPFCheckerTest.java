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

package com.adobe.epubcheck.opf;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.adobe.epubcheck.util.FileResourceProvider;
import com.adobe.epubcheck.util.GenericResourceProvider;
import com.adobe.epubcheck.util.URLResourceProvider;
import com.adobe.epubcheck.util.ValidationReport;

public class OPFCheckerTest {

	private String path = "testdocs/";

	private ValidationReport testReport;

	private DocumentValidator opfChecker;

	private GenericResourceProvider resourceProvider;

	private boolean verbose;

	/*
	 * TEST DEBUG FUNCTION
	 */
	public void testValidateDocument(String fileName, int errors, int warnings,
			float version, boolean verbose) {
		if (verbose)
			this.verbose = verbose;
		testValidateDocument(fileName, errors, warnings, version);

	}

	public void testValidateDocument(String fileName, int errors, int warnings,
			float version) {
		testReport = new ValidationReport(fileName);
		String relativePath = null;

		if (version == 2)
			relativePath = "20/single/opf/";
		else if (version == 3)
			relativePath = "30/single/opf/";

		if (fileName.startsWith("http://") || fileName.startsWith("https://"))
			resourceProvider = new URLResourceProvider(fileName);
		else
			resourceProvider = new FileResourceProvider(path + relativePath
					+ fileName);

		opfChecker = new OPFChecker(path + relativePath + fileName,
				resourceProvider, testReport);
		opfChecker.validate();

		if (verbose) {
			verbose = false;
			System.out.println(testReport);
		}

		assertEquals(errors, testReport.getErrorCount());
		assertEquals(warnings, testReport.getWarningCount());
	}

	@Test
	public void testValidateDocumentValidOPFBase001() {
		testValidateDocument("valid/base-001.opf", 0, 0, 3);
	}

	@Test
	public void testValidateDocumentValidOPFBindings001() {
		testValidateDocument("valid/bindings-001.opf", 0, 0, 3);
	}

	@Test
	public void testValidateDocumentValidOPFMediaOverlay001() {
		testValidateDocument("valid/media-overlay-001.opf", 0, 0, 3);
	}

	@Test
	public void testValidateDocumentValidOPFMediaOverlay002() {
		testValidateDocument("valid/media-overlay-002.opf", 0, 0, 3);
	}

	@Test
	public void testValidateDocumentValidOPFMinimal() {
		testValidateDocument("valid/minimal.opf", 0, 0, 3);
	}

	@Test
	public void testValidateDocumentNoPackageElement() {
		testValidateDocument("invalid/noPackageElement.opf", 9, 0, 3);
	}

	@Test
	public void testValidateDocumentNoMetadataElement() {
		testValidateDocument("invalid/noMetadataElement.opf", 5, 0, 3);
	}

	@Test
	public void testValidateDocumentNoNav() {
		testValidateDocument("invalid/noNav.opf", 2, 0, 3);
	}

	@Test
	public void testValidateDocumentInvalidMetaAbout() {
		testValidateDocument("invalid/invalidMetaAbout.opf", 2, 0, 3);
	}

	@Test
	public void testValidateDocumentNoDcNamespace() {
		testValidateDocument("invalid/noDcNamespace.opf", 5, 0, 3);
	}

	@Test
	public void testValidateDocumentBindings001() {
		testValidateDocument("invalid/bindings-001.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentCoverImage() {
		testValidateDocument("invalid/cover-image.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentFallback001() {
		testValidateDocument("invalid/fallback-001.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentFallback002() {
		testValidateDocument("invalid/fallback-002.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentIdUnique() {
		testValidateDocument("invalid/id-unique.opf", 2, 0, 3);
	}

	@Test
	public void testValidateDocumentItemref001() {
		testValidateDocument("invalid/itemref-001.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentMediaOverlay001() {
		testValidateDocument("invalid/media-overlay-001.opf", 4, 0, 3);
	}

	@Test
	public void testValidateDocumentMediaOverlay002() {
		testValidateDocument("invalid/media-overlay-002.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentMediaOverlayMeta001() {
		testValidateDocument("invalid/media-overlay-meta-001.opf", 2, 0, 3);
	}

	@Test
	public void testValidateDocumentMinlegth() {
		testValidateDocument("invalid/minlength.opf", 6, 0, 3);
	}

	@Test
	public void testValidateDocumentModifiedSyntax() {
		testValidateDocument("invalid/modified-syntax.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentModified() {
		testValidateDocument("invalid/modified.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentNav001() {
		testValidateDocument("invalid/nav-001.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentNav002() {
		testValidateDocument("invalid/nav-002.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentNav003() {
		testValidateDocument("invalid/nav-003.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentOrder() {
		testValidateDocument("invalid/order.opf", 2, 0, 3);
	}

	@Test
	public void testValidateDocumentRefinesRelative() {
		testValidateDocument("invalid/refines-relative.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentTocncx001() {
		testValidateDocument("invalid/tocncx-001.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentTocncx002() {
		testValidateDocument("invalid/tocncx-002.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentUid001() {
		testValidateDocument("invalid/uid-001.opf", 1, 0, 3);
	}

	@Test
	public void testValidateDocumentUid002() {
		testValidateDocument("invalid/uid-002.opf", 1, 0, 3);
	}

}
