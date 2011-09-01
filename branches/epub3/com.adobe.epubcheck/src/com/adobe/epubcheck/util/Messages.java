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

package com.adobe.epubcheck.util;

public class Messages {

	public static String FULL_EPUB = "File is validated as a complete epub archive.";

	public static String SINGLE_FILE = "File is validated as a single file of type %1$s and version %2$s ! Only a subset of the available tests is run!";

	public static String MISSING_FILE = "File %1$s is missing in the package!";

	public static String INVALID_OPF_Version = "Failed obtaining OPF version: %1$s";

	public static String NAV_NOT_SUPPORTED = "The nav file is not supported for epub versions less than 3.0!";

	public static String OPV_VERSION_TEST = "Tests are done only for the OPF version!";

	public static String MODE_VERSION_NOT_SUPPORTED = "The checker doesn't validate type %1$s and version %2$s!";
}
