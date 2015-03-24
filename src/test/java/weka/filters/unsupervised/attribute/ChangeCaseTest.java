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

/**
 * Tests ChangeCase. Run from the command line with:<p>
 * java weka.filters.unsupervised.attribute.ChangeCaseTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8034 $
 */
public class ChangeCaseTest extends AbstractFilterTest {
  
  public ChangeCaseTest(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
    System.setProperty("weka.test.Regression.root", "src/test/resources");
    super.setUp();
  }

  /**
   * Creates an example ChangeCase.
   */
  public Filter getFilter() {
    ChangeCase f = new ChangeCase();
    return f;
  }

  public void testTypical() {
    Instances result = useFilter();
    // Number of attributes and instances shouldn't change
    assertEquals(m_Instances.numInstances(), result.numInstances());
  }

  public static Test suite() {
    return new TestSuite(ChangeCaseTest.class);
  }

  public static void main(String[] args){
    TestRunner.run(suite());
  }
}
