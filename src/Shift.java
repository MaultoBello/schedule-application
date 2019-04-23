
public class Shift {
	
	private Volunteer[] pair;
	
	public Shift() {
		pair = new Volunteer[2];
	}
	
	public Shift(Volunteer vol) {
		this();
		pair[0] = new Volunteer(vol);
	}
	
	public Shift(Volunteer vol1, Volunteer vol2) {		// [FIX THIS] Are all/any of these constructors actually used?
		this();
		pair[0] = new Volunteer(vol1);
		pair[1] = new Volunteer(vol1);
	}
	
	public Volunteer[] getVolunteers() {
		Volunteer[] copy = new Volunteer[2];
		
		if(pair[0] != null) {
			copy[0] = new Volunteer(pair[0]);
		}
		
		if(pair[1] != null) {
			copy[1] = new Volunteer(pair[1]);
		}
		return copy;
	}
	
	public void register(Volunteer vol) {
		if (isFull()) {								// [FIX THIS] Is this how the error should be dealt with? Try returning -1 when a shift could not be scheduled instead.
			System.out.println("ERROR: TRYING TO SCHEDULE ON A FULL SHIFT");
			System.exit(-1);
		}
		if (pair[0] == null) {
			pair[0] = new Volunteer(vol);
		} else if (pair[1] == null) {
			pair[1] = new Volunteer(vol);
		}	
	}
	
	public boolean isFull() {
		return pair[0] != null && pair[1] != null;
	}
	
	public boolean isEmpty() {
		return pair[0] == null && pair[1] == null;
	}
	
	public void unschedule() {
		pair[0] = null;
		pair[1] = null;
	}
	
}
