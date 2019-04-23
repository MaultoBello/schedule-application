
public class Volunteer {
	
	private String name;
	private int start = Generator.start;
	private int end = Generator.end;
	private boolean[][] availability;
	
	public Volunteer(String name) {
		this.name = name;
		availability = new boolean[5][end-start];
		for(int i = 0; i < 5; ++i) {
			for (int j = 0; j < (end-start); ++j) {
				availability[i][j] = false;
			}
		}
	}
	
	public Volunteer(Volunteer vol) {
		this.name = vol.name;
		availability = new boolean[5][end-start];
		this.setSchedule(vol.getBinaryCode());
	}
	
	public String getCutName() {
		if(name.length() <= 7) {
			return name;
		} else {
			return name.substring(0, 6) + ".";
		}
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public void displaySchedule() {
		System.out.println();
		System.out.println("Time\t\tLunes\tMartes\tMiér.\tJueves\tViernes\n");
		for(int time = 0; time < (end-start); ++time) {
			System.out.print(start + time + ":00\t\t");
			for(int day = 0; day < 5; ++day) {
				System.out.print(availability[day][time]+ "\t");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public String getName() {
		return name;
	}
	
	// Do you want it to be time as the parameter or distance from start? Be consistent
	boolean isAvailable(int day, int time) {
		return availability[day][time-Generator.start];
	}
	
	public void setAvailability(int day, int time) {
		availability[day][time] = true;
	}
	
	public void setAvailability(int day, int time, boolean value) {
		availability[day][time] = value;
	}
	
	public String getBinaryCode() {
		String code = "";
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < (end-start); ++j) {
				if(availability[i][j] = true) {
					code += "1";
				} else {
					code += "0";
				}
			}
		}
		return code;
	}
	
	public void setSchedule(String code) {
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < (end-start); ++j) {
				if(code.charAt(i*(end-start)+j) == '1') {
					availability[i][j] = true;
				} else if(code.charAt(i*(end-start)+j) == '0') {
					availability[i][j] = false;
				}
			}
		}
	}
	
	public int getAvailableHours(Shift[][] schedule) {
		int AvailableHours = 0;
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < (end-start); ++j) {
				if (availability[i][j] == true && !schedule[i][j].isFull()) {
					++AvailableHours;
				}
			}
		}
		return AvailableHours;
	}
	
	public int getFirstAvailable(Shift[][] schedule) {
		int first = -1;
		boolean found = false;
		for(int i = 0; i < 5 && !found; ++i) {
			for(int j = 0; j < (end-start) && !found; ++j) {
				if (availability[i][j] == true && !schedule[i][j].isFull()) {
					first = i*(end-start)+j;
					found = true;
				}
			}
		}
		return first;
	}
}
