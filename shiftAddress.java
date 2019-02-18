
public class shiftAddress {
	private int day = 0;
	private int time = Generator.start;
	
	shiftAddress(int d, int t) {
		setDay(d);
		setTime(t);
	}
	
	int getTime() {
		return time;
	}
	
	void setTime(int t) {
		if (!(t < Generator.start || t > Generator.end)) {
			time = t;
		}
	}
	
	int getDay() {
		return day;
	}
	
	void setDay(int d) {
		if (!(d < 0 || d > 4)) {
			day = d;
		}
	}
}
