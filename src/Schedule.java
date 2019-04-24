import java.util.ArrayList;

public class Schedule {
	
	// An array to hold assigned shifts (three-dimensional: day x hour x 2 shifts each)
	private Volunteer[][][] schedule;
	
	// An ArrayList to hold volunteers that are currently unscheduled
	private ArrayList<Volunteer> unscheduled;
	
	Generator gen;
	
	Schedule(Generator g) {
		schedule = new Volunteer[5][gen.getEnd()-gen.getStart()][2];
	}
	
	
}
