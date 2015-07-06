# Running EpubCheck #

To run the tool you need java (1.6 or above). Any Operating System should do.

epubcheck is a commandline tool, all detected errors are simply printed to `stderr` output stream

_There's no native GUI – however, there are some thirdparty apps offering a GUI. See [below](#Third-party_apps_with_GUI.md) for further details._


## Running from Commandline ##

You can run epubcheck from the commandline in the following modes:

### Basic mode ###
```
java -jar epubcheck-3.0.1.jar file.epub
```

### Advanced mode ###
```
java -jar epubcheck-3.0.1.jar singleFile -mode MODE -v VERSION
```

  * **MODE** must be one of the following:
    * **`opf`** for package document validation;
    * **`nav`** for navigation document validation (available only for version 3.0);
    * **`mo`** for media overlay validation (available only for version 3.0);
    * **`xhtml`**;
    * **`svg`**;
    * _**`exp`** for Expanded EPUB validation (see next section)_

  * **VERSION** must be one of
    * `2.0`
    * `3.0`

_Note that when validating a single file, only a subset of the available tests is run. Also, when validating a full EPUB, both mode and version are ignored._

### Expanded mode ###
```
java -jar epubcheck-3.0.1.jar folder/ -mode exp [-save]
```
When using expanded mode, there's an optional flag `-save` to save the created archive upon validation.

### Additional flags ###
  * `-out file.xml` outputs an assessment XML document
  * `-quiet` or `-q` outputs only if there is any warning or error
  * `-help`, `--help` or `-?` displays a help message


---


## Third-party apps with GUI ##

There are a bunch of third-party apps which offer a graphical user interface (_GUI_) for epubcheck. You don't need any commandline or 'developer' knowledge to use them...
  * [pagina EPUB-Checker](http://www.pagina-online.de/produkte/epub-checker/)
  * [Rainwater Soft ePubChecker](http://www.rainwater-soft.com/epubchecker/) _(seems to be deprecated)_

If you know of more, just let us know...