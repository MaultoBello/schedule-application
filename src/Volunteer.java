public class Volunteer {
	
	private String name;
	private boolean[][] availability;
	
	Generator gen;
	
	/**
	 * Creates a new volunteer with a name and a schedule of availability
	 * @param name	name of the volunteer
	 * @param code	binary code corresponding to volunteer availability
	 */
	public Volunteer(String name, String code, Generator g) {
		this.name = name;
		gen = g;
		availability = new boolean[5][gen.getEnd()-gen.getStart()];
		this.setSchedule(code);
	}
	
	/**
	 * Copy constructor for volunteer
	 * @param vol	the volunteer being copied
	 */
	public Volunteer(Volunteer vol) {
		this.name = vol.name;
		this.gen = vol.gen;
		availability = new boolean[5][gen.getEnd()-gen.getStart()];
		this.setSchedule(vol.getBinaryCode());
	}

	/**
	 * Converts a name to six letters plus a period. Used in the case
	 * that a name gets too long to display properly.
	 * @return shortened name
	 */
	public String getCutName() {
		if(name.length() <= 7) {
			return name;
		} else {
			return name.substring(0, 6) + ".";
		}
	}
	
	/**
	 * Displays the availability schedule for the volunteer
	 * @return the Volunteer object
	 */
	public Volunteer displaySchedule() {
		System.out.println();
		System.out.println("Time\t\tLunes\tMartes\tMiér.\tJueves\tViernes\n");
		for(int time = 0; time < (gen.getEnd()-gen.getStart()); ++time) {
			System.out.print(gen.getStart() + time + ":00\t\t");
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
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < (gen.getEnd()-gen.getStart()); ++j) {
				if(availability[i][j] = true) {
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
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < (gen.getEnd()-gen.getStart()); ++j) {
				if(code.charAt(i*(gen.getEnd()-gen.getStart())+j) == '1') {
					availability[i][j] = true;
				} else if(code.charAt(i*(gen.getEnd()-gen.getStart())+j) == '0') {
					availability[i][j] = false;
				}
			}
		}
		return this;
	}
	
	/**
	 * Returns the number of hours available for this volunteer in regard to a specific schedule
	 * @return	the number of hours available in relation to the provided generator schedule
	 */
	public int getAvailableHours() {
		int AvailableHours = 0;
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < (gen.getEnd()-gen.getStart()); ++j) {
				if (availability[i][j] == true && !gen.isFull(i, j)) {
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
	public int getFirstAvailable() {
		int first = -1;
		boolean found = false;
		for(int i = 0; i < 5 && !found; ++i) {
			for(int j = 0; j < (gen.getEnd()-gen.getStart()) && !found; ++j) {
				if (availability[i][j] == true && !gen.isFull(i, j)) {
					first = i*(gen.getEnd()-gen.getStart())+j;
					found = true;
				}
			}
		}
		return first;
	}
}
