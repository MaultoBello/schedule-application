import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Generator {
	
	// The start and end times of the schedule (in 24-hour format)
	public static int start = 8;
	public static int end = 16;
	
	// A collection of volunteer class schedules
	private ArrayList<Volunteer> timeTables;
	
	// An array to hold the final shift schedule
	private Shift[][] schedule;
	
	public Generator() {
		schedule = new Shift[5][end-start];
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < (end-start); ++j) {
				schedule[i][j] = new Shift();
			}
		}
		timeTables = new ArrayList<Volunteer>();
	}
	
	/**
	 * @param name	name of the volunteer being added
	 * @return	the Generator object
	 */
	public Generator addSchedule(String name) {
		Volunteer vol = new Volunteer(name);
		timeTables.add(vol);
		return this;
	}
	
	/**
	 * @param name	the name of the volunteer being added
	 * @param code	a binary code corresponding to the volunteer's availability
	 * @return	the Generator object
	 */
	public Generator addSchedule(String name, String code) {
		Volunteer vol = new Volunteer(name);
		vol.setSchedule(code);
		timeTables.add(vol);
		return this;
	}
	
	/**
	 * @return	the daily schedule start time (a positive integer 0 - 24)
	 */
	public int getStart() {
		return start;
	}
	
	/**
	 * @return	the daily schedule end time (a positive integer 0 - 24)
	 */
	public int getEnd() {
		return end;
	}
	
	
	/**
	 * Sets the availability of a certain volunteer at a certain hour
	 * @param index	the index number of the specified volunteer
	 * @param day	the day that is being modified
	 * @param hour	the hour that is being modified (0 - 24, must be in range of daily shift times)
	 * @param avail	boolean value corresponding to whether or not the volunteer is available at the specified hour
	 */
	public void setAvailability(int index, int day, int hour, boolean avail) {
		timeTables.get(index).setAvailability(day, hour, avail);
	}
	
	public void loadSchedule() {
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader("testData.txt"));
		} catch (IOException e) {
			System.out.println("Could not open file!");
			System.exit(-1);
		}
		String current;
		try {
			while((current = input.readLine()) != null) {
				String[] data = current.split(" ");
				this.addSchedule(data[0], data[1]);
			}	
		} catch (IOException e) {
			System.out.println("Could not read file!");
			System.exit(-1);
		}
	}
	
	public Generator sortTables() {	
		for(int i = 1; i < timeTables.size();++i) {
			Volunteer toCompare = timeTables.get(i);
			int j = i - 1;
			while(j >= 0 && timeTables.get(j).getAvailableHours(schedule) > toCompare.getAvailableHours(schedule)) {
				timeTables.set(j+1, timeTables.get(j));
				j--;
			}
			timeTables.set(j+1, toCompare);
		}
		for(int i = 0; i < timeTables.size(); ++i) {
			System.out.println(timeTables.get(i).getAvailableHours(schedule));
		}
		return this;
	}
	
	public Generator generateSchedule() {
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < (end-start); ++j) {
				schedule[i][j].unschedule();
			}
		}
		ArrayList<Volunteer> people = (ArrayList<Volunteer>)timeTables.clone();
		sortTables();
		while(people.size() > 0) {
			int first = people.get(0).getFirstAvailable(schedule);
			int i = first / (end-start);
			int j = first % (end-start);
			if (i >= 0 && j >= 0) {
				schedule[i][j].register(people.get(0));
				first = people.get(0).getFirstAvailable(schedule);
				i = first / (end-start);
				j = first % (end-start);
				if(i >= 0 && j >= 0) {
					schedule[i][j].register(people.get(0));
				}
			}
			people.remove(0);
		}
		return this;
	}
	
	public Generator displaySchedule() {
		System.out.println();
		System.out.println("Time\t\tLunes\t\tMartes\t\tMiércoles\tJueves\t\tViernes\n");
		for(int time = 0; time < (end-start); ++time) {
			System.out.print(start + time + ":00\t\t");
			for(int day = 0; day < 5; ++day) {
				Volunteer[] vols = schedule[day][time].getVolunteers();
				String spot1 = (vols[0] != null) ? vols[0].getCutName() : "EMPTY";
				String spot2 = (vols[1] != null) ? vols[1].getCutName() : "EMPTY";
				System.out.print(spot1 + "|" +  spot2 + "\t");
				
			}
			System.out.println();
		}
		System.out.println();
		return this;
	}
	
	public static void main(String[] args) {
		
		Generator schedule = new Generator();
		schedule.loadSchedule();
		
		System.out.println("Welcome to \"Shifter\"");
		System.out.println("The options are: [1] create new schedule, [2] modify schedule, [3] view final schedule, [4] quit");
		System.out.println("What is your option?");
		Scanner input = new Scanner(System.in);
		int option = input.nextInt();
		
		while(true) {
			
			if (option == 1) {
				System.out.print("\n--[ CREATING NEW SCHEDULE ]--\n");
				System.out.print("Enter the name of the schedule's owner: ");
				String name = input.next();
				System.out.print("Enter the name of the schedule's binary code: ");
				String code = input.next();
				schedule.addSchedule(name, code).;
				table.displaySchedule();
				System.out.println();
			} else if (option == 2) {
				System.out.print("\n--[ MODIFYING SCHEDULE ]--\n");
				System.out.print("Enter the number of the schedule you want to change: ");
				int picked = input.nextInt();
				System.out.print("\nEnter the day you want to change: ");
				int pickedDay = input.nextInt();
				System.out.print("\nEnter the hour you want to change: ");
				int pickedHour = input.nextInt()-schedule.getStart();
				System.out.print("\nEnter 0 for unAvailable and something else for Available: ");
				int Available = input.nextInt();
				if (Available == 0) {
					schedule.setAvailability(picked, pickedDay, pickedHour, false);
				} else {
					schedule.setAvailability(picked, pickedDay, pickedHour, true);	
				}
				System.out.println();
			} else if (option == 3) {
				System.out.print("\n--[ VIEWING FINAL SCHEDULE ]--\n");
				schedule.generateSchedule();
				schedule.displaySchedule();
			} else if (option == 4) {
				System.out.println("Have a great day!");
				break;
			}
			
			System.out.println("The options are: [1] create new schedule, [2] modify schedule, [3] view final schedule, [4] quit");
			System.out.println("What is your option?");
			option = input.nextInt();
		}
		
		input.close();
	}
}
