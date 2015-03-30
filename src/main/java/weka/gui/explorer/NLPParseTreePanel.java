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
 *    NLPParseTreePanel.java
 *    Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.gui.explorer;

import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.StringUtils;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.tokenizers.Tokenizer;
import weka.core.tokenizers.WhiteSpaceTokenizer;
import weka.filters.unsupervised.attribute.PartOfSpeechTagging;
import weka.gui.ExtensionFileFilter;
import weka.gui.GenericObjectEditor;
import weka.gui.Logger;
import weka.gui.PropertyPanel;
import weka.gui.SysErrLog;
import weka.gui.explorer.Explorer.ExplorerPanel;
import weka.gui.explorer.Explorer.LogHandler;
import weka.gui.explorer.tree.StanfordTree;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/** 
 * This panel allows the user to visualize the parse tree generated from
 * a string obtained from a dataset, using a specific parser model.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NLPParseTreePanel
  extends JPanel
  implements ExplorerPanel, LogHandler {

  /** for serialization. */
  private static final long serialVersionUID = 2078066653508312179L;

  /** the parent frame. */
  protected Explorer m_Explorer;

  /** The destination for log/status messages. */
  protected Logger m_Log;

  /** the current instances. */
  protected Instances m_Instances;

  /** the combobox for listing the string attributes. */
  protected JComboBox m_ComboBoxAtts;

  /** the combobox for listing the values of the selected string attribute. */
  protected JComboBox m_ComboBoxValues;

  /** for displaying the parse trees. */
  protected JTabbedPane m_PanelTrees;

  /** the file chooser for loading the parser models. */
  protected JFileChooser m_FileChooserModel;

  /** the text field for the parser file name. */
  protected JTextField m_TextModel;

  /** the button for the parser filechooser. */
  protected JButton m_ButtonModel;

  /** the current parser model. */
  protected File m_FileModel;

  /** the additional options for the parser. */
  protected JTextField m_TextOptions;

  /** the tokenizer algorithm to use. */
  protected Tokenizer m_Tokenizer;

  /** Lets the user configure the tokenizer. */
  protected GenericObjectEditor m_EditorTokenizer;

  /** The panel showing the current tokenizer selection. */
  protected PropertyPanel m_PanelTokenizer;

  /** the button for parsing. */
  protected JButton m_ButtonParse;

  /** the parser in use. */
  protected LexicalizedParser m_Parser;

  /**
   * Creates the Experiment panel.
   */
  public NLPParseTreePanel() {
    super();
    initialize();
    initGUI();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    ExtensionFileFilter filter;

    m_Explorer  = null;
    m_Instances = null;
    m_Log       = new SysErrLog();
    m_FileModel = null;
    m_Parser    = null;
    m_Tokenizer = new WhiteSpaceTokenizer();

    m_FileChooserModel = new JFileChooser();
    m_FileChooserModel.setFileSelectionMode(JFileChooser.FILES_ONLY);
    m_FileChooserModel.setMultiSelectionEnabled(false);
    filter = new ExtensionFileFilter(new String[]{"ser", "ser.gz"}, "Serialized parser models (*.ser, *.ser.gz)");
    m_FileChooserModel.addChoosableFileFilter(filter);
    m_FileChooserModel.setAcceptAllFileFilterUsed(true);
    m_FileChooserModel.setFileFilter(filter);

    m_EditorTokenizer = new GenericObjectEditor();
    m_EditorTokenizer.setClassType(Tokenizer.class);
    m_EditorTokenizer.setValue(m_Tokenizer);
    m_EditorTokenizer.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        repaint();
      }
    });
  }

  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    JLabel          label;
    JPanel          panelAll;
    JPanel          panel;
    List<JLabel>    labels;
    int             width;

    setLayout(new BorderLayout());

    panelAll = new JPanel(new GridLayout(6, 1));
    add(panelAll, BorderLayout.NORTH);

    labels = new ArrayList<JLabel>();

    // attributes
    m_ComboBoxAtts = new JComboBox(new DefaultComboBoxModel());
    m_ComboBoxAtts.setPreferredSize(new Dimension(300, 25));
    m_ComboBoxAtts.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        updateValues();
      }
    });
    label = new JLabel("Atttribute");
    label.setLabelFor(m_ComboBoxAtts);
    labels.add(label);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(label);
    panel.add(m_ComboBoxAtts);
    panelAll.add(panel);

    // values
    m_ComboBoxValues = new JComboBox(new DefaultComboBoxModel());
    m_ComboBoxValues.setPreferredSize(new Dimension(300, 25));
    label = new JLabel("Value");
    label.setLabelFor(m_ComboBoxValues);
    labels.add(label);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(label);
    panel.add(m_ComboBoxValues);
    panelAll.add(panel);

    // parser model
    m_TextModel = new JTextField(30);
    m_TextModel.setEditable(false);
    m_ButtonModel = new JButton("...");
    m_ButtonModel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        loadFile();
      }
    });
    label = new JLabel("Parser");
    label.setLabelFor(m_TextModel);
    labels.add(label);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(label);
    panel.add(m_TextModel);
    panel.add(m_ButtonModel);
    panelAll.add(panel);

    // additional parser options
    m_TextOptions = new JTextField(30);
    m_TextOptions.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        clear();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        clear();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        clear();
      }
    });
    label = new JLabel("Options");
    label.setLabelFor(m_TextOptions);
    labels.add(label);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(label);
    panel.add(m_TextOptions);
    panelAll.add(panel);

    // tokenizer
    m_PanelTokenizer = new PropertyPanel(m_EditorTokenizer);
    m_PanelTokenizer.setPreferredSize(new Dimension(400, 25));
    label = new JLabel("Tokenizer");
    label.setLabelFor(m_PanelTokenizer);
    labels.add(label);
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(label);
    panel.add(m_PanelTokenizer);
    panelAll.add(panel);

    // parse
    m_ButtonParse = new JButton("Start");
    m_ButtonParse.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        parse();
      }
    });
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_ButtonParse);
    panelAll.add(panel);

    // trees
    m_PanelTrees = new JTabbedPane();
    add(m_PanelTrees, BorderLayout.CENTER);

    // adjust labels
    width = 0;
    for (JLabel l: labels)
      width = Math.max(width, (int) l.getPreferredSize().getWidth());
    for (JLabel l: labels)
      l.setPreferredSize(new Dimension(width, (int) l.getPreferredSize().getHeight()));
  }

  /**
   * Brings up the filechooser to allow user to load a parser model file.
   */
  protected void loadFile() {
    int     retVal;

    retVal = m_FileChooserModel.showOpenDialog(this);
    if (retVal != JFileChooser.APPROVE_OPTION)
      return;

    loadFile(m_FileChooserModel.getSelectedFile());
  }

  /**
   * Attempts load the specified parser model file.
   *
   * @param file the file to load
   */
  protected void loadFile(File file) {
    m_FileModel = file;
    m_TextModel.setText(file.toString());
    clear();
  }

  /**
   * Updates the combobox with the attribute names.
   */
  protected void updateAttributes() {
    List<String>    names;
    int             i;

    names = new ArrayList<String>();
    if (m_Instances != null) {
      for (i = 0; i < m_Instances.numAttributes(); i++) {
        if (m_Instances.attribute(i).isString())
          names.add(m_Instances.attribute(i).name());
      }
    }
    m_ComboBoxAtts.setModel(new DefaultComboBoxModel(names.toArray(new String[names.size()])));
    if (names.size() > 0)
      m_ComboBoxAtts.setSelectedIndex(0);
  }

  /**
   * Updates the combobox with the string values.
   */
  protected void updateValues() {
    List<String>    values;
    int             index;
    int             i;

    values = new ArrayList<String>();
    if (m_ComboBoxAtts.getSelectedIndex() > -1) {
      index = m_Instances.attribute((String) m_ComboBoxAtts.getSelectedItem()).index();
      for (i = 0; i < m_Instances.numInstances(); i++) {
        if (m_Instances.instance(i).isMissing(index))
          continue;
        values.add(m_Instances.instance(i).stringValue(index));
      }
    }
    m_ComboBoxValues.setModel(new DefaultComboBoxModel(values.toArray(new String[values.size()])));
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

    try {
      preProcessor = new DocumentPreprocessor(new StringReader(doc));
      preProcessor.setTokenizerFactory(PartOfSpeechTagging.getTokenizerFactory());

      for (List sentence : preProcessor)
        result.add(StringUtils.joinWithOriginalWhiteSpace(sentence));
    }
    catch (Exception e) {
      showErrorMessage("Parsing error", "Failed to split document into sentences!", e);
    }

    return result;
  }

  /**
   * Perform the parsing the sentence, if possible
   *
   * @return the panel with the parse tree
   */
  protected JPanel parse(String sentence) {
    JPanel        result;
    JTextArea     text;
    JScrollPane   scroll;
    StanfordTree  stree;
    List<String>  words;
    Tree          tree;

    result = new JPanel(new BorderLayout());

    text = new JTextArea(5, 40);
    text.setLineWrap(true);
    text.setEditable(false);
    text.setFont(new Font("monospaced", Font.PLAIN, 12));
    text.setText(sentence);
    scroll = new JScrollPane(text);
    scroll.getHorizontalScrollBar().setBlockIncrement(50);
    scroll.getHorizontalScrollBar().setUnitIncrement(20);
    scroll.getVerticalScrollBar().setBlockIncrement(50);
    scroll.getVerticalScrollBar().setUnitIncrement(20);
    result.add(scroll, BorderLayout.NORTH);

    stree = new StanfordTree();
    scroll = new JScrollPane(stree);
    scroll.getHorizontalScrollBar().setBlockIncrement(50);
    scroll.getHorizontalScrollBar().setUnitIncrement(20);
    scroll.getVerticalScrollBar().setBlockIncrement(50);
    scroll.getVerticalScrollBar().setUnitIncrement(20);
    result.add(scroll, BorderLayout.CENTER);

    // perform parsing
    words = new ArrayList<String>();
    m_Tokenizer.tokenize(sentence);
    while (m_Tokenizer.hasMoreElements())
      words.add(m_Tokenizer.nextElement());
    // generate parse tree
    tree = m_Parser.apply(Sentence.toWordList(words.toArray(new String[words.size()])));
    stree.setTree(tree);
    stree.expandAll();

    return result;
  }

  /**
   * Perform the parsing, if possible.
   */
  protected void parse() {
    List<String>  sentences;
    JPanel        panel;
    int           i;

    if (m_ComboBoxValues.getSelectedIndex() == -1) {
      showErrorMessage("Input missing", "No string value selected for parsing!", null);
      return;
    }
    if (m_FileModel == null) {
      showErrorMessage("Parser", "No parser model file selected!", null);
      return;
    }
    if (!m_FileModel.exists() || m_FileModel.isDirectory()) {
      showErrorMessage("Parser", "Parser model file does not exist of is a directory!", null);
      return;
    }
    if (m_Parser == null) {
      try {
        m_Parser = edu.stanford.nlp.parser.lexparser.LexicalizedParser.loadModel(
          m_FileModel.getAbsolutePath(), Utils.splitOptions(m_TextOptions.getText()));
      }
      catch (Exception e) {
        showErrorMessage("Parser instantiation", "Failed to instantiate parser!", e);
        return;
      }
    }
    m_Tokenizer = (Tokenizer) m_EditorTokenizer.getValue();

    m_ButtonParse.setEnabled(false);
    m_PanelTrees.removeAll();
    sentences = getSentences((String) m_ComboBoxValues.getSelectedItem());
    for (i = 0; i < sentences.size(); i++) {
      try {
        panel = parse(sentences.get(i));
        m_PanelTrees.addTab("" + (i + 1), panel);
      }
      catch (Exception e) {
        showErrorMessage("Parsing error", "Failed to parse sentence #" + (i+1) + ": " + sentences.get(i), e);
        break;
      }
    }
    m_ButtonParse.setEnabled(true);
  }

  /**
   * Clears the display.
   */
  protected void clear() {
    m_PanelTrees.removeAll();
    m_Parser = null;
  }

  /**
   * Prints an error message for an exception in the console, the log and
   * as dialog.
   *
   * @param title the title for the dialog
   * @param msg the message (without exception)
   * @param e the exception, can be null
   */
  protected void showErrorMessage(String title, String msg, Exception e) {
    System.err.println(msg);
    if (e != null) {
      e.printStackTrace();
      msg = msg + "\n" + e;
    }
    m_Log.logMessage(msg);
    JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Sets the Logger to receive informational messages.
   *
   * @param newLog 	the Logger that will now get info messages
   */
  public void setLog(Logger newLog) {
    m_Log = newLog;
  }

  /**
   * Tells the panel to use a new set of instances.
   *
   * @param inst 	ignored
   */
  public void setInstances(Instances inst) {
    m_Instances = inst;
    clear();
    updateAttributes();
  }

  /**
   * Sets the Explorer to use as parent frame (used for sending notifications
   * about changes in the data).
   * 
   * @param parent	the parent frame
   */
  public void setExplorer(Explorer parent) {
    m_Explorer = parent;
  }
  
  /**
   * returns the parent Explorer frame.
   * 
   * @return		the parent
   */
  public Explorer getExplorer() {
    return m_Explorer;
  }
  
  /**
   * Returns the title for the tab in the Explorer.
   * 
   * @return 		the title of this tab
   */
  public String getTabTitle() {
    return "NLP Parse Trees";
  }
  
  /**
   * Returns the tooltip for the tab in the Explorer.
   * 
   * @return 		the tooltip of this tab
   */
  public String getTabTitleToolTip() {
    return "Allows generation of parse trees from string values in datasets.";
  }
}
