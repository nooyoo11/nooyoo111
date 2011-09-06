package com.adobe.epubcheck.ocf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.adobe.epubcheck.util.GenericResourceProvider;

public class OCFPackage implements GenericResourceProvider {

	ZipFile zip;
	Hashtable<String, EncryptionFilter> enc;
	String uniqueIdentifier;

	public OCFPackage(ZipFile zip) {
		this.zip = zip;
		this.enc = new Hashtable<String, EncryptionFilter>();
	}

	public void setEncryption(String name, EncryptionFilter encryptionFilter) {
		enc.put(name, encryptionFilter);
	}

	public void setUniqueIdentifier(String idval) {
		uniqueIdentifier = idval;
	}

	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public boolean hasEntry(String name) {
		return zip.getEntry(name) != null;
	}

	public boolean canDecrypt(String name) {
		EncryptionFilter filter = (EncryptionFilter) enc.get(name);
		if (filter == null)
			return true;
		return filter.canDecrypt();
	}

	public InputStream getInputStream(String name) throws IOException {
		ZipEntry entry = zip.getEntry(name);
		if (entry == null)
			return null;
		InputStream in = zip.getInputStream(entry);
		EncryptionFilter filter = (EncryptionFilter) enc.get(name);
		if (filter == null)
			return in;
		if (filter.canDecrypt())
			return filter.decrypt(in);
		return null;
	}

	public HashSet<String> getFileEntries() throws IOException {
		HashSet<String> entryNames = new HashSet<String>();

		for (Enumeration entries = zip.entries(); entries.hasMoreElements();) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			if (!entry.isDirectory()) {
				entryNames.add(entry.getName());
			}
		}

		return entryNames;
	}

	public HashSet<String> getDirectoryEntries() throws IOException {
		HashSet<String> entryNames = new HashSet<String>();

		for (Enumeration entries = zip.entries(); entries.hasMoreElements();) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			if (entry.isDirectory()) {
				entryNames.add(entry.getName());
			}
		}

		return entryNames;
	}
}
