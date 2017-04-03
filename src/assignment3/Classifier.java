package assignment3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.io.PrintStream;

public class Classifier {
		
	public Classifier(){
		
	}
	
	// The input child array lists the number of children of each node, used to construct the tree
	// The input probabilites array lists the probability of each node having a value of 0, starting with the head node
	public ArrayList<DepTreeNode> generateTree(int[] children, double[] probabilitiesGiven0, double[] probabilitiesGiven1, double initialProbability){
		
		if(children.length != probabilitiesGiven0.length + 1){
			System.out.println("Illegal input size to generate tree.  Must have one probability per node and one child value per node except the head.");
			return null;
		}
		
		ArrayList<DepTreeNode> nodes = new ArrayList<DepTreeNode>();
		
		DepTreeNode root = new DepTreeNode(initialProbability);
		nodes.add(root);
		
		
		
		for(int i = 0; i < children.length; i++){
			
			// iterate down the tree, in order
			//System.out.println("child "+i+"size"+nodes.size());
			
			for(int j = 0; j < children[i]; j++){
				//System.out.println("j "+j);
				// create new children nodes
				//System.out.println("1"+probabilitiesGiven1[nodes.size()-1]);
				DepTreeNode node = new DepTreeNode(nodes.get(i), probabilitiesGiven0[nodes.size()-1], probabilitiesGiven1[nodes.size()-1], nodes.size());
				nodes.get(i).addChild(node);
				nodes.add(node);
			}
			
		}
		
		return nodes;
		
	}
	
	public Sample generateSample(ArrayList<DepTreeNode> dependenceTree){
				
		Boolean[] features = new Boolean[dependenceTree.size()];
		
		for(DepTreeNode node: dependenceTree){
			
			double randomNumber = Math.random();
			
			if(node.getParent() != null){ // all nodes but the root node
				
				if(features[node.getParent().getNodeNumber()]){ // the parent feature is 1
					if(randomNumber >= node.getProbability0Given1()){ 
						features[node.getNodeNumber()] = true;
					} else {
						features[node.getNodeNumber()] = false;
					}
					
				} else{
					if(randomNumber >= node.getProbability0Given0()){ // the parent feature is 0
						features[node.getNodeNumber()] = true;
					} else {
						features[node.getNodeNumber()] = false;
					}
				}
				
			} else { // root node: only one probability case
				
				if(randomNumber >= node.getProbability0Given0()){
					features[node.getNodeNumber()] = true;
				} else {
					features[node.getNodeNumber()] = false;
				}
			}
		}
		
		return new Sample(features);
	}
	
	public ArrayList<Sample> generateSamples(int samples, ArrayList<DepTreeNode> dependenceTree){
		
		ArrayList<Sample> sampleClass = new ArrayList<Sample>();
		
		for(int i=0; i<samples; i++){
			sampleClass.add(generateSample(dependenceTree));
		}
		
		return sampleClass;
	}
	
	private double[] calculateMean(List<Sample> samples){
		
		if(samples.size() == 0){
			System.out.println("No samples were given to the mean calculation algorithm.");
			return null;
		}
		
		int dimensions = samples.get(0).getFeatures().length;
		double[] output = new double[dimensions];
		
		// for each of the dimensions
		for(int i=0; i<dimensions; i++){
			
			// only need to store the number of 1 samples; all samples that are not 1 are 0.
			double samples1 = 0;
			
			// for each of the samples
			for(Sample sample: samples){
				if(sample.getFeature(i)){ // if the feature is false, ie. 0
					samples1++;
				}
			}
			
			output[i] = samples1 / (double) samples.size();
		}
				
		return output;
	}
	
	private TwoDoubleArrays calculateDependentMean(ArrayList<DepTreeNode> dependenceTree){
		
		double[] output1 = new double[dependenceTree.size()];
		double[] output2 = new double[dependenceTree.size()];
		
		for(DepTreeNode node: dependenceTree){
			
			output1[node.getNodeNumber()] = 1-node.getProbability0Given0();
			output2[node.getNodeNumber()] = 1-node.getProbability0Given1();
		}
		/*System.out.println("Means");
		System.out.println(Arrays.toString(output1));
		System.out.println(Arrays.toString(output2));
		*/
		return new TwoDoubleArrays(output1, output2);
	}
	
	private double[] calculateStandardDeviation(List<Sample> samples, double[] mean){
		
		if(samples.size() == 0){
			System.out.println("No samples were given to the standard deviation calculation algorithm.");
			return null;
		}
		
		int dimensions = samples.get(0).getFeatures().length;
		double[] output = new double[dimensions];
		
		for(int i=0; i<dimensions; i++){
			
			double sumOfSquares = 0;
			
			// for each sample, add the difference between its value and the mean squared to the running total
			for(Sample sample: samples){
				
				double featureValue = 0;
				
				if(sample.getFeature(i)){ // if the feature is true, its value is 1.
					featureValue = 1;
				}
				
				sumOfSquares += Math.pow(featureValue - mean[i], 2);
			}
			
			// the standard deviation for that feature is the square root of the variance and the variance is the average of the sum of squares.
			output[i] = Math.pow(sumOfSquares / samples.size(), 0.5); 
		}
		
		return output;
	}
	
	private TwoDoubleArrays calculateDependentStandardDeviation(List<Sample> samples, ArrayList<DepTreeNode> dependenceTree){
		
		double[] output1 = new double[dependenceTree.size()];
		double[] output2 = new double[dependenceTree.size()];
		
		// standard deviation of the initial node is independent; use the calculated probability for the 0th node as the mean
		double sumOfSquares = 0;
		
		// for each sample, add the difference between its value and the mean squared to the running total
		for(Sample sample: samples){
			
			double featureValue = 0;
			
			if(sample.getFeature(0)){ // if the feature is true, its value is 1.
				featureValue = 1;
			}
			
			sumOfSquares += Math.pow(featureValue - dependenceTree.get(0).getProbability0Given0(), 2);
		}
		
		// the standard deviation for that feature is the square root of the variance and the variance is the average of the sum of squares.
		output1[0] = Math.pow(sumOfSquares / samples.size(), 0.5); 
		output2[0] = output1[0];
		
		
		// now the rest of the nodes have dependent standard deviations
		for(int i=1; i<dependenceTree.size(); i++){
			
			double sumOfSquaresGiven0 = 0;
			double sumOfSquaresGiven1 = 0;
			double samplesGiven0 = 0;
			double samplesGiven1 = 0;
			
			// for each sample, add the difference between its value and the mean squared to the running total
			for(Sample sample: samples){
				
				double featureValue = 0;
				if(sample.getFeature(i)){ // if the feature is true, its value is 1.
					featureValue = 1;
				}
				
				double mean;
				
				// check the status of the feature that the feature under investigation is depending on 
				if(!sample.getFeature(dependenceTree.get(i).getParent().getNodeNumber())){ // parent feature is 0
					mean = 1-dependenceTree.get(i).getProbability0Given0();
					sumOfSquaresGiven0 += Math.pow(featureValue - mean, 2);
					samplesGiven0++;
				} else {
					mean = 1-dependenceTree.get(i).getProbability0Given1();
					sumOfSquaresGiven1 += Math.pow(featureValue - mean, 2);
					samplesGiven1++;
				}
			}
			
			// the standard deviation for that feature is the square root of the variance and the variance is the average of the sum of squares.
			if(new Double(samplesGiven0).compareTo(0.0) != 0){
				output1[dependenceTree.get(i).getNodeNumber()] = Math.pow(sumOfSquaresGiven0 / samplesGiven0, 0.5);
			} else {
				output1[dependenceTree.get(i).getNodeNumber()] = 0;
			}
			if(new Double(samplesGiven1).compareTo(0.0) != 0){
				output2[dependenceTree.get(i).getNodeNumber()] = Math.pow(sumOfSquaresGiven1 / samplesGiven1, 0.5);
			} else {
				output2[dependenceTree.get(i).getNodeNumber()] = 0;
			}
			//output2[dependenceTree.get(i).getNodeNumber()] = Math.pow(sumOfSquaresGiven1 / samplesGiven1, 0.5);
			
		}
		/*System.out.println("SDs");
		System.out.println(Arrays.toString(output1));
		System.out.println(Arrays.toString(output2));
		*/
		return new TwoDoubleArrays(output1, output2);
	}
	
	private double calculateClassProbability(Boolean value, double mean, double standardDeviation){
		
		double v = 0;
		if(value){
			v = 1;
		}
		
		double coefficient = 1/Math.pow((2 * Math.PI * Math.pow(standardDeviation, 2)),0.5);
		double exponential = Math.exp(-1*Math.pow(v - mean, 2) / (2 * Math.pow(standardDeviation, 2)));
		
		return coefficient * exponential;
	}
	
	// return true if the sample is a member of the first class and false if it is a member of the second class.  Assumes equal probability for both classes.
	private Boolean classifySample(Sample sample, double[] means1, double[] standardDeviations1, double[] means2, double[] standardDeviations2, double probabilityInClass1){
		
		double posterior1 = 1; // operations will be multiplicative so initialize to 1
		double posterior2 = 1;
		
		for(int i = 0; i<sample.getFeatures().length; i++){
			if(new Double(standardDeviations1[i]).compareTo(0.0) != 0){
				double term = calculateClassProbability(sample.getFeature(i), means1[i], standardDeviations1[i]) * probabilityInClass1;
				posterior1 *= term; 
				//System.out.println("SD: "+standardDeviations1[i]+"|"+means1[i]+"|"+term);
				
			}
			if(new Double(standardDeviations2[i]).compareTo(0.0) != 0){
				double term = calculateClassProbability(sample.getFeature(i), means2[i], standardDeviations2[i]) * (1-probabilityInClass1);
				posterior2 *= term;
				//System.out.println("SD: "+standardDeviations2[i]+"|"+means2[i]+"|"+term);
				
			}
		}
		
		//System.out.println("|"+posterior1+"|"+posterior2);
		if(posterior1 < posterior2){
			return false;
		} else {
			return true;
		}
	}
	
	// return true if the sample is a member of the first class and false if it is a member of the second class.  Assumes equal probability for both classes.
	private Boolean classifyDependentSample(Sample sample, TwoDoubleArrays means1, TwoDoubleArrays standardDeviations1, 
			TwoDoubleArrays means2, TwoDoubleArrays standardDeviations2, ArrayList<DepTreeNode> dependenceTree1, 
			ArrayList<DepTreeNode> dependenceTree2){
		
		double posterior1 = 1; // operations will be multiplicative so initialize to 1
		double posterior2 = 1;
		
		// evaluate the root node as an independent node
		if(new Double(standardDeviations1.getArray1()[0]).compareTo(0.0) != 0 && new Double(means1.getArray1()[0]).compareTo(0.0) != 0){
			posterior1 *= calculateClassProbability(sample.getFeature(0), means1.getArray1()[0], standardDeviations1.getArray1()[0]);
		}
		
		if(new Double(standardDeviations2.getArray1()[0]).compareTo(0.0) != 0 && new Double(means2.getArray1()[0]).compareTo(0.0) != 0){
			posterior2 *= calculateClassProbability(sample.getFeature(0), means2.getArray1()[0], standardDeviations2.getArray1()[0]);
		}
		
		// for each feature in the dependence tree order calculate the class 1 probability
		for(int i = 1; i<dependenceTree1.size(); i++){
			
			int nodeNumber = dependenceTree1.get(i).getNodeNumber();
			//System.out.println(dependenceTree1.get(i).toString());
			if(!sample.getFeature(dependenceTree1.get(i).getParent().getNodeNumber())){ // parent feature is 0: use the means and SDs stored in array1
				posterior1 *= calculateClassProbability(sample.getFeature(nodeNumber), means1.getArray1()[nodeNumber], 
						standardDeviations1.getArray1()[nodeNumber]);
			} else { // parent feature is 1: user the means and SDs stored in array2
				posterior1 *= calculateClassProbability(sample.getFeature(nodeNumber), means1.getArray2()[nodeNumber], 
						standardDeviations1.getArray2()[nodeNumber]);
			}
			
		}
		
		// for each feature in the dependence tree order calculate the class 2 probability
		for(int i = 1; i<dependenceTree2.size(); i++){
			
			int nodeNumber = dependenceTree2.get(i).getNodeNumber();
			
			if(!sample.getFeature(dependenceTree2.get(i).getParent().getNodeNumber())){ // parent feature is 0: use the means and SDs stored in array1
				posterior2 *= calculateClassProbability(sample.getFeature(nodeNumber), means2.getArray1()[nodeNumber], 
						standardDeviations2.getArray1()[nodeNumber]);
			} else { // parent feature is 1: user the means and SDs stored in array2
				posterior2 *= calculateClassProbability(sample.getFeature(nodeNumber), means2.getArray2()[nodeNumber], 
						standardDeviations2.getArray2()[nodeNumber]);
			}
			
		}
		
		if(posterior1 < posterior2){
			return false;
		} else {
			return true;
		}
	}
	
	// return a 2 member array. Index 0 is the correctly identified percentage and index 1 is the falsely identified percentage
	// assume the samples are actually from class1.
	private double[] classifySamplesIndependently(List<Sample> trainingSamples1, List<Sample> trainingSamples2, List<Sample> testingSamples){
		
		if(trainingSamples1.size() == 0 || trainingSamples2.size() == 0 || testingSamples.size() == 0){
			System.out.println("Attempted to classify using an empty list.");
			return null;
		}
		
		double totalSamples = (double) testingSamples.size();
		double[] sample1Means = calculateMean(trainingSamples1);
		double[] sample2Means = calculateMean(trainingSamples2);
		double[] sample1StandardDeviations = calculateStandardDeviation(trainingSamples1, sample1Means);
		double[] sample2StandardDeviations = calculateStandardDeviation(trainingSamples2, sample2Means);
		/*Boolean[] results = new Boolean[sampleNumber];
		
		for(int i=0; i<sampleNumber; i++){
			results[i] = classifySample(testingSamples.get(i), sample1Means, sample1StandardDeviations, sample2Means, sample2StandardDeviations);
		}*/
		
		double class1Samples = 0;
		// the ratio of class 1 to class 2 samples is the same as the ratio of their training samples because both training samples are 0.8 * total sample size (or more generically n-1 / n of the total sample size)
		double probabilityInClass1 = (double) trainingSamples1.size() / (trainingSamples1.size() + trainingSamples2.size()); 
		//System.out.println("|"+trainingSamples1.size()+"|"+trainingSamples2.size()+"|"+probabilityInClass1);
		//double probabilityInClass1 = 0.5;
		
		for(Sample sample: testingSamples){
			if(classifySample(sample, sample1Means, sample1StandardDeviations, sample2Means, sample2StandardDeviations, probabilityInClass1)){
				class1Samples++;
			}
		}
		
		double[] output = new double[2];
		output[0] = class1Samples / totalSamples;
		output[1] = (totalSamples - class1Samples) / totalSamples;
		
		return output;
		
	}
	
	private double[] classifySamplesDependently(List<Sample> trainingSamples1, List<Sample> trainingSamples2, List<Sample> testingSamples, 
			ArrayList<DepTreeNode> class1Tree, ArrayList<DepTreeNode> class2Tree){
		
		double[] output = new double[2];
		double totalSamples = (double) testingSamples.size();
		
		TwoDoubleArrays sample1Means = calculateDependentMean(class1Tree);
		TwoDoubleArrays sample2Means = calculateDependentMean(class2Tree);
		TwoDoubleArrays sample1SD = calculateDependentStandardDeviation(trainingSamples1, class1Tree);
		TwoDoubleArrays sample2SD = calculateDependentStandardDeviation(trainingSamples2, class2Tree);
		
		double class1Samples = 0;
		
		for(Sample sample: testingSamples){
			if(classifyDependentSample(sample, sample1Means, sample1SD, sample2Means, sample2SD, class1Tree, class2Tree)){
				class1Samples++;
			}
		}
		output[0] = class1Samples / totalSamples;
		output[1] = (totalSamples - class1Samples) / totalSamples;
		
		return output;
	}
	
	public ArrayList<Sample> copySampleList(List<Sample> original){
		
		ArrayList<Sample> newList = new ArrayList<Sample>();
		
		for(Sample sample: original){
			Sample s = new Sample(sample);
			newList.add(s);
		}
		return newList;
	}
	
	private void printConfusionMatrix(double[] sample1Results, double[] sample2Results){
		
		String output = "";
		
		output += "|"+sample1Results[0]+"|"+sample1Results[1]+"|\n";
		output += "|"+sample2Results[1]+"|"+sample2Results[0]+"|\n";
		
		System.out.println(output);
	}
	
	// assuming number of samples in each class are identical
	// technique 0: independent bayesian, 1: dependent bayesian, 2: decision tree
	public void performNFoldCrossValidation(int n, List<Sample> class1Samples, List<Sample> class2Samples, int technique){
		
		int totalSamples1 = class1Samples.size();
		int totalSamples2 = class2Samples.size();
		
		// trees declared here so they will not be re-computed each loop.  Only needed for technique 1.
		ArrayList<DepTreeNode> class1Tree = inferDependenceTree(class1Samples);
		ArrayList<DepTreeNode> class2Tree = inferDependenceTree(class2Samples);
		
		double[] averageResults1 = new double[2];
		double[] averageResults2 = new double[2];
		
		for(int i=0; i<n; i++){
			int testingStartIndex1 = ((i * totalSamples1 / n) + (totalSamples1 / n * (n - 1))) % totalSamples1;
			int testingEndIndex1 = ((i * totalSamples1 / n) + totalSamples1) % totalSamples1;
			
			int testingStartIndex2 = ((i * totalSamples2 / n) + (totalSamples2 / n * (n - 1))) % totalSamples2;
			int testingEndIndex2 = ((i * totalSamples2 / n) + totalSamples2) % totalSamples2;
			
			// want the testEndIndex to be the final value in the list, rather than the first one
			if(testingEndIndex1 == 0){
				testingEndIndex1 = totalSamples1;
			}
			if(testingEndIndex2 == 0){
				testingEndIndex2 = totalSamples2;
			}
			
			ArrayList<Sample> class1TrainingSamples = copySampleList(class1Samples);
			List<Sample> class1TestingSamples;
			//System.out.println("|"+testingStartIndex1+"|"+testingEndIndex1+"|"+totalSamples1);
			if(testingEndIndex1 > testingStartIndex1){
				class1TestingSamples = class1Samples.subList(testingStartIndex1, testingEndIndex1);
				class1TrainingSamples.subList(testingStartIndex1, testingEndIndex1).clear();
			} else {// wraparound
				//System.out.println("wrap1");
				class1TestingSamples = class1Samples.subList(testingStartIndex1, totalSamples1);
				class1TestingSamples.addAll(class1Samples.subList(0, testingEndIndex1));
				class1TrainingSamples.subList(testingStartIndex1, totalSamples1).clear();
				class1TrainingSamples.subList(0, testingEndIndex1);
			}
			
			ArrayList<Sample> class2TrainingSamples = copySampleList(class2Samples);
			List<Sample> class2TestingSamples;
			//System.out.println("|"+testingStartIndex2+"|"+testingEndIndex2+"|"+totalSamples2);
			if(testingEndIndex2 > testingStartIndex2){
				class2TestingSamples = class2Samples.subList(testingStartIndex2, testingEndIndex2);
				class2TrainingSamples.subList(testingStartIndex2, testingEndIndex2).clear();
			} else {// wraparound
				//System.out.println("wrap2");
				class2TestingSamples = class2Samples.subList(testingStartIndex2, totalSamples2);
				class2TestingSamples.addAll(class2Samples.subList(0, testingEndIndex2));
				class2TrainingSamples.subList(testingStartIndex2, totalSamples2).clear();
				class2TrainingSamples.subList(0, testingEndIndex2);
			}
			
			double[] results1 = new double[2];
			double[] results2 = new double[2];
			// perform cross validation, print out confusion matrix
			if(technique == 0){			
				
				results1 = classifySamplesIndependently(class1TrainingSamples, class2TrainingSamples, class1TestingSamples);
				results2 = classifySamplesIndependently(class2TrainingSamples, class1TrainingSamples, class2TestingSamples);
				
			} else if(technique == 1){
				
				results1 = classifySamplesDependently(class1TrainingSamples, class2TrainingSamples, class1TestingSamples, class1Tree, class2Tree);
				results2 = classifySamplesDependently(class2TrainingSamples, class1TrainingSamples, class2TestingSamples, class2Tree, class1Tree);
				
			} else if(technique == 2){
			
				results1 = classifySamplesByDecisionTree(class1TrainingSamples, class2TrainingSamples, class1TestingSamples, false);
				results2 = classifySamplesByDecisionTree(class2TrainingSamples, class1TrainingSamples, class2TestingSamples, false);
				
			} else if(technique == 3){
			
				results1 = classifySamplesByDecisionTree(class1TrainingSamples, class2TrainingSamples, class1TestingSamples, true);
				results2 = classifySamplesByDecisionTree(class2TrainingSamples, class1TrainingSamples, class2TestingSamples, true);
				
			} else {
				System.out.println("Invalid technique for performNFoldCrossValidation");
				return;
			}
			//System.out.println("Confusion matrix "+i+":");
			//printConfusionMatrix(results1, results2);
			
			averageResults1[0] += results1[0];
			averageResults1[1] += results1[1];
			averageResults2[0] += results2[0];
			averageResults2[1] += results2[1];
		}
		
		averageResults1[0] /= n;
		averageResults1[1] /= n;
		averageResults2[0] /= n;
		averageResults2[1] /= n;
		
		System.out.println("Average of confusion matrices:");
		printConfusionMatrix(averageResults1, averageResults2);
	}
	
	private double log2(double input){
		return Math.log10(input) / Math.log10(2);
	}
	
	// expected mutual information measure, for binary data
	private double evaluateEMIMTerm(double pxy, double px, double py){
		if(new Double(pxy).compareTo(0.0) == 0){
			return 0;
		}
		
		return pxy*log2(pxy / (px * py));
	}
	
	private ArrayList<WeightedBranch> generateAllBranches(List<Sample> samples){
		
		if(samples.size() == 0){
			System.out.println("Attempted to generate all branches from an empty sample list.");
			return null;
		}
		
		ArrayList<WeightedBranch> branches = new ArrayList<WeightedBranch>();
		int totalSamples = samples.size();
		int totalFeatures = samples.get(0).getFeatures().length;
		
		// for each 2-feature pair
		for(int i=0; i<totalFeatures; i++){
			for(int j=i+1; j<totalFeatures; j++){
				
				// could make this expandable to non-binary features using loops, arraylists
				double feature1Zeros = 0;
				double feature2Zeros = 0;
				double feature1Ones = 0;
				double feature2Ones = 0;
				double found00Pairs = 0;
				double found10Pairs = 0;
				double found01Pairs = 0;
				double found11Pairs = 0;
				
				// calculate the probability of 0 given 0 and the probability of 0 given 1 for each sample
				for(Sample sample: samples){
					if(!sample.getFeature(i)){ // feature is 0
						feature1Zeros++;
						if(!sample.getFeature(j)){
							feature2Zeros++;
							found00Pairs++;
						} else {
							feature2Ones++;
							found01Pairs++;
						}
					} else {
						feature1Ones++;
						if(!sample.getFeature(j)){
							feature2Zeros++;
							found10Pairs++;
						} else {
							feature2Ones++;
							found11Pairs++;
						}
					}
				}

				// probability feature n has a valute of 0 or 1
				double pF1Is0 = feature1Zeros / totalSamples;
				double pF1Is1 = feature1Ones / totalSamples;
				double pF2Is0 = feature2Zeros / totalSamples;
				double pF2Is1 = feature2Ones / totalSamples;
				double p00 = found00Pairs / totalSamples;
				double p01 = found01Pairs / totalSamples;
				double p10 = found10Pairs / totalSamples;
				double p11 = found11Pairs / totalSamples;
				
				//System.out.println("|"+pF1Is0+"|"+pF1Is1+"|"+pF2Is0+"|"+pF2Is1+"|"+p00+"|"+p01+"|"+p10+"|"+p11);
				
				// expected mutual information measure
				double term1 = evaluateEMIMTerm(p00, pF1Is0, pF2Is0);
				double term2 = evaluateEMIMTerm(p01, pF1Is0, pF2Is1);
				double term3 = evaluateEMIMTerm(p10, pF1Is1, pF2Is0);
				double term4 = evaluateEMIMTerm(p11, pF1Is1, pF2Is1);
				//System.out.println("|"+term1+"|"+term2+"|"+term3+"|"+term4);
				double emim = term1 + term2 + term3 + term4;
				branches.add(new WeightedBranch(i, j, emim));
				//System.out.println("Full tree");
				//System.out.println(Arrays.toString(branches.toArray()));
			}
		}
		
		
		return branches;
		
	}
	
	// total nodes indictes the number of nodes in the feature graph, which is also the total number of features.
	private ArrayList<WeightedBranch> buildSpanningTree(ArrayList<WeightedBranch> branches, int totalNodes){
		
		if(branches.size()==0){
			System.out.println("Empty list of branches passed to buildSpanningTree.");
			return null;
		}
		
		if(totalNodes < 2){
			System.out.println("Attempted to call buildSpanningTree with under 2 nodes.");
			return null;
		}
		
		// check to make sure that the correct number of branches are included for the given nodes
		int expectedBranches = 0;
		for(int i=1; i<totalNodes; i++){
			expectedBranches+=i;
		}
		if(branches.size() != expectedBranches){
			System.out.println("Contract violated: buildSpanningTree was called with an inconsistent number of branches and total nodes.");
			return null;
		}
		
		ArrayList<WeightedBranch> tree = new ArrayList<WeightedBranch>();
		ArrayList<Integer> connectedNodes = new ArrayList<Integer>();
		
		// first, sort the branches by weight, with the highest weight at index 0
		Collections.sort(branches, Collections.reverseOrder(new WeightedBranchComparator()));
		
		// initialize the tree and connected nodes with the highest weight branch available
		WeightedBranch highestWeightBranch = branches.get(0);
		connectedNodes.add(highestWeightBranch.getNode1());
		connectedNodes.add(highestWeightBranch.getNode2());
		tree.add(new WeightedBranch(highestWeightBranch)); 
		branches.remove(highestWeightBranch);
		
		// now add more branches until all nodes are connected.
		while(connectedNodes.size() < totalNodes){
			
			ArrayList<Integer> branchIndicesToRemove = new ArrayList<Integer>();
			
			for(WeightedBranch branch: branches){
				
				// remove any branches that are already subsumed by the tree
				if(connectedNodes.contains(branch.getNode1()) && connectedNodes.contains(branch.getNode2())){
					branchIndicesToRemove.add(branches.indexOf(branch));
				} else {
					// otherwise, ensure that the next weighted branch is connected
					if(connectedNodes.contains(branch.getNode1()) || connectedNodes.contains(branch.getNode2())){
						// then add it to the tree and remove it from the list of branches
						if(connectedNodes.contains(branch.getNode1())){
							connectedNodes.add(branch.getNode2());
						} else {
							connectedNodes.add(branch.getNode1());
						}
						tree.add(new WeightedBranch(branch)); 
						branchIndicesToRemove.add(branches.indexOf(branch));
						break; // and exit if all nodes are connected, or start searching again
					}
					// if it isn't connected, skip it and continue searching
				}
				
			}
			
			for(Integer index: branchIndicesToRemove){
				branches.remove(index);
			}
			
			// could be an issue if there somehow is no way to reach one of the nodes
		}
		
		return tree;
	}

	// return the probability the node (in feature2Index) will be 0 given the parent (in feature1Index) has the value given (0 for false, 1 for true) 
	private double getNodeProbabilities(List<Sample> samples, int feature1Index, int feature2Index, Boolean given){
		
		double givenValuesFound = 0;
		double value2Is0GivenValue1Found = 0;
		
		// count for each sample
		for(Sample sample: samples){
			// if the value of the given feature is the same as the given value
			if((sample.getFeature(feature1Index) && given) || (!sample.getFeature(feature1Index) && !given)){
				givenValuesFound++;
				// if the value of the second feature is 0
				if(!sample.getFeature(feature2Index)){
					value2Is0GivenValue1Found++;
				}
			}
		}
		
		if(new Double(givenValuesFound).compareTo(0.0) == 0){
			return 0;
		}
		
		return value2Is0GivenValue1Found / givenValuesFound;
	}
	
	private ArrayList<DepTreeNode> convertBranchTreeToNodeTree(ArrayList<WeightedBranch> branches, List<Sample> samples){
		
		ArrayList<DepTreeNode> nodes = new ArrayList<DepTreeNode>();
		int totalNodes = branches.size()+1;
		//System.out.println(totalNodes);
		
		// choose node 0 as the root node
		double zeroCount = 0;
		double totalSamples = samples.size();
		for(Sample sample: samples){
			if(!sample.getFeature(0)){
				zeroCount++;
			}
		}
		DepTreeNode root = new DepTreeNode(zeroCount / totalSamples);
		nodes.add(root);
		
		// now add all the other nodes to the tree: iterates along the nodes list.  Note that these nodes are indexed sequentially, NOT stored according to their "nodeNumber".
		for(int i=0; i<totalNodes; i++){
			
			ArrayList<Integer> branchIndicesToDelete = new ArrayList<Integer>();
			int nodeNumber = nodes.get(i).getNodeNumber();
			//System.out.println("Evaluating node index "+i+" with node number "+nodeNumber);
			
			for(WeightedBranch branch: branches){
				// if the branch is connected to the node currently under examination
				if(branch.getNode1() == nodeNumber || branch.getNode2() == nodeNumber){
					
					double probability0Given0;
					double probability0Given1;
					int nodeIndex;
					
					// if the new node is node 2
					if(branch.getNode1() == nodeNumber){
						//System.out.println("a");
						probability0Given0 = getNodeProbabilities(samples, branch.getNode1(), branch.getNode2(), false);
						probability0Given1 = getNodeProbabilities(samples, branch.getNode1(), branch.getNode2(), true);
						nodeIndex = branch.getNode2();
					} else { // the new node is node 1
						//System.out.println("b"+branch.getNode2()+"|"+branch.getNode1());
						probability0Given0 = getNodeProbabilities(samples, branch.getNode2(), branch.getNode1(), false);
						probability0Given1 = getNodeProbabilities(samples, branch.getNode2(), branch.getNode1(), true);
						nodeIndex = branch.getNode1();
					}
					
					//System.out.println("2"+probability0Given1+"nodeIndex: "+nodeIndex+", parent node "+nodes.get(i).getNodeNumber()+", weight: "+branch.getWeight());
					DepTreeNode node = new DepTreeNode(nodes.get(i), probability0Given0, probability0Given1, nodeIndex);
					nodes.add(node);
					nodes.get(i).addChild(node);
					
					branchIndicesToDelete.add(branches.indexOf(branch));
					
				}
				
			}
			
			ArrayList<WeightedBranch> temp = new ArrayList<WeightedBranch>();
			// delete the used branches from the list now
			for(int j=0; j<branches.size(); j++){
				if(!branchIndicesToDelete.contains(j)){
					temp.add(branches.get(j));
				}
			}
			branches = temp;
			
			//System.out.println("Step "+i);
			//System.out.println(root.toString());
			//System.out.println(Arrays.toString(branches.toArray()));
		}
		
		return nodes;
	}
	
	
	public ArrayList<DepTreeNode> inferDependenceTree(List<Sample> samples){
		
		ArrayList<WeightedBranch> branches = generateAllBranches(samples);
		ArrayList<WeightedBranch> spanningTree = buildSpanningTree(branches, samples.get(0).getFeatures().length);
		//System.out.println(Arrays.toString(spanningTree.toArray()));
		ArrayList<DepTreeNode> tree = convertBranchTreeToNodeTree(spanningTree, samples);
		
		return tree;
		
	}
	
	private double calculateEntropy(double positiveSamples, double negativeSamples){
		
		double entropy = 0;
		double totalSamples = positiveSamples + negativeSamples;
		
		if(positiveSamples != 0){
			entropy += -1 * positiveSamples / totalSamples * log2(positiveSamples / totalSamples);
		}
		if(negativeSamples != 0){
			entropy += -1 * negativeSamples / totalSamples * log2(negativeSamples / totalSamples);
		}
		//System.out.println("Entropy: "+entropy+", positive: "+positiveSamples+", negative: "+negativeSamples);
		
		return entropy;
	}
	
	private double calculateGain(List<Sample> class1Samples, List<Sample> class2Samples, int feature){
		
		double c1Size = class1Samples.size();
		double c2Size = class2Samples.size();
		double totalSamples = c1Size + c2Size;
		double gain = calculateEntropy(c1Size, c2Size); // class 2 is the "negative" result
		//System.out.println("Gain component 1: "+gain);
		
		double featureIs0InC1 = 0;
		double featureIs0InC2 = 0;
		
		// count all the times the feature is 0 in both of the class samples
		for(Sample sample: class1Samples){
			if(!sample.getFeature(feature)){
				featureIs0InC1++;
			}
		}
		for(Sample sample: class2Samples){
			if(!sample.getFeature(feature)){
				featureIs0InC2++;
			}
		}
		
		// the number of times the feature is 1 will be the total samples - the times the feature is 0
		double featureIs1InC1 = c1Size - featureIs0InC1;
		double featureIs1InC2 = c2Size - featureIs0InC2;
		
		// now add the subset entropies, offset by the occurrence rate, to the gain value
		gain -= (featureIs0InC1 + featureIs0InC2) / totalSamples * calculateEntropy(featureIs0InC1, featureIs0InC2);
		//System.out.println("Gain component 2: "+gain);
		gain -= (featureIs1InC1 + featureIs1InC2) / totalSamples * calculateEntropy(featureIs1InC1, featureIs1InC2);
		//System.out.println("Gain component 3: "+gain);
		
		return gain;
	}
	
	private DecTreeNode buildNextDecTreeNode(List<Sample> class1Samples, List<Sample> class2Samples, ArrayList<Integer> featuresToTest, DecTreeNode parent, Boolean is0Node){
		
		// check for end conditions
		
		// no features left to test
		if(featuresToTest.size() == 0){
			//System.out.println("No features left to test. Returning.");
			return null;
		}
		
		// all examples are either positive or negative
		//System.out.println("Remaining class 1 samples: "+class1Samples.size());
		//System.out.println("Remaining class 2 samples: "+class2Samples.size());
		if(class1Samples.size() == 0 || class2Samples.size() == 0){
			//System.out.println("No samples left to distinguish. Returning.");
			return null;
		}
		
		double maxGainFound = 0;
		int bestGainFeature = -1;
		
		// for each feature, calculate its gain
		for(Integer i: featuresToTest){
			double featureGain = calculateGain(class1Samples, class2Samples, i);
			//System.out.println("Feature gain for feature "+i+" is: "+featureGain);
			// if a better gain is found, save it as the new max and save the feature associated with it
			if(featureGain > maxGainFound){
				maxGainFound = featureGain;
				bestGainFeature = i;
			}
		}
		
		if(bestGainFeature == -1){
			//System.out.println("No feature gains found. Returning.");
			return null;
		}
		
		ArrayList<Sample> class1SamplesWithFeature0 = new ArrayList<Sample>();
		ArrayList<Sample> class2SamplesWithFeature0 = new ArrayList<Sample>();
		ArrayList<Sample> class1SamplesWithFeature1 = new ArrayList<Sample>();
		ArrayList<Sample> class2SamplesWithFeature1 = new ArrayList<Sample>();
		ArrayList<Integer> remainingFeatures = new ArrayList<Integer>();
		
		// need to parse the samples by feature category
		for(Sample sample: class1Samples){
			if(!sample.getFeature(bestGainFeature)){
				class1SamplesWithFeature0.add(new Sample(sample));
			} else {
				class1SamplesWithFeature1.add(new Sample(sample));
			}
		}
		
		for(Sample sample: class2Samples){
			if(!sample.getFeature(bestGainFeature)){
				class2SamplesWithFeature0.add(new Sample(sample));
			} else {
				class2SamplesWithFeature1.add(new Sample(sample));
			}
		}
		for(Integer i: featuresToTest){
			if(i != bestGainFeature){
				remainingFeatures.add(i);
			}
		}
		
		// take the highest gain feature and make a node with it
		DecTreeNode node = new DecTreeNode(parent, bestGainFeature, class1SamplesWithFeature0, class2SamplesWithFeature0,
				class1SamplesWithFeature1, class2SamplesWithFeature1, remainingFeatures);
		//System.out.println("Created "+node.toString());
		
		if(parent != null){
			parent.addChild(node, is0Node);
		}
		
		// now recursively build the node's descendants
		buildNextDecTreeNode(node.getClass1SamplesWithFeature0(), node.getClass2SamplesWithFeature0(), node.getFeaturesToTest(), node, true);
		buildNextDecTreeNode(node.getClass1SamplesWithFeature1(), node.getClass2SamplesWithFeature1(), node.getFeaturesToTest(), node, false);
		
		return node;
		
	}
	
	// return true if the sample is a member of the first class and false if it is a member of the second class.  Assumes equal probability for both classes.
	private Boolean classifySampleByDecisionTree(DecTreeNode node, Sample sample){
		
		//System.out.println("Classifying at node "+node.toString());
		
		// if the node under evaluation is a leaf node, classify the sample as whichever sample group
		// is more strongly represented in the node
		if(!node.hasChildren()){
			int class1TrainingSamples;
			int class2TrainingSamples;
			if(!sample.getFeature(node.getFeature())){// the sample's feature in question is 0
				class1TrainingSamples = node.getClass1SamplesWithFeature0().size();
				class2TrainingSamples = node.getClass2SamplesWithFeature0().size();
			} else { // the sample's feature in question is 1
				class1TrainingSamples = node.getClass1SamplesWithFeature1().size();
				class2TrainingSamples = node.getClass2SamplesWithFeature1().size();
			}
			
			if(class1TrainingSamples >= class2TrainingSamples){
				return true;
			} else {
				return false;
			}
		}
		
		// otherwise, choose the branch represented by the sample's feature value and recurse
		if(!sample.getFeature(node.getFeature())){ // the feature in question is 0
			if(node.getChildren()[0] != null){
				return classifySampleByDecisionTree(node.getChildren()[0], sample); // the child in index 0 is always the 0 branch
			} else{
				//System.out.println("0node1: "+node.getFeature()+","+node.getClass1SamplesWithFeature0().size());
				//System.out.println("0node2: "+node.getFeature()+","+node.getClass2SamplesWithFeature0().size());
				if(node.getClass1SamplesWithFeature0().size() >= node.getClass2SamplesWithFeature0().size()){
					return true;
				}
				return false;
			}
		} else {
			if(node.getChildren()[1] != null){
				return classifySampleByDecisionTree(node.getChildren()[1], sample);
			} else {
				//System.out.println("1node1: "+node.getFeature()+","+node.getClass1SamplesWithFeature1().size());
				//System.out.println("1node2: "+node.getFeature()+","+node.getClass2SamplesWithFeature1().size());
				if(node.getClass1SamplesWithFeature1().size() >= node.getClass2SamplesWithFeature1().size()){
					return true;
				}
				return false;
			}
		}
	}
	
	private double[] classifySamplesByDecisionTree(List<Sample> trainingSamples1, List<Sample> trainingSamples2, List<Sample> testingSamples, Boolean printTree){
		
		if(trainingSamples1.size() == 0 || trainingSamples2.size() == 0){
			System.out.println("Attempted Decision Tree classification with an empty sample list.");
			return null;
		}
		
		double testingSampleSize = testingSamples.size();
		double[] results = new double[2];
		results[0] = 0;
		results[1] = 0;
		
		// populate the features list
		ArrayList<Integer> features = new ArrayList<Integer>();
		for(int i=0; i<trainingSamples1.get(0).getFeatures().length; i++){
			features.add(i);
		}
		
		// create the decision tree
		DecTreeNode root = buildNextDecTreeNode(trainingSamples1, trainingSamples2, features, null, true);
		if(printTree)
			printDecisionTree(root);
		
		// now classify the testing samples using the decision tree
		for(Sample sample: testingSamples){
			if(classifySampleByDecisionTree(root, sample)){
				results[0]++;
			} else {
				results[1]++;
			}
		}
		
		results[0] /= testingSampleSize;
		results[1] /= testingSampleSize;
		
		return results;
	}
	
	private void printDecisionTree(DecTreeNode root){
		
		System.out.println("Decision Tree Generated: ");
		//System.out.println(root.printAll());
		//BTreePrinter.printNode(root);
		
		PrintStream stdout = System.out;
		
		OutputStreamWriter os = null;
		try{
			os = new OutputStreamWriter(System.out);
			root.printTree(os);
		} catch(IOException e){
			System.out.println("Failed to initialize output stream writer.");
		} finally {
			if(os!=null){
				try {
					os.close();
				} catch(IOException e){
					System.out.println("Nothing to be done, failed to close output stream writer");
					System.exit(1);
				}
			}
		}
		System.setOut(stdout);
		
	}
	
	public ArrayList<ArrayList<Sample>> extractSampleSets(){
		
		double[][] inputData = readFile();
		
		int inputWidth = inputData[0].length;
		int inputHeight = inputData.length;
		Boolean[][] features = new Boolean[inputHeight][inputWidth - 1];
		
		// for each column
		for(int x=1; x<inputWidth; x++){
			
			// for each row, find the maximum and min
			double max = 0;
			double min = Double.MAX_VALUE; 
			for(int y=0; y<inputHeight; y++){
				if(inputData[y][x] > max){
					max = inputData[y][x];
				}
				if(inputData[y][x] < min){
					min = inputData[y][x];
				}
			}
			
			// then set the threshold value to the midpoint, and classify each value according to the threshold value 
			// 0 if below the threshold, 1 otherwise
			double threshold = (max + min) / 2;
			for(int y=0; y<inputHeight; y++){
				if(inputData[y][x] < threshold){
					features[y][x-1] = false;
				} else {
					features[y][x-1] = true;
				}
			}
		}
		
		ArrayList<ArrayList<Sample>> output = new ArrayList<ArrayList<Sample>>();
		
		ArrayList<Sample> class1Samples = new ArrayList<Sample>();
		ArrayList<Sample> class2Samples = new ArrayList<Sample>();
		ArrayList<Sample> class3Samples = new ArrayList<Sample>();
		
		output.add(class1Samples);
		output.add(class2Samples);
		output.add(class3Samples);
		
		for(int y=0; y<inputHeight; y++){
			//System.out.println(new Double(inputData[y][0]).intValue() == 1);
			if(new Double(inputData[y][0]).intValue() == 1){
				class1Samples.add(new Sample(features[y]));
			} else if(new Double(inputData[y][0]).intValue() == 2){
				class2Samples.add(new Sample(features[y]));
			} else if(new Double(inputData[y][0]).intValue() == 3){
				class3Samples.add(new Sample(features[y]));
			}
		}
		//System.out.println("c1: "+class1Samples.size()+", c2: "+class2Samples.size()+", c3: "+class3Samples.size());
		
		return output;
	}
	
	
	public double[][] readFile(){
		
		Scanner scanner;
		
		try {
			scanner = new Scanner(new File("wine.csv"));
		} catch (FileNotFoundException e){
			System.out.println("Wine file could not be opened.");
			return null;
		}
		
		scanner.useDelimiter(",");
				
		double[][] output = new double[178][14];
		
		int maxEntries = output.length*output[0].length;
		int entries = 0;
		int x = 0;
		int y = 0;
		while(scanner.hasNext() && entries < maxEntries){
			entries++;
			String csvValue = scanner.next();
			String trimmed = csvValue.trim();
			double value = Double.parseDouble(trimmed);
			//System.out.println(value);
			if(x<output[0].length){
				output[y][x] = value;
				x++;
			} else {
				x=0;
				y++;
				output[y][x] = value;
				x++;
			}
		}
		scanner.close();
		
		return output;
	}
	
	public double rv(){
		
		double output = ThreadLocalRandom.current().nextDouble(1);
		
		// ensure that a value of 0 is not generated
		while(output == 0.000000){
			output = ThreadLocalRandom.current().nextDouble(1);
		}
		return output;
	}

	public static void main(String[] args) {
		
		Classifier c = new Classifier();
/*
		int[] treeGeneration = {2,0,0};
		double[] treeProbabilitiesGiven0 = {0,0};
		double[] treeProbabilitiesGiven1 = {1,1};
		double initialProbability = 0.5;
		
		ArrayList<DepTreeNode> dependenceTree = c.generateTree(treeGeneration, treeProbabilitiesGiven0, treeProbabilitiesGiven1, initialProbability);
		System.out.println("Generated the following dependency tree for test: \n"+dependenceTree.get(0).toString());
		
		ArrayList<Sample> samples = c.generateSamples(10000, dependenceTree);
		for(Sample s: samples){
			//System.out.println(s.toString());
		}
		
		System.out.println("Independent Classification: "+Arrays.toString(c.independentClassification(samples.subList(0, 10000))));
	*/	
		ArrayList<ArrayList<Sample>> inputs = c.extractSampleSets();
		ArrayList<Sample> class1SamplesRL = inputs.get(0);
		ArrayList<Sample> class2SamplesRL = inputs.get(1);
		ArrayList<Sample> class3SamplesRL = inputs.get(2);
		/*
		for(Sample s: class1SamplesRL){
			System.out.println(s.toString());
		}
		for(Sample s: class2SamplesRL){
			System.out.println(s.toString());
		}
		for(Sample s: class2SamplesRL){
			System.out.println(s.toString());
		}*/
		
		System.out.println("Inferred dependency tree from Class 1 Real Life samples.");
		System.out.println(c.inferDependenceTree(class1SamplesRL).get(0).toString());
		
		System.out.println("Inferred dependency tree from Class 2 Real Life samples.");
		System.out.println(c.inferDependenceTree(class2SamplesRL).get(0).toString());
		
		System.out.println("Inferred dependency tree from Class 3 Real Life samples.");
		System.out.println(c.inferDependenceTree(class3SamplesRL).get(0).toString());
		
		System.out.println("=========================================================");
		
		System.out.println("Independent Classification of Class 1 and 2 samples: ");
		c.performNFoldCrossValidation(4, class1SamplesRL, class2SamplesRL, 0);
		
		System.out.println("Dependent Classification of Class 1 and 2 samples: ");
		c.performNFoldCrossValidation(4, class1SamplesRL, class2SamplesRL, 1);
		
		System.out.println("Decision Tree Classification of Class 1 and 2 samples: ");
		c.performNFoldCrossValidation(4, class1SamplesRL, class2SamplesRL, 2);
		
		System.out.println("=========================================================");
		
		System.out.println("Independent Classification of Class 1 and 3 samples: ");
		c.performNFoldCrossValidation(5, class1SamplesRL, class3SamplesRL, 0);
		
		System.out.println("Dependent Classification of Class 1 and 3 samples: ");
		c.performNFoldCrossValidation(5, class1SamplesRL, class3SamplesRL, 1);
		
		System.out.println("Decision Tree Classification of Class 1 and 3 samples: ");
		c.performNFoldCrossValidation(5, class1SamplesRL, class3SamplesRL, 2);
		
		System.out.println("=========================================================");
		
		System.out.println("Independent Classification of Class 2 and 3 samples: ");
		c.performNFoldCrossValidation(5, class2SamplesRL, class3SamplesRL, 0);
		
		System.out.println("Dependent Classification of Class 2 and 3 samples: ");
		c.performNFoldCrossValidation(5, class2SamplesRL, class3SamplesRL, 1);
		
		System.out.println("Decision Tree Classification of Class 2 and 3 samples: ");
		c.performNFoldCrossValidation(5, class2SamplesRL, class3SamplesRL, 2);
		
		
			
		int[] treeGenerationClass1 = {2, 1, 3, 0, 0, 0, 1, 2, 0, 0};
		//double[] treeProbabilitiesGiven0Class1 = {0.2, 0.7, 0.9, 0.5, 0.4, 0.2, 0.1, 0.5, 0.9};
		//double[] treeProbabilitiesGiven1Class1 = {0.8, 0.3, 0.3, 0.7, 0.9, 0.7, 0.2, 0.1, 0.1};
		double[] treeProbabilitiesGiven0Class1 = {c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv()};
		double[] treeProbabilitiesGiven1Class1 = {c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv()};
		//double initialProbabilityClass1 = 0.6;
		double initialProbabilityClass1 = c.rv();
		ArrayList<DepTreeNode> dependenceTree1 = c.generateTree(treeGenerationClass1, treeProbabilitiesGiven0Class1, treeProbabilitiesGiven1Class1, initialProbabilityClass1);
		System.out.println("Generated the following dependency tree for class 1: \n"+dependenceTree1.get(0).toString());
		
		ArrayList<Sample> class1Samples = c.generateSamples(2000, dependenceTree1);
		
		System.out.println("Inferred dependency tree from Class 1 samples.");
		System.out.println(c.inferDependenceTree(class1Samples).get(0).toString());
		
		//int[] treeGenerationClass2 = {3, 3, 0, 2, 0, 0, 0, 0, 1, 0};
		//double[] treeProbabilitiesGiven0Class2 = {0.1, 0.4, 0.2, 0.3, 0.1, 0.3, 0.8, 0.1, 0.4};
		//double[] treeProbabilitiesGiven1Class2 = {0.2, 0.9, 0.9, 0.4, 0.6, 0.4, 0.1, 0.5, 0.8};
		int[] treeGenerationClass2 = {2, 1, 3, 0, 0, 0, 1, 2, 0, 0};
		double[] treeProbabilitiesGiven0Class2 = {c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv()};
		double[] treeProbabilitiesGiven1Class2 = {c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv()};
		double initialProbabilityClass2 = c.rv();
		//double initialProbabilityClass2 = 0.5;
		ArrayList<DepTreeNode> dependenceTree2 = c.generateTree(treeGenerationClass2, treeProbabilitiesGiven0Class2, treeProbabilitiesGiven1Class2, initialProbabilityClass2);
		System.out.println("Generated the following dependency tree for class 2: \n"+dependenceTree2.get(0).toString());

		ArrayList<Sample> class2Samples = c.generateSamples(2000, dependenceTree2);
		
		System.out.println("Inferred dependency tree from Class 2 samples.");
		System.out.println(c.inferDependenceTree(class2Samples).get(0).toString());
		
		//int[] treeGenerationClass3 = {2, 2, 2, 2, 1, 0, 0, 0, 0, 0};
		//double[] treeProbabilitiesGiven0Class3 = {0.2, 0.3, 0.6, 0.2, 0.6, 0.2, 0.1, 0.8, 0.9};
		//double[] treeProbabilitiesGiven1Class3 = {0.9, 0.8, 0.7, 0.6, 0.1, 0.6, 0.6, 0.2, 0.2};
		//double initialProbabilityClass3 = 0.9;
		int[] treeGenerationClass3 = {2, 1, 3, 0, 0, 0, 1, 2, 0, 0};
		double[] treeProbabilitiesGiven0Class3 = {c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv()};
		double[] treeProbabilitiesGiven1Class3 = {c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv()};
		double initialProbabilityClass3 = c.rv();
		
		ArrayList<DepTreeNode> dependenceTree3 = c.generateTree(treeGenerationClass3, treeProbabilitiesGiven0Class3, treeProbabilitiesGiven1Class3, initialProbabilityClass3);
		System.out.println("Generated the following dependency tree for class 3: \n"+dependenceTree3.get(0).toString());
		ArrayList<Sample> class3Samples = c.generateSamples(2000, dependenceTree3);
		System.out.println("Inferred dependency tree from Class 3 samples.");
		System.out.println(c.inferDependenceTree(class3Samples).get(0).toString());
		
		//int[] treeGenerationClass4 = {1, 1, 1, 2, 1, 1, 0, 2, 0, 0};
		//double[] treeProbabilitiesGiven0Class4 = {0.1, 0.4, 0.2, 0.3, 0.1, 0.3, 0.8, 0.1, 0.4};
		//double[] treeProbabilitiesGiven1Class4 = {0.2, 0.9, 0.9, 0.4, 0.6, 0.4, 0.1, 0.5, 0.8};
		//double initialProbabilityClass4 = 0.1;
		int[] treeGenerationClass4 = {2, 1, 3, 0, 0, 0, 1, 2, 0, 0};
		double[] treeProbabilitiesGiven0Class4 = {c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv()};
		double[] treeProbabilitiesGiven1Class4 = {c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv(), c.rv()};
		double initialProbabilityClass4 = c.rv();
		
		ArrayList<DepTreeNode> dependenceTree4 = c.generateTree(treeGenerationClass4, treeProbabilitiesGiven0Class4, treeProbabilitiesGiven1Class4, initialProbabilityClass4);
		System.out.println("Generated the following dependency tree for class 4: \n"+dependenceTree4.get(0).toString());
		ArrayList<Sample> class4Samples = c.generateSamples(2000, dependenceTree4);
		System.out.println("Inferred dependency tree from Class 4 samples.");
		System.out.println(c.inferDependenceTree(class4Samples).get(0).toString());
		
		System.out.println("=========================================================");

		System.out.println("Independent Classification of Class 1 and 2 samples: ");
		c.performNFoldCrossValidation(5, class1Samples, class2Samples, 0);
		
		System.out.println("Dependent Classification of Class 1 and 2 samples: ");
		c.performNFoldCrossValidation(5, class1Samples, class2Samples, 1);
		
		System.out.println("Decision Tree Classification of Class 1 and 2 samples: ");
		c.performNFoldCrossValidation(5, class1Samples, class2Samples, 2);
		
		System.out.println("=========================================================");
		
		System.out.println("Independent Classification of Class 1 and 3 samples: ");
		c.performNFoldCrossValidation(5, class1Samples, class3Samples, 0);
		
		System.out.println("Dependent Classification of Class 1 and 3 samples: ");
		c.performNFoldCrossValidation(5, class1Samples, class3Samples, 1);
		
		System.out.println("Decision Tree Classification of Class 1 and 3 samples: ");
		c.performNFoldCrossValidation(5, class1Samples, class3Samples, 2);
		
		
		System.out.println("=========================================================");
		
		System.out.println("Independent Classification of Class 1 and 4 samples: ");
		c.performNFoldCrossValidation(5, class1Samples, class4Samples, 0);
		
		System.out.println("Dependent Classification of Class 1 and 4 samples: ");
		c.performNFoldCrossValidation(5, class1Samples, class4Samples, 1);
		
		System.out.println("Decision Tree Classification of Class 1 and 4 samples: ");
		c.performNFoldCrossValidation(5, class1Samples, class4Samples, 2);
		
		System.out.println("=========================================================");
		
		System.out.println("Independent Classification of Class 2 and 3 samples: ");
		c.performNFoldCrossValidation(5, class2Samples, class3Samples, 0);
		
		System.out.println("Dependent Classification of Class 2 and 3 samples: ");
		c.performNFoldCrossValidation(5, class2Samples, class3Samples, 1);
		
		System.out.println("Decision Tree Classification of Class 2 and 3 samples: ");
		c.performNFoldCrossValidation(5, class2Samples, class3Samples, 2);
		
		System.out.println("=========================================================");
		
		System.out.println("Independent Classification of Class 2 and 4 samples: ");
		c.performNFoldCrossValidation(5, class2Samples, class4Samples, 0);
		
		System.out.println("Dependent Classification of Class 2 and 4 samples: ");
		c.performNFoldCrossValidation(5, class2Samples, class4Samples, 1);
		
		System.out.println("Decision Tree Classification of Class 2 and 4 samples: ");
		c.performNFoldCrossValidation(5, class2Samples, class4Samples, 2);
		
		System.out.println("=========================================================");
		
		System.out.println("Independent Classification of Class 3 and 4 samples: ");
		c.performNFoldCrossValidation(4, class3Samples, class4Samples, 0);
		
		System.out.println("Dependent Classification of Class 3 and 4 samples: ");
		c.performNFoldCrossValidation(4, class3Samples, class4Samples, 1);
		
		System.out.println("Decision Tree Classification of Class 3 and 4 samples: ");
		c.performNFoldCrossValidation(4, class3Samples, class4Samples, 2);
	
		System.out.println("=========================================================");
		
		//Sample s = c.generateSample(dependenceTree1);
		
		//System.out.println(s.toString());
		
		//System.out.println(Arrays.toString(c.classifySamplesIndependently(class1Samples.subList(0, 1600), class2Samples.subList(0, 1600), class1Samples.subList(1600, 2000))));
		
		
	}

}
