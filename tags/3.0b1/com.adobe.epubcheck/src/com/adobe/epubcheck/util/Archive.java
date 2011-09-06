package com.adobe.epubcheck.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Archive {

	ArrayList<String> paths;

	ArrayList<String> names;

	File baseDir;

	File epubFile;

	String epubName;

	boolean deleteOnExit = true;

	public Archive(String base, boolean save) {
		this.deleteOnExit = !save;
		baseDir = new File(base);
		if (!baseDir.exists() || !baseDir.isDirectory())
			throw new RuntimeException(
					"The path specified for the archive is invalid!");
		epubName = baseDir.getName() + ".epub";
		epubFile = new File(epubName);
		if (deleteOnExit)
			epubFile.deleteOnExit();

		paths = new ArrayList<String>();
		names = new ArrayList<String>();
	}

	public Archive(String base) {

		baseDir = new File(base);
		if (!baseDir.exists() || !baseDir.isDirectory())
			throw new RuntimeException(
					"The path specified for the archive is invalid!");
		epubName = baseDir.getName() + ".epub";
		epubFile = new File(epubName);
		epubFile.deleteOnExit();

		paths = new ArrayList<String>();
		names = new ArrayList<String>();
	}

	public String getEpubName() {
		return epubName;
	}

	public File getEpubFile() {
		return epubFile;
	}

	public void deleteEpubFile() {
		epubFile.delete();
	}

	public void createArchive() {
		collectFiles(baseDir, "");
		byte[] buf = new byte[1024];
		try {

			ZipOutputStream out = new ZipOutputStream((new FileOutputStream(
					epubName)));

			int index = names.indexOf("mimetype");
			if (index >= 0) {
				FileInputStream in = new FileInputStream(paths.get(index));

				ZipEntry entry = new ZipEntry(names.get(index));
				entry.setMethod(ZipEntry.STORED);
				entry.setCompressedSize(20);
				entry.setSize(20);
				CRC32 crc = new CRC32();
				entry.setCrc(crc.getValue());
				out.putNextEntry(entry);

				int len;
				while ((len = in.read(buf)) > 0) {
					crc.update(buf, 0, len);
					entry.setCrc(crc.getValue());
					out.write(buf, 0, len);
				}

				paths.remove(index);
				names.remove(index);
			}

			for (int i = 0; i < paths.size(); i++) {
				FileInputStream in = new FileInputStream(paths.get(i));

				out.putNextEntry(new ZipEntry(names.get(i)));

				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				out.closeEntry();
				in.close();
			}

			out.close();
		} catch (IOException e) {
		}
	}

	private void collectFiles(File dir, String dirName) {

		File files[] = dir.listFiles();

		for (int i = 0; i < files.length; i++)
			if (files[i].isFile()) {
				names.add(dirName + files[i].getName());
				paths.add(files[i].getAbsolutePath());
			} else if (!files[i].getName().equals(".svn"))
				collectFiles(files[i], dirName + files[i].getName() + "/");
	}

	public void listFiles() {
		for (int i = 0; i < names.size(); i++)
			System.out.println(names.get(i));
	}
}
