# Using EpubCheck in your development #
The following pages contain useful information about how you can use epubcheck in your development:

  * [Using EpubCheck as a library](Library.md)
  * [Extraction of information](Extraction.md)

<br />
# Developing EpubCheck #
If you want to contribute to the project, this information might be useful to setup project environment on your system.

### Tasks ###
  * [Development Tasks](Tasks.md)

### Start contributing ###
The project repository is using [Maven](http://maven.apache.org/). To build and test it you need Maven (2.3 or above) to be installed on your system.

After checking out the [sources](http://code.google.com/p/epubcheck/source/) with SVN, you can easily import the repository into [Eclipse](http://www.eclipse.org/) using _"File" > "Import …" > "Maven" > "Existing Maven Projects" >_ then browse for the directory containing the `pom.xml` file _> "Finish"._

### Test suite (JUnit) ###
Note that you should provide unit tests (we're using JUnit) for every patch you do. Also, every patch must leave the JUnit test suite passing 100%! You can run the tests either from Eclipse or on the commandline from within `trunk/`:
```
mvn clean test
```

### Building epubcheck ###
Run the following command from within `trunk/` to build epubcheck from sources:
```
mvn install
```

### Commit your patches ###
If you want your patches to be included in the next release, please contact us for SVN commit rights.