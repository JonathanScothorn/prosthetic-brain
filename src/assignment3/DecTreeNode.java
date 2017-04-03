package assignment3;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class DecTreeNode {
	
	private DecTreeNode parent;
	private int feature;
	private int depth;
	private Boolean hasChildren;
	private DecTreeNode[] children;
	private ArrayList<Sample> class1SamplesWithFeature0;
	private ArrayList<Sample> class2SamplesWithFeature0;
	private ArrayList<Sample> class1SamplesWithFeature1;
	private ArrayList<Sample> class2SamplesWithFeature1;
	private ArrayList<Integer> featuresToTest;
	
	public DecTreeNode(int feature, ArrayList<Sample> c1samples0, ArrayList<Sample> c2samples0, 
			ArrayList<Sample> c1samples1, ArrayList<Sample> c2samples1, ArrayList<Integer> featuresToTest){
		this(null, feature, c1samples0, c2samples0, c1samples1, c2samples1, featuresToTest);
	}
	
	public DecTreeNode(DecTreeNode parent, int feature, ArrayList<Sample> c1samples0, ArrayList<Sample> c2samples0, 
			ArrayList<Sample> c1samples1, ArrayList<Sample> c2samples1, ArrayList<Integer> featuresToTest){
		this.parent = parent;
		this.feature = feature;
		this.class1SamplesWithFeature0 = c1samples0;
		this.class2SamplesWithFeature0 = c2samples0;
		this.class1SamplesWithFeature1 = c1samples1;
		this.class2SamplesWithFeature1 = c2samples1;
		this.featuresToTest = featuresToTest;
		children = new DecTreeNode[2];
		children[0] = null;
		children[1] = null;
		hasChildren = false;
		
		depth = 0;
		DecTreeNode tempParent = parent;
		
		while(tempParent != null){
			depth++;
			tempParent = tempParent.getParent();
		}
	}
	
	public DecTreeNode getParent() {
		return parent;
	}
	
	public DecTreeNode[] getChildren() {
		return children;
	}

	public void addChild(DecTreeNode child, boolean is0){
		if(is0){
			children[0] = child;		
		} else {
			children[1] = child;
		}
		hasChildren = true;
	}
	
	public Boolean hasChildren(){
		return hasChildren;
	}
	
	public ArrayList<Sample> getClass1SamplesWithFeature0() {
		return class1SamplesWithFeature0;
	}
	
	public ArrayList<Sample> getClass2SamplesWithFeature0() {
		return class2SamplesWithFeature0;
	}
	
	public ArrayList<Sample> getClass1SamplesWithFeature1() {
		return class1SamplesWithFeature1;
	}
	
	public ArrayList<Sample> getClass2SamplesWithFeature1() {
		return class2SamplesWithFeature1;
	}

	public ArrayList<Integer> getFeaturesToTest() {
		return featuresToTest;
	}

	public int getFeature() {
		return feature;
	}

	
	@Override
	public String toString(){
		return ""+feature;
	}
	
	public String printAll(){
		String output = "";
		
		for(int i=0; i<depth; i++){
			output+=" ";
		}
		
		output += "Decision tree node with depth "+depth+" for feature "+feature+"\n";
		
		if(children[0] != null){
			output += children[0].toString();
		}
		if(children[1] != null){
			output += children[1].toString();
		}
	
		
		return output;
	}

	public ArrayList<Sample> getClass2Samples() {
		return class2SamplesWithFeature0;
	}

	
    public void printTree(OutputStreamWriter out) throws IOException {
        if (children[1] != null) {
            children[1].printTree(out, true, "");
        }
        printNodeValue(out);
        if (children[0] != null) {
            children[0].printTree(out, false, "");
        }
    }
    private void printNodeValue(OutputStreamWriter out) throws IOException {
        out.write(""+feature);
        out.write('\n');
    }
    // use string and not stringbuffer on purpose as we need to change the indent at each recursion
    private void printTree(OutputStreamWriter out, boolean isRight, String indent) throws IOException {
        if (children[1] != null) {
            children[1].printTree(out, true, indent + (isRight ? "        " : " |      "));
        }
        out.write(indent);
        if (isRight) {
            out.write(" /");
        } else {
            out.write(" \\");
        }
        out.write("----- ");
        printNodeValue(out);
        if (children[0] != null) {
            children[0].printTree(out, false, indent + (isRight ? " |      " : "        "));
        }
    }
}
