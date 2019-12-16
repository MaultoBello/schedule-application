import java.io.File;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

public class GUI extends Application {
	
	// ----- Figure out how to implement a progress bar (involves multithreading)
	// ---- clean up the code and make it maintainable
	
	Button startButton;
	Button saveButton;
	Button loadButton;
	Label consecutiveCount;
	Label loadingLabel;
	GridPane layout;
	Schedule storedSched;
	
	GridPane scheduleDisplay;
	VolunteerButton[][][] volButtons;
	
	boolean isSwitchMode = false;
	
	// You're currently figuring out a way to implement switching
	VolunteerButton toSwitch;
	ArrayList<VolunteerButton> switchables;
	
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Schedule App (Demo)");
		
		layout = new GridPane();
		layout.setPadding(new Insets(10, 10, 10, 10));
		layout.setMinSize(300, 300);
		layout.setVgap(15);
		layout.setHgap(15);
		
		HBox taskBar = new HBox();
		taskBar.setSpacing(10);
		taskBar.setAlignment(Pos.CENTER_LEFT);
		layout.add(taskBar,0, 0 );
		
		consecutiveCount = new Label();
		taskBar.getChildren().add(consecutiveCount);
		
		startButton = new Button();
		startButton.setText("Generate New Population");
		startButton.setOnAction(this::initiateGeneration);
		taskBar.getChildren().add(startButton);
		
		FileChooser chooser = new FileChooser();
		
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("schedule files (*.sch)", "*.sch");
		chooser.getExtensionFilters().add(extFilter);
		
		saveButton = new Button();
		saveButton.setText("Save File");
		saveButton.setOnAction(e -> {
            File selectedFile = chooser.showSaveDialog(primaryStage);
            if (selectedFile != null) {
            	VolunteerFileIO.writeSchedule(storedSched, selectedFile);
            }
        });
		taskBar.getChildren().add(saveButton);
		
		loadButton = new Button();
		loadButton.setText("Load File");
		loadButton.setOnAction(e -> {
            File selectedFile = chooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
            	storedSched = VolunteerFileIO.loadSchedule(selectedFile);
            	updateDisplay();
            }
        });
		taskBar.getChildren().add(loadButton);
		
		loadingLabel = new Label();
		taskBar.getChildren().add(loadingLabel);
		
		scheduleDisplay = new GridPane();
		scheduleDisplay.setVgap(20);
		scheduleDisplay.setHgap(20);
		layout.add(scheduleDisplay, 0, 1);
		
		volButtons = new VolunteerButton[5][Generator.getShiftsInDay()][2];

		Scene scene = new Scene(layout, 300, 250);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private void initiateGeneration(ActionEvent event) {
		
		System.out.println("Starting Generation");
		
		Generator gen = new Generator();
		gen.loadTimeTables();
		gen.generatePopulation();
		Schedule[] population = gen.getPopulation();
		
		Schedule best = gen.computeBest();
		int staticCounter = 0;
		// TODO update this number as required
		while (staticCounter < 50) {
			double ratio =  (Double.valueOf(gen.computeBest().numOfConsecutives()))/40;
			System.out.println(ratio);
			gen.evolvePopulation();
			if(best.numOfConsecutives() == gen.computeBest().numOfConsecutives()) staticCounter++;
			else {
				best = gen.computeBest();
				staticCounter = 0;
			}
		}
		
		storedSched = gen.getPopulation()[0];
		updateDisplay();
			
		System.out.println("/////////////////////////////////////////////");
		
		population = gen.getPopulation();
		for(int i = 0; i < population.length; ++i) {
			population[i].displaySchedule();
			System.out.println(population[i].numOfConsecutives());
		}
		
	}
	
	private void blankDisplay() {
		scheduleDisplay.getChildren().clear();
		scheduleDisplay.add(new Label("Monday"), 1, 0);
		scheduleDisplay.add(new Label("Tuesday"), 2, 0);
		scheduleDisplay.add(new Label("Wednesday"), 3, 0);
		scheduleDisplay.add(new Label("Thursday"), 4, 0);
		scheduleDisplay.add(new Label("Friday"), 5, 0);
		
		scheduleDisplay.add(new Label("8:30 - 10:00"), 0, 1);
		scheduleDisplay.add(new Label("10:00 - 11:00"), 0, 2);
		scheduleDisplay.add(new Label("11:00 - 12:00"), 0, 3);
		scheduleDisplay.add(new Label("12:00 - 1:00"), 0, 4);
		scheduleDisplay.add(new Label("1:00 - 2:00"), 0, 5);
		scheduleDisplay.add(new Label("2:00 - 3:00"), 0, 6);
		scheduleDisplay.add(new Label("3:00 - 4:30"), 0, 7);
		
		for(int time = 0; time < (Generator.getShiftsInDay()); ++time) {
			for(int day = 0; day < 5; ++day) {
				volButtons[day][time][0] = null;
				volButtons[day][time][1] = null;
			}
		}
	}
	
	private void normalizeButtons() {
		for(int time = 0; time < (Generator.getShiftsInDay()); ++time) {
			for(int day = 0; day < 5; ++day) {
				if (volButtons[day][time][0].getVolunteer() == null) {
					volButtons[day][time][0].setStyle("-fx-base: #07b7ed");
				} else {
					volButtons[day][time][0].setStyle("-fx-base: rgba(236, 236, 236, 1);");
				}
				if (volButtons[day][time][1].getVolunteer() == null) {
					volButtons[day][time][1].setStyle("-fx-base: #07b7ed");
				} else {
					volButtons[day][time][1].setStyle("-fx-base: rgba(236, 236, 236, 1);");
				}

			}
		}
	}
	
	private void darkenButtons() {
		int day = toSwitch.getDay();
		int time = toSwitch.getTime();
		int spot = toSwitch.getSpot();
		for(int timeIndex = 0; timeIndex < (Generator.getShiftsInDay()); ++timeIndex) {
			for(int dayIndex = 0; dayIndex < 5; ++dayIndex) {
					VolunteerButton volBut1 = volButtons[dayIndex][timeIndex][0];
					VolunteerButton volBut2 = volButtons[dayIndex][timeIndex][1];
					if ((timeIndex == time) && (dayIndex == day)) {
						if (spot == 0) {
							volBut1.setStyle("-fx-base: #81cf15;");
							volBut2.setStyle("-fx-base: rgba(150, 150, 150, 1);");
						} else {
							volBut1.setStyle("-fx-base: rgba(150, 150, 150, 1);");
							volBut2.setStyle("-fx-base: #81cf15;");
						}
					} else {
						switchables = new ArrayList<VolunteerButton>();
						if (storedSched.areSwapable(dayIndex, timeIndex, volBut1.getVolunteer(), day, time, toSwitch.getVolunteer()))  {
							switchables.add(volBut1);
						} else {
							volBut1.setStyle("-fx-base: rgba(150, 150, 150, 1);");
						}
						if (storedSched.areSwapable(dayIndex, timeIndex, volBut2.getVolunteer(), day, time, toSwitch.getVolunteer()))  {
							switchables.add(volBut2);
						} else {
							volBut2.setStyle("-fx-base: rgba(150, 150, 150, 1);");
						}
					}
			}
		}
	}
	
	private void toggleSwitchMode(VolunteerButton volBut) {
		if (isSwitchMode) {
			boolean swapped = storedSched.swap(volBut.getDay(), volBut.getTime(), volBut.getSpot(), toSwitch.getDay(), toSwitch.getTime(), toSwitch.getSpot());
			if(swapped) updateDisplay();
			normalizeButtons();
			isSwitchMode = false;
		} else {
			if (volBut.getVolunteer() != null) {
				toSwitch = volBut;
				darkenButtons();
				isSwitchMode = true;
			}
		}
	}
	
	private void updateDisplay() {
		Volunteer[][][] toDisplay = storedSched.getSchedule();
		blankDisplay();
		for(int time = 0; time < (Generator.getShiftsInDay()); ++time) {
			for(int day = 0; day < 5; ++day) {
				
				Volunteer vol1 = toDisplay[day][time][0];
				VolunteerButton spot1 = new VolunteerButton(vol1, day, time, 0);
				spot1.setText((vol1 != null) ? vol1.getName() : "EMPTY");
				spot1.setOnAction(new EventHandler<ActionEvent>() {
				    @Override public void handle(ActionEvent e) {
				        toggleSwitchMode(spot1);
				    }
				});
				volButtons[day][time][0] = spot1;
				
				Volunteer vol2 = toDisplay[day][time][1];
				VolunteerButton spot2 = new VolunteerButton(vol2, day, time, 1);
				spot2.setText((vol2 != null) ? vol2.getName()  : "EMPTY");
				spot2.setOnAction(new EventHandler<ActionEvent>() {
				    @Override public void handle(ActionEvent e) {
				        toggleSwitchMode(spot2);
				    }
				});
				volButtons[day][time][1] = spot2;
				
				VBox shift = new VBox();
				shift.getChildren().addAll(spot1, spot2);
				scheduleDisplay.add(shift, day+1, time+1);
				
			}
		}
		consecutiveCount.setText("# of Consecutives: " + storedSched.numOfConsecutives());
		normalizeButtons();
	}
	
	public static void main(String[] args) {
		
		launch(args);
		
	}
	
}
