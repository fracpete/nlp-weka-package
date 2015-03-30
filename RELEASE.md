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

* make sure there are no *intermediate* Weka releases in local Maven repository
  (just delete the `weka-dev` sub-directory)

* Run the following command

  <pre>
  mvn --batch-mode release:prepare release:perform -s /home/fracpete/.m2/settings-central.xml
  </pre>

* go to https://oss.sonatype.org/ and close/release the artifacts

* do a `git push`

