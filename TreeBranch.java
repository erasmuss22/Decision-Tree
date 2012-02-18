///////////////////////////////////////////////////////////////////////////////
//                   ALL STUDENTS COMPLETE THESE SECTIONS
// Main Class File:  BuildAndTestDecisionTree
// File:             TreeBranch.java
// Semester:         Fall 2011
//
// Author:           Erin Rasmussen    ejrasmussen2@wisc.edu
// CS Login:         rasmusse
// Lecturer's Name:  Shavlik
//////////////////////////// 80 columns wide //////////////////////////////////

/**
  * This class represents the binary options of each feature. Each TreeNode has
  * two branches, unless it is a leaf.
  *
  * <p>Bugs: none known
  *
  * @author Erin Rasmussen
  */

public class TreeBranch {
	private String option;
	
	public TreeBranch(){}
	
	public TreeBranch(String option){
		this.option = option;
	}
	
	public String getOption(){
		return this.option;
	}
	
	public void setOption(String option){
		this.option = option;
	}
	
}
