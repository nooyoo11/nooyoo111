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

package com.adobe.epubcheck.ocf;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.opf.OPFChecker;
import com.adobe.epubcheck.opf.OPFChecker30;
import com.adobe.epubcheck.util.CheckUtil;
import com.adobe.epubcheck.util.EPUBVersion;
import com.adobe.epubcheck.util.InvalidVersionException;
import com.adobe.epubcheck.util.OPSType;
import com.adobe.epubcheck.util.ResourceUtil;
import com.adobe.epubcheck.xml.XMLHandler;
import com.adobe.epubcheck.xml.XMLParser;
import com.adobe.epubcheck.xml.XMLValidator;

public class OCFChecker {

	OCFPackage ocf;

	Report report;

	Hashtable encryptedItems;

	private EPUBVersion version = EPUBVersion.VERSION_3;

	static final String containerEntry = "META-INF/container.xml";

	static final String encryptionEntry = "META-INF/encryption.xml";

	static final String signatureEntry = "META-INF/signatures.xml";

	static XMLValidator containerValidator = new XMLValidator(
			"schema/20/rng/container.rng");

	static XMLValidator encryptionValidator = new XMLValidator(
			"schema/20/rng/encryption.rng");

	static XMLValidator signatureValidator = new XMLValidator(
			"schema/20/rng/signatures.rng");

	static XMLValidator containerValidator30 = new XMLValidator(
			"schema/30/ocf-container-30.rnc");

	static XMLValidator encryptionValidator30 = new XMLValidator(
			"schema/30/ocf-encryption-30.rnc");

	static XMLValidator signatureValidator30 = new XMLValidator(
			"schema/30/ocf-signatures-30.rnc");

	private static HashMap<OPSType, XMLValidator> xmlValidatorMap;
	static {
		HashMap<OPSType, XMLValidator> map = new HashMap<OPSType, XMLValidator>();
		map.put(new OPSType(containerEntry, EPUBVersion.VERSION_2),
				containerValidator);
		map.put(new OPSType(containerEntry, EPUBVersion.VERSION_3),
				containerValidator30);

		map.put(new OPSType(encryptionEntry, EPUBVersion.VERSION_2),
				encryptionValidator);
		map.put(new OPSType(encryptionEntry, EPUBVersion.VERSION_3),
				encryptionValidator30);

		map.put(new OPSType(signatureEntry, EPUBVersion.VERSION_2),
				signatureValidator);
		map.put(new OPSType(signatureEntry, EPUBVersion.VERSION_3),
				signatureValidator30);

		xmlValidatorMap = map;
	}

	public OCFChecker(OCFPackage ocf, Report report) {
		this.ocf = ocf;
		this.report = report;
	}

	XMLParser parser = null;

	public void runChecks() {

		String rootPath;

		if (!ocf.hasEntry(containerEntry)) {
			report.error(null, 0, 0,
					"Required META-INF/container.xml resource is missing");
			return;
		}
		// retrieve rootpath
		XMLParser containerParser = null;
		try {
			containerParser = new XMLParser(ocf.getInputStream(containerEntry),
					containerEntry, "xml", report);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		OCFHandler containerHandler = new OCFHandler(containerParser);
		containerParser.addXMLHandler(containerHandler);
		containerParser.process();

		rootPath = containerHandler.getRootPath();

		if (rootPath != null && ocf.hasEntry(rootPath))
			try {
				version = ResourceUtil.retrieveOpfVersion(ocf
						.getInputStream(rootPath));

				// checking mimeType file for trailing spaces
				if (ocf.hasEntry("mimetype")
						&& !CheckUtil.checkTrailingSpaces(
								ocf.getInputStream("mimetype"), version))
					report.error("mimetype", 0, 0,
							"Mimetype file should contain only the string \"application/epub+zip\".");

				validate();
			} catch (InvalidVersionException e) {
				report.error(rootPath, -1, -1, e.getMessage());
			} catch (IOException ignore) {
				// missing file will be reported in OPFChecker
			}

		// george@oxygenxml.com: Check if we have a rootPath, see issue 95.
		// There is no need to report the missing root-path because that will be
		// reported by the
		// validation step on META-INF/container.xml.
		if (rootPath != null) {
			OPFChecker opfChecker = null;

			if (version == EPUBVersion.VERSION_2)
				opfChecker = new OPFChecker(ocf, report, rootPath,
						containerHandler.getContainerEntries(), version);
			else
				opfChecker = new OPFChecker30(ocf, report, rootPath,
						containerHandler.getContainerEntries(), version);
			opfChecker.runChecks();
		}
	}

	public void parse(String path, XMLHandler handler, Report report,
			XMLValidator validator) throws IOException {

		parser = new XMLParser(ocf.getInputStream(path), containerEntry, "xml",
				report);
		parser.addXMLHandler(handler);
		parser.addValidator(validator);
		parser.process();
	}

	public boolean validate() {
		try {
			// validate container
			XMLHandler handler = new OCFHandler(parser);
			parse(containerEntry, handler, report,
					xmlValidatorMap.get(new OPSType(containerEntry, version)));

			// Validate encryption.xml
			if (ocf.hasEntry(encryptionEntry)) {
				handler = new EncryptionHandler(ocf, parser);
				parse(encryptionEntry, handler, report,
						xmlValidatorMap.get(new OPSType(encryptionEntry,
								version)));
			}

			// validate encryption.xml
			if (ocf.hasEntry(signatureEntry)) {
				handler = new OCFHandler(parser);
				parse(signatureEntry, handler, report,
						xmlValidatorMap
								.get(new OPSType(signatureEntry, version)));
			}
		} catch (Exception ignore) {
		}

		return false;
	}

	/**
	 * This method processes the rootPath String and returns the base path to
	 * the directory that contains the OPF content file.
	 * 
	 * @param rootPath
	 *            path+name of OPF content file
	 * @return String containing path to OPF content file's directory inside ZIP
	 */
	public String processRootPath(String rootPath) {
		String rootBase = rootPath;
		if (rootPath.endsWith(".opf")) {
			int slash = rootPath.lastIndexOf("/");
			if (slash < rootPath.lastIndexOf("\\"))
				slash = rootPath.lastIndexOf("\\");
			if (slash >= 0 && (slash + 1) < rootPath.length())
				rootBase = rootPath.substring(0, slash + 1);
			else
				rootBase = rootPath;
			return rootBase;
		} else {
			System.out.println("RootPath is not an OPF file");
			return null;
		}
	}

}
