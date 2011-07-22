package com.adobe.epubcheck.opf;

public class InvalidVersionException extends Exception {

	public static String UNSUPPORTED_VERSION = "Version not supported";
	public static String VERSION_NOT_FOUND = "Version not found, or invalid version format";
	
	public InvalidVersionException(String message) {
		super(message);

	}

}
