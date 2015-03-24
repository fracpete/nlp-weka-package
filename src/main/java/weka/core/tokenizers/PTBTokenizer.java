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
 * PBTTokenizer.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.core.tokenizers;

import edu.stanford.nlp.ling.Word;
import weka.core.RevisionUtils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * <a href="http://www.cis.upenn.edu/~treebank/">Penn Treebank tokenizer</a>.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PTBTokenizer
  extends Tokenizer {

  private static final long serialVersionUID = 1010088668175214165L;

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
    return
      "Penn Treebank tokenizer.\n\n"
	+ "For more details see:\n"
	+ "http://www.cis.upenn.edu/~treebank/";
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
    edu.stanford.nlp.process.PTBTokenizer<Word> tokenizer;

    tokenizer = edu.stanford.nlp.process.PTBTokenizer.newPTBTokenizer(new StringReader(s));
    m_Tokens = new ArrayList<String>();
    while (tokenizer.hasNext())
      m_Tokens.add(tokenizer.next().value());
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
