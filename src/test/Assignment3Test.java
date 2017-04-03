package test;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import assignment3.Sample;
import assignment3.Classifier;
import assignment3.WeightedBranch;
import assignment3.WeightedBranchComparator;

public class Assignment3Test {

	private Sample sample;
	private Classifier classifier;
	
	@org.junit.Before
	public void setUp() throws Exception{
		
		Boolean[] features = {true, false, true};
		sample = new Sample(features);
		classifier = new Classifier();
	}
	
	@org.junit.Test
	public void testCopySampleList() throws Exception {
		
		ArrayList<Sample> list = new ArrayList<Sample>();
		Boolean[] features = {false, false, true};
		Sample sample2 = new Sample(features);
		list.add(sample);
		list.add(sample2);
		
		ArrayList<Sample> copiedList = classifier.copySampleList(list);
		copiedList.get(0).setFeatures(features);
		
		// first list item 0 feature 0 should still be true
		assertEquals(true, list.get(0).getFeature(0));
		assertEquals(false, copiedList.get(0).getFeature(0));
		
	}
	
	// testing if subList requires a copy before being executed (it does)
	@org.junit.Test
	public void testSubList() throws Exception{
		
		ArrayList<Sample> list = new ArrayList<Sample>();
		Boolean[] features = {false, false, true};
		Sample sample2 = new Sample(features);
		list.add(sample);
		list.add(sample2);
		
		ArrayList<Sample> copiedList = classifier.copySampleList(list);
		List<Sample> removedList = copiedList.subList(0, 1);
		list.subList(0, 1).clear();
		
		// first list item 0 feature 0 should still be true
		assertEquals(true, removedList.get(0).getFeature(0));
		assertEquals(false, list.get(0).getFeature(0));
		
	}
	
	@org.junit.Test
	public void testBranchSorting() throws Exception{
		
		ArrayList<WeightedBranch> list = new ArrayList<WeightedBranch>();
		WeightedBranch b1 = new WeightedBranch(1,2,19);
		WeightedBranch b2 = new WeightedBranch(2,3,25);
		WeightedBranch b3 = new WeightedBranch(3,4,22);
		list.add(b1);
		list.add(b2);
		list.add(b3);
		
		Collections.sort(list, Collections.reverseOrder(new WeightedBranchComparator()));
		
		assertEquals(b3, list.get(1));
		assertEquals(b1, list.get(2));
		
		
	}
	
	@org.junit.Test 
	public void testArrayListContains() throws Exception{
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(1);
		assertEquals(true, list.contains(1));
		
	}
	
	@org.junit.Test 
	public void testCSVInput() throws Exception{
		
		double[][] array = classifier.readFile();
		assertEquals(560, array[177][13], 0);
		
	}
	
	@org.junit.After
	public void tearDown() throws Exception {
		
	}
}
