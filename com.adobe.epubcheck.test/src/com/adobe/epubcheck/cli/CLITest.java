package com.adobe.epubcheck.cli;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.Test;

import com.adobe.epubcheck.tool.Checker;

public class CLITest {
	private static String epubPath = "com.adobe.epubcheck.test/testdocs/30/epub/";
	private static String expPath = "com.adobe.epubcheck.test/testdocs/30/expanded/";
	private static String singlePath = "com.adobe.epubcheck.test/testdocs/30/single/";
	
	@Test
	public void testNPE() {		
		assertEquals(1, run(null));		
	}
	
	@Test
	public void testValidEPUB() {		
		assertEquals(0, run(new String[]{epubPath + "valid/lorem.epub"}));		
	}
		
	@Test
	public void testInvalidEPUB() {		
		assertEquals(1, run(new String[]{epubPath + "invalid/lorem-xht-sch-1.epub"}));		
	}
	
	@Test
	public void testValidExp() {		
		assertEquals(0, run(new String[]{expPath + "valid/lorem-basic/", "-mode", "exp"}));		
	}
	
	@Test
	public void testInvalidExp() {		
		assertEquals(1, run(new String[]{expPath + "invalid/lorem-xhtml-rng-1/", "-mode", "exp"}));		
	}
	
	@Test
	public void testValidSingle() {		
		assertEquals(0, run(new String[]{singlePath + "nav/valid/nav001.xhtml", "-mode", "nav"}));		
	}
	
	@Test
	public void testInvalidSingle() {		
		assertEquals(1, run(new String[]{singlePath + "nav/invalid/noTocNav.xhtml", "-mode", "nav"}));		
	}
	
	private int run(String[] args) {
		PrintStream outOrig = System.out;
		PrintStream errOrig = System.err;
		System.setOut(new NullPrintStream());
		System.setErr(new NullPrintStream());
		int result = Checker.run(args);
		System.setOut(outOrig);
		System.setErr(errOrig);
		return result;
	}
	
	class NullPrintStream extends PrintStream {
		public NullPrintStream() {
			super(new OutputStream() {				
				@Override
				public void write(int b) throws IOException {
					
				}
			});
		}		
	}
}	
	
