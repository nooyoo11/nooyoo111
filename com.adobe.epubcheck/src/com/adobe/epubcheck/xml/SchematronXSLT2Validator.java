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

package com.adobe.epubcheck.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.util.ResourceUtil;

public class SchematronXSLT2Validator implements ErrorListener, URIResolver {

	InputStream fileToValidate;

	String schematronSchema;

	Report report;

	ArrayList schematronXsls;

	StreamSource compiledSchema = null;

	TransformerFactory transformerFactory;

	Transformer saxonTransformer;

	public void compile() throws TransformerException, IOException {

		String resourcePath = ResourceUtil.getResourcePath(schematronSchema);
		URL systemIdURL = ResourceUtil.getResourceURL(resourcePath);
		if (systemIdURL == null) {
			throw new RuntimeException("Could not find resource "
					+ resourcePath);
		}
		StreamSource schemaSource = new StreamSource(systemIdURL.toString());

		StreamSource input = schemaSource;

		for (int i = 0; i < schematronXsls.size(); i++) {
			File tempFile = File.createTempFile("epubcheck" + i, null);
			tempFile.deleteOnExit();
			StreamResult result = new StreamResult(tempFile);

			saxonTransformer = transformerFactory
					.newTransformer((StreamSource) schematronXsls.get(i));
			saxonTransformer.transform(input, result);

			input = new StreamSource(result.toString());
			input.setSystemId(tempFile.toURI().toString());
		}
		compiledSchema = input;
	}

	public InputStream generateSVRL() throws TransformerException,
			UnsupportedEncodingException {

		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);

		saxonTransformer = transformerFactory.newTransformer(compiledSchema);
		saxonTransformer.setErrorListener(this);

		saxonTransformer.transform(new StreamSource(fileToValidate), result);

		return new ByteArrayInputStream(sw.toString().getBytes("UTF-8"));
	}

	public SchematronXSLT2Validator(InputStream file, String schemaName,
			Report report) {
		schematronXsls = new ArrayList();
		schematronXsls
				.add(new StreamSource(
						ResourceUtil.getResourceStream(ResourceUtil
								.getResourcePath("epub30schemas/iso-schematron-xslt2/iso_dsdl_include.xsl"))));
		schematronXsls
				.add(new StreamSource(
						ResourceUtil.getResourceStream(ResourceUtil
								.getResourcePath("epub30schemas/iso-schematron-xslt2/iso_abstract_expand.xsl"))));
		schematronXsls
				.add(new StreamSource(
						ResourceUtil.getResourceStream(ResourceUtil
								.getResourcePath("epub30schemas/iso-schematron-xslt2/iso_svrl_for_xslt2.xsl"))));

		fileToValidate = file;
		this.report = report;

		schematronSchema = schemaName;

		try {
			String resourcePath = ResourceUtil
					.getResourcePath("lib/saxon9he.jar");
			URL systemIdURL = ResourceUtil.getResourceURL(resourcePath);

			URL saxon9Url[] = new URL[] { systemIdURL };

			ClassLoader saxon9ClassLoader = new URLClassLoader(saxon9Url);
			Class TransformerFactoryClass = saxon9ClassLoader
					.loadClass("net.sf.saxon.TransformerFactoryImpl");

			transformerFactory = (TransformerFactory) TransformerFactoryClass
					.newInstance();
			transformerFactory.setURIResolver(this);
			transformerFactory.setErrorListener(this);
		} catch (Throwable t) {
			report.error("saxon9he.jar", -1,
					"Failed to load net.sf.saxon.TransformerFactoryImpl class: "
							+ t.getMessage());
		}
	}

	// @Override
	public void error(TransformerException e) throws TransformerException {
		report.error(schematronSchema, -1,
				"TransformerException: " + e.getMessage());
	}

	// @Override
	public void fatalError(TransformerException e) throws TransformerException {
		report.error(schematronSchema, -1,
				"TransformerException: " + e.getMessage());
	}

	// @Override
	public void warning(TransformerException e) throws TransformerException {
		report.error(schematronSchema, -1,
				"TransformerException: " + e.getMessage());
	}

	public Source resolve(String href, String base) throws TransformerException {
		if (href.startsWith("./mod/"))
			return new StreamSource(ResourceUtil.getResourceStream(ResourceUtil
					.getResourcePath("epub30schemas/" + href.substring(2))));
		else if (href.endsWith(".xsl"))
			return new StreamSource(ResourceUtil.getResourceStream(ResourceUtil
					.getResourcePath("epub30schemas/iso-schematron-xslt2/"
							+ href)));
		return null;
	}
}
