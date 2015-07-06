**Note: These error messages apply to EpubCheck version 1.1 and below only. New error messages are provided in EpubCheck 1.2. This page should be updated to include EpubCheck 1.2 error descriptions, though those errors are much more clear.**

# Deciphering errors #

A lot of error messages that EpubChecker generates come from the schema validation library, so they can be somewhat hard to understand. Below is a quick guide. Every error message is preceded by the resource name and the line number where an error occured.

## required attributes missing ##

Some elements have attributes that must be provided. For instance XHTML img element must have src and alt attributes.

## required elements missing ##

Some elements require certain child elements to be provided. For instance XHTML html element must contain body element.

## bad value for attribute "xxx" ##

An attribute is given a value that it cannot have. For instance, id attribute's value must be an XML name and cannot start with digit or have spaces in it.

## attribute "xxx" not allowed at this point; ignored ##

An element cannot have an attribute with such name. Many commonly used HTML attributes cannot be used in XHTML. CSS should be used in most cases instead.

Sometimes a document does not use that attribute explicitly, yet this problem is still reported. This can happen if the document uses DTD and DTD implicitly adds an unspecified attribute (e.g. XHTML 1.0 transitional places default value on clear attribute for br element, which is not allowed in XHTML 1.1). Epubcheck always uses XHTML 1.1 syntax (as specified in OPS 2.0) no matter which DTD is declared in the document (if any).

## unknown element "xxx" from namespace "xxx" ##

Unrecognized element. For instance, XHTML does not have font element, CSS should be used instead.

## element "xxx" from namespace "xxx" not allowed in this context ##

The element is used in the context which is not appropriate. For instance, XHTML tr element is used outside of the table.

## unfinished element ##

An element does not contain something that it must contain. For instance, XHTML head element which does not contain title element.

## mimetype entry missing or not the first in archive ##

A valid epub file must contain an uncompressed file named mimetype as a first entry in the zip archive. That file must contain epub mime type (application/epub+zip).

## mimetype contains wrong type (application/epub+zip expected) ##

Either mimetype is wrong or mimetype does not start at the proper byte offset in the archive. (OCF spec requires it to start at offset 38).

## empty media-type attribute ##

The media-type attribute is empty ("") or missing, which is not allowed.

## invalid content for media-type attribute ##

The content of the media-type attribute does not conform to the requirements set fourth in RFC4288 Section 4.2. Usually, this is because it contains an invalid character or does not have type/subtype separated by forward slash.

## 'XXX' is not a permissible spine media-type ##

According to the OPF spec, only OCF Content Documents are allowed to be referenced in the spine (OPF Spec, Section 4.2). This error is reported when a non-OCF Content Document media-type is referenced in the spine.

## warning: use of non-registered URI schema type in href: XXX ##

The validator will generate a warning whenvere it finds an href that references a non-registered schema-type. The list of registered schema types is kept as a resource called registeredSchemas.txt in the com.adobe.epubcheck.ops package (you can add your own, but make sure to follow the format listed)

## The "id" attribute does not have a unique value! ##

This error is generated when the schematron assertion of unique id attribute values in the content OPF file fails.

## assertion failed: different playOrder values ... that refer to same target ##

duplicate items in the TOC that point to the same piece of content, but have different playOrder assigned to them

## org.xml.sax.SAXParseException: no implementation available for schema language with namespace URI "http://www.ascc.net/xml/schematron" ##

Epubcheck is quite strict about where its lib/ directory resides. In most configurations, "lib/saxon.jar" **must** be in the same directory as the epubcheck jar file.

Note that off-the-shelf saxon.jar does not work well with epubcheck  because it registers its own XML parser without  XML 1.1 support. The version of saxon.jar which comes in the epubcheck distribution has this parser registration taken out.