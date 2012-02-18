///////////////////////////////////////////////////////////////////////////////
//                   ALL STUDENTS COMPLETE THESE SECTIONS
// Title:            BuildAndTestDecisionTree
// Files:            BuildAndTestDecisionTree.java, TreeNode.java, TreeBranch.java
// Semester:         Fall 2011
//
// Author:           Erin Rasmussen     ejrasmussen2@wisc.edu
// CS Login:         rasmusse
// Lecturer's Name:  Shavlik

//////////////////////////// 80 columns wide //////////////////////////////////
import java.util.*;
import java.io.*;

////////////////////////////////////////////////////////////////////////////
//                                                                        //
// Code for HW1, Problem 2 - Inducing Decision Trees, CS540, Spring 2008. //
//                                                                        //
////////////////////////////////////////////////////////////////////////////

/* BuildAndTestDecisionTree.java 

   Copyright 2008, 2011 by Jude Shavlik.
   May be freely used for non-profit educational purposes.

   To run after compiling, type:

     java BuildAndTestDecisionTree <trainsetFilename> <testsetFilename>

   Eg,

     java BuildAndTestDecisionTree train-house-votes-1984.data test-house-votes-1984.data

   where <trainset> and <testset> are the input files of examples.

   Notes:  you may separate these classes into individual files if you
           wish.  We've put everything in one file for your
           convenience in getting started.

           All that is required is that you keep the name of the
           BuildAndTestDecisionTree class and don't change the calling
           convention for its main function.  There is no need to
           worry about "error detection" when reading data files.
           We'll be responsible for that.  HOWEVER, DO BE AWARE THAT
           WE WILL USE ONE OR MORE DIFFERENT DATASETS DURING TESTING, SO DON'T
           WRITE CODE THAT IS SPECIFIC TO THE "VOTES" DATASET.  (As
           stated above, you may assume that our additional datasets
           are properly formatted in the style used for the votes data.)

           A weakness of our design is that the category and feature
           names are defined in BOTH the train and test files.  These
           names MUST match, though this isn't checked.  However,
           we'll live with the weakness because it reduces simplicity
           overall (note: you can use the SAME filename for both the
           train and the test set, as a debugging method; you should
           get ALL the test examples correct in this case, since we are
	   not "pruning" decision trees to avoid overfitting the training data
	     - but be sure you understand Problem 1's method for pruning decision trees). */

public class BuildAndTestDecisionTree
{
	// "Main" reads in the names of the files we want to use, then reads in their examples.
	public static void main(String[] args)
	{   
		if (args.length != 2)
		{
			System.out.println("You must call BuildAndTestDecisionTree as follows:\n\n" +
			"  java BuildAndTestDecisionTree <trainsetFilename> <testsetFilename>");
		}    

		// Read in the file names.
		String trainset = args[0];
		String testset  = args[1];
		// Read in the examples from the files.
		ListOfExamples trainExamples = new ListOfExamples(trainset);
		ListOfExamples testExamples  = new ListOfExamples(testset);
		if (!trainExamples.ReadInExamplesFromFile(trainset) ||
				!testExamples.ReadInExamplesFromFile(testset))
		{
			System.out.println("\nSomething went wrong reading the datasets ... giving up.");
		}
		else
		{ // The following is included so you can see the data organization.
			// You'll need to REPLACE it with code that:
			//      
			//     1) uses the TRAINING SET of examples to build a decision tree
			//     
			//     2) prints out the induced decision tree (using simple, indented ASCII text)
			//
			//     3) categorizes the TESTING SET using the induced tree, reporting
			//        which examples were INCORRECTLY classified, as well as the
			//        FRACTION that were incorrectly classified.
			//        Just print out the NAMES of the examples incorrectly classified
			//        (though during debugging you might wish to print out the full
			//        example to see if it was processed correctly by your decision tree)       

			trainExamples.DescribeDataset();
			testExamples.DescribeDataset();
			trainExamples.PrintThisExample(0);  // Print out an example
			TreeNode root = buildTree(trainExamples);
			ValuePair[] results = testTree(root, testExamples);
			double count = 0;
			System.out.println();
			for (int i = 0; i < results.length; i++){
				if (!results[i].firstValue.equals(results[i].secondValue)){
					System.out.println(testExamples.elementAt(i).name);
					count++;
				}
			}
			System.out.println();
			double examples = testExamples.numberOfExamples;
			double accuracy = 100 * ((examples - count) / examples);
			System.out.println("Accuracy: " + accuracy + "%");
			printTree(0, root);
			//trainExamples.PrintAllExamples(); // Don't waste paper printing all of this out!
			//testExamples.PrintAllExamples();  // Instead, just look at it on the screen.
		}

		Utilities.waitHere("Hit <enter> when ready to exit.");
	}

	/**
	  * The testTree method takes the tree produced from the training set and
	  * tests the testSet against it. It stores the expected result and actual
	  * result of each example in a ValuePair array.
	  *
	  * @param (root) (the root node of the training set tree)
	  * @param (testSet) (the list of examples of the testing set)
	  * @return (a ValuePair array with the expected and actual result)
	  */
	
	public static ValuePair[] testTree(TreeNode root, ListOfExamples testSet){
		ValuePair[] results = new ValuePair[testSet.numberOfExamples];
		Example temp;
		TreeNode place;
		for (int i = 0; i < testSet.numberOfExamples; i++){
			temp = testSet.elementAt(i);
			place = root;
			while (place.getLeftChild() != null && place.getRightChild() != null){
				if (temp.elementAt(featureIndex(testSet, place.getFeature())).
						equals(place.getLeft())){
					place = place.getLeftChild();
				}
				else {
					place = place.getRightChild();
				}
			}
			ValuePair compare = new ValuePair(temp.category, place.getFeature());
			results[i] = compare;
		}
		return results;
	}

	/**
	  * This method uses an auxiliary method to build the decision tree using
	  * the ID3 algorithm
	  *
	  * @param (trainSet) (the list of training examples)
	  * @return (the root node of the tree)
	  */
	
	public static TreeNode buildTree(ListOfExamples trainSet){
		double totalEntropy = totalEntropy(trainSet, trainSet.numberOfExamples);
		double[] remainder = remainderOfFeature(trainSet, trainSet.numberOfExamples,
				new ArrayList<ValuePair>());
		double[] gain = gain(totalEntropy, remainder);
		int maxIndex = maxIndex(gain);
		List<ValuePair> path = new ArrayList<ValuePair>();
		List<ValuePair> path2 = new ArrayList<ValuePair>();
		ValuePair temp = new ValuePair(trainSet.featureNames[maxIndex],
				trainSet.featureValues[maxIndex].firstValue);
		path.add(temp);
		TreeNode root = new TreeNode(trainSet.featureNames[maxIndex],
				trainSet.featureValues[maxIndex].firstValue,
				trainSet.featureValues[maxIndex].secondValue);
		TreeNode left = new TreeNode();
		root.setLeftChild(left);
		left.setParent(root);
		ListOfExamples listCopy = listCopy(trainSet);
		ListOfExamples list = trimList(path, trainSet);
		buildTreeAux(list, root.getLeftChild(), path, trainSet);
		temp = new ValuePair(trainSet.featureNames[maxIndex],
				trainSet.featureValues[maxIndex].secondValue);
		path2.add(temp);
		TreeNode right = new TreeNode();
		root.setRightChild(right);
		right.setParent(root);
		list = trimList(path2, listCopy);
		buildTreeAux(list, root.getRightChild(), path2, trainSet);
		return root;
	}

	/**
	  * This is the recursive, auxiliary method to create the decision tree
	  * using the ID3 algorithm.
	  *
	  * @param (trainSet) (The shrinking list of examples that changes based on the path)
	  * @param (tree) (the current node to work on and pass its children)
	  * @param (path) (the features and binary values leading to this node)
	  * @param (wholeTrainSet) (the whole list of features to avoid Null Pointer)
	  */
	
	public static void buildTreeAux(ListOfExamples trainSet, TreeNode tree,
			List<ValuePair> path,
			ListOfExamples wholeTrainSet){
		if (trainSet.size() == 0){
			ValuePair inter = path.get(path.size() - 1);
			path.remove(path.size() - 1);
			String temp = majorityValue(trimList(path, wholeTrainSet), path);
			path.add(inter);
			tree.addCategory(temp);
			return;
		}
		else if(examplesEqual(trainSet, path)){
			tree.addCategory(trainSet.elementAt(0).category);
			return;
		}
		else if(path.size() == trainSet.numberOfFeatures){
			int count = 0;
			Example temp;
			Iterator<Example> iter = trainSet.iterator();
			String category;
			while(iter.hasNext()){
				temp = iter.next();
				if (temp.category.equals(trainSet.categories.firstValue)){
					count++;
				}
			}
			if (count > (trainSet.numberOfExamples - count)){
				category = trainSet.categories.firstValue;
			}
			else {
				category = trainSet.categories.secondValue;  //I chose secondValue as a tiebreaker
			}
			if (path.get(path.size() - 1).secondValue.equals(trainSet.featureValues[featureIndex(trainSet,
					path.get(path.size() - 1).firstValue)].firstValue)){
				tree.addCategory(category);
				return;
			}
			else {
				path.get(path.size() - 1).secondValue = tree.getRight();
				tree.addCategory(category);
				return;
			}
		}
		else {
			double[] remainder = remainderOfFeature(trainSet, trainSet.numberOfExamples, path);
			double [] gain = new double[trainSet.numberOfFeatures];
			double entropy = totalEntropy(trainSet, trainSet.numberOfExamples);
			for (int i = 0; i < gain.length; i++){
				if (!path.contains(trainSet.featureNames[i])){
					gain[i] = entropy - remainder[i];
				}
				else {
					gain[i] = 0;
				}
			}
			int maxIndex = 0;
			for (int i = 0; i < gain.length; i++){
				if (gain[i] > gain[maxIndex] && !path.contains(featureIndex(trainSet,
						trainSet.featureNames[i]))){
					maxIndex = i;
				}
			}
			ValuePair intermediate = new ValuePair(trainSet.featureNames[maxIndex], 
					trainSet.featureValues[maxIndex].secondValue);
			List<ValuePair> pathCopy = pathCopy(path);
			ListOfExamples listCopy = listCopy(trainSet);
			if (!path.contains(intermediate)){
				path.add(intermediate);
			}
			tree.addFeature(trainSet.featureNames[maxIndex], trainSet.featureValues[maxIndex].firstValue,
					trainSet.featureValues[maxIndex].secondValue);
			TreeNode right = new TreeNode();
			tree.setRightChild(right);
			right.setParent(tree);
			ListOfExamples list = trimList(path, trainSet);
			buildTreeAux(list, tree.getRightChild(), path, wholeTrainSet);
			intermediate = new ValuePair(trainSet.featureNames[maxIndex],
					trainSet.featureValues[maxIndex].firstValue);
			pathCopy.add(intermediate);
			TreeNode left = new TreeNode();
			tree.setLeftChild(left);
			left.setParent(tree);
			listCopy = trimList(pathCopy, listCopy);
			buildTreeAux(listCopy, tree.getLeftChild(), pathCopy, wholeTrainSet);
		}
		return;
	}

	/**
	  * This recursive method formats the printing of the finished decison tree
	  *
	  * @param (spacesToIndent) (the spaces to indent with each recursion)
	  * @param (node) (the node to print and pass its children)
	  */
	
	public static void printTree(int spacesToIndent, TreeNode node){
		if (node.getLeftChild() == null && node.getRightChild() == null){
			System.out.print(node.getFeature());
			System.out.println();
		}
		else {
			System.out.println();
			for (int i = 0; i < spacesToIndent; i++){
				System.out.print(" ");
			}
			System.out.print(node.getFeature() + " = " + node.getRight() + " : ");
			printTree(spacesToIndent + 3, node.getRightChild());
			for (int i = 0; i < spacesToIndent; i++){
				System.out.print(" ");
			}
			System.out.print(node.getFeature() + " = " + node.getLeft() + " : ");
			printTree(spacesToIndent + 3, node.getLeftChild());
		}
	}
	
	/**
	  * This copies the list of examples rather than making a reference to it
	  *
	  * @param (list) (the list to be copied)
	  * @return (a copy of the list)
	  */
	
	public static ListOfExamples listCopy(ListOfExamples list){
		ListOfExamples copy = new ListOfExamples(list);
		Iterator<Example> iter = list.iterator();
		while(iter.hasNext()){
			copy.add(iter.next());
		}
		return copy;
	}

	/**
	  * This creates a copy of the path and not a reference to it.
	  *
	  * @param (path) (the path to be copied)
	  * @return (a copy of the path)
	  */
	
	public static List<ValuePair> pathCopy(List<ValuePair> path){
		List<ValuePair> copy = new ArrayList<ValuePair>();
		for (int i = 0; i < path.size(); i++){
			copy.add(i, path.get(i));
		}
		return copy;
	}

	/**
	  * This method checks whether or not all the examples in a trimmed list
	  * are equal to check a base case.
	  *
	  * @param (trainSet) (the list of examples)
	  * @param (path) (the path leading to the current list)
	  * @return (a boolean value of if the values are equal)
	  */
	
	public static boolean examplesEqual(ListOfExamples trainSet, List<ValuePair> path){
		ListOfExamples list = trimList(path, trainSet);
		Example temp;
		int count = 0;
		Iterator<Example> iter = list.iterator();
		while(iter.hasNext()){
			temp = iter.next();
			if (temp.category.equals(trainSet.categories.firstValue)){
				count++;
			}
		}
		if (count == 0 || count == list.numberOfExamples){
			return true;
		}
		else {
			return false;
		}
	}

	/**
	  * This method returns the majority category of a list of examples
	  *
	  * @param (trainSet) (the list of examples trimmed from the path)
	  * @param (path) (the path leading to this node)
	  * @return (the majority category of the list)
	  */
	
	public static String majorityValue(ListOfExamples trainSet, List<ValuePair> path){
		ListOfExamples list = trimList(path, trainSet);
		int count = 0;
		Example temp;
		Iterator<Example> iter = list.iterator();
		while (iter.hasNext()){
			temp = iter.next();
			if (temp.category.equals(trainSet.categories.firstValue)){
				count++;
			}
		}
		if (count > (list.numberOfFeatures - count)){
			return trainSet.categories.firstValue;
		}
		else {
			return trainSet.categories.secondValue;  //I choose the secondValue to be the tiebreaker
		}
	}

	/**
	  * This method returns a trimmed down list based on examples that only
	  * have certain features and feature values.
	  *
	  * @param (path) (the path of features and feature values leading to this list)
	  * @param (trainSet) (the trainSet of examples before the latest feature was added to the path)
	  * @return (a trimmed list of examples)
	  */
	
	public static ListOfExamples trimList(List<ValuePair> path, ListOfExamples trainSet){
		ListOfExamples trim = trainSet;
		int count = 0;
		ListOfExamples trim2 = listCopy(trainSet);
		Iterator<Example> iter;
		Example temp;
		for (int i = 0; i < path.size(); i++){
			iter = trim.iterator();
			while(iter.hasNext()){
				temp = iter.next();
				if (!temp.elementAt(featureIndex(trainSet, path.get(i).firstValue))
						.equals(path.get(i).secondValue) && trim2.contains(temp)){
					trim2.remove(temp);
					count++;
				}
			}
		}
		trim2.numberOfExamples = trim2.numberOfExamples - count;
		return trim2;
	}

	/**
	  * This returns the index of a feature based on its name.
	  *
	  * @param (trainSet) (the list of examples)
	  * @param (feature) (the feature to find its index)
	  * @return (the int index of the feature)
	  */
	
	public static int featureIndex(ListOfExamples trainSet, String feature){
		for (int i = 0; i < trainSet.numberOfFeatures; i++){
			if (feature.equals(trainSet.featureNames[i])){
				return i;
			}
		}
		return 0;
	}

	/**
	  * This returns the total entropy of the full list to start the ID3 algorithm
	  *
	  * @param (trainSet) (the list of training examples)
	  * @param (numberOfExamples) (the amount of examples in the list)
	  * @return (the entropy of the entire list)
	  */
	
	public static double totalEntropy(ListOfExamples trainSet, int numberOfExamples){
		Iterator<Example> iter = trainSet.iterator();
		Example temp;
		double count = 0;
		while (iter.hasNext()){
			temp = iter.next();
			if (temp.category.equals(trainSet.categories.firstValue)){
				count++;
			}
		}
		double fraction = count / numberOfExamples;
		if (fraction == 0 || fraction == 1){
			return 0;
		}
		else {
			return -((fraction * (logBase2(fraction))) + (((1 - fraction) * 
					((logBase2(1 - fraction))))));
		}
	}

	/**
	  * This returns the entropy of a feature based on the features and feature values
	  * in the path.
	  *
	  * @param (trainSet) (the trimmed list of training examples)
	  * @param (numberOfExamples) (the amount of examples in the list)
	  * @return (an array of each features entropy)
	  */
	
	public static double[] featureEntropy(ListOfExamples trainSet, int numberOfExamples){
		Iterator<Example> iter;
		double[] entropy = new double[trainSet.numberOfFeatures];
		Example temp;
		double countPos, countValue1, countPosAndVal1, countPosAndVal2;
		double fractionEnt1, fractionEnt2;
		for (int i = 0; i < trainSet.numberOfFeatures; i++){
			countPos = 0;
			countValue1 = 0;
			countPosAndVal1 = 0;
			countPosAndVal2 = 0;
			iter = trainSet.iterator();
			while (iter.hasNext()){
				temp = iter.next();
				if (temp.elementAt(i).equals(trainSet.featureValues[i].firstValue)){
					countValue1++;
				}
				if (temp.category.equals(trainSet.categories.firstValue)){
					countPos++;
				}
				if (temp.elementAt(i).equals(trainSet.featureValues[i].firstValue)
						&& temp.category.equals(trainSet.categories.firstValue)){
					countPosAndVal1++;
				}
				if (temp.elementAt(i).equals(trainSet.featureValues[i].secondValue)
						&& temp.category.equals(trainSet.categories.firstValue)){
					countPosAndVal2++;
				}
			}
			fractionEnt1 = countPosAndVal1 / countValue1;
			fractionEnt2 = countPosAndVal2 / (numberOfExamples - countValue1);
			if (fractionEnt1 == 0 || fractionEnt1 == 1 || fractionEnt2 == 0 ||
					fractionEnt2 ==1){
				entropy[i] = 0;
			}
			else {
				entropy[i] = (((fractionEnt1 * logBase2(fractionEnt1)) + 
						((1 - fractionEnt1) * logBase2(1 - fractionEnt1)))) - 
						((fractionEnt2 * logBase2(fractionEnt2)) + 
								((1 - fractionEnt2) * logBase2(1 - fractionEnt2)));
			}
		}
		return entropy;
	}

	/**
	  * This method calculates the remainder of each feature.
	  *
	  * @param (trainSet) (the list of examples)
	  * @param (numberOfExamples) (the amount of examples in the list)
	  * @parma (path) (the path of features and feature values to this point)
	  * @return (the remainder of each feature)
	  */
	
	public static double[] remainderOfFeature(ListOfExamples trainSet, 
			int numberOfExamples, List<ValuePair> path){
		Iterator<Example> iter = trainSet.iterator();
		double[] remainder = new double[trainSet.numberOfFeatures];
		Example temp;
		boolean contains;
		double countPos, countValue1, countPosAndVal1, countPosAndVal2;
		double fraction, fractionEnt1, fractionEnt2;
		for (int i = 0; i < trainSet.numberOfFeatures; i++){
			contains = false;
			countPos = 0;
			countValue1 = 0;
			countPosAndVal1 = 0;
			countPosAndVal2 = 0;
			iter = trainSet.iterator();
			while (iter.hasNext()){
				temp = iter.next();
				if (temp.elementAt(i).equals(trainSet.featureValues[i].firstValue)){
					countValue1++;
				}
				if (temp.category.equals(trainSet.categories.firstValue)){
					countPos++;
				}
				if (temp.elementAt(i).equals(trainSet.featureValues[i].firstValue)
						&& temp.category.equals(trainSet.categories.firstValue)){
					countPosAndVal1++;
				}
				if (temp.elementAt(i).equals(trainSet.featureValues[i].secondValue) 
						&& temp.category.equals(trainSet.categories.firstValue)){
					countPosAndVal2++;
				}
			}
			fraction = countValue1 / numberOfExamples;
			if (countValue1 == 0){
				fractionEnt1 = 0;
			}
			else {
				fractionEnt1 = countPosAndVal1 / countValue1;
			}
			if ((numberOfExamples - countValue1) == 0) {
				fractionEnt2 = 0;
			}
			else {
				fractionEnt2 = countPosAndVal2 / (numberOfExamples - countValue1);
			}
			if ((fractionEnt1 == 0 || fractionEnt1 == 1)
					&& (fractionEnt2 == 0 || fractionEnt2 == 1)){
				for (int j = 0; j < path.size(); j++){
					if (path.get(j).firstValue.equals(trainSet.featureNames[i])){
						contains = true;
					}
				}
				if (!contains){
					remainder[i] = 0;
				}
				else {
					remainder[i] = 100;
				}
			}
			else if (fractionEnt1 == 0 || fractionEnt1 == 1){
				for (int j = 0; j < path.size(); j++){
					if (path.get(j).firstValue.equals(trainSet.featureNames[i])){
						contains = true;
					}
				}
				if (!contains){
					remainder[i] = -(-((1 - fraction) * ((fractionEnt2 * logBase2(fractionEnt2)) + 
							((1 - fractionEnt2) * logBase2(1 - fractionEnt2)))));
				}
				else {
					remainder[i] = 100;
				}
			}
			else if (fractionEnt2 == 0 || fractionEnt2 == 1){
				for (int j = 0; j < path.size(); j++){
					if (path.get(j).firstValue.equals(trainSet.featureNames[i])){
						contains = true;
					}
				}
				if (!contains){
					remainder[i] = -((fraction * ((fractionEnt1 * logBase2(fractionEnt1)) + 
							((1 - fractionEnt1) * logBase2(1 - fractionEnt1)))));
				}
				else {
					remainder[i] = 100;
				}
			}
			else {
				for (int j = 0; j < path.size(); j++){
					if (path.get(j).firstValue.equals(trainSet.featureNames[i])){
						contains = true;
					}
				}
				if (!contains){
					remainder[i] = -((fraction * ((fractionEnt1 * logBase2(fractionEnt1)) + 
							((1 - fractionEnt1) * logBase2(1 - fractionEnt1)))) + 
							((1 - fraction) * ((fractionEnt2 * logBase2(fractionEnt2)) + 
									((1 - fractionEnt2) * logBase2(1 - fractionEnt2)))));
				}
				else {
					remainder[i] = 100;
				}
			}
		}
		return remainder;
	}

	/**
	  * This method performs the log base 2 operation to save on typing
	  *
	  * @param (number) (the number to take the log base 2 of)
	  * @return (the log base 2 of the number)
	  */
	
	public static double logBase2(double number){
		return Math.log(number) / Math.log(2);
	}

	/**
	  * This method returns the gain of each feature.
	  *
	  * @param (totalEntropy) (the total entropy of the feature being checked)
	  * @param (remainder) (the array of remainders of each value)
	  * @return (the gain of each feature)
	  */
	
	public static double[] gain(double totalEntropy, double[] remainder){
		double[] gain = new double[remainder.length];
		for (int i = 0; i < remainder.length; i++){
			gain[i] = totalEntropy - remainder[i]; 
		}
		return gain;
	}

	/**
	  * This method returns the highest gaining feature's index.
	  *
	  * @param (gain) (the array of feature gains)
	  * @return (the index of the feature with the highest index)
	  */
	
	public static int maxIndex(double[] gain){
		int max = 0;
		for (int i = 0; i < gain.length; i++){
			if (gain[i] > gain[max]){
				max = i;
			}
		}
		return max;
	}

}

@SuppressWarnings("serial")
// This Class, an extension of Vector, holds an individual example.
// The new method PrintFeatures() can be used to
// display the contents of the example. 
class Example extends Vector<String>
{
	public String name, category;  // The name and the category of this example.
	// The items in the vector are the feature values.
	public ListOfExamples parent;  // The data set in which this is one example.  

	// The instance constructor.
	public Example(int numberOfFeatures)
	{
		super(numberOfFeatures);
	}

	// Print out this example in human-readable form.
	public void PrintFeatures()
	{
		System.out.print("Example " + name + ",  category = " + category + "\n");
		for (int i = 0; i < parent.numberOfFeatures; i++)
		{
			System.out.print("     " + parent.featureNames[i]
			                                               + " = " +  elementAt(i) + "\n");
		}
	}    
}

// A simple class that holds a pair of strings
// Since each feature is Boolean, we use a ValuePair to store both of its possible values
class ValuePair {
	public String firstValue;
	public String secondValue;

	public ValuePair(String first, String second) {
		firstValue = first;
		secondValue = second;
	}
}

// This Class holds all of our examples  from one dataset
// (train OR test, not BOTH).  It extends the Vector class.
// Be sure you're not confused.  We're using TWO vectors.  An Example
// is a vector of feature values, while a ListOfExamples is a vector of examples.
// Also, there is one ListOfExamples for the TRAINING SET and one for the TESTING SET.
@SuppressWarnings("serial")
class ListOfExamples extends Vector<Example>
{
	public String[] featureNames; // The names of the features used to describe examples.
	public ValuePair[] featureValues; // A list of the possible values for each of the features.
	public ValuePair categories; // The names of the two categories.
	public String   nameOfDataset; // Assign a name, for use in printing info.
	public int      numberOfFeatures; // For future use in classification it is helpful
	// to know how many features we have.
	public int      numberOfExamples; // Number of examples in this dataset.

	// The instance constructor.
	public ListOfExamples(String name) 
	{
		nameOfDataset = name; // Hold on to the name of the data set.
	}

	//Copy constructor
	public ListOfExamples(ListOfExamples list){
		this.featureNames = list.featureNames;
		this.featureValues = list.featureValues;
		this.categories = list.categories;
		this.nameOfDataset = list.nameOfDataset;
		this.numberOfFeatures = list.numberOfFeatures;
		this.numberOfExamples = list.numberOfExamples;
	}

	public void DescribeDataset()
	{
		System.out.println("Dataset " + nameOfDataset + " contains "
				+ numberOfExamples + " examples, each with "
				+ numberOfFeatures + " features.");
		System.out.println("Valid category labels: "
				+ categories.firstValue + " and "
				+ categories.secondValue);
		System.out.println("The feature names (with their possible values) are:");
		for (int i = 0; i < numberOfFeatures; i++)
		{
			System.out.println("   " + featureNames[i] +
					" (" + featureValues[i].firstValue +
					" or " + featureValues[i].secondValue + ")");
		}
	}

	// Print out ALL the examples.
	public void PrintAllExamples()
	{
		System.out.println("List of Examples");
		for (int i = 0; i < size(); i++)
		{
			Example thisExample = (Example)elementAt(i);  

			thisExample.PrintFeatures();
		}
	}

	// Print out the SPECIFIED example.
	public void PrintThisExample(int i)
	{
		Example thisExample = (Example)elementAt(i); 

		thisExample.PrintFeatures();
	}

	// Read this example file from disk.
	// You needn't understand this method.  We're taking
	// responsibility of getting data out of a file and
	// into the ListOfExamples and Example instances.
	public boolean ReadInExamplesFromFile(String filename)
	{        
		try 
		{
			FileReader inputFile = null;

			try
			{
				inputFile = new FileReader(filename);
			}
			catch (IOException ioe)
			{
				System.out.println("Error opening file: (" + ioe + ")");
				return false;
			}

			StreamTokenizer fileTokens = new StreamTokenizer(inputFile);
			fileTokens.lowerCaseMode(true); // Ignore case.
			fileTokens.commentChar('/');    // Everything on a line after '/' is ignored.

			// Read in the names of the two possible categories
			String firstValue = readNextWord(fileTokens);
			String secondValue = readNextWord(fileTokens);
			categories = new ValuePair(firstValue, secondValue);
			numberOfFeatures = readNextInteger(fileTokens);

			// Build a vector of all the feature names.
			featureNames = new String[numberOfFeatures];
			featureValues = new ValuePair[numberOfFeatures];
			for (int i = 0; i < numberOfFeatures; i++)
			{
				featureNames[i] = readNextWord(fileTokens);
				firstValue = readNextWord(fileTokens);
				secondValue = readNextWord(fileTokens);
				featureValues[i] = new ValuePair(firstValue, secondValue);
			}

			// Read in the examples.
			numberOfExamples = readNextInteger(fileTokens);
			for (int i = 0; i < numberOfExamples; i++)
			{
				Example example = new Example(numberOfFeatures);

				// Create and fill in an example instance.
				example.parent   = this; // Provide a "back" pointer.
				example.name     = readNextWord(fileTokens);
				example.category = readNextWord(fileTokens);
				for (int j = 0; j < numberOfFeatures; j++)
				{ String featureValue = readNextWord(fileTokens);           
				// You may assume that the only feature values ever used
				// are the ones specified in the header of the file
				if (featureValue == null ||
						(!featureValue.equalsIgnoreCase(featureValues[j].firstValue) &&
								!featureValue.equalsIgnoreCase(featureValues[j].secondValue)))
				{
					System.out.println("Read " + featureValue + " from " + filename
							+ " when expecting \"" + featureValues[j].firstValue + 
							"\" or \"" + featureValues[j].secondValue + "\"." 
							+ "  Ex #" + i + ", feature #" + j);
					return false;
				}
				example.addElement(featureValue);
				}

				addElement(example); // Add to the list of examples.
			}
			return true;  // Indicate success

		}
		// Don't try to do all kinds of fancy error dectection and correction.
		catch (Exception e)
		{
			System.out.println("Error in ReadInExamples - check "
					+ filename + "\n   msg=" + e);
			return false;
		}
	}

	// Read the next word in this stream.
	private String readNextWord(StreamTokenizer st)
	{
		try
		{
			switch (st.nextToken())
			{
			case StreamTokenizer.TT_WORD:
				return st.sval;

			default:
				System.out.println("Expecting a string in readNextWord().");
				return null;
			}
		}
		catch (IOException ioe)
		{
			System.out.println("I/O Exception? " + ioe);
			return null;
		}
	}  

	// Read the next number in this stream.
	private int readNextInteger(StreamTokenizer st)
	{
		try
		{
			switch (st.nextToken())
			{          
			case StreamTokenizer.TT_NUMBER:
				return (int)st.nval;

			default:
				System.out.println("Expecting an integer in readNextInteger().");
				return -1;
			}
		}
		catch (IOException ioe)
		{
			System.out.println("I/O Exception? " + ioe);
			return -1;
		}
	}
}

class Utilities
{
	// This method can be used to wait until you're ready to proceed.
	public static void waitHere(String msg)
	{
		System.out.println("");
		System.out.print(msg);
		try { System.in.read(); }
		catch(Exception e) {} // Ignore any errors while reading.
	}
}
