# Running EpubCheck for Web #


---


**_ATTENTION: This content is deprecated as of Dec 2012. epubcheck 3.0 isn't beeing released as a java web archive anymore..._**

---


**EpubCheck for Web** is a simple web interface for EpubCheck. It can be deployed using any servlet-compatible web server, such as [Apache Tomcat](http://tomcat.apache.org). Note that you need Java version no less than 1.5.

To run EpubCheck on the web server, select a `.war` extension download. You may want to rename it, as the name is typically used as a base path for the web app. Drop the war file where your sever expects to find web applications (e.g. `C:\Program Files\Apache Software Foundation\Tomcat 6.0\webapp`). If your file was named `epubcheck.war`, you should be able to point your browser to `http://your-server/epubcheck` and see a simple web page to upload epub file.