The OverlayChecker::validate() method currently only verifies the publication's SMIL file(s), it should also verify the OPF metadata specific to Media Overlays (total duration == sum of individual spine items duration, CSS "active" class checks?).

[com.adobe.epubcheck.overlay.OverlayChecker](http://code.google.com/p/epubcheck/source/browse/branches/epub3-maven/com.adobe.epubcheck/src/main/java/com/adobe/epubcheck/overlay/OverlayChecker.java)

The OverlayHandler class doesn't verify durations (clip-begin / end pairs), it should also cross-check with the corresponding OPF metadata.

[com.adobe.epubcheck.overlay.OverlayHandler](http://code.google.com/p/epubcheck/source/browse/branches/epub3-maven/com.adobe.epubcheck/src/main/java/com/adobe/epubcheck/overlay/OverlayHandler.java)

The OverlayCheckerFactory doesn't need to do anything else.

[com.adobe.epubcheck.overlay.OverlayCheckerFactory](http://code.google.com/p/epubcheck/source/browse/branches/epub3-maven/com.adobe.epubcheck/src/main/java/com/adobe/epubcheck/overlay/OverlayCheckerFactory.java)