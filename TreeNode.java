///////////////////////////////////////////////////////////////////////////////
//                   ALL STUDENTS COMPLETE THESE SECTIONS
// Main Class File:  BuildAndTestDecisionTree
// File:             TreeNode.java
// Semester:         Fall 2011
//
// Author:           Erin Rasmussen    ejrasmussen2@wisc.edu
// CS Login:         rasmusse
// Lecturer's Name:  Shavlik
//////////////////////////// 80 columns wide //////////////////////////////////
import java.util.*;

/**
 * This class represents the features of the decision tree, or the category
 * if one is calculated.
 *
 * <p>Bugs: none known
 *
 * @author Erin Rasmussen
 */

public class TreeNode {
	private TreeNode leftChild;
	private TreeNode rightChild;
	private String feature;
	private TreeBranch left;
	private TreeBranch right;
	private List<ValuePair> path;
	private TreeNode parent;
	
	public TreeNode(String feature, String option1, String option2) {
		/*this.path = path;
		this.path.add(new ValuePair(feature, null));*/
		this.feature = feature;
		left = new TreeBranch();
		left.setOption(option1);
		right = new TreeBranch();
		right.setOption(option2);
		leftChild = null;
		rightChild = null;
		parent = null;
	}
	
	public TreeNode(){
		this.feature = null;
		left = new TreeBranch();
		right = new TreeBranch();
		leftChild = null;
		rightChild = null;
		parent = null;
	}
	
	public void setParent(TreeNode parent){
		this.parent = parent;
	}
	
	public TreeNode getParent(){
		return parent;
	}
	
	public void addFeature(String feature, String option1, String option2){
		this.feature = feature;
		this.left.setOption(option1);
		this.right.setOption(option2);
	}
	
	public void setLeftChild(String feature, String option1, String option2,
			List<ValuePair> path){
		leftChild = new TreeNode(feature, option1, option2);
		ValuePair temp = new ValuePair(feature, null);
		path.add(temp);
	}
	
	public void setRightChild(TreeNode right){
		this.rightChild = right;
	}
	
	public void setLeftChild(TreeNode left){
		this.leftChild = left;
	}
	
	public void setRightChild(String feature, String option1, String option2,
			List<ValuePair> path){
		rightChild = new TreeNode(feature, option1, option2);
		ValuePair temp = new ValuePair(feature, null);
		path.add(temp);
	}
	
	public List<ValuePair> getPath(){
		return path;
	}
	
	public void addCategory(String category){
		this.feature = category;
	}
	
	public String getFeature(){
		return feature;
	}
	
	public String getLeft(){
		return this.left.getOption();
	}
	
	public String getRight(){
		return this.right.getOption();
	}
	
	public TreeNode getLeftChild(){
		return this.leftChild;
	}
	
	public TreeNode getRightChild(){
		return this.rightChild;
	}
	
}
