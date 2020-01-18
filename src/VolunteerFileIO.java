import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class VolunteerFileIO {
	
	public static void writeSchedule(Schedule sched, File schedFile) {
		Volunteer[][][] scheduleArr = sched.getSchedule();
		try {
			FileOutputStream fileOut = new FileOutputStream(schedFile);
			ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
			for(int dayIndex = 0; dayIndex < 5; ++dayIndex) {
				for(int timeIndex = 0; timeIndex < Schedule.getShiftsInDay(); ++timeIndex) {
					objOut.writeObject(scheduleArr[dayIndex][timeIndex][0]);
					objOut.writeObject(scheduleArr[dayIndex][timeIndex][1]);
				}
			}
			objOut.writeObject(sched.getUnscheduled());
			objOut.close();
			fileOut.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Error initializing output stream");
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Schedule loadSchedule(File schedFile) {
		Volunteer[][][] scheduleArr = new Volunteer[5][Schedule.getShiftsInDay()][2];
		ArrayList<Volunteer> unscheduledArrList = new ArrayList<Volunteer>();
		try {
			FileInputStream fileIn = new FileInputStream(schedFile);
			ObjectInputStream objIn = new ObjectInputStream(fileIn);
			for(int dayIndex = 0; dayIndex < 5; ++dayIndex) {
				for(int timeIndex = 0; timeIndex < Schedule.getShiftsInDay(); ++timeIndex) {
					scheduleArr[dayIndex][timeIndex][0] = (Volunteer) objIn.readObject();
					scheduleArr[dayIndex][timeIndex][1] = (Volunteer) objIn.readObject();
				}
			}
			unscheduledArrList = (ArrayList<Volunteer>) objIn.readObject();
			objIn.close();
			fileIn.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Error initializing input stream");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Schedule(scheduleArr, unscheduledArrList);
	} 
}
