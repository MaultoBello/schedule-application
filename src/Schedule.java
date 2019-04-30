import java.util.ArrayList;

public class Schedule {
	
	private final float mutationRate = 0.5f;
	
	// An array to hold assigned shifts (three-dimensional: day x hour x 2 shifts each)
	private Volunteer[][][] schedule;
	
	// An ArrayList to hold volunteers that are currently unscheduled
	private ArrayList<Volunteer> unscheduled;
	
	public Schedule(ArrayList<Volunteer> volunteers) {
		schedule = new Volunteer[5][Generator.getEnd()-Generator.getStart()][2];
		unscheduled = new ArrayList<Volunteer>();
		for(int i = 0; i < volunteers.size(); ++i) {
			unscheduled.add(new Volunteer(volunteers.get(i)));
		}
		createInitial();
	}
	
	public Schedule(Schedule s) {
		this.unscheduled = s.getUnscheduled();
		this.schedule = s.getSchedule();
	}
	
	private boolean scheduleCanFitMore() {
		for(int volIndex = 0; volIndex < unscheduled.size(); ++volIndex) {
			if(unscheduled.get(volIndex).getFirstAvailable(this) != -1) return true;
		}
		return false;
	}
	
	public Schedule createInitial() {
		boolean needsSorting = true;
		
		// the current index for the volunteer within "unscheduled" that is being checked for
		int currentIndex = 0;
		
		while(scheduleCanFitMore()) {
			
			if (needsSorting) sortTables();
			needsSorting = false;
			
			Volunteer current = unscheduled.get(currentIndex);
			
			while(current.getNumOfShifts() < 2) {
				
				// The first available time of the person with the least available hours remaining
				int first = current.getFirstAvailable(this);
				
				// A value of -1 indicates that the individual does not have any available hours remaining
				if (first > -1) {
					
					int day = first / (Generator.getEnd() - Generator.getStart());
					int hour = first % (Generator.getEnd() - Generator.getStart());
					
					// If the registering resulted in a shift filling up, then the available hours for volunteers might have potentially changed, so sort the list again
					boolean isFilled = register(day, hour, current);
					if(isFilled) needsSorting = true;
					
				} else {
					
					// otherwise update the index and move on to the next person in "unscheduled"
					currentIndex++;
					break;
				}	
				
				if(current.getNumOfShifts() == 2) unscheduled.remove(currentIndex);
			}
		}
		
		mutate();
	
		return this;
	}
	
	private int firstEmptySpot() {
		int first = -1;
		boolean found = false;
		for(int day = 0; day < 5 && !found; ++day) {
			for(int hour = 0; hour < Generator.getEnd()-Generator.getStart() && !found; ++hour) {
				if (schedule[day][hour][0] == null || schedule[day][hour][1] == null) {
					first = day*(Generator.getEnd()-Generator.getStart())+hour;
					found = true;
				}
			}
		}
		return first;
	}
	
	private Schedule fitIfPossible() {
		for(int volIndex = 0; volIndex < unscheduled.size(); ++volIndex) {
			boolean swapped = false;
			for(int day = 0; day < 5 && !swapped; day++) {
				for(int hour = 0; hour < Generator.getEnd() - Generator.getStart() && !swapped; ++hour) {
					if (unscheduled.get(volIndex).isAvailable(day,  hour)) {
						int emptyDay = firstEmptySpot() / (Generator.getEnd()-Generator.getStart());
						int emptyHour = firstEmptySpot() % (Generator.getEnd()-Generator.getStart());
						int emptySpot = (schedule[emptyDay][emptyHour] == null) ? 0 : 1;
						if(schedule[day][hour][0] != null && schedule[day][hour][0].isAvailable(emptyDay, emptyHour)) {
							swapped = swap(day, hour, 0, emptyDay, emptyHour, emptySpot);
						} else if(schedule[day][hour][1] != null && schedule[day][hour][1].isAvailable(emptyDay, emptyHour)) {
							swapped = swap(day, hour, 0, emptyDay, emptyHour, emptySpot);
						}
					}
				}
			}
		}
		return this;
	}
	
	// Right now you say vols < 2, but at some point in the future, you're going to have 
	public Schedule mutate() {
		for(int day = 0; day < 5; ++day) {
			for(int hour = 0; hour < Generator.getEnd()-Generator.getStart(); ++hour) {
				for(int vol = 0; vol < 2; ++vol)
				if(Math.random() < mutationRate) {
					
					int dayToSwap = (int)Math.floor(Math.random()*5);
					
					int hourToSwap = (int)Math.floor(Math.random()*(Generator.getEnd()-Generator.getStart()));
					
					int volToSwap = (int)Math.floor(Math.random()*2);
					
					// picking either the first part of the schedule (first volunteer in each shift) or second part (second volunteer in each shift)
					swap(day, hour, vol, dayToSwap, hourToSwap, volToSwap);
				}
			}
		}
		
		fitIfPossible();
		return this;
	}
	
	private boolean areSwapable(int d1, int h1, Volunteer v1, int d2, int h2, Volunteer v2) {
		if(v1 != null && v2 != null) return v1.isAvailable(d2, h2) && v2.isAvailable(d1, h2);
		
		// if either or both of the volunteers are null, then they can obviously be swapped without consideration for availability
		else return true;
	}
	
	/**
	 * @param d1	the day of the first shift being swapped
	 * @param h1	the hour of the first shift being swapped
	 * @param index1	the index of the volunteer being swapped in the first shift (first volunteer, second volunteer, etc.)
	 * @param d2	the day of the second shift being swapped
	 * @param h2	the hour of the second shift being swapped
	 * @param index2	the index of the volunteer being swapped in the second shift (first volunteer, second volunteer, etc.)
	 * @return	a boolean value corresponding to whether or not the swap was successful
	 */
	private boolean swap(int d1, int h1, int index1, int d2, int h2, int index2) {
		if(areSwapable(d1, h1, schedule[d1][h1][index1], d2, h2, schedule[d2][h2][index2])) {
			Volunteer temp = schedule[d1][h1][index1];
			schedule[d1][h1][index1] = schedule[d2][h2][index2];
			schedule[d2][h2][index2] = temp;
		} else return false;
		return true;
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
		Volunteer[][][] toReturn = new Volunteer[5][Generator.getEnd()-Generator.getStart()][2];
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < Generator.getEnd()-Generator.getStart(); ++j) {
				toReturn[i][j][0] = (schedule[i][j][0] == null) ? null : new Volunteer(schedule[i][j][0]);
				toReturn[i][j][1] = (schedule[i][j][1] == null) ? null : new Volunteer(schedule[i][j][1]);
			}
		}
		return toReturn;
	}

	public int numOfVols(int day, int hour) {
		int num = 0;
		for(int volIndex = 0; volIndex < schedule[day][hour].length; ++volIndex) {
			if (schedule[day][hour][volIndex] != null) num++;
		}
		return num;
	}
	
	/** registers a volunteer in a given time slot on the schedule
	 * @param day	the day that is being registered for
	 * @param hour	the hour that is being registered for
	 * @param vol	the volunteer that is being registered
	 * @return	whether or not the registering resulted in a shift filling up
	 */
	public boolean register(int day, int hour, Volunteer vol) {
		if (numOfVols(day, hour) < 2) {
			if (schedule[day][hour][0] == null) {
				schedule[day][hour][0] = vol;
			} else {
				schedule[day][hour][1] = vol;
			}
			vol.updateShiftNum(1);
		}
		return numOfVols(day, hour) == 2;
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
		System.out.println("Time\t\tLunes\t\t\tMartes\t\t\tMiércoles\t\tJueves\t\t\tViernes\n");
		for(int time = 0; time < (Generator.getEnd()-Generator.getStart()); ++time) {
			System.out.print(Generator.getStart() + time + ":00\t\t");
			for(int day = 0; day < 5; ++day) {
				String spot1 = (schedule[day][time][0] != null) ? schedule[day][time][0].getCutName() : "EMPTY";
				String spot2 = (schedule[day][time][1] != null) ? schedule[day][time][1].getCutName() : "EMPTY";
				System.out.print(spot1 + "\t| " +  spot2 + "\t\t");
				
			}
			System.out.println();
		}
		System.out.println();
		return this;
	}	
	
	public static void main(String[] args) {
		Generator gen = new Generator();
		gen.loadTimeTables();
		gen.generatePopulation();
		
		Schedule[] population = gen.getPopulation();
		for(int i = 0; i < population.length; ++i) {
			population[i].displaySchedule();
		}
		
		
	}
	
}
