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
 * PartOfSpeechTagging.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.filters.unsupervised.attribute;

import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.StringUtils;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Range;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.tokenizers.Tokenizer;
import weka.core.tokenizers.WhiteSpaceTokenizer;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.SimpleStreamFilter;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PartOfSpeechTagging
  extends SimpleStreamFilter {

  private static final long serialVersionUID = 5180866251200474411L;

  /** the tokenizer factory to use. */
  protected static TokenizerFactory m_TokenizerFactory;

  /** the attribute range to work on. */
  protected Range m_AttributeIndices = new Range("first-last");

  /** the tokenizer algorithm to use. */
  protected Tokenizer m_Tokenizer = new WhiteSpaceTokenizer();

  /** the model to use. */
  protected File m_Model = new File(".");

  /** additional options, */
  protected String m_AdditionalOptions = "";

  /** the parser in use. */
  protected LexicalizedParser m_Parser = null;

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Performs part-of-speech tagging using the Stanford parser and the user-specified model.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<Option>();

    result.addElement(new Option("\tThe attribute range to work on.\n"
      + "This is a comma separated list of attribute indices, with "
      + "\"first\" and \"last\" valid values.\n"
      + "\tSpecify an inclusive range with \"-\".\n"
      + "\tE.g: \"first-3,5,6-10,last\".\n" + "\t(default: first-last)", "R",
      1, "-R <range>"));

    result.addElement(new Option("\tInverts the attribute selection range.\n"
      + "\t(default: off)", "V", 0, "-V"));

    result.addElement(new Option(
      "\tThe tokenizing algorihtm (classname plus parameters) to use.\n"
        + "\t(default: " + WordTokenizer.class.getName() + ")", "tokenizer", 1,
      "-tokenizer <spec>"));

    result.addElement(new Option(
      "\tThe stanford model file to use.\n"
        + "\t(default: .", "model", 1, "-model <file>"));

    result.addElement(new Option(
      "\tThe additional options for the parser.\n"
        + "\t(default: ", "additional", 1, "-additional <options>"));

    result.addAll(Collections.list(super.listOptions()));

    return result.elements();
  }

  /**
   * Parses a given list of options.
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    String tmpStr;
    String className;
    String[] classOptions;

    tmpStr = Utils.getOption("R", options);
    if (tmpStr.length() != 0)
      setAttributeIndices(tmpStr);
    else
      setAttributeIndices("first-last");

    setInvertSelection(Utils.getFlag("V", options));

    tmpStr = Utils.getOption("tokenizer", options);
    if (tmpStr.length() == 0) {
      setTokenizer(new WordTokenizer());
    }
    else {
      classOptions = Utils.splitOptions(tmpStr);
      if (classOptions.length == 0)
	throw new Exception("Invalid tokenizer specification string");
      className = classOptions[0];
      classOptions[0] = "";
      Tokenizer tokenizer = (Tokenizer) Class.forName(className).newInstance();
      tokenizer.setOptions(classOptions);
      setTokenizer(tokenizer);
    }

    tmpStr = Utils.getOption("model", options);
    if (tmpStr.length() != 0)
      setModel(new File(tmpStr));
    else
      setModel(new File("."));

    tmpStr = Utils.getOption("additional", options);
    if (tmpStr.length() != 0)
      setAdditionalOptions(tmpStr);
    else
      setAdditionalOptions("");

    if (getInputFormat() != null)
      setInputFormat(getInputFormat());

    super.setOptions(options);

    Utils.checkForRemainingOptions(options);
  }

  /**
   * Gets the current settings of the filter.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    Vector<String> result = new Vector<String>();

    result.add("-R");
    result.add(getAttributeIndices());

    if (getInvertSelection())
      result.add("-V");

    result.add("-tokenizer");
    result.add("" + Utils.toCommandLine(getTokenizer()));

    result.add("-model");
    result.add("" + getModel());

    if (!getAdditionalOptions().isEmpty()) {
      result.add("-additional");
      result.add("" + getAdditionalOptions());
    }

    Collections.addAll(result, super.getOptions());

    return result.toArray(new String[result.size()]);
  }

  /**
   * Sets which attributes are to be acted on.
   *
   * @param value a string representing the list of attributes. Since the string
   *          will typically come from a user, attributes are indexed from1. <br/>
   *          eg: first-3,5,6-last
   */
  public void setAttributeIndices(String value) {
    m_AttributeIndices.setRanges(value);
  }

  /**
   * Gets the current range selection.
   *
   * @return a string containing a comma separated list of ranges
   */
  public String getAttributeIndices() {
    return m_AttributeIndices.getRanges();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String attributeIndicesTipText() {
    return "Specify range of attributes to act on; "
      + "this is a comma separated list of attribute indices, with "
      + "\"first\" and \"last\" valid values; specify an inclusive "
      + "range with \"-\"; eg: \"first-3,5,6-10,last\".";
  }

  /**
   * Sets whether to invert the selection of the attributes.
   *
   * @param value if true then the selection is inverted
   */
  public void setInvertSelection(boolean value) {
    m_AttributeIndices.setInvert(value);
  }

  /**
   * Gets whether to invert the selection of the attributes.
   *
   * @return true if the selection is inverted
   */
  public boolean getInvertSelection() {
    return m_AttributeIndices.getInvert();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String invertSelectionTipText() {
    return "If set to true, the selection will be inverted; eg: the attribute "
      + "indices '2-4' then mean everything apart from '2-4'.";
  }

  /**
   * the tokenizer algorithm to use.
   *
   * @param value the configured tokenizing algorithm
   */
  public void setTokenizer(Tokenizer value) {
    m_Tokenizer = value;
  }

  /**
   * Returns the current tokenizer algorithm.
   *
   * @return the current tokenizer algorithm
   */
  public Tokenizer getTokenizer() {
    return m_Tokenizer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String tokenizerTipText() {
    return "The tokenizing algorithm to use on the strings.";
  }

  /**
   * Sets the parser model to use.
   *
   * @param value the model file
   */
  public void setModel(File value) {
    m_Model = value;
  }

  /**
   * Returns the parser model to use.
   *
   * @return the model file
   */
  public File getModel() {
    return m_Model;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String modelTipText() {
    return "The Stanford model file to use.";
  }

  /**
   * Sets the additional options for the parser.
   *
   * @param value the additional options
   */
  public void setAdditionalOptions(String value) {
    m_AdditionalOptions = value;
  }

  /**
   * Gets the current additional options for the stanford parser.
   *
   * @return the additional options
   */
  public String getAdditionalOptions() {
    return m_AdditionalOptions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String additionalOptionsTipText() {
    return "The additional options for the stanford parser.";
  }

  /**
   * Returns the Capabilities of this filter.
   *
   * @return the capabilities of this object
   * @see weka.core.Capabilities
   */
  @Override
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();
    result.disableAll();

    // attributes
    result.enableAllAttributes();
    result.enable(Capability.MISSING_VALUES);

    // class
    result.enableAllClasses();
    result.enable(Capability.MISSING_CLASS_VALUES);
    result.enable(Capability.NO_CLASS);

    return result;
  }

  /**
   * resets the filter, i.e., m_NewBatch to true and m_FirstBatchDone to false.
   *
   * @see #m_NewBatch
   * @see #m_FirstBatchDone
   */
  @Override
  protected void reset() {
    super.reset();

    m_Parser = null;
  }

  /**
   * Determines the output format based on the input format and returns this. In
   * case the output format cannot be returned immediately, i.e.,
   * hasImmediateOutputFormat() returns false, then this method will called from
   * batchFinished() after the call of preprocess(Instances), in which, e.g.,
   * statistics for the actual processing step can be gathered.
   *
   * @param inputFormat the input format to base the output format on
   * @return the output format
   * @throws Exception in case the determination goes wrong
   */
  @Override
  protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
    if (!m_Model.exists())
      throw new IllegalStateException("Model file does not exist: " + m_Model);
    else if (m_Model.isDirectory())
      throw new IllegalStateException("Model file points to a directory: " + m_Model);
    m_Parser = edu.stanford.nlp.parser.lexparser.LexicalizedParser.loadModel(
      m_Model.getAbsolutePath(), Utils.splitOptions(m_AdditionalOptions));

    m_AttributeIndices.setUpper(inputFormat.numAttributes() - 1);

    return new Instances(inputFormat, 0);
  }

  /**
   * Shortens the string if over specified maximum length.
   *
   * @param s the string to (potentially) shorten
   * @param max the maximum string length
   * @return the processed string
   */
  protected String shorten(String s, int max) {
    if (s.length() > max)
      return s.substring(0, max) + "...";
    else
      return s;
  }

  /**
   * Outputs the debug message out stdout if debug flag set.
   *
   * @param msg		the message to output
   */
  protected void debug(String msg) {
    if (getDebug())
      System.out.println(getClass().getName() + ": " + msg);
  }

  /**
   * Traverses the tree and adds the leaf data to the string buffer.
   *
   * @param parentTree	the tree to process
   * @param content	the string buffer to add the content to
   */
  protected void traverseTree(Tree parentTree, StringBuilder content) {
    Tree	childTree;
    int		i;

    for (i = 0; i < parentTree.children().length; i++) {
      childTree = parentTree.children()[i];
      if (childTree.isLeaf()) {
	if (content.length() > 0)
	  content.append(", ");
	content.append(parentTree.label().value() + ":" + childTree.label().value());
      }
      traverseTree(childTree, content);
    }
  }

  /**
   * Obtains the sentences from the document.
   *
   * @param doc	the document to turn into sentences.
   * @return the list of sentences
   */
  protected List<String> getSentences(String doc) {
    List<String>		result;
    DocumentPreprocessor preProcessor;

    result = new ArrayList<String>();

    preProcessor = new DocumentPreprocessor(new StringReader(doc));
    preProcessor.setTokenizerFactory(getTokenizerFactory());

    for (List sentence: preProcessor)
      result.add(StringUtils.joinWithOriginalWhiteSpace(sentence));

    return result;
  }

  /**
   * processes the given instance (may change the provided instance) and returns
   * the modified version.
   *
   * @param instance the instance to process
   * @return the modified data
   * @throws Exception in case the processing goes wrong
   */
  @Override
  protected Instance process(Instance instance) throws Exception {
    double[]		values;
    double[]		current;
    List<String>	words;
    int			i;
    Tree		tree;
    List<String>	sentences;
    StringBuilder	tagged;
    String		newString;

    words = new ArrayList<String>();
    values = new double[instance.numAttributes()];
    current = instance.toDoubleArray();

    for (i = 0; i < instance.numAttributes() - 1; i++) {
      if (!instance.attribute(i).isString() || !m_AttributeIndices.isInRange(i)) {
	values[i] = current[i];
	continue;
      }
      if (instance.isMissing(i)) {
	values[i] = Utils.missingValue();
	continue;
      }
      // split into sentences
      sentences = getSentences(instance.stringValue(i));
      if (getDebug())
	debug(sentences.size() + " sentence(s) [" + instance.stringValue(i).length() + "]: " + shorten(instance.stringValue(i), 40));
      tagged = new StringBuilder();
      for (String sentence: sentences) {
	if (getDebug())
	  debug("    " + shorten(sentence, 30) + " [" + sentence.length() + "]");
	// split into words
	words.clear();
	m_Tokenizer.tokenize(sentence);
	while (m_Tokenizer.hasMoreElements())
	  words.add(m_Tokenizer.nextElement());
	// generate parse tree
	tree = m_Parser.apply(Sentence.toWordList(words.toArray(new String[words.size()])));
	traverseTree(tree, tagged);
	tagged.append(". ");
      }
      newString = tagged.toString().trim();
      values[i] = getOutputFormat().attribute(i).addStringValue(newString);
    }

    return new DenseInstance(instance.weight(), values);
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision: -1 $");
  }

  /**
   * Returns the tokenizer factory to use.
   *
   * @return		the factory
   */
  protected static synchronized TokenizerFactory getTokenizerFactory() {
    if (m_TokenizerFactory == null) {
      m_TokenizerFactory = PTBTokenizer.factory(
	new CoreLabelTokenFactory(),
	"normalizeParentheses=false,normalizeOtherBrackets=false,invertible=true");
    }
    return m_TokenizerFactory;
  }

  /**
   * Main method for executing this filter.
   *
   * @param args arguments to the filter: use -h for help
   */
  public static void main(String[] args) {
    runFilter(new PartOfSpeechTagging(), args);
  }
}
