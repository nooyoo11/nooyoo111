<h1><font color='#DD0000' size='28'>Important Note</font></h1>

<font size='28'>This project has <a href='https://github.com/IDPF/epubcheck'>moved to GitHub</a>!</font>

# EpubCheck #

EpubCheck is a tool to validate [IDPF](http://www.idpf.org) EPUB files, version 2.0 and later. It can detect many types of errors in EPUB. OCF container structure, OPF and OPS mark-up, and internal reference consistency are checked. EpubCheck can be run as a standalone command-line tool or used as a Java library.

Note that you must be able to run Java from the command-line and be familiar with command-line tools to use this effectively. There are <a href='http://blog.threepress.org/2010/12/16/running-epubcheck-on-your-computer/'>step-by-step guidelines</a> available.

## News ##

_2013-05-27_  EpubCheck [version 3.0.1](https://code.google.com/p/epubcheck/downloads/detail?name=epubcheck-3.0.1.zip) is now available! . See also the list of [issues fixed](https://code.google.com/p/epubcheck/issues/list?can=1&q=Milestone%3D3.0.1) since the previous release (3.0). This is now the recommended version to use for both EPUB 2.0(1) and 3.0 validation.

_2012-12-21_  EpubCheck [version 3.0](https://code.google.com/p/epubcheck/downloads/detail?name=epubcheck-3.0.zip) is now available! . See also the list of [issues fixed](https://code.google.com/p/epubcheck/issues/list?can=1&q=closed-after%3A2012%2F12%2F02++closed-before%3A2012%2F12%2F22) since the previous release candidate (RC-2).

_2012-12-02_  EpubCheck version 3.0-RC-2 is now available [here](http://code.google.com/p/epubcheck/downloads/detail?name=epubcheck-3.0-RC-2.zip). A list of issues fixed since the previous release candidate (RC-1) is available [here](http://code.google.com/p/epubcheck/issues/list?can=1&q=closed-after%3A2012%2F10%2F20++closed-before%3A2012%2F12%2F02).

_2012-10-20_ EpubCheck version 3.0-RC-1 is now available [here](http://code.google.com/p/epubcheck/downloads/detail?name=epubcheck-3.0-RC-1.zip&can=2&q=#makechanges). A list of issues fixed since the April release is available [here](http://code.google.com/p/epubcheck/issues/list?can=1&q=label%3AMilestone-3.0.0).

_2012-04-26_ EpubCheck version 3.0b5 is now available [here](http://code.google.com/p/epubcheck/downloads/detail?name=epubcheck-3.0b5.zip). A list of issues fixed since the January release is available [here](http://code.google.com/p/epubcheck/issues/list?can=1&q=Milestone%3DApril2012+&colspec=ID+Type+Status+Priority+Milestone+Owner+Summary&cells=tiles).

_2012-01-15_ A fourth beta of EpubCheck 3.0 is available [here](http://code.google.com/p/epubcheck/downloads/detail?name=epubcheck-3.0b4.zip). A list of issues fixed since the November release is available [here](http://code.google.com/p/epubcheck/issues/list?can=1&q=Milestone%3D30b4+&colspec=ID+Type+Status+Priority+Milestone+Owner+Summary&cells=tiles).

_2011-11-01_ A third beta of EpubCheck 3.0 is available in the [Development Builds](http://code.google.com/p/epubcheck/downloads/list?can=2&q=Build-Development) section.


_2011-09-06_ A second beta of EpubCheck 3.0 is available in the [Development Builds](http://code.google.com/p/epubcheck/downloads/list?can=2&q=Build-Development) section. [Read more](EPUBCheck30.md).

_2011-03-24_ We are proud to announce that the new EpubCheck v1.2 is available for download. It is available in the Downloads section for this GoogleCode project, and as a Featured download.

## Builds ##

  * [Stable build.](http://code.google.com/p/epubcheck/downloads/list?can=2&q=Build-Stable) (The build you would use to validate an epub.)
  * [Development build.](http://code.google.com/p/epubcheck/downloads/list?can=2&q=Build-Development) (The build you would use to check out the latest changes to the tool.)

## Quick start ##

  * [Running EpubCheck from command line](Running.md)
  * [Using EpubCheck as a library](Library.md)
  * [Decyphering Errors](Errors.md)

## Development ##

  * [Development Home](Development.md)

## Authors/Contributors ##

  * Peter Sorotokin
  * Garth Conboy
  * Markus Gylling
  * Piotr Kula
  * Paul Norton
  * Jessica Hekman
  * Liza Daly
  * George Bina
  * Bogdan Iordache
  * Romain Deltour
  * Thomas Ledoux
  * Tobias Fischer

Most of the EpubCheck functionality comes from the schema validation tool [Jing](http://www.thaiopensource.com/relaxng/jing.html) and schemas that were developed by [IDPF](http://www.idpf.org) and [DAISY](http://www.daisy.org/). Initial EpubCheck development was largely done at [Adobe Systems](http://www.adobe.com).