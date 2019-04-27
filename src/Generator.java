import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Generator {
	
	// The start and end times of the schedule (in 24-hour format)
	private final int start = 8;
	private final int end = 16;

	private final int numOfSched = 20;
	
	// A collection of volunteer class schedules
	private ArrayList<Volunteer> timeTables;
	
	// An array to hold the current population of schedules
	private Schedule[] population;
	
	/**
	 * Creates a new generator object with an empty schedule
	 * and no volunteers
	 */
	public Generator() {
		population = new Schedule[numOfSched];
		for(int i = 0; i < numOfSched; ++i) {
			population[i] = new Schedule(this);
		}
		timeTables = new ArrayList<Volunteer>();
	}
	
	/**
	 * Adds a volunteer to the generator's database with a binary
	 * code that corresponds to the volunteers weekly availability
	 * @param name	the name of the volunteer being added
	 * @param code	a binary code corresponding to the volunteer's availability
	 * @return	the Generator object
	 */
	public Generator addTimeTable(String name, String code) {
		Volunteer vol = new Volunteer(name, code, this);
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
	 * @param hour	the hour that is being modified
	 * @param avail	boolean value corresponding to whether or not the volunteer is available at the specified hour
	 */
	public void setAvailability(int index, int day, int hour, boolean avail) {
		timeTables.get(index).setAvailability(day, hour, avail);
	}
	
	/**
	 * Loads saved volunteer time tables from a file
	 */
	public void loadTimeTables() {
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader("testData.txt"));
		} catch (IOException e) {
			System.out.println("Could not open file!");
			System.exit(-1);
		}
		String line;
		try {
			while((line = input.readLine()) != null) {
				
				// Each line is formatted as "NAME [SPACE] CODE"
				String[] data = line.split(" ");
				this.addTimeTable(data[0], data[1]);
			}	
		} catch (IOException e) {
			System.out.println("Could not read file!");
			System.exit(-1);
		}
	}
	
	/**
	 * Generates the shift schedule
	 * @return	the Generator object
	 */
	public Generator generateSchedule() {
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < (end-start); ++j) {
				schedule[i][j][0] = null;
				schedule[i][j][1] = null;
			}
		}
		ArrayList<Volunteer> people = (ArrayList<Volunteer>)timeTables.clone();
		sortTables();
		while(people.size() > 0) {
			int first = people.get(0).getFirstAvailable(this);
			int i = first / (end-start);
			int j = first % (end-start);
			if (i >= 0 && j >= 0) {
				register(i, j, people.get(0));
				first = people.get(0).getFirstAvailable(this);
				i = first / (end-start);
				j = first % (end-start);
				if(i >= 0 && j >= 0) {
					register(i, j, people.get(0));
				}
			}
			people.remove(0);
		}
		return this;
	}
	
	public static void main(String[] args) {
		
		Generator schedule = new Generator();
		schedule.loadTimeTables();
		
		System.out.println("Welcome to \"Shifter\"");
		System.out.println("The options are: [1] create new schedule, [2] modify schedule, [3] view final schedule, [4] quit");
		System.out.println("What is your option?");
		Scanner input = new Scanner(System.in);
		int option = input.nextInt();
		
		while(true) {
			
			if (option == 1) {
				System.out.print("\n--[ CREATING NEW TIMETABLE ]--\n");
				System.out.print("Enter the name of the time table's owner: ");
				String name = input.next();
				System.out.print("Enter the time table's binary code: ");
				String code = input.next();
				schedule.addTimeTable(name, code);
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
