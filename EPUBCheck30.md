# EPUBCheck 3.0 Information #

## About ##

EpubCheck 3.0 is the continuation of the EpubCheck 1.2 project, upgraded to support validation of [EPUB 3.0](http://idpf.org/epub/30) content besides the legacy [EPUB 2.0](http://idpf.org/epub/201).

At the time of this writing, the available build is considered stable and recommended for both EPUB 2.0(1) and 3.0 validation.

Besides supporting both EPUB 2 and 3, another new feature added to EpubCheck 3.0 is _single file validation_. Currently, the following types are supported: OPF/Package Documents, XHTML, SVG, Navigation Documents and Media Overlay documents (the latter two naturally are only supported in EPUB 3.0 mode).

EpubCheck 3.0 also supports validation of an unzipped EPUB with the possibility to save the created EPUB archive. Note that if your unzipped input EPUB is found to be invalid, then the created archive will be discarded.


## Running ##

To run the tool you need java (1.6 or above). Any Operating System should do.

Wiki page [Running EpubCheck](Running.md) provides detailed information about running epubcheck in all its different modes.


## Contributing to EpubCheck ##

If you'd like to contribute to the epubcheck project, then there's some things you'll probably want to do:
  * Join the [discussion group](http://groups.google.com/group/epubcheck) for epubcheck
  * Check out the [latest source code](http://code.google.com/p/epubcheck/source/checkout) from epubcheck SVN repository and start fixing issues
  * [Report problems and issues](http://code.google.com/p/epubcheck/issues/list) and submit test cases that expose these problems, either as full `.epub` archives, or as single files.