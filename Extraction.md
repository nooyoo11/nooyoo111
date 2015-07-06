# Extraction of information #

With the 3.0 version of epubcheck, it's now possible to extract some information while parsing and checking a epub file.
This extraction is available in two flavors depending whether you only want to use the command line or if you are ready to customize your own `Report` in Java.

## XML output ##

The new `-out` argument can be used to output a xml file containing some information extracted from the input epub file.

Calling `java -jar epubcheck-3.0.1.jar -out output.xml file.epub` will generate the file `output.xml` containing information on the `file.epub`.

The output file uses the _jhove_ schema (available at http://hul.harvard.edu/ois/xml/xsd/jhove/jhove.xsd or see the project http://sourceforge.net/projects/jhove/) in order to display the information so that properties of any type can be output.

If you need another schema to work with, you can create a XSL stylesheet in order to transform the given output. If you prefer to directly output another kind of information, you must use the second method explain just below.


## Customizing the Java `Report` ##

In order to generate a specific report, you need to write a new Java class implementing the `com.adobe.epubcheck.api.Report` interface.

In particular, the `info(String resource, FeatureEnum feature, String value)` method will be called during the parsing process each time a particular feature has been detected in the epub file. The list of the currently detected features can be found in the `com.adobe.epubcheck.util.FeatureEnum` enumeration.

As a starting point, you can use the `com.adobe.epubcheck.util.XmlReportImpl` class or the `com.adobe.epubcheck.util.ValidationReport` test class.
You might also want to take a look in the default Report implementation in `com.adobe.epubcheck.util.DefaultReportImpl`

Once your new report class is implemented, you should call it by following the instructions on wiki page [Using EpubCheck as a library](Library.md).