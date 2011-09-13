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

import java.util.HashSet;

import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.ocf.OCFPackage;
import com.adobe.epubcheck.util.HandlerUtil;
import com.adobe.epubcheck.xml.XMLElement;

public class OPFHandler30 extends OPFHandler {

	HashSet<String> prefixSet;

	static String[] predefinedPrefixes = { "dcterms", "marc", "media", "onix",
			"xsd" };

	static HashSet<String> metaPropertySet;
	static {
		HashSet<String> set = new HashSet<String>();
		set.add("alternate-script");
		set.add("display-seq");
		set.add("file-as");
		set.add("group-position");
		set.add("identifier-type");
		set.add("meta-auth");
		set.add("role");
		set.add("title-type");
		metaPropertySet = set;
	}

	static HashSet<String> linkRelSet;
	static {
		HashSet<String> set = new HashSet<String>();
		set.add("marc21xml-record");
		set.add("mods-record");
		set.add("onix-record");
		set.add("xml-signature");
		set.add("xmp-record");
		linkRelSet = set;
	}

	static HashSet<String> itemPropertySet;
	static {
		HashSet<String> set = new HashSet<String>();
		set.add("cover-image");
		set.add("mathml");
		set.add("nav");
		set.add("remote-resources");
		set.add("scripted");
		set.add("svg");
		set.add("switch");
		itemPropertySet = set;
	}

	Report report;

	String path;

	int line;

	int column;

	OPFHandler30(OCFPackage ocf, String path, Report report,
			XRefChecker xrefChecker) {
		super(ocf, path, report, xrefChecker);
		this.report = report;
		this.path = path;
		prefixSet = new HashSet<String>();

		for (int i = 0; i < predefinedPrefixes.length; i++)
			prefixSet.add(predefinedPrefixes[i]);
	}

	public void startElement(XMLElement e, int line, int column) {
		super.startElement(e, line, column);
		this.line = line;
		this.column = column;
		String name = e.getName();

		if (name.equals("package"))
			HandlerUtil.processPrefixes(e.getAttribute("prefix"), prefixSet,
					report, path, line, column);
		else if (name.equals("meta"))
			processMeta(e);
		else if (name.equals("link"))
			processLinkRel(e.getAttribute("rel"));
		else if (name.equals("item"))
			processItemProperties(e.getAttribute("properties"));
		else if (name.equals("itemref"))
			processItemrefProperties(e.getAttribute("properties"));
	}

	private void processItemrefProperties(String property) {
		if (property == null)
			return;
		property = property.replaceAll("[\\s]+", " ");

		String propertyArray[] = property.split(" ");
		boolean right = false, left = false;

		for (int i = 0; i < propertyArray.length; i++)
			if (propertyArray[i].endsWith(":"))
				report.error(path, line, column,
						"Property is not allowed to be composed only by a prefix!");
			else if (propertyArray[i].contains(":"))
				checkPrefix(propertyArray[i].substring(0,
						propertyArray[i].indexOf(':')));
			else if (propertyArray[i].equals("page-spread-left"))
				left = true;
			else if (propertyArray[i].equals("page-spread-right"))
				right = true;
			else
				report.error(path, line, column, "Undefined itemref property: "
						+ propertyArray[i]);

		if (right && left)
			report.error(path, line, column,
					"itemref can't have both page-spread-right and page-spread-left properties!");
	}

	private void processItemProperties(String property) {
		if (property == null)
			return;
		property = property.replaceAll("[\\s]+", " ");

		String propertyArray[] = property.split(" ");
		for (int i = 0; i < propertyArray.length; i++)
			if (propertyArray[i].endsWith(":"))
				report.error(path, line, column,
						"Property is not allowed to be composed only by a prefix!");
			else if (propertyArray[i].contains(":"))
				checkPrefix(propertyArray[i].substring(0,
						propertyArray[i].indexOf(':')));
			else if (!itemPropertySet.contains(propertyArray[i]))
				report.error(path, line, column, "Undefined item property: "
						+ propertyArray[i]);
	}

	private void processLinkRel(String rel) {
		if (rel == null)
			return;
		rel = rel.replaceAll("[\\s]+", " ");

		String relArray[] = rel.split(" ");
		for (int i = 0; i < relArray.length; i++)
			if (relArray[i].endsWith(":"))
				report.error(path, line, column,
						"Link rel is not allowed to be composed only by a prefix!");
			else if (relArray[i].contains(":"))
				checkPrefix(relArray[i].substring(0, relArray[i].indexOf(':')));
			else if (!linkRelSet.contains(relArray[i]))
				report.error(path, line, column, "Undefined link rel: "
						+ relArray[i]);
	}

	private void processMeta(XMLElement e) {
		processMetaProperty(e.getAttribute("property"));
		processMetaScheme(e.getAttribute("scheme"));
	}

	private void processMetaScheme(String scheme) {
		if (scheme == null)
			return;
		scheme = scheme.replaceAll("[\\s]+", " ");

		if (scheme.contains(":") && !scheme.endsWith(":")) {
			checkPrefix(scheme.substring(0, scheme.indexOf(':')));
		} else if (scheme.endsWith(":"))
			report.error(path, line, column,
					"Property is not allowed to be composed only by a prefix!");
		else
			report.error(path, line, column,
					"Unprefixed values for scheme attribute not allowed!");
	}

	boolean checkPrefix(String prefix) {
		prefix = prefix.trim();
		if (!prefixSet.contains(prefix)) {
			report.error(path, line, column, "Undecleared prefix: " + prefix);
			return false;
		}
		return true;
	}

	private void processMetaProperty(String property) {
		if (property == null)
			return;
		property = property.trim();
		if (property.contains(":") && !property.endsWith(":")) {
			checkPrefix(property.substring(0, property.indexOf(':')));
		} else if (property.endsWith(":"))
			report.error(path, line, column,
					"Meta property is not allowed to be composed only by a prefix!");
		else if (!metaPropertySet.contains(property))
			report.error(path, line, column, "Undefined propery " + property);
	}
}
