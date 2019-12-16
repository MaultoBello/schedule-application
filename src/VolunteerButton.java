import javafx.scene.control.Button;

public class VolunteerButton extends Button {
		Volunteer vol;
		
		int day;
		int time;
		int spot;
		
		public VolunteerButton(Volunteer vol, int day, int time, int spot) {
			this.vol = vol;
			this.day = day;
			this.time = time;
			this.spot = spot;
		}
		
		public void switchVolunteer(VolunteerButton toSwitch) {
			Volunteer temp = this.vol;
			this.vol = toSwitch.vol;
			toSwitch.vol = temp;
		}
		
		public Volunteer getVolunteer() {
			return (vol == null) ? null : new Volunteer(vol);
		}
		
		public int getDay() {
			return day;
		}
		
		public int getTime() {
			return time;
		}
		
		public int getSpot() {
			return spot;
		}
}
