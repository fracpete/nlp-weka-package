How to make a release
=====================

Weka package
------------

* Run the following command to generate the package archive for version `1.0.0`:

  <pre>
  ant -f build_package.xml -Dpackage=nlp-1.0.0 clean make_package
  </pre>

* Create a release tag on github (v1.0.0)
* add release notes
* upload package archive from `dist`


Maven
-----

* Update version in `pom.xml` to match package release

* Run the following command

  <pre>
  mvn --batch-mode release:prepare release:perform
  </pre>

* go to https://oss.sonatype.org/ and close/release the artifacts

