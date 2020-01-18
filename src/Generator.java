import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javafx.scene.control.ProgressBar;

// Purpose: to generate, store, mutate, and evolve a population
public class Generator {

	private final int populationSize = 20;

	private Schedule bestStored;
	
	// A collection of volunteer class schedules
	private ArrayList<Volunteer> timeTables;
	
	// An array to hold the current population of schedules
	private Schedule[] population;
	
	/**
	 * Creates a new generator object with an empty schedule
	 * and no volunteers
	 */
	public Generator() {
		population = new Schedule[populationSize];
		timeTables = new ArrayList<Volunteer>();
	}
	
	// FIXME Memory leak
	public ArrayList<Volunteer> getTimeTables() {
		return timeTables;
	}
	
	public Generator generatePopulation() {
		for(int i = 0; i < populationSize; ++i) {
			population[i] = new Schedule(timeTables);
		}
		storeBest();
		return this;
	}
	
	public Generator evolvePopulation() {
		
		// Population is sorted by best # of consecutive shifts, so all children are offspring of population[0]
		for(int popIndex = 1; popIndex < population.length; ++popIndex) {
			population[popIndex] = new Schedule(population[0]);
			population[popIndex].mutate();
		}
		storeBest();
		return this;
	}
	
	private void storeBest() {
		Arrays.sort(population, Comparator.comparing(Schedule::numOfConsecutives).reversed());
		bestStored = population[0];
	}
	
	public Schedule getBest() {
		return bestStored;
	}
	
	public Schedule[] getPopulation() {
		Schedule[] toReturn = new Schedule[populationSize];
		for(int popIndex = 0; popIndex < populationSize; ++popIndex) {
			toReturn[popIndex] = new Schedule(population[popIndex]);
		}
		return toReturn;
	}
	
	/**
	 * Adds a volunteer to the generator's database with a binary
	 * code that corresponds to the volunteers weekly availability
	 * @param name	the name of the volunteer being added
	 * @param code	a binary code corresponding to the volunteer's availability
	 * @return	the Generator object
	 */
	public Generator addTimeTable(String name, String lastname, String code) {
		Volunteer vol = new Volunteer(name, lastname, code);
		timeTables.add(vol);
		return this;
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
	
	// FIXME Make it more open to possible file format mismatches
	// FIXME Have it display errors for when binary code is not 35 digits
	/**
	 * Loads saved volunteer time tables from a file
	 */
	public void loadTimeTables() {
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader("src/testData.txt"));
		} catch (IOException e) {
			System.out.println("Could not open file!");
			System.exit(-1);
		}
		String line;
		try {
			while((line = input.readLine()) != null) {
				
				// Each line is formatted as "NAME [SPACE] SURNAME [SPACE] CODE"
				String[] data = line.split(" ");
				this.addTimeTable(data[0], data[1], data[2]);
			}	
		} catch (IOException e) {
			System.out.println("Could not read file!");
			System.exit(-1);
		}
	}
}
