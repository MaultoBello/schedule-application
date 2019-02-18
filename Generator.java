import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Generator {
	
	public static int start = 8;
	public static int end = 16;
	private ArrayList<Volunteer> schedules;
	private Shift[][] shiftTimes;
	
	public Generator() {
		shiftTimes = new Shift[5][end-start];
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < (end-start); ++j) {
				shiftTimes[i][j] = new Shift();
			}
		}
		schedules = new ArrayList<Volunteer>();
	}
	
	public Volunteer addSchedule(String name) {
		Volunteer vol = new Volunteer(name);
		schedules.add(vol);
		return vol;
	}
	
	public Volunteer addSchedule(String name, String code) {
		Volunteer vol = new Volunteer(name);
		vol.setSchedule(code);
		schedules.add(vol);
		return vol;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public void setAvailability(int index, int day, int hour, boolean Availability) {
		schedules.get(index).setAvailability(day, hour, Availability);
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
	
	// You're using bubblesort; please use something else for efficiency
	// You're using the word "schedule" for a lot of different things here. Please reconsider.
	public void sortSchedules(ArrayList<Volunteer> toSort) {
		boolean isSorted = false;
		while(!isSorted) {
			isSorted = true;
			for(int i = 0; i < toSort.size()-1; ++i) {
				if (toSort.get(i).getAvailableHours(shiftTimes) > toSort.get(i+1).getAvailableHours(shiftTimes)) {
					isSorted = false;
					Volunteer temp = toSort.get(i);
					toSort.set(i, toSort.get(i+1));
					toSort.set(i+1, temp);
				}
			}
		}
	}
	
	public void generateSchedule() {
		for(int i = 0; i < 5; ++i) {
			for(int j = 0; j < (end-start); ++j) {
				shiftTimes[i][j].unschedule();
			}
		}
		ArrayList<Volunteer> people = (ArrayList<Volunteer>)schedules.clone(); // [FIX THIS] That's a weird warning there
		sortSchedules(people);
		while(people.size() > 0) {
			int first = people.get(0).getFirstAvailable(shiftTimes);
			int i = first / (end-start);
			int j = first % (end-start);
			if (i >= 0 && j >= 0) {
				shiftTimes[i][j].register(people.get(0));
				first = people.get(0).getFirstAvailable(shiftTimes);
				i = first / (end-start);
				j = first % (end-start);
				if(i >= 0 && j >= 0) {
					shiftTimes[i][j].register(people.get(0));
				}
			}
			people.remove(0);
		}
	}
	
	public void displaySchedule() {
		System.out.println();
		System.out.println("Time\t\tLunes\t\tMartes\t\tMiércoles\tJueves\t\tViernes\n");
		for(int time = 0; time < (end-start); ++time) {
			System.out.print(start + time + ":00\t\t");
			for(int day = 0; day < 5; ++day) {
				Volunteer[] vols = shiftTimes[day][time].getVolunteers();
				String spot1 = (vols[0] != null) ? vols[0].getCutName() : "EMPTY";
				String spot2 = (vols[1] != null) ? vols[1].getCutName() : "EMPTY";
				System.out.print(spot1 + "|" +  spot2 + "\t");
				
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		
		Generator schedule = new Generator();
		schedule.loadSchedule();
		
		for(int i = 0; i < schedule.schedules.size(); ++i) {
			System.out.println(schedule.schedules.get(i).getName());
			schedule.schedules.get(i).displaySchedule();
			ArrayList<shiftAddress> twoGs = twoGroups.getTwoGroups(schedule.schedules.get(i));
			for (int j = 0; j < twoGs.size(); ++j) {
				System.out.println(twoGs.get(j).getDay() + " " + twoGs.get(j).getTime());
			}
		}
		
		/*System.out.println("Welcome to \"Shifter\"");
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
				Volunteer table = schedule.addSchedule(name, code);
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
		
		input.close();*/
	}
}
