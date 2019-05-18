import java.util.ArrayList;

public class Schedule {
	
	/*      [[ TO-DO]]
	 * 
	 * 		So the biggest problem right now that you have to fix is that the intial schedule is being generated in
	 * 	such a way that one volunteer is being schedule with the same other volunteer for two shifts. This shouldn't be possible.
	 */
	
	private final float mutationRate = 0.1f;
	
	private int volsPerShift = 2;
	
	private int shiftsPerVol = 2;
	
	// An array to hold assigned shifts (three-dimensional: day x hour x 2 shifts each)
	private Volunteer[][][] schedule;
	
	// An ArrayList to hold volunteers that are currently unscheduled
	private ArrayList<Volunteer> unscheduled;
	
	public Schedule(ArrayList<Volunteer> volunteers) {
		schedule = new Volunteer[5][Generator.getEnd()-Generator.getStart()][volsPerShift];
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
	
	// Traversing the array every time might not be the best solution, takes a lot of resources
	// Can you think of something else?
	private ArrayList<Volunteer> getCoVolunteers(Volunteer vol) {
		
		// This variable keeps track of number of shifts that we checked for this volunteer
		// If the max number of shifts a volunteer can hold have been checked, there is no
		// point in traversing through the rest of the for loop, so we can stop it as a way
		// to make it more optimized
		int shiftCheckNum = 0;
		boolean maxShiftsChecked = false;
		
		ArrayList<Volunteer> toReturn = new ArrayList<Volunteer>();
		for(int day = 0; day < 5 && !maxShiftsChecked; ++day) {
			for(int hour = 0; hour < (Generator.getEnd()-Generator.getStart()) && !maxShiftsChecked; ++hour) {
				Volunteer[] vols = volsAtPos(day, hour);
				for(int volIndex = 0; volIndex < volsPerShift; ++volIndex) {
					if (vols[volIndex] != null && vols[volIndex].isSame(vol)) {
						shiftCheckNum++;
						for(int volIndex2 = 0; volIndex2 < volsPerShift; ++volIndex2) {
							if (vols[volIndex2] != null && !vols[volIndex2].isSame(vol)) {
								toReturn.add(new Volunteer(vols[volIndex]));
							}
						}
					}
				}
				if(shiftCheckNum == shiftsPerVol) {
					maxShiftsChecked = true;
				}
			}
		}
		return toReturn;
	}
	
	/**
	 * Checks if the given volunteer can be viably schedule at the given time
	 * @param day	the day being tested for
	 * @param hour	the time (hour) being tested for
	 * @param vol	the volunteer being tested
	 * @return	whether or not the given volunteer can be registered at the given time
	 */
	public boolean canRegister(int day, int hour, Volunteer vol) {
		
		// The volunteer being scheduled needs to be available at the given time
		if (vol.isAvailable(day, hour)) {
		
			ArrayList<Volunteer> currentCoVolunteers = getCoVolunteers(vol);
			Volunteer[] potentialCoVolunteers = volsAtPos(day, hour);
			
			/* This variable is used to break the loop if it is discovered that the potential
			 * spot being examined is not viable for the unscheduled volunteer to inhabit */
			boolean viable = true;
			
			for (int currentIndex = 0; currentIndex < currentCoVolunteers.size() && viable; ++currentIndex) {
				for (int potentialIndex = 0; potentialIndex < potentialCoVolunteers.length && viable; ++potentialIndex) {
					
					// If one of the covolunteers in the potential shift spot would be the same as either the unscheduled volunteer
					// or one of the unscheduled volunteer's current covolunteers, then the shift location is not viable
					if (currentCoVolunteers.get(currentIndex).isSame(potentialCoVolunteers[potentialIndex]) 
							|| vol.isSame(potentialCoVolunteers[potentialIndex])) {
						
						viable = false;
						
					}
				}
			}
			
			if (viable) {
				return true;
			}
		}
		
		return false;
		
	}
	
	// this function allows users to be scheduled next to the same person twice, don't let this happen
	private boolean scheduleCanFitMore() {
		for(int volIndex = 0; volIndex < unscheduled.size(); ++volIndex) {
			
			Volunteer currentVol = unscheduled.get(volIndex);
			int firstAvailable = currentVol.getFirstAvailable(this);
			
			if (firstAvailable != -1) {
				
				/* If the individual has a previously scheduled shift, then we need to take that into account
				 * The individual CANNOT be schedule with the same person twice, he or she must have different
				 * partners for each of his or her shifts */
				if (unscheduled.get(volIndex).getNumOfShifts() > 0) {
					
					int day = firstAvailable / (Generator.getEnd()-Generator.getStart());
					int hour = firstAvailable % (Generator.getEnd()-Generator.getStart());
					
					if (canRegister(day, hour, currentVol)) return true;
					
				} else return true;
			}
		}
		return false;
	}
	
	// NOTE TO SELF: It feels weird using a while loop here. Consider using a for loop instead
	public Schedule createInitial() {
		boolean needsSorting = true;
		
		// the current index for the volunteer within "unscheduled" that is being checked for
		int currentIndex = 0;
		
		while(scheduleCanFitMore()) {
			
			if (needsSorting) sortTables();
			needsSorting = false;
			
			Volunteer current = unscheduled.get(currentIndex);
			
			while(current.getNumOfShifts() < volsPerShift) {
				
				// The first available time of the person with the least available hours remaining
				int first = current.getFirstAvailable(this);
				
				// A value of -1 indicates that the individual does not have any available hours remaining
				if (first > -1) {
					
					int day = first / (Generator.getEnd() - Generator.getStart());
					int hour = first % (Generator.getEnd() - Generator.getStart());
					
					if(canRegister(day, hour, current)) {
					
						// If the registering resulted in a shift filling up, then the available hours for volunteers might have potentially changed, so sort the list again
						boolean isFilled = register(day, hour, current);
						if(isFilled) needsSorting = true;
					
					}
					
				} else {
					
					// otherwise update the index and move on to the next person in "unscheduled"
					currentIndex++;
					break;
				}	
				
				if(current.getNumOfShifts() == shiftsPerVol) unscheduled.remove(currentIndex);
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
				for(int spotIndex = 0; spotIndex < volsPerShift; ++spotIndex) {
					if(schedule[day][hour][spotIndex] == null) {
						first = day*(Generator.getEnd()-Generator.getStart())+hour;
					}
				}
				if (schedule[day][hour][0] == null || schedule[day][hour][1] == null) {
					first = day*(Generator.getEnd()-Generator.getStart())+hour;
					found = true;
				}
			}
		}
		return first;
	}
	
	private Schedule fitIfPossible() {
		for(int needsPlacement = 0; needsPlacement < unscheduled.size(); ++needsPlacement) {
			boolean swapped = true;
			while (unscheduled.get(needsPlacement).getNumOfShifts() < shiftsPerVol && swapped == true) {
				swapped = false;
				for(int day = 0; day < 5 && !swapped; day++) {
					for(int hour = 0; hour < Generator.getEnd() - Generator.getStart() && !swapped; ++hour) {
						if (unscheduled.get(needsPlacement).isAvailable(day, hour)) {
							int emptyDay = firstEmptySpot() / (Generator.getEnd()-Generator.getStart());
							int emptyHour = firstEmptySpot() % (Generator.getEnd()-Generator.getStart());
							int emptySpot = 0;
							for(int spotIndex = 0; spotIndex < volsPerShift; ++spotIndex) {
								if(schedule[emptyDay][emptyHour][spotIndex] == null) {
									emptySpot = spotIndex;
									break;
								}
							}
							
							for(int vol = 0; vol < volsPerShift && !swapped; ++vol) {
								if((day != emptyDay && hour != emptyHour) && (schedule[day][hour][vol] == null || 
										schedule[day][hour][vol].isAvailable(emptyDay, emptyHour))) {
									swapped = swap(day, hour, vol, emptyDay, emptyHour, emptySpot);
									if (swapped) {
										register(day, hour, unscheduled.get(needsPlacement));
									}
								}
							}
						}
					}
				}
			} 
		}
		
		int currentIndex = 0;
		
		while(unscheduled.size() > 0 && currentIndex < unscheduled.size()) {
			
			if(unscheduled.get(currentIndex).getNumOfShifts() == shiftsPerVol) {
				unscheduled.remove(currentIndex);
			} else {
				System.out.println(unscheduled.get(currentIndex).getNumOfShifts());
				System.out.println(unscheduled.get(currentIndex).getName());
				currentIndex++;
			}
		}
		
		return this;
	}
	
	// Right now you say vols < volsPerShift, but at some point in the future, you're going to have 
	public Schedule mutate() {
		for(int day = 0; day < 5; ++day) {
			for(int hour = 0; hour < Generator.getEnd()-Generator.getStart(); ++hour) {
				for(int vol = 0; vol < volsPerShift; ++vol) {
					if(Math.random() < mutationRate) {
						
						int dayToSwap = (int)Math.floor(Math.random()*5);
						
						int hourToSwap = (int)Math.floor(Math.random()*(Generator.getEnd()-Generator.getStart()));
						
						int volToSwap = (int)Math.floor(Math.random()*volsPerShift);
						
						// picking either the first part of the schedule (first volunteer in each shift) or second part (second volunteer in each shift)
						swap(day, hour, vol, dayToSwap, hourToSwap, volToSwap);
					}
				}
			}
		}
		
		if (unscheduled.size() > 0) fitIfPossible();
		return this;
	}
	
	private boolean areSwapable(int d1, int h1, Volunteer v1, int d2, int h2, Volunteer v2) {
		
		// If both are either equal or null, no point in swapping them
		if ((v1 != null && v1.isSame(v2)) || (v1 == null && v2 == null)) return false;
		
		// If either are not available in the other's spot, they cannot be swapped
		if((v1 != null && !v1.isAvailable(d2, h2)) || (v2 != null && !v2.isAvailable(d1, h1))) return false;
		
		/* If v1 is already present in the spot occupied by v2 (e.i. v1 is one of v2's co-volunteers) or vice versa, 
		 * they cannot be swapped because that would result in two of the same volunteer in one shift (physically impossible) */
		for(int spotIndex = 0; spotIndex < volsPerShift; ++spotIndex) {
			if((v1 != null && v1.isSame(schedule[d2][h2][spotIndex])) || (v2 != null && v2.isSame(schedule[d1][h1][spotIndex]))) return false;
		}
		
		// If none of the condition above are true, then the two shifts may then be logically swapped
		return true;
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
		Volunteer[] toReturn = new Volunteer[volsPerShift];
		for(int volIndex = 0; volIndex < volsPerShift; ++volIndex) {
			if(!(schedule[day][hour][volIndex] == null)) toReturn[volIndex] = new Volunteer(schedule[day][hour][volIndex]);
		}
		return toReturn;
	}
	
	public int numOfConsecutives() {
		int num = 0;
		for(int day = 0; day < 5; ++day) {
			for(int hour = 0; hour < Generator.getEnd()-Generator.getStart()-1; ++hour) {
				for(int firstIndex = 0; firstIndex < volsPerShift; ++firstIndex) {
					for(int secondIndex = 0; secondIndex < volsPerShift; ++secondIndex) {
						if(schedule[day][hour][firstIndex] == null) break;
						else if(schedule[day][hour][firstIndex].isSame(schedule[day][hour+1][secondIndex])) {
							num++;
							break;
						}
					}
				}
			}
		}
		return num;
	}
	
	public ArrayList<Volunteer> getUnscheduled() {
		ArrayList<Volunteer> toReturn = new ArrayList<Volunteer>();
		for(int i = 0; i < unscheduled.size(); ++i) {
			toReturn.add(new Volunteer(unscheduled.get(i)));
		}
		return toReturn;
	}
	
	public Volunteer[][][] getSchedule() {
		Volunteer[][][] toReturn = new Volunteer[5][Generator.getEnd()-Generator.getStart()][volsPerShift];
		for(int day = 0; day < 5; ++day) {
			for(int hour = 0; hour < Generator.getEnd()-Generator.getStart(); ++hour) {
				for(int spot = 0; spot < volsPerShift; ++spot) {
					if(!(schedule[day][hour][spot] == null)) {
						toReturn[day][hour][spot] = new Volunteer(schedule[day][hour][spot]);
					}
				}
			}
		}
		return toReturn;
	}

	public int numOfVols(int day, int hour) {
		int num = 0;
		for(int volIndex = 0; volIndex < volsPerShift; ++volIndex) {
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
		if (numOfVols(day, hour) < volsPerShift) {
			for(int volIndex = 0; volIndex < volsPerShift; ++volIndex) {
				if(schedule[day][hour][volIndex] == null) {
					schedule[day][hour][volIndex] = vol;
					vol.updateShiftNum(1);					
					break;
				}
			}
			if(vol.getNumOfShifts() == volsPerShift) {
				return true;
			}
		}
		return false;
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
		
		/*ArrayList<Volunteer> vols = gen.getTimeTables();
		for(int i = 0; i < vols.size(); ++i) {
			System.out.println(vols.get(i).getName());
			vols.get(i).displaySchedule();
		}*/
		
		Schedule[] population = gen.getPopulation();
		for(int i = 0; i < population.length; ++i) {
			population[i].displaySchedule();
			System.out.println(population[i].numOfConsecutives());
		}
		
		Schedule best = gen.computeBest();
		int staticCounter = 0;
		while (staticCounter < 1000) {
			gen.evolvePopulation();
			if(best.numOfConsecutives() == gen.computeBest().numOfConsecutives()) staticCounter++;
			else {
				best = gen.computeBest();
				staticCounter = 0;
			}
		}
			
		System.out.println("/////////////////////////////////////////////");
		
		population = gen.getPopulation();
		for(int i = 0; i < population.length; ++i) {
			population[i].displaySchedule();
			System.out.println(population[i].numOfConsecutives());
		}
		
		
	}
	
}
