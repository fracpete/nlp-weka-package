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

import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.Range;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.filters.SimpleStreamFilter;

import java.util.Collections;
import java.util.Enumeration;
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
public class ChangeCase
  extends SimpleStreamFilter {

  private static final long serialVersionUID = 5180866251200474411L;

  /** the attribute range to work on. */
  protected Range m_AttributeIndices = new Range("first-last");

  /** whether to use uppercase instead of lowercase. */
  protected boolean m_UpperCase;

  /**
   * Returns a string describing this filter.
   *
   * @return a description of the filter suitable for displaying in the
   *         explorer/experimenter gui
   */
  @Override
  public String globalInfo() {
    return "Changes the case to lower case (default) or upper case.";
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

    result.addElement(new Option("\tWhether to convert to upper case.\n"
      + "\t(default: off)", "uppercase", 0, "-uppercase"));

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

    setUpperCase(Utils.getFlag("uppercase", options));

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

    if (getUpperCase())
      result.add("-uppercase");

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
   * Sets whether to convert to uppercase instead of lowercase.
   *
   * @param value if true then convert to uppercase
   */
  public void setUpperCase(boolean value) {
    m_UpperCase = value;
  }

  /**
   * Gets whether to convert to uppercase instead of lowercase.
   *
   * @return true if to convert to uppercase
   */
  public boolean getUpperCase() {
    return m_UpperCase;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String upperCaseTipText() {
    return "If set to true, the strings get converted to upper case instead of lower case.";
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
    m_AttributeIndices.setUpper(inputFormat.numAttributes() - 1);
    return new Instances(inputFormat, 0);
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
    int			i;
    String		newString;

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
      if (m_UpperCase)
	newString = instance.stringValue(i).toUpperCase();
      else
        newString = instance.stringValue(i).toLowerCase();
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
   * Main method for executing this filter.
   *
   * @param args arguments to the filter: use -h for help
   */
  public static void main(String[] args) {
    runFilter(new ChangeCase(), args);
  }
}
