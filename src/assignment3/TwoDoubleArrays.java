package assignment3;

// simple class to encapsulate 2 double arrays so that they can both be passed back by a method return.
public class TwoDoubleArrays {

	private double[] array1;
	private double[] array2;
	
	public TwoDoubleArrays(double[] array1, double[] array2){
		this.setArray1(array1);
		this.setArray2(array2);
	}

	public double[] getArray1() {
		return array1;
	}

	public void setArray1(double[] array1) {
		this.array1 = array1;
	}

	public double[] getArray2() {
		return array2;
	}

	public void setArray2(double[] array2) {
		this.array2 = array2;
	}
	
}
