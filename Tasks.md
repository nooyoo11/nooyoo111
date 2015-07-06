# Tasks #

This is a list of development tasks to make EpubCheck better.

## Image validation ##

Currently only image signature is checked (to make sure that JPEGs are not mislabeled as GIFs). We can try to load the image to make sure it is not corrupted.

## Common sense checks ##

Create a comand-line flag to apply "rule of thumb" type of wisdom in addition to pure standard compliance (e.g. 300k chapters are probably too big and not going to look nice on handhelds, 1500dpi resolution images are probably excessive).

## Test cases ##

Additional test cases and regressions for issues are needed.