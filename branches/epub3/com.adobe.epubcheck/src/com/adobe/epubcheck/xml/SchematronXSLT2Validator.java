///*
// * Copyright (c) 2011 Adobe Systems Incorporated
// *
// *  Permission is hereby granted, free of charge, to any person obtaining a copy of
// *  this software and associated documentation files (the "Software"), to deal in
// *  the Software without restriction, including without limitation the rights to
// *  use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
// *  the Software, and to permit persons to whom the Software is furnished to do so,
// *  subject to the following conditions:
// *
// *  The above copyright notice and this permission notice shall be included in all
// *  copies or substantial portions of the Software.
// *
// *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// *  FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
// *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
// *  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
// *
// */
//
//package com.adobe.epubcheck.xml;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//import java.util.ArrayList;
//
//import javax.xml.transform.ErrorListener;
//import javax.xml.transform.Source;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.URIResolver;
//import javax.xml.transform.sax.SAXResult;
//import javax.xml.transform.stream.StreamResult;
//import javax.xml.transform.stream.StreamSource;
//
//import org.xml.sax.Attributes;
//import org.xml.sax.SAXException;
//import org.xml.sax.helpers.DefaultHandler;
//
//import com.adobe.epubcheck.api.Report;
//import com.adobe.epubcheck.util.ResourceUtil;
//
//public class SchematronXSLT2Validator extends DefaultHandler implements
//		ErrorListener, URIResolver {
//
//	InputStream fileToValidate;
//
//	String schematronSchema;
//
//	Report report;
//
//	ArrayList<StreamSource> schematronXsls;
//
//	StreamSource compiledSchema = null;
//
//	// FIXME use jaxp discovery instead
//	TransformerFactory transformerFactory = net.sf.saxon.TransformerFactoryImpl
//			.newInstance();
//
//	Transformer saxonTransformer;
//
//	boolean compiling = false;
//
//	String fileName;
//
//	public void compile() throws TransformerException, IOException {
//		compiling = true;
//		String resourcePath = ResourceUtil.getResourcePath(schematronSchema);
//		URL systemIdURL = ResourceUtil.getResourceURL(resourcePath);
//		if (systemIdURL == null) {
//			throw new RuntimeException("Could not find resource "
//					+ resourcePath);
//		}
//		StreamSource schemaSource = new StreamSource(systemIdURL.toString());
//
//		StreamSource input = schemaSource;
//
//		for (int i = 0; i < schematronXsls.size(); i++) {
//			File tempFile = File.createTempFile("epubcheck" + i, null);
//			tempFile.deleteOnExit();
//			StreamResult result = new StreamResult(tempFile);
//
//			saxonTransformer = transformerFactory
//					.newTransformer((StreamSource) schematronXsls.get(i));
//			saxonTransformer.transform(input, result);
//
//			input = new StreamSource(tempFile);
//			input.setSystemId(tempFile.toURI().toString());
//		}
//		compiledSchema = input;
//		compiling = false;
//	}
//
//	public void execute() throws IOException, TransformerException {
//		//System.err.println(compiledSchema.getSystemId());
//		saxonTransformer = transformerFactory.newTransformer(compiledSchema);
//		saxonTransformer.setErrorListener(this);
//		saxonTransformer.transform(new StreamSource(fileToValidate), new SAXResult(new SchematronOutputHandler()));
//	}
//
//	// public InputStream generateSVRL() throws TransformerException,
//	// UnsupportedEncodingException {
//	//
//	// StringWriter sw = new StringWriter();
//	// StreamResult result = new StreamResult(sw);
//	//
//	// saxonTransformer = transformerFactory.newTransformer(compiledSchema);
//	// saxonTransformer.setErrorListener(this);
//	//
//	// saxonTransformer.transform(new StreamSource(fileToValidate), result);
//	//
//	// return new ByteArrayInputStream(sw.toString().getBytes("UTF-8"));
//	// }
//
//	public SchematronXSLT2Validator(String fileName, InputStream file,
//			String schemaName, Report report) {
//		this.fileName = fileName;
//		// System.err.println("sch on: " + fileName);
//		schematronXsls = new ArrayList<StreamSource>();
//
//		schematronXsls
//				.add(new StreamSource(
//						ResourceUtil.getResourceStream(ResourceUtil
//								.getResourcePath("schema/30/iso-schematron-xslt2/iso_dsdl_include.xsl"))));
//		schematronXsls
//				.add(new StreamSource(
//						ResourceUtil.getResourceStream(ResourceUtil
//								.getResourcePath("schema/30/iso-schematron-xslt2/iso_abstract_expand.xsl")))); //
//		schematronXsls
//				.add(new StreamSource(
//						ResourceUtil.getResourceStream(ResourceUtil
//								.getResourcePath("schema/30/iso-schematron-xslt2/iso_svrl_for_xslt2.xsl")))); // iso_schematron_message_xslt2
//
//		fileToValidate = file;
//		this.report = report;
//
//		schematronSchema = schemaName;
//
//		transformerFactory.setURIResolver(this);
//		transformerFactory.setErrorListener(this);
//
//	}
//
//	// @Override
//	public void error(TransformerException e) throws TransformerException {
//		report.error(schematronSchema, -1, -1,
//				"TransformerException: " + e.getMessage());
//	}
//
//	// @Override
//	public void fatalError(TransformerException e) throws TransformerException {
//		report.error(schematronSchema, -1, -1,
//				"TransformerException: " + e.getMessage());
//	}
//
//	// @Override
//	public void warning(TransformerException e) throws TransformerException {
//		// FIXME temp solution to remove xpath warnings during execution
//		if (compiling) {
//			report.warning(schematronSchema, -1, -1, "TransformerException: "
//					+ e.getMessage());
//		}
//	}
//
//	public Source resolve(String href, String base) throws TransformerException {
//		if (href.startsWith("./mod/")) {
//			return new StreamSource(ResourceUtil.getResourceStream(ResourceUtil
//					.getResourcePath("schema/30/" + href.substring(2))));
//		} else if (href.endsWith(".xsl")) {
//			return new StreamSource(ResourceUtil.getResourceStream(ResourceUtil
//					.getResourcePath("schema/30/iso-schematron-xslt2/" + href)));
//		}
//		return null;
//	}
//
//	class SchematronOutputHandler extends DefaultHandler {
//		boolean inText = false;
//		boolean inFailedAssert = false;
//		boolean inReport = false;
//
//		StringBuilder message = new StringBuilder();
//
//		public void characters(char ch[], int start, int length)
//				throws SAXException {
//			//System.err.println("sch: characters");
//			if (inText && (inFailedAssert || inReport)) {
//				for (int i = start; i < length; i++) {
//					message.append(ch[i]);
//				}
//			}
//		}
//
//		@Override
//		public void startElement(String uri, String localName, String qName,
//				Attributes attributes) throws SAXException {
//			//System.err.println("sch: start element: " + localName);
//			if (localName.equals("text"))
//				inText = true;
//			if (localName.equals("failed-assert"))
//				inFailedAssert = true;
//			if (localName.equals("report"))
//				inReport = true;
//		}
//
//		@Override
//		public void endElement(String uri, String localName, String qName)
//				throws SAXException {
//			//System.err.println("sch: end element: " + localName);
//			if (inText && (inFailedAssert || inReport)) {
//				report.error(fileName, -1, -1, message.toString());
//				message.delete(0, message.length());
//			}
//			if (localName.equals("text"))
//				inText = false;
//			if (localName.equals("failed-assert"))
//				inFailedAssert = false;
//			if (localName.equals("report"))
//				inReport = false;
//		}
//	}
//}
