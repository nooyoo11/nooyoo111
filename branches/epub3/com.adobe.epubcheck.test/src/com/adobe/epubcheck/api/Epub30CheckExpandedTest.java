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

package com.adobe.epubcheck.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.adobe.epubcheck.opf.DocumentValidator;
import com.adobe.epubcheck.util.Archive;
import com.adobe.epubcheck.util.ValidationReport;

public class Epub30CheckExpandedTest {

	private ValidationReport testReport;

	private DocumentValidator epubCheck;

	private boolean verbose;

	private static String path = "com.adobe.epubcheck.test/testdocs/30/expanded/";

	/*
	 * TEST DEBUG FUNCTION
	 */

	public void testValidateDocument(String fileName, int errors, int warnings,
			boolean verbose)  {
		if (verbose)
			this.verbose = verbose;
		testValidateDocument(fileName, errors, warnings);
	}

	public void testValidateDocument(String fileName, int errors, int warnings) {

		Archive epub = new Archive(path + fileName);
		testReport = new ValidationReport(epub.getEpubName());
		epub.createArchive();

		epubCheck = new EpubCheck(epub.getEpubFile(), testReport);

		epubCheck.validate();

		if (verbose) {
			verbose = false;
			System.out.println(testReport);
		}

		assertEquals(errors, testReport.getErrorCount());
		assertEquals(warnings, testReport.getWarningCount());
	}

	@Test
	public void testValidateEPUBPLoremBasic() {
		testValidateDocument("valid/lorem-basic", 0, 0);
	}

	@Test
	public void testValidateEPUBWastelandBasic() {
		testValidateDocument("valid/wasteland-basic", 0, 0);
	}

	@Test
	public void testValidateEPUBLoremAudio() {
		testValidateDocument("valid/lorem-audio", 0, 0);
	}

	@Test
	public void testValidateEPUBLoremxhtmlrng1() {
		testValidateDocument("invalid/lorem-xhtml-rng-1", 1, 0);
	}

	@Test
	public void testValidateEPUBLoremxhtmlsch1() {
		testValidateDocument("invalid/lorem-xhtml-sch-1", 1, 0);
	}

	@Test
	public void testValidateEPUBPLoremBasicMathml() {
		testValidateDocument("invalid/lorem-basic-switch", 1, 0);
	}

	@Test
	public void testValidateEPUBPLoremMimetype() {
		testValidateDocument("invalid/lorem-mimetype", 2, 0);
	}

	@Test
	public void testValidateEPUBPLoremMimetype2() {
		testValidateDocument("invalid/lorem-mimetype-2", 2, 0);
	}
	
	@Test
	public void testValidateEPUBPLoremBasicSwitch() {
		testValidateDocument("valid/lorem-basic-switch", 0, 0);
	}

	@Test
	public void testValidateEPUBPLoremLink() {
		testValidateDocument("valid/lorem-link", 0, 0);
	}

	@Test
	public void testValidateEPUBPLoremForeign() {
		testValidateDocument("valid/lorem-foreign", 0, 0);
	}

	@Test
	public void testValidateEPUBPLoremObjectFallbacks() {
		testValidateDocument("valid/lorem-object-fallbacks", 0, 0);
	}
	
	@Test
	public void testValidateEPUBPLoremBindings() {
		testValidateDocument("valid/lorem-bindings", 0, 0);
	}

	@Test
	public void testValidateEPUBPLoremInvalidBindings() {
		testValidateDocument("invalid/lorem-bindings", 1, 0);
	}
	
	@Test
	public void testValidateEPUBPLoremPoster() {
		testValidateDocument("valid/lorem-poster", 0, 0);
	}

	@Test
	public void testValidateEPUBPLoremSvg() {
		testValidateDocument("valid/lorem-svg", 0, 0);
	}

	@Test
	public void testValidateEPUBPLoremSvgHyperlink() {
		testValidateDocument("valid/lorem-svg-hyperlink", 0, 0);
	}

	@Test
	public void testValidateEPUBPInvalidLoremPoster() {
		testValidateDocument("invalid/lorem-poster", 1, 0);
	}

	@Test
	public void testValidateEPUBPInvalidLoremForeign() {
		testValidateDocument("invalid/lorem-foreign", 1, 0);
	}

	@Test
	public void testValidateEPUB30_navInvalid() {
		// invalid nav issuse reported by MattG
		testValidateDocument("invalid/nav-invalid/", 1, 0);
	}
	
	@Test
	public void testValidateEPUB30_issue134_1() {
		// svg in both contentdocs, opf props set right
		testValidateDocument("valid/lorem-svg-dual/", 0, 0);
	}
	
	@Test
	public void testValidateEPUB30_issue134_2() {
		// svg in both contentdocs, no opf props set right
		testValidateDocument("invalid/lorem-svg-dual/", 2, 0);
	}
	
	@Test
	public void testValidateEPUB30_issue134_3() {
		// svg in both contentdocs, only one opf prop set right
		testValidateDocument("invalid/lorem-svg-dual-2/", 1, 0);
	}
			
	@Test
	public void testValidateEPUB30_CSSImport_valid() {		
		testValidateDocument("valid/lorem-css-import/", 0, 0);
	}
	
	@Test
	public void testValidateEPUB30_CSSImport_invalid_1() {		
		testValidateDocument("invalid/lorem-css-import-1/", 1, 0);
	}
	
	@Test
	public void testValidateEPUB30_CSSImport_invalid_2() {		
		testValidateDocument("invalid/lorem-css-import-2/", 1, 1);
	}
	
	@Test
	public void testValidateEPUB30_CSSFontFace_valid() {		
		testValidateDocument("valid/wasteland-otf/", 0, 0);
	}
	
	@Test
	public void testValidateEPUB30_CSSFontFace_invalid() {
		//referenced fonts missing
		testValidateDocument("invalid/wasteland-otf/", 3, 0);
	}
	
	@Test
	public void testValidateEPUB30_remoteAudio_valid() {		
		testValidateDocument("valid/lorem-remote/", 0, 0);
	}
	
	@Test
	public void testValidateEPUB30_remoteImg_invalid() {
		//remote img, properly declared in opf
		testValidateDocument("invalid/lorem-remote/", 1, 0);
	}
	
	@Test
	public void testValidateEPUB30_remoteImg_invalid2() {
		//remote img, not declared in opf
		//we should only get one error here... tbf
		testValidateDocument("invalid/lorem-remote-2/", 3, 0);
	}
	
	@Test
	public void testValidateEPUB30_circularFallback() {
		testValidateDocument("invalid/fallbacks-circular/", 5, 0);
	}
	
	@Test
	public void testValidateEPUB30_nonresolvingFallback() {
		//dupe messages, tbf
		testValidateDocument("invalid/fallbacks-nonresolving/", 4, 0);
	}
	
	@Test
	public void testValidateEPUB30_okFallback() {
		testValidateDocument("valid/fallbacks/", 0, 0);
	}
	
	@Test
	public void testValidateEPUB30_svgCoverImage() {
		testValidateDocument("valid/svg-cover/", 0, 0);
	}
	
	@Test
	public void testValidateEPUB30_svgInSpine() {
		//svg in spine, svg cover image
		testValidateDocument("valid/svg-in-spine/", 0, 0);
	}
	
	@Test
	public void testValidateEPUB30_videoAudioTrigger() {
		testValidateDocument("valid/cc-shared-culture/", 0, 0);
	}
}
