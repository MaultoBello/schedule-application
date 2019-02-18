import java.util.ArrayList;

public class twoGroups {
	
	static public ArrayList<shiftAddress> getTwoGroups(Volunteer vol) {
		ArrayList<shiftAddress> twoGroups = new ArrayList<shiftAddress>();
		for (int i = 0; i < 5; ++i) {
			for (int j = Generator.start; j < Generator.end-1; ++j) {
				if (vol.isAvailable(i, j) && vol.isAvailable(i, j+1)) {
					twoGroups.add(new shiftAddress(i, j));
				}
			}
		}
		return twoGroups;
	}
}
