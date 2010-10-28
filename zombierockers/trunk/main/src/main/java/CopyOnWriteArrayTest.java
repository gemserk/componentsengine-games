import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CopyOnWriteArrayTest {
	static final int INITIAL_QUANTITY = 5000;
	private static final int ADDTESTQUANTITY = 1000;
	static int ITERATIONS = 10;
	public static void main(String[] args) {
		
		for (int i = 0; i < ITERATIONS; i++) {
			testArrayList(new ArrayList(), "TIME-ARRAYLIST: ");
			testArrayList(new CopyOnWriteArrayList(), "TIME-COPYONWRITE: ");
			testArrayList(new ArrayList(){
				@Override
				public boolean add(Object e) {
					this.clone();
					return super.add(e);
				}
			}, "TIME-MANUALCOPY: ");
		}
		
		
		
	}

	private static void testArrayList(List list, String testName) {
		addObjectsToArray(list, INITIAL_QUANTITY);
		long iniTime = System.nanoTime();
		addObjectsToArray(list, ADDTESTQUANTITY);
		long endTime = System.nanoTime();
		System.out.println(testName + (endTime - iniTime)/1000000f);
	}
	
	static void addObjectsToArray(List<Object> list, int quantity){
		for (int i = 0; i < quantity; i++) {
			list.add(new Object());			
		}
	}
	
	
	
}
