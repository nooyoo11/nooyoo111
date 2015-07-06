# Using EpubCheck as a library #

You can use EpubCheck as a library in your Java application. EpubCheck public interfaces can be found in **`com.adobe.epubcheck.api`** package. **`EpubCheck`** class can be used to instantiate a validation engine. Use one of its constructors and then call **`validate()`** method.

**Basic example** (snippet):
```
File epubFile = new File("/path/to/your/epub/file.epub");

// simple constructor; errors are printed on stderr stream
EpubCheck epubcheck = new EpubCheck(epubFile);

// validate() returns true if no errors or warnings are found
Boolean result = epubcheck.validate();
```

**`Report`** is an interface that you can implement to get a list of the errors and warnings reported by the validation engine (instead of the error list being printed out on `stderr`). You'll find more detailed information about this topic on wiki page [Extraction#Customizing\_the\_Java\_Report](Extraction#Customizing_the_Java_Report.md)

Regular `.jar` download contains all you need to use EpubCheck as a library.


## For Maven Users ##

EpubCheck is now available as a [Maven artifact](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.idpf%22%20AND%20a%3A%22epubcheck%22) from the Maven Central Repository.

To use it as a library in a Maven-built project, add these lines to your POM dependencies:

```
<dependency>
    <groupId>org.idpf</groupId>
    <artifactId>epubcheck</artifactId>
    <version>3.0.1</version>
</dependency>
```