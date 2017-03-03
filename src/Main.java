import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

	private boolean isDead;
	private boolean cafDoorPermanentlyLocked = false;

	enum Items {
		KEYS, BACKPACK, BADGE, GUN, PHONE
	}

	private ArrayList<Items> unlockedItems;
	// Item list
	private boolean hasGuy;
	private Scanner scan = new Scanner(System.in);

	//
	enum Location {
		OPERATING_ROOM, HALLWAY, BASEMENT, PARKING_LOT, CAFETERIA
	}

	private Location currentLocation;
	private ArrayList<Location> unlockedPlaces;
	private ArrayList<Location> locationHistory;

	// LocationList

	// The current user input is whatever the user entered last in console
	private String userInput;

	public static void main(String[] args) {
		Main consoleGame = new Main();
		consoleGame.resetGame();
		consoleGame.playGame();
	}

	private void playGame() {
		showMessage("You wake up in an operating room. You see a dead body and a back pack");
		askQuestion("Do you search the body or take the backpack?", "body:backpack");

		if (userInput.equals("body")) {
			unlockedItems.add(Items.BADGE);
			showMessage("Congratulations, you unlock the badge");
		} else if (userInput.equals("backpack")) {
			unlockedItems.add(Items.BACKPACK);
			showMessage("Congratulations, you unlock the backpack");

		}
		// in the hospital room
		// either get badge or back pack

		showMessage("You hear a noice and run into the hallway.");
		askQuestion("Do you go left or right?", "right:left");
		if (userInput.equals("right:")) {
			if (hasGuy) {// determine if character has already met th

			} else {
				showMessage("You hear ");

			}
		}
		// THIS IS WHERE THE CAF STARTS
		else if (userInput.equals("left")) {
			//checks if user has backpack or not
			if (true) {
				unlockedPlaces.add(Location.CAFETERIA);
				currentLocation = Location.CAFETERIA;
				showMessage("You find yourself in front of a door. Above it, a white sign with black font reads: CAFETERIA You check your backpack and see that you have a key labeled cafeteria. You enter inside.");
				showMessage("You smell food somewhere, you're stomcah grumbles in response. You go towards the scent and you notice a warm and inviting plate of dinner.");
				//eat food?
				askQuestion("Do you eat the food?", "yes:no");

				if (userInput.equals("yes")) {
					showMessage("You die cuz of food poisoning lmao.");
					isDead = true;
				} else if (userInput.equals("no")) {
					showMessage("You leave the food on the table. you continue exploring.");
				}
				
				showMessage("You notice a phone on the floor, it has a bright red LED on the front of the phone.");
				//take phone?
				askQuestion("Do you want to take the phone?", "yes:no");

				if (userInput.equals("yes")) {
					//do you have the backpack to store your cellphone?
					if (true){
					unlockedItems.add(Items.PHONE);
					showMessage("Congratulations, you unlock the phone!");
					}
					//you don't have space lmao
					else {
						showMessage("Sorry, you do not have any where to put the phone.");
					}
				}
				
				 else if (userInput.equals("no")) {
					showMessage("You leave the phone on the ground and you continue exploring the cafeteria.");
				}
				showMessage("You come across a stairway and a white double door set with paint peeling off. The stairway is labelled: BASEMENT and the door is labelled: EXIT");
				//parking lot or basement?
				askQuestion("Do you go down to the basement or through the exit?", "basement:exit");
				
				if (userInput.equals("basement")){
					currentLocation = Location.BASEMENT;
				}
				else if (userInput.equals("exit"));{
					if (hasGuy == true){
						showMessage("You try to open the door but it is locked. You notice that there is a number pad to the left of the door. Luckily THEGUY (lets figure out his name lmao) reassures you that he knows the code, he goes towards the number pad and enters the pin.");
						showMessage("The door clicks. You push it open and a gust of cold wind greets you. You look around and notice that you are in the parking lot of the hospital.");
						unlockedPlaces.add(Location.PARKING_LOT);
						currentLocation = Location.PARKING_LOT;
					}
					else {
						showMessage("You try to open the door but it is locked. You notice that there is a number pad to the left of the door. The 3 spaces displayed on the top of the number pad suggests that there is a 3 number pin.");
						//this is where the rng method is called
						int parkingLotDoorPin = 420; //this number is rng
						
						for (int i = 0; i <= 3; i++){
							showMessage("Enter the 3 digit pin:");
							int parkingLotDoorPinUserInput = scan.nextInt();
							if (parkingLotDoorPin == parkingLotDoorPinUserInput){
								showMessage("You guessed the pin correctly! The door clicks. You push it open and a gust of cold wind greets you. You look around and notice that you are in the parking lot of the hospital.");
								unlockedPlaces.add(Location.PARKING_LOT);
								currentLocation = Location.PARKING_LOT;
								i = 4;
							}
							else if (parkingLotDoorPin != parkingLotDoorPinUserInput){
								showMessage("A buzzer buzzes twice. The pin you entered is wrong");
								if (i == 4){
									cafDoorPermanentlyLocked = true;
								}
								showMessage("A long buzz erupts from the number pad. The door shivered as internal locking mechanisms revved into work.");
							}
						}
						
						
					}
				}
				
				
				
				
			} 
			//can't go in the cafeteria because no backpack
		else {
				showMessage(
						"You find yourself in front of a door. Above it, a white sign with black font reads: CAFETERIA You try to open the door, but it is locked. There is no other way but to return to the hallway behind you.");
				currentLocation = Location.HALLWAY;

		}
		}
		}

	

	/**
	 * Show user a message. Instead of
	 * 
	 * @param message
	 */
	private void showMessage(String message) {
		System.out.println(message);
	}

	/**
	 * Reset the game and set all variables to their default
	 */
	private void resetGame() {
		this.currentLocation = Location.OPERATING_ROOM;
		this.locationHistory = null;
		this.hasGuy = false;
		this.unlockedItems = null;
	}

	private void setUnlockedItems(String itemName) {

	}

	/**
	 * Used to ask question to user. Keep asking until user has entered an
	 * acceptable answer
	 *
	 * @param question
	 *            the question to ask user
	 * @param acceptedAnswers
	 *            list of accepted answers separated by colon ":"
	 * @return user's input
	 */
	private String askQuestion(String question, String acceptedAnswers) {
		String input;
		while (true) {
			System.out.println(question + " ( " + acceptedAnswers.replaceAll(":", " / ") + " )");
			// Convert user input to lower case so it's easier to parse
			input = scan.nextLine().toLowerCase();
			// Validate user's input
			String message = validateInput(input, acceptedAnswers);
			if (message.equals("")) {
				break;
			}
			System.out.println(message);
		}
		// Set the current user input to the input
		this.userInput = input;
		return input;
	}

	/**
	 * Validate the user input with the accepted input.
	 * 
	 * @param userInput
	 * @param acceptedInput
	 * @return
	 */
	private String validateInput(String userInput, String acceptedInput) {

		String message = "";

		// split the accept answer into an acceptable array
		String[] acceptedInputArray = acceptedInput.split(":");

		// Check if the user's input equals to the accepted input by converting
		// it to a List first
		List<String> acceptedList = Arrays.asList(acceptedInputArray);
		if (!acceptedList.contains(userInput)) {
			message = "Opps! You must either type \"" + acceptedInput.replace(":", "\" or \"")
					+ "\". Please try again.";
		}

		return message;
	}

}
