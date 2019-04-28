public class Volunteer {
	
	private String name;
	private String surname;
	
	// A boolean array containing the hourly availability of the volunteer
	private boolean[][] availability;
	
	// The number of shifts the volunteer is currently scheduled in
	private int numOfShifts;
	
	/**
	 * Creates a new volunteer with a name and a schedule of availability
	 * @param name	name of the volunteer
	 * @param code	binary code corresponding to volunteer availability
	 */
	public Volunteer(String name, String surname, String code) {
		this.name = name;
		this.surname = surname;
		availability = new boolean[5][Generator.getEnd()-Generator.getStart()];
		this.setSchedule(code);
	}
	
	public Volunteer updateShiftNum(int increment) {
		if (numOfShifts + increment > 0) {
			numOfShifts += increment;
		}
		return this;
	}
	
	public int getNumOfShifts() {
		return numOfShifts;
	}
	
	public boolean isSame(Volunteer vol) {
		return vol.name == name && vol.surname == surname;
	}
	
	/**
	 * Copy constructor for volunteer
	 * @param vol	the volunteer being copied
	 */
	public Volunteer(Volunteer vol) {
		this.name = vol.name;
		this.surname = vol.surname;
		availability = new boolean[5][Generator.getEnd()-Generator.getStart()];
		this.setSchedule(vol.getBinaryCode());
	}

	/**
	 * Converts a name to six letters plus a period. Used in the case
	 * that a name gets too long to display properly.
	 * @return shortened name
	 */
	public String getCutName() {
		if(name.length() <= 5) {
			return name;
		} else {
			return name.substring(0, 4) + ".";
		}
	}
	
	/**
	 * Displays the availability schedule for the volunteer
	 * @return the Volunteer object
	 */
	public Volunteer displaySchedule() {
		System.out.println();
		System.out.println("Time\t\tLunes\tMartes\tMiér.\tJueves\tViernes\n");
		for(int time = 0; time < (Generator.getEnd()-Generator.getStart()); ++time) {
			System.out.print(Generator.getStart() + time + ":00\t\t");
			for(int day = 0; day < 5; ++day) {
				System.out.print(availability[day][time]+ "\t");
			}
			System.out.println();
		}
		System.out.println();
		return this;
	}
	
	/**
	 * @return volunteer's name
	 */
	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}

	/**
	 * @param day	the day being tested for
	 * @param time	the time (hour) being tested for
	 * @return	whether or not the volunteer is available at that hour
	 */
	boolean isAvailable(int day, int time) {
		return availability[day][time];
	}
	
	/**
	 * Changes the availability at a specific time
	 * @param day	the day being modified
	 * @param time	the hour being modified
	 * @param value
	 * @return	the Volunteer object
	 */
	public Volunteer setAvailability(int day, int time, boolean value) {
		availability[day][time] = value;
		return this;
	}
	
	/**
	 * Returns the volunteer's availability as a series of 0's (unavailable) and 1's (available)
	 * @return a binary code corresponding to the availability of the volunteer
	 */
	public String getBinaryCode() {
		String code = "";
		for(int day = 0; day < 5; ++day) {
			for(int hour = 0; hour < (Generator.getEnd()-Generator.getStart()); ++hour) {
				if(availability[day][hour] == true) {
					code += "1";
				} else {
					code += "0";
				}
			}
		}
		return code;
	}
	
	/**
	 * Changes the volunteer's availability schedule
	 * @param code	binary code corresponding to new volunteer availability schedule
	 * @return	the Volunteer object
	 */
	public Volunteer setSchedule(String code) {
		for(int day = 0; day < 5; ++day) {
			for(int hour = 0; hour < (Generator.getEnd()-Generator.getStart()); ++hour) {
				if(code.charAt(day*(Generator.getEnd()-Generator.getStart())+hour) == '1') {
					availability[day][hour] = true;
				} else if(code.charAt(day*(Generator.getEnd()-Generator.getStart())+hour) == '0') {
					availability[day][hour] = false;
				}
			}
		}
		return this;
	}
	
	/**
	 * Returns the number of hours available for this volunteer in regard to a specific schedule
	 * @return	the number of hours available in relation to the provided generator schedule
	 */
	public int getAvailableHours(Schedule sched) {
		int AvailableHours = 0;
		for(int day = 0; day < 5; ++day) {
			for(int hour = 0; hour < (Generator.getEnd()-Generator.getStart()); ++hour) {
				if (availability[day][hour] == true && sched.numOfVols(day, hour) < 2) {
					++AvailableHours;
				}
			}
		}
		return AvailableHours;
	}
	
	/**
	 * Returns the first available hour in relation to a specific schedule
	 * @return	a number corresponding to the position of the first available hour
	 */
	public int getFirstAvailable(Schedule sched) {
		int first = -1;
		boolean found = false;
		for(int day = 0; day < 5 && !found; ++day) {
			for(int hour = 0; hour < (Generator.getEnd()-Generator.getStart()) && !found; ++hour) {
				if (availability[day][hour] == true && sched.numOfVols(day, hour) < 2) {
					Volunteer[] vols = sched.volsAtPos(day, hour);
					if(!(vols[0] == null)) {
						if(vols[0].isSame(this)) {
							continue;
						}
					}
					first = day*(Generator.getEnd()-Generator.getStart())+hour;
					found = true;
				}
			}
		}
		return first;
	}
}
