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

package com.adobe.epubcheck.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.adobe.epubcheck.api.Report;

public class SvrlParser extends DefaultHandler {

	Report report;

	SAXParser saxParser;

	boolean failed;

	boolean text;

	String errorsLocation;

	String path;

	StringBuffer message;

	public SvrlParser(String path, InputStream is, Report report)
			throws ParserConfigurationException, SAXException, IOException {

		this.report = report;
		this.path = path;
		failed = false;
		text = false;

		saxParser = SAXParserFactory.newInstance().newSAXParser();
		saxParser.parse(is, this);
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals("svrl:failed-assert")) {
			failed = true;
			errorsLocation = attributes.getValue("location") + ": ";
			message = new StringBuffer();
		} else if (qName.equals("svrl:text"))
			text = true;
	}

	public void characters(char ch[], int start, int length)
			throws SAXException {
		if (failed && text)
			message.append(new String(ch, start, length));

	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equals("svrl:failed-assert"))
			failed = false;
		else if (qName.equals("svrl:text")) {
			text = false;
			report.error(path + ": " + errorsLocation, -1, -1, message.toString());
			message = new StringBuffer();
		}
	}
}
