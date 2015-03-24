nlp-weka-package
===================================

Contains various natural language processing components. Makes use of the
[Stanford Parser](http://nlp.stanford.edu/software/).

Filters:

* `weka.filters.unsupervised.attribute.PartOfSpeechTagging`

  Performs part-of-speech tagging.

Tokenizers:

* `weka.core.tokenizers.PTBTokenizer`

  Penn Treebank tokenizer

* `weka.core.tokenizers.WhiteSpaceTokenizer`

  simple tokenizers, uses String.split("\\s")


How to use packages
-------------------

For more information on how to install the package, see:

http://weka.wikispaces.com/How+do+I+use+the+package+manager%3F


Maven
-----

Add the following dependency in your `pom.xml` to include the package:

```xml
    <dependency>
      <groupId>com.github.fracpete</groupId>
      <artifactId>nlp-weka-package</artifactId>
      <version>X.Y.Z</version>
      <type>jar</type>
      <exclusions>
        <exclusion>
          <groupId>nz.ac.waikato.cms.weka</groupId>
          <artifactId>weka-dev</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
```

