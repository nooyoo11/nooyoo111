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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.adobe.epubcheck.api.EpubCheck;
import com.adobe.epubcheck.api.Report;
import com.adobe.epubcheck.opf.DocumentValidator;
import com.adobe.epubcheck.opf.OPFChecker;
import com.adobe.epubcheck.ops.OPSChecker;
import com.adobe.epubcheck.util.DefaultReportImpl;
import com.adobe.epubcheck.util.FileResourceProvider;
import com.adobe.epubcheck.util.GenericResourceProvider;
import com.adobe.epubcheck.util.OPSType;
import com.adobe.epubcheck.util.URLResourceProvider;

public class Checker {

	private static String path = null, mimeType = null;
	private static float version = 3;

	private static void validateArguments(String fileName, String mimeType,
			float version) {
		if (fileName.endsWith(".epub") || fileName.endsWith(".opf"))
			return;

		if (!verifyMimeTypeAndVersion()) {
			System.out.println("-help displays help ");
			throw new RuntimeException(
					"The checker doesn't validate media-Type " + mimeType
							+ " and version " + version
							+ "!\nThe tool will exit!");
		}
		return;
	}

	public static void validateFile(GenericResourceProvider resourceProvider,
			String fileName, String mimeType, float version, Report report) {

		validateArguments(fileName, mimeType, version);

		DocumentValidator check = null;
		if (fileName.endsWith(".epub")) {
			try {
				check = new EpubCheck(
						resourceProvider.getInputStream(fileName), report);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else if (fileName.endsWith(".opf")) {
			check = new OPFChecker(fileName, resourceProvider, report);
		} else
			check = new OPSChecker(fileName, mimeType, resourceProvider,
					report, version);

		if (check.validate())
			System.out.println("No errors or warnings detected");
		else {
			System.err.println("\nCheck finished with warnings or errors!\n");
		}
	}

	public static void validateFile(String path, String mimeType,
			float version, Report report) {

		boolean fromFile = false;

		String fileName = path.substring(path.lastIndexOf('/') + 1,
				path.length());

		validateArguments(fileName, mimeType, version);

		GenericResourceProvider resourceProvider;
		DocumentValidator check;

		if (path.startsWith("http://") || path.startsWith("https://"))
			resourceProvider = new URLResourceProvider(path);
		else {
			resourceProvider = new FileResourceProvider(path);
			fromFile = true;
		}

		if (fileName.endsWith(".epub")) {
			if (fromFile)
				check = new EpubCheck(new File(path), report);
			else
				try {
					check = new EpubCheck(
							resourceProvider.getInputStream(path), report);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
		} else if (fileName.endsWith(".opf")) {
			check = new OPFChecker(fileName, resourceProvider, report);
		} else
			check = new OPSChecker(fileName, mimeType, resourceProvider,
					report, version);

		if (check.validate())
			System.out.println("No errors or warnings detected");
		else {
			System.err.println("\nCheck finished with warnings or errors!\n");
		}
	}

	private static HashMap mimeTypeValidatorMap;

	static {
		HashMap map = new HashMap();
		String TRUE = "true";
		map.put(new OPSType("application/xhtml+xml", 2), TRUE);
		map.put(new OPSType("application/xhtml+xml", 3), TRUE);

		map.put(new OPSType("image/svg+xml", 2), TRUE);
		map.put(new OPSType("image/svg+xml", 3), TRUE);

		map.put(new OPSType("application/smil+xml", 3), TRUE);
		map.put(new OPSType("nav", 3), TRUE);

		mimeTypeValidatorMap = map;
	}

	public static void main(String[] args) {

		processArguments(args);

		Report report = new DefaultReportImpl(path);

		validateFile(path, mimeType, version, report);
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
			else if (args[i].equals("-type") || args[i].equals("-t"))
				if (i + 1 < args.length) {
					mimeType = args[++i];
					continue;
				} else {
					System.out.println("-help displays help ");
					throw new RuntimeException(
							"After the argument -t or -type, the type of the file to be checked is expected");
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
		} else if (path.endsWith(".epub") || path.endsWith(".opf")) {
			if (mimeType != null)
				System.err
						.println("The mimeType and version arguments are ignored for epubs and opfs!"
								+ "(They are retrieved from the files.)");
		} else if (mimeType == null)
			throw new RuntimeException(
					"For files other than epubs and opfs, type must be specified! Default version is 3.0.");
	}

	private static boolean verifyMimeTypeAndVersion() {
		return mimeTypeValidatorMap.get(new OPSType(mimeType, version)) != null;
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
				.println("If checking a non-epub or a non-opf "
						+ "file, the epub version of the file must be specified using -v "
						+ "and the mimeType of the file using -t.");
		System.out.println("The default version is: 3.0.");
		System.out.println("Types and versions supported: ");
		System.out.println("-t application/xhtml+xml -v 2.0");
		System.out.println("-t application/xhtml+xml -v 3.0");

		System.out.println("-t image/svg+xml -v 2.0");
		System.out.println("-t image/svg+xml -v 3.0");

		System.out.println("-t application/smil+xml -v 3.0");
		System.out.println("-t nav -v 3.0");

		System.out.println("This tool also accepts the following flags:");
		System.out.println("-? or -help 	= displays this help message");
	}

	public static void displayVersion() {
		System.out.println("Epubcheck Version " + EpubCheck.VERSION + "\n");
	}
}
