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

/**
 * WhiteSpaceTokenizer.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.core.tokenizers;

import weka.core.RevisionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Simply uses Java's String.split(\\s) method for generating the tokens.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WhiteSpaceTokenizer
  extends Tokenizer {

  private static final long serialVersionUID = 7007134400576117968L;

  /** the tokens. */
  protected List<String> m_Tokens = null;

  /**
   * Returns a string describing the stemmer
   *
   * @return a description suitable for displaying in the explorer/experimenter
   *         gui
   */
  @Override
  public String globalInfo() {
    return "Simply uses Java's 'String.split(\"\\\\s\")' method for generating the tokens.";
  }

  /**
   * Tests if this enumeration contains more elements.
   *
   * @return true if and only if this enumeration object contains at least one
   *         more element to provide; false otherwise.
   */
  @Override
  public boolean hasMoreElements() {
    return (m_Tokens != null) && (m_Tokens.size() > 0);
  }

  /**
   * Returns the next element of this enumeration if this enumeration object has
   * at least one more element to provide.
   *
   * @return the next element of this enumeration.
   */
  @Override
  public String nextElement() {
    return m_Tokens.remove(0);
  }

  /**
   * Sets the string to tokenize. Tokenization happens immediately.
   *
   * @param s the string to tokenize
   */
  @Override
  public void tokenize(String s) {
    m_Tokens = new ArrayList<String>(Arrays.asList(s.split("\\s")));
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  @Override
  public String getRevision() {
    return RevisionUtils.extract("$Revision: -1 $");
  }
}
