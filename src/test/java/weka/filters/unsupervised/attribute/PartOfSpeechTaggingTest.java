/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2015 University of Waikato
 */

package weka.filters.unsupervised.attribute;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.core.Instances;
import weka.filters.AbstractFilterTest;
import weka.filters.Filter;
import weka.tests.TestHelper;

import java.io.File;

/**
 * Tests PartOfSpeechTagging. Run from the command line with:<p>
 * java weka.filters.unsupervised.attribute.PartOfSpeechTaggingTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8034 $
 */
public class PartOfSpeechTaggingTest extends AbstractFilterTest {

  public PartOfSpeechTaggingTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    TestHelper.setRegressionRoot();
    super.setUp();
    TestHelper.copyResourceToTmp("weka/filters/unsupervised/attribute/data/englishPCFG.ser.gz");
  }

  /**
   * Called by JUnit after each test method.
   */
  @Override
  protected void tearDown() {
    TestHelper.deleteFileFromTmp("englishPCFG.ser.gz");
    super.tearDown();
  }

  /**
   * Creates an example PartOfSpeechTagging.
   */
  public Filter getFilter() {
    PartOfSpeechTagging f = new PartOfSpeechTagging();
    f.setModel(new File(TestHelper.getTmpDirectory() + File.separator + "englishPCFG.ser.gz"));
    return f;
  }

  public void testTypical() {
    Instances result = useFilter();
    // Number of attributes and instances shouldn't change
    assertEquals(m_Instances.numInstances(), result.numInstances());
  }

  public static Test suite() {
    return new TestSuite(PartOfSpeechTaggingTest.class);
  }

  public static void main(String[] args){
    TestRunner.run(suite());
  }
}
