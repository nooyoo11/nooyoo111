# Introduction #

The epubcheck tool is an application that can be run at a command line and that provides for validation of ePub documents.

# How epubcheck Processes an ePub Document #

When the epubcheck tool is used to validate a document it does so by using a set of "Checkers" each of which examines a particular portion of the ePub file. As the tool examines the file, it will use an "OCFChecker" to validate the OCF structure, an "OPFChecker" to validate the OPF file, and so on.

In the following sections we'll examine what each of the checkers does. The purpose of this document is to outline what the tool does, not necessarily how it does it, so much of the details of **how** the checkers work will be glossed over.

## Examining the File ##

The first thing validated is the zip file. The epubcheck tool ensures that the zip file has a 'zip header' or section at the begining of the file. It also checks that the 'mimetype' file is at the proper location and has the appropriate content. Technically it does this by reading from byte 30 in the file looking for 'mimetype' and then from byte 38 in the file looking for 'application/epub+zip'.

After these checks the zip file is loaded as a zip package, which will fail if the zip file is corrupt, has bad info in the header, or is otherwise incomplete.

## Parsing and Validating the Files in the Package ##

Most of the files in an ePub document are XML files. Each XML file is checked to make sure it is well-formed, and that it validates.

For each of the XML based files (OPF, NCX, XHTML, DTBook, SVG) the tool has one or more schema files which defines the structure of the file, and the tool validates the files against those schema. In addition to validating the files against the schemas, the tool will also run a set of checks for things that aren't related to the validity of the individual XML files but are required for a valid ePub document. These checks include things like ensuring that if there's an image called out in the XHTML file, that image really exists in the zip file and is listed in the manifest.

## Checking the OCF Related Content ##

The 'encryption.xml', 'container.xml', and 'signatures.xml' files, if they exist, are checked against the respective schemas. OCFChecker also retrieves the OPF file

## OPF File ##
Validates the OPF against the schema
Checks the 'unique-id' to ensure that it references an actual id in the OPF file.
Checks the existence of the NCX file
Checks each item in the manifest
  * that they exist in the package
  * for invalid content in the media-type attribute
  * for text/html, which is not appropriate for epubs
  * for deprecated media-types in OPS documents
  * for newer media-types in OEBPS 1.2 documents
  * for fallbacks for unknown media-types.
Opens each item and runs the appropriate checker (OPSChecker for XHTML, DTBookChecker for DTBook, BitmapChecker for images, etc.)
Checks each item in the spine.
  * must be valid for the spine
  * must be a preferred type, or have a provided fallback.

## XHTML Files ##
Validates the XHTML file against the schema files.
Checks that each reference image exists in the package.

## DTBook Files ##
Validate the DTBook file against the schema.

## Checking Bitmaps ##
Validates the image header and image type.

## NCX File ##
Validates the NCX against the schema.