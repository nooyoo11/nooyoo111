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
 *    <AdobeIP#0000474>
 */

package com.adobe.epubcheck.tool;

import java.util.HashMap;

import com.adobe.epubcheck.api.EpubCheck;
import com.adobe.epubcheck.api.EpubCheckFactory;
import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.opf.DocumentValidator;
import com.adobe.epubcheck.opf.DocumentValidatorFactory;
import com.adobe.epubcheck.opf.OPFCheckerFactory;
import com.adobe.epubcheck.ops.OPSCheckerFactory;
import com.adobe.epubcheck.util.DefaultReportImpl;
import com.adobe.epubcheck.util.FileResourceProvider;
import com.adobe.epubcheck.util.GenericResourceProvider;
import com.adobe.epubcheck.util.OPSType;
import com.adobe.epubcheck.util.URLResourceProvider;

public class Checker {

	private static String path = null, mode = null;
	private static float version = 3;
	private static OPSType opsType;

	private static HashMap modeMimeTypeMap;

	static {
		HashMap map = new HashMap();

		map.put(new OPSType("xhtml", 2), "application/xhtml+xml");
		map.put(new OPSType("xhtml", 3), "application/xhtml+xml");

		map.put(new OPSType("svg", 2), "image/svg+xml");
		map.put(new OPSType("svg", 3), "image/svg+xml");

		map.put(new OPSType("mo", 3), "application/smil+xml");
		map.put(new OPSType("nav", 3), "nav");
		// TODO expanded epubs
		// map.put(new OPSType("exp", 3), );
		modeMimeTypeMap = map;
	}

	private static HashMap documentValidatorFactoryMap;

	static {
		HashMap map = new HashMap();
		map.put(new OPSType(null, 2), EpubCheckFactory.getInstance());
		map.put(new OPSType(null, 3), EpubCheckFactory.getInstance());

		map.put(new OPSType("opf", 2), OPFCheckerFactory.getInstance());
		map.put(new OPSType("opf", 3), OPFCheckerFactory.getInstance());

		map.put(new OPSType("xhtml", 2), OPSCheckerFactory.getInstance());
		map.put(new OPSType("xhtml", 3), OPSCheckerFactory.getInstance());

		map.put(new OPSType("svg", 2), OPSCheckerFactory.getInstance());
		map.put(new OPSType("svg", 3), OPSCheckerFactory.getInstance());

		map.put(new OPSType("mo", 3), OPSCheckerFactory.getInstance());
		map.put(new OPSType("nav", 3), OPSCheckerFactory.getInstance());
		// TODO expanded epubs
		// map.put(new OPSType("exp", 3), );
		documentValidatorFactoryMap = map;
	}

	public static void validateFile(GenericResourceProvider resourceProvider,
			String fileName, String mimeType, float version, Report report) {

		opsType = new OPSType(mode, version);

		DocumentValidatorFactory factory = (DocumentValidatorFactory) documentValidatorFactoryMap
				.get(opsType);

		if (factory == null) {
			System.out.println("-help displays help ");
			throw new RuntimeException("The checker doesn't validate type "
					+ mimeType + " and version " + version + "!");
		}

		DocumentValidator check = factory.newInstance(report, path,
				resourceProvider, (String) modeMimeTypeMap.get(opsType),
				version);

		if (check.validate())
			System.out.println("No errors or warnings detected");
		else {
			System.err.println("\nCheck finished with warnings or errors!\n");
		}
	}

	public static void validateFile(String path, String mimeType,
			float version, Report report) {

		GenericResourceProvider resourceProvider;

		if (path.startsWith("http://") || path.startsWith("https://"))
			resourceProvider = new URLResourceProvider(path);
		else
			resourceProvider = new FileResourceProvider(path);

		opsType = new OPSType(mode, version);

		DocumentValidatorFactory factory = (DocumentValidatorFactory) documentValidatorFactoryMap
				.get(opsType);

		if (factory == null) {
			System.out.println("-help displays help ");
			throw new RuntimeException("The checker doesn't validate type "
					+ mimeType + " and version " + version + "!");
		}

		DocumentValidator check = factory.newInstance(report, path,
				resourceProvider, (String) modeMimeTypeMap.get(opsType),
				version);

		if (check.validate())
			System.out.println("No errors or warnings detected");
		else {
			System.err.println("\nCheck finished with warnings or errors!\n");
		}
	}

	public static void main(String[] args) {

		processArguments(args);

		Report report = new DefaultReportImpl(path);

		validateFile(path, mode, version, report);
	}

	/**
	 * This method iterates through all of the arguments passed to main to find
	 * accepted flags and the name of the file to check. This method returns the
	 * last argument that ends with ".epub" (which is assumed to be the file to
	 * check) Here are the currently accepted flags: <br>
	 * <br>
	 * -? or -help = display usage instructions <br>
	 * -v or -version = display tool version number
	 * 
	 * @param args
	 *            String[] containing arguments passed to main
	 * @return the name of the file to check
	 */
	public static void processArguments(String[] args) {
		// Exit if there are no arguments passed to main
		displayVersion();
		if (args.length < 1) {
			System.err.println("At least one argument expected");
			System.exit(1);
		}

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-version") || args[i].equals("-v"))
				if (i + 1 < args.length) {
					++i;
					if (args[i].equals("2.0"))
						version = 2;
					else if (args[i].equals("3.0"))
						version = 3;
					else {
						System.out.println("-help displays help ");
						throw new RuntimeException(
								"Invalid version specified for the file to check");
					}
					continue;
				} else {
					System.out.println("-help displays help ");
					throw new RuntimeException(
							"After the argument -v or -version, the actual version of the file to be checked is expected");
				}
			else if (args[i].equals("-mode"))
				if (i + 1 < args.length) {
					mode = args[++i];
					continue;
				} else {
					System.out.println("-help displays help ");
					throw new RuntimeException(
							"After the argument -mode, the type of the file to be checked is expected");
				}
			else if (args[i].equals("-help") || args[i].equals("-?"))
				displayHelp(); // display help message
			else
				path = args[i];
		}

		if (path != null) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < path.length(); i++)
				if (path.charAt(i) == '\\')
					sb.append('/');
				else
					sb.append(path.charAt(i));
			path = sb.toString();
		}

		if (path == null) {
			System.err.println("No file to check was specified in arguments!");
			System.err.println("The tool will EXIT!");
			System.exit(1);
		} else if (path.endsWith(".epub")) {
			if (mode != null || version != 3) {
				System.err
						.println("The mode and version arguments are ignored for epubs!"
								+ "(They are retrieved from the files.)");
				mode = null;
			}
		} else if (mode == null)
			throw new RuntimeException(
					"For files other than epubs, mode must be specified! Default version is 3.0.");
	}

	/**
	 * This method displays a short help message that describes the command-line
	 * usage of this tool
	 */
	public static void displayHelp() {
		displayVersion();

		System.out.println("When running this tool, the first argument "
				+ "should be the name (with the path) of the file to check.");
		System.out
				.println("If checking a non-epub "
						+ "file, the epub version of the file must be specified using -v "
						+ "and the type of the file using -mode.");
		System.out.println("The default version is: 3.0.");
		System.out.println("Modes and versions supported: ");
		System.out.println("-mode opf -v 2.0");
		System.out.println("-mode opf -v 3.0");

		System.out.println("-mode xhtml -v 2.0");
		System.out.println("-mode xhtml -v 3.0");

		System.out.println("-mode svg -v 2.0");
		System.out.println("-mode svg -v 3.0");

		System.out.println("-mode mo -v 3.0 // For MediaOverlay validation");
		System.out.println("-mode nav -v 3.0");

		System.out.println("This tool also accepts the following flags:");
		System.out.println("-? or -help 	= displays this help message");
	}

	public static void displayVersion() {
		System.out.println("Epubcheck Version " + EpubCheck.VERSION + "\n");
	}
}
