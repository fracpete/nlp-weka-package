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
 * StanfordTree.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.explorer.tree;

import edu.stanford.nlp.trees.Tree;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Specialized tree for displaying Stanford parse trees.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8629 $
 */
public class StanfordTree
  extends JTree {

  /** for serialization. */
  private static final long serialVersionUID = -3618290386432060103L;
  
  /** the underlying Stanford tree. */
  protected Tree m_Tree;

  /**
   * Initializes the tree.
   */
  public StanfordTree() {
    initialize();
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_Tree = null;
    
    getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

    setCellRenderer(new StanfordRenderer());
    
    buildTree();
  }
  
  /**
   * Builds the tree.
   * 
   * @param parentNode	the current parent, null for root
   * @param parentTree		the tree to attach
   * @return		the generated node
   */
  protected StanfordNode buildTree(StanfordNode parentNode, Tree parentTree) {
    StanfordNode	result;
    StanfordNode	childNode;
    Tree		childTree;
    int			i;
    
    if (parentNode == null) {
      parentNode = new StanfordNode(StanfordNode.ROOT, parentTree);
      buildTree(parentNode, parentTree);
    }
    else {
      for (i = 0; i < parentTree.children().length; i++) {
	childTree = parentTree.children()[i];
	childNode = new StanfordNode(childTree.label().value(), childTree);
	parentNode.add(childNode);
	buildTree(childNode, childTree);
      }
    }

    result = parentNode;
    
    return result;
  }
  
  /**
   * Builds the tree from the current JSON object.
   */
  protected void buildTree() {
    StanfordNode	root;
    
    if (m_Tree == null)
      root = new StanfordNode("empty", null);
    else
      root = buildTree(null, m_Tree);
    
    setModel(new DefaultTreeModel(root));
    expand(root);
  }
  
  /**
   * Sets the Stanford parse tree object to display.
   * 
   * @param value	the tree to display
   */
  public void setTree(Tree value) {
    m_Tree = value;
    buildTree();
  }
  
  /**
   * Returns the Stanford parse tree on display.
   * 
   * @return		the tree, null if none displayed
   */
  public Tree getTree() {
    return m_Tree;
  }

  /**
   * Expands the specified node.
   *
   * @param node	the node to expand
   */
  public void expand(DefaultMutableTreeNode node) {
    expandPath(new TreePath(node.getPath()));
  }

  /**
   * Expands all nodes in the tree.
   */
  public void expandAll() {
    TreeNode root = (TreeNode) getModel().getRoot();
    if (root != null)
      toggleAll(new TreePath(root), true);
  }

  /**
   * Expands the sub-tree below the specified node.
   *
   * @param node	the node to expand
   */
  public void expandAll(DefaultMutableTreeNode node) {
    expandAll(new TreePath(node.getPath()));
  }

  /**
   * Expands the sub-tree below the specified node.
   *
   * @param path	the path to the sub-tree
   */
  public void expandAll(TreePath path) {
    toggleAll(path, true);
  }

  /**
   * Collapses the specified node.
   *
   * @param node	the node to collapse
   */
  public void collapse(DefaultMutableTreeNode node) {
    collapsePath(new TreePath(node.getPath()));
  }

  /**
   * Collapses all nodes in the tree.
   */
  public void collapseAll() {
    TreeNode root = (TreeNode) getModel().getRoot();
    if (root != null)
      toggleAll(new TreePath(root), false);
  }

  /**
   * Collapses the sub-tree below the specified node.
   *
   * @param node	the node to collapse
   */
  public void collapseAll(DefaultMutableTreeNode node) {
    collapseAll(new TreePath(node.getPath()));
  }

  /**
   * Collapses the sub-tree below the specified node.
   *
   * @param path	the path to the sub-tree
   */
  public void collapseAll(TreePath path) {
    toggleAll(path, false);
  }

  /**
   * Performs the expand/collapse recursively.
   *
   * @param parent	the parent path
   * @param expand	whether to expand or collapse
   */
  protected void toggleAll(TreePath parent, boolean expand) {
    TreeNode 	node;
    int		i;
    TreeNode	child;

    node = (TreeNode) parent.getLastPathComponent();
    for (i = 0; i < node.getChildCount(); i++) {
      child = node.getChildAt(i);
      toggleAll(parent.pathByAddingChild(child), expand);
    }

    if (expand)
      expandPath(parent);
    else
      collapsePath(parent);
  }
}
