package onetimetests;

public class SpeedTesting {
	private int count = 0;
	
	private int countRecursive() {
		if (count > 2000) return count;
		count++;
		return countRecursive();
	}

	private int countIterative() {
		while (count <= 2000) count++;
		return count;
	}
	
	public static void main(String[] args) {
		long starttime, endtime;
		long result = 0;
		starttime = System.currentTimeMillis();
		for (int i = 0; i< 10000; i++) result += (new SpeedTesting()).countRecursive();
		endtime = System.currentTimeMillis();
		System.out.println(endtime-starttime);

		starttime = System.currentTimeMillis();
		for (int i = 0; i< 10000; i++) result += (new SpeedTesting()).countIterative();
		endtime = System.currentTimeMillis();
		System.out.println(endtime-starttime);
		System.out.println("please ignore: " + result); // avoid optimization
	}
}
