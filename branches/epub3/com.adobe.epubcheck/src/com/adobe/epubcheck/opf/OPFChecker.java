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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.bitmap.BitmapCheckerFactory;
import com.adobe.epubcheck.css.CSSCheckerFactory;
import com.adobe.epubcheck.dtbook.DTBookCheckerFactory;
import com.adobe.epubcheck.ncx.NCXCheckerFactory;
import com.adobe.epubcheck.ocf.OCFPackage;
import com.adobe.epubcheck.ops.OPSCheckerFactory;
import com.adobe.epubcheck.util.EPUBVersion;
import com.adobe.epubcheck.util.GenericResourceProvider;
import com.adobe.epubcheck.util.PathUtil;
import com.adobe.epubcheck.xml.XMLParser;
import com.adobe.epubcheck.xml.XMLValidator;

public class OPFChecker implements DocumentValidator {

	OCFPackage ocf;

	Report report;

	String path;

	HashSet<String> containerEntries;

	protected XMLValidator opfValidator = new XMLValidator(
			"schema/20/rng/opf.rng");

	protected XMLValidator opfSchematronValidator = new XMLValidator(
			"schema/20/sch/opf.sch");

	XRefChecker xrefChecker;

	protected Hashtable contentCheckerFactoryMap;

	OPFHandler opfHandler = null;

	protected EPUBVersion version;

	protected GenericResourceProvider resourceProvider = null;

	private void initContentCheckerFactoryMap() {
		Hashtable<String, ContentCheckerFactory> map = new Hashtable<String, ContentCheckerFactory>();
		map.put("application/xhtml+xml", OPSCheckerFactory.getInstance());
		map.put("text/html", OPSCheckerFactory.getInstance());
		map.put("text/x-oeb1-document", OPSCheckerFactory.getInstance());
		map.put("image/jpeg", BitmapCheckerFactory.getInstance());
		map.put("image/gif", BitmapCheckerFactory.getInstance());
		map.put("image/png", BitmapCheckerFactory.getInstance());
		map.put("image/svg+xml", OPSCheckerFactory.getInstance());
		map.put("application/x-dtbook+xml", DTBookCheckerFactory.getInstance());
		map.put("text/css", CSSCheckerFactory.getInstance());

		contentCheckerFactoryMap = map;
	}

	public OPFChecker(OCFPackage ocf, Report report, String path,
			HashSet<String> containerEntries, EPUBVersion version) {
		this.ocf = ocf;
		this.resourceProvider = ocf;
		this.report = report;
		this.path = path;
		this.containerEntries = containerEntries;
		this.xrefChecker = new XRefChecker(ocf, report, version);
		this.version = version;
		initContentCheckerFactoryMap();
	}

	public OPFChecker(String path, GenericResourceProvider resourceProvider,
			Report report) {

		this.resourceProvider = resourceProvider;
		this.report = report;
		this.path = path;
		this.version = EPUBVersion.VERSION_2;
		initContentCheckerFactoryMap();
	}

	public OPFChecker() {
		// TODO Auto-generated constructor stub
	}

	public void runChecks() {
		if (!ocf.hasEntry(path)) {
			report.error(null, 0, 0, "OPF file " + path + " is missing");
			return;
		}
		validate();

		if (!opfHandler.checkUniqueIdentExists()) {
			report.error(
					path,
					-1,
					-1,
					"unique-identifier attribute in package element must reference an existing identifier element id");
		}

		int itemCount = opfHandler.getItemCount();
		for (int i = 0; i < itemCount; i++) {
			OPFItem item = opfHandler.getItem(i);
			try {
				xrefChecker.registerResource(item.getPath(),
						item.getMimeType(), item.isInSpine(),
						checkItemFallbacks(item, opfHandler),
						checkImageFallbacks(item, opfHandler));
			} catch (IllegalArgumentException e) {
				report.error(path, item.getLineNumber(),
						item.getColumnNumber(), e.getMessage());
			}
			checkItem(item, opfHandler);
		}

		for (int i = 0; i < itemCount; i++) {
			OPFItem item = opfHandler.getItem(i);

			if (!item.path.startsWith("http://"))
				checkItemContent(item, opfHandler);
		}

		try {
			Iterator<String> filesIter = ocf.getFileEntries().iterator();
			while (filesIter.hasNext()) {
				String entry = (String) filesIter.next();

				if (opfHandler.getItemByPath(entry) == null
						&& !entry.startsWith("META-INF/")
						&& !entry.startsWith("META-INF\\")
						&& !entry.equals("mimetype")
						&& !containerEntries.contains(entry)) {
					report.warning(
							null,
							-1,
							-1,
							"item ("
									+ entry
									+ ") exists in the zip file, but is not declared in the OPF file");
				}
			}

			Iterator<String> directoriesIter = ocf.getDirectoryEntries()
					.iterator();
			while (directoriesIter.hasNext()) {
				String directory = (String) directoriesIter.next();
				boolean hasContents = false;
				filesIter = ocf.getFileEntries().iterator();
				while (filesIter.hasNext()) {
					String file = (String) filesIter.next();
					if (file.startsWith(directory)) {
						hasContents = true;
					}
				}
				if (!hasContents) {
					report.warning(null, -1, -1,
							"zip file contains empty directory " + directory);
				}

			}

		} catch (IOException e) {
			report.error(null, -1, -1, "Unable to read zip file entries.");
		}

		xrefChecker.checkReferences();
	}

	public void initHandler() {
		opfHandler = new OPFHandler(ocf, path, report, xrefChecker);
	}

	@Override
	public boolean validate() {
		XMLParser opfParser = null;
		int errorsSoFar = report.getErrorCount();
		int warningsSoFar = report.getWarningCount();

		initHandler();

		try {

			opfParser = new XMLParser(new BufferedInputStream(
					resourceProvider.getInputStream(path)), path, "opf", report);

			opfParser.addXMLHandler(opfHandler);

			opfParser.addValidator(opfValidator);
			opfParser.addValidator(opfSchematronValidator);

			opfParser.process();
		} catch (IOException e) {
			report.error(path, 0, 0, e.getMessage());
		}

		int refCount = opfHandler.getReferenceCount();
		for (int i = 0; i < refCount; i++) {
			OPFReference ref = opfHandler.getReference(i);
			String itemPath = PathUtil.removeAnchor(ref.getHref());
			if (opfHandler.getItemByPath(itemPath) == null) {
				report.error(path, ref.getLineNumber(), ref.getColumnNumber(),
						"File listed in reference element in guide was not declared in OPF manifest: "
								+ ref.getHref());
			}
		}

		int itemCount = opfHandler.getItemCount();
		for (int i = 0; i < itemCount; i++) {
			OPFItem item = opfHandler.getItem(i);
			checkItem(item, opfHandler);
		}

		int spineItemCount = opfHandler.getSpineItemCount();
		for (int i = 0; i < spineItemCount; i++) {
			OPFItem item = opfHandler.getSpineItem(i);
			checkSpineItem(item, opfHandler);
		}

		return errorsSoFar == report.getErrorCount()
				&& warningsSoFar == report.getWarningCount();
	}

	public static boolean isBlessedItemType(String type) {
		return type.equals("application/xhtml+xml")
				|| type.equals("application/x-dtbook+xml");

	}

	public static boolean isDeprecatedBlessedItemType(String type) {
		return type.equals("text/x-oeb1-document") || type.equals("text/html");
	}

	public static boolean isBlessedStyleType(String type) {
		return type.equals("text/css");
	}

	public static boolean isDeprecatedBlessedStyleType(String type) {
		return type.equals("text/x-oeb1-css");
	}

	public static boolean isBlessedImageType(String type) {
		return type.equals("image/gif") || type.equals("image/png")
				|| type.equals("image/jpeg") || type.equals("image/svg+xml");
	}

	protected void checkItem(OPFItem item, OPFHandler opfHandler) {
		String mimeType = item.getMimeType();
		String fallback = item.getFallback();
		if (mimeType == null || mimeType.equals("")) {
			// Ensures that media-type attribute is not empty
//			report.error(path, item.getLineNumber(), item.getColumnNumber(),
//					"empty media-type attribute");
		} else if (!mimeType
				.matches("[a-zA-Z0-9!#$&+-^_]+/[a-zA-Z0-9!#$&+-^_]+")) {
			/*
			 * Ensures that media-type attribute has correct content. The
			 * media-type must have a type and a sub-type divided by '/' The
			 * allowable content for the media-type attribute is defined in
			 * RFC4288 section 4.2
			 */
//			report.error(path, item.getLineNumber(), item.getColumnNumber(),
//					"invalid content for media-type attribute");
		} else if (isDeprecatedBlessedItemType(mimeType)
				|| isDeprecatedBlessedStyleType(mimeType)) {
			if (opfHandler.getOpf20PackageFile()
					&& mimeType.equals("text/html"))
				report.warning(path, item.getLineNumber(),
						item.getColumnNumber(),
						"text/html is not appropriate for XHTML/OPS, use application/xhtml+xml instead");
			else if (opfHandler.getOpf12PackageFile()
					&& mimeType.equals("text/html"))
				report.warning(path, item.getLineNumber(),
						item.getColumnNumber(),
						"text/html is not appropriate for OEBPS 1.2, use text/x-oeb1-document instead");
			else if (opfHandler.getOpf20PackageFile())
				report.warning(path, item.getLineNumber(),
						item.getColumnNumber(), "deprecated media-type '"
								+ mimeType + "'");
		}
		if (opfHandler.getOpf12PackageFile() && fallback == null) {
			if (isBlessedItemType(mimeType))
				report.warning(
						path,
						item.getLineNumber(),
						item.getColumnNumber(),
						"use of OPS media-type '"
								+ mimeType
								+ "' in OEBPS 1.2 context; use text/x-oeb1-document instead");
			else if (isBlessedStyleType(mimeType))
				report.warning(
						path,
						item.getLineNumber(),
						item.getColumnNumber(),
						"use of OPS media-type '"
								+ mimeType
								+ "' in OEBPS 1.2 context; use text/x-oeb1-css instead");
		}
		if (fallback != null) {
			OPFItem fallbackItem = opfHandler.getItemById(fallback);
			if (fallbackItem == null)
				report.error(path, item.getLineNumber(),
						item.getColumnNumber(),
						"fallback item could not be found");
		}
		String fallbackStyle = item.getFallbackStyle();
		if (fallbackStyle != null) {
			OPFItem fallbackStyleItem = opfHandler.getItemById(fallbackStyle);
			if (fallbackStyleItem == null)
				report.error(path, item.getLineNumber(),
						item.getColumnNumber(),
						"fallback-style item could not be found");
		}
	}

	protected void checkItemContent(OPFItem item, OPFHandler opfHandler) {
		String mimeType = item.getMimeType();
		String path = item.getPath();
		String properties = item.getProperties();

		if (mimeType != null) {
			ContentCheckerFactory checkerFactory;
			if (item.isNcx())
				checkerFactory = NCXCheckerFactory.getInstance();
			else
				checkerFactory = (ContentCheckerFactory) contentCheckerFactoryMap
						.get(mimeType);
			if (checkerFactory == null)
				checkerFactory = GenericContentCheckerFactory.getInstance();
			if (checkerFactory != null) {
				ContentChecker checker = checkerFactory.newInstance(ocf,
						report, path, mimeType, properties, xrefChecker,
						version);
				checker.runChecks();
			}
		}
	}

	protected void checkSpineItem(OPFItem item, OPFHandler opfHandler) {
		// These checks are okay to be done on <spine> items, but they really
		// should be done on all
		// <manifest> items instead. I am avoiding making this change now
		// pending a few issue
		// resolutions in the EPUB Maint Working Group (e.g. embedded fonts not
		// needing fallbacks).
		// [GC 11/15/09]
		String mimeType = item.getMimeType();
		if (mimeType != null) {
			if (isBlessedStyleType(mimeType)
					|| isDeprecatedBlessedStyleType(mimeType)
					|| isBlessedImageType(mimeType))
				report.error(path, item.getLineNumber(),
						item.getColumnNumber(), "'" + mimeType
								+ "' is not a permissible spine media-type");
			else if (!isBlessedItemType(mimeType)
					&& !isDeprecatedBlessedItemType(mimeType)
					&& item.getFallback() == null)
				report.error(path, item.getLineNumber(),
						item.getColumnNumber(), "non-standard media-type '"
								+ mimeType + "' with no fallback");
			else if (!isBlessedItemType(mimeType)
					&& !isDeprecatedBlessedItemType(mimeType)
					&& !checkItemFallbacks(item, opfHandler))
				report.error(
						path,
						item.getLineNumber(),
						item.getColumnNumber(),
						"non-standard media-type '"
								+ mimeType
								+ "' with fallback to non-spine-allowed media-type");
		}
	}

	protected boolean checkItemFallbacks(OPFItem item, OPFHandler opfHandler) {
		String fallback = item.getFallback();
		if (fallback != null) {
			OPFItem fallbackItem = opfHandler.getItemById(fallback);
			if (fallbackItem != null) {
				String mimeType = fallbackItem.getMimeType();
				if (mimeType != null) {
					if (isBlessedItemType(mimeType)
							|| isDeprecatedBlessedItemType(mimeType))
						return true;
					if (checkItemFallbacks(fallbackItem, opfHandler))
						return true;
				}
			}
		}
		String fallbackStyle = item.getFallbackStyle();
		if (fallbackStyle != null) {
			OPFItem fallbackStyleItem = opfHandler.getItemById(fallbackStyle);
			if (fallbackStyleItem != null) {
				String mimeType = fallbackStyleItem.getMimeType();
				if (mimeType != null) {
					if (isBlessedStyleType(mimeType)
							|| isDeprecatedBlessedStyleType(mimeType))
						return true;
				}
			}
		}
		return false;
	}

	protected boolean checkImageFallbacks(OPFItem item, OPFHandler opfHandler) {
		String fallback = item.getFallback();
		if (fallback != null) {
			OPFItem fallbackItem = opfHandler.getItemById(fallback);
			if (fallbackItem != null) {
				String mimeType = fallbackItem.getMimeType();
				if (mimeType != null) {
					if (isBlessedImageType(mimeType))
						return true;
					if (checkImageFallbacks(fallbackItem, opfHandler))
						return true;
				}
			}
		}
		return false;
	}

}
