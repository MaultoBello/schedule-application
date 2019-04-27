import java.util.ArrayList;

public class Schedule {
	
	// An array to hold assigned shifts (three-dimensional: day x hour x 2 shifts each)
	private Volunteer[][][] schedule;
	
	// An ArrayList to hold volunteers that are currently unscheduled
	private ArrayList<Volunteer> unscheduled;
	
	Generator gen;
	
	public Schedule(Generator g) {
		schedule = new Volunteer[5][gen.getEnd()-gen.getStart()][2];
	}
	
	public Schedule(Schedule s) {
		this.unscheduled = s.getUnscheduled();
		this.schedule = s.getSchedule();
	}
	
	private boolean isMax() {
		for(int i = 0; i < unscheduled.size(); ++i) {
			if()
		}
	}
	
	public Schedule createInitial() {
		
	}
	
	public Volunteer[] volsAtPos(int day, int hour) {
		Volunteer[] toReturn = new Volunteer[2];
		if (!(schedule[day][hour][0] == null)) {
			toReturn[0] = new Volunteer(schedule[day][hour][0]);
			if (!(schedule[day][hour][1] == null)) {
				toReturn[1] = new Volunteer(schedule[day][hour][1]);
			}
		}
		return toReturn;
	}
	
	public ArrayList<Volunteer> getUnscheduled() {
		ArrayList<Volunteer> toReturn = new ArrayList<Volunteer>();
		for(int i = 0; i < unscheduled.size(); ++i) {
			toReturn.add(new Volunteer(unscheduled.get(i)));
		}
		return toReturn;
	}
	
	public Volunteer[][][] getSchedule() {
		Volunteer[][][] toReturn = new Volunteer[5][gen.getEnd()-gen.getStart()][2];
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < gen.getEnd()-gen.getStart(); ++j) {
				toReturn[i][j][0] = new Volunteer(schedule[i][j][0]);
				toReturn[i][j][1] = new Volunteer(schedule[i][j][1]);
			}
		}
		return toReturn;
	}

	public boolean isFull(int day, int hour) {
		return schedule[day][hour][0] != null && schedule[day][hour][1] != null;
	}
	
	public Schedule register(int day, int hour, Volunteer vol) {
		if (!isFull(day, hour)) {
			if (schedule[day][hour][0] == null) {
				schedule[day][hour][0] = new Volunteer(vol);
			} else {
				schedule[day][hour][1] = new Volunteer(vol);
			}
		}
		return this;
	}

	public Schedule sortTables() {	
		for(int i = 1; i < unscheduled.size(); ++i) {
			Volunteer toCompare = unscheduled.get(i);
			int j = i - 1;
			while(j >= 0 && unscheduled.get(j).getAvailableHours(this) > toCompare.getAvailableHours(this)) {
				unscheduled.set(j+1, unscheduled.get(j));
				j--;
			}
			unscheduled.set(j+1, toCompare);
		}
		return this;
	}
	
	public Schedule displaySchedule() {
		System.out.println();
		System.out.println("Time\t\tLunes\t\tMartes\t\tMiércoles\tJueves\t\tViernes\n");
		for(int time = 0; time < (gen.getEnd()-gen.getStart()); ++time) {
			System.out.print(gen.getStart() + time + ":00\t\t");
			for(int day = 0; day < 5; ++day) {
				String spot1 = (schedule[day][time][0] != null) ? schedule[day][time][0].getCutName() : "EMPTY";
				String spot2 = (schedule[day][time][1] != null) ? schedule[day][time][1].getCutName() : "EMPTY";
				System.out.print(spot1 + "|" +  spot2 + "\t");
				
			}
			System.out.println();
		}
		System.out.println();
		return this;
	}
	
	
}
