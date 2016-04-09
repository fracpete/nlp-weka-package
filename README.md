nlp-weka-package
================

Contains various natural language processing components.

Parsers
-------

Makes use of the [Stanford Parser](http://nlp.stanford.edu/software/). You can download parser models from [Maven Central](http://search.maven.org/remotecontent?filepath=edu/stanford/nlp/stanford-parser/3.4.1/stanford-parser-3.4.1-models.jar), unzip them (a .jar file is simply a ZIP file) and point to the correct parser model. A simple parser model is available from:

* Linux

  `$HOME/wekafiles/nlp/models`

* Windows

  `%USERPROFILE%\\wekfiles\\nlp\\models`

Filters
-------

* `weka.filters.unsupervised.attribute.PartOfSpeechTagging`

  Performs part-of-speech tagging.

* `weka.filters.unsupervised.attribute.ChangeCase`

  Changes strings to upper or lower case.

Tokenizers
----------

* `weka.core.tokenizers.PTBTokenizer`

  Penn Treebank tokenizer

* `weka.core.tokenizers.WhiteSpaceTokenizer`

  simple tokenizer, uses String.split("\\s")

Explorer
--------

You can test parsers (and associated options) with the Explorer tab *NLP Parse trees*.
You simply select a `STRING` attribute from the currently loaded dataset that you
want to analyze and then you can select a row from the dataset to parse.
Once you have selected a parser model (and maybe added some custom options),
you can parse the string/document. For each sentence in the string/document,
a separate parse tree will get generated and displayed.


Releases
--------

* [2015.3.30](https://github.com/fracpete/nlp-weka-package/releases/download/v2015.3.30/nlp-2015.3.30.zip)
* [2015.3.25](https://github.com/fracpete/nlp-weka-package/releases/download/v2015.3.25/nlp-2015.3.25.zip)


How to use packages
-------------------

For more information on how to install the package, see:

http://weka.wikispaces.com/How+do+I+use+the+package+manager%3F


Maven
------

Add the following dependency in your `pom.xml` to include the package:

```xml
    <dependency>
      <groupId>com.github.fracpete</groupId>
      <artifactId>nlp-weka-package</artifactId>
      <version>2015.3.30</version>
      <type>jar</type>
      <exclusions>
        <exclusion>
          <groupId>nz.ac.waikato.cms.weka</groupId>
          <artifactId>weka-dev</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
```

