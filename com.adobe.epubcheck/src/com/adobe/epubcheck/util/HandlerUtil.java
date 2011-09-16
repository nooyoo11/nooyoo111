package com.adobe.epubcheck.util;

import java.util.HashSet;

import com.adobe.epubcheck.api.Report;

public class HandlerUtil {

	public static void processPrefixes(String prefix,
			HashSet<String> prefixSet, Report report, String path, int line,
			int column) {
		if (prefix == null)
			return;
		prefix = prefix.replaceAll("[\\s]+", " ");

		String prefixArray[] = prefix.split(" ");
		boolean validPrefix;
		for (int i = 0; i < prefixArray.length; i++) {
			validPrefix = true;
			if (!prefixArray[i].endsWith(":")) {
				report.error(path, line, column, "Invalid prefix "
						+ prefixArray[i]);
				validPrefix = false;
			}
			if (i + 1 >= prefixArray.length) {
				report.error(path, line, column, "URL for prefix "
						+ prefixArray[i] + "doesn't exist");
				return;
			}
			i++;
			if (!prefixArray[i].startsWith("http://"))
				report.error(path, line, column, "URL expected instead of "
						+ prefixArray[i - 1]);
			else if (validPrefix) {
				if (!prefixSet.contains(prefixArray[i - 1].substring(0,
						prefixArray[i - 1].length() - 1)))
					prefixSet.add(prefixArray[i - 1].substring(0,
							prefixArray[i - 1].length() - 1));
				else
					report.error(
							path,
							line,
							column,
							"Redeclaration of "
									+ prefixArray[i - 1].substring(0,
											prefixArray[i - 1].length() - 1)
									+ " prefix! Make sure it is not a reserved prefix!");
			}
		}

	}
}
