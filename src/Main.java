import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import hsa_new.Console;

public class Main {

	// If debug mode, show debug messages such as randomly generated number, etc
	public static final boolean DEBUG_MODE = true;

	private Scanner scan = new Scanner(System.in);

	// All different locations in the game
	enum Location {
		OPERATING_ROOM, HALLWAY, BASEMENT, PARKING_LOT, CAFETERIA
	}

	// All items in the game
	enum Items {
		KEYS, BACKPACK, BADGE, GUN, FLASHLIGHT, PHONE
	}

	// Vehicles in the game
	enum Vehicles {

		 MERCEDES_BENZ(30), TRUCK(75), SCHOOL_BUS(75);

		private int successRate;

		private Vehicles(int success) {
			this.successRate = success;
		}

		private int getSuccessRate() {
			return successRate;
		}
	}

	// The current user location
	private Location currentLocation;

	// List of location and items that the user has unlocked or equipped
	private List<Location> unlockedPlaces;
	private List<Location> locationHistory;
	private List<Items> unlockedItems;

	// If the user has Steven with them
	private boolean hasSteven;
	// If user has met Steven
	private boolean userHasMetSteven = false;
	// If user has saved Steven
	private boolean saveSteven = false;

	// User vehicle
	private Vehicles userVehicle;

	// The current user input is whatever the user entered last in console
	private String userInput;

	// Doors are permanently locked if user has guessed the pin incorrectly too
	// many times
	private boolean caffParkingLotDoorsPermanentlyLocked = false;
	
	// Variable to store which vehicle the user chose
	String vehicle;

	/**
	 * The main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Console c = new Console();
		Main consoleGame = new Main();
		consoleGame.resetGame();
		consoleGame.playGame();
	}

	/**
	 * Start the game
	 */
	private void playGame() {

		stateLocation(Location.PARKING_LOT);

	}

	private void stateLocation(Location location) {

		// Set the current location to whatever the location passed is
		currentLocation = location;

		switch (location) {
		case HALLWAY:

			// If the user has never been to a hallway, show them the message
			if (!userHasBeenTo(Location.HALLWAY)) {
				showMessage("The hallway seems to be a dark and scary place.");
			}

			// This loop is going to keep happening unless the user has keys to
			// go left
			while (true) {

				// Ask user which direction they want to go to
				askQuestion("Do you want to go left or right? Choose wisely...", "left:right");

				// If user wants to go left...
				if (userInput.equals("left")) {

					showMessage("You see a dark door in front of you.");

					// Firstly, lets check if the user has keys (in the
					// backpack) to go to the cafeteria
					if (userHas(Items.KEYS)) {
						showMessage("The door is locked... so you unlock the door with the keys in your backpack.");
						unlockedPlaces.add(Location.CAFETERIA);
						stateLocation(Location.CAFETERIA);
						break;
					} else {
						// Nope, they don't have the keys...
						showMessage("It seems to be locked. You are now on your way back.");
						showMessage("There you are... right outside of the operaing room again...");
					}

				} else {
					break;
				}

			}

			// If they want to go right
			if (userInput.equals("right")) {

				// If user has not met the guy before...
				if (!userHasMetSteven) {

					showMessage(
							"You are now turning right and walking slowly as you the noises of someone crying getting closer");
					showMessage("And there is a wounded guy right in front of you.");

					askQuestion("Do you want to save the guy, or rob him and take all his stuff?", "rob:save");

					// If user want to rob the guy...
					if (userInput.equals("rob")) {

						// If the user has backpack to store robbed things in
						if (userHas(Items.BACKPACK)) {

							// now the user has met the guy
							userHasMetSteven = true;

							unlockedItems.add(Items.FLASHLIGHT);
							unlockedItems.add(Items.KEYS);

							showMessage("You have taken keys and flashlight from this man and left him to die.");

							// if user has no backpack, send them to basement
						} else {
							showMessage("This guy has things you could've taken... But you have nowhere to store it.");
							showMessage("You continue walking straight and see an old door. You slowly open it.");
							stateLocation(Location.BASEMENT);

						}

						// User wants to save this guy
					} else {

						userHasMetSteven = true;
						saveSteven = true;
						hasSteven = true;

						// Send them back to the operating room to save them
						stateLocation(Location.OPERATING_ROOM);
					}

					// User has met the guy before already
				} else {
					showMessage("There is a dark, old door in front of you.");
					showMessage("You open it slowly, after a shrilling creak you go through it...");

					// update user location to the basement
					stateLocation(Location.BASEMENT);
				}

			}

			break;

		case OPERATING_ROOM:

			// If the user has never been to the operating room
			if (!userHasBeenTo(Location.OPERATING_ROOM)) {
				showMessage("You wake up in an operating room. A bright white light emits from a lamp at the far right corner of the room.");
				locationHistory.add(Location.OPERATING_ROOM);
			}

			// If the user is in the operating room because user choose to save
			// the guy
			if (saveSteven) {
				showMessage("You are now in the operating room...");
				showMessage(
						"You seem to have saved him using medication located around the room. He seems pretty stable. He introduces himself as Steven.");
				showMessage("You are now looking around in the room...");
			}

			// If user does not have backpack or the badge, ask them which they
			// want to pick
			if (!userHas(Items.BACKPACK) && !userHas(Items.BADGE)) {

				showMessage("You see a dead body and a back pack.");
				askQuestion("Do you search the body or take the backpack?", "body:backpack");

				// If the user wants to search the body...
				if (userInput.equals("body")) {
					// Add item to the user inventory
					unlockedItems.add(Items.BADGE);
					showMessage("The body reeks of sweat and death, but after searching for 2 agonizing minutes you found a badge on the dead body.");

				} else if (userInput.equals("backpack")) {
					unlockedItems.add(Items.BACKPACK);

					showMessage("Okay, you now have the backpack. You can use this to store more stuff in...");
				}

				// If user has the backpack but no badge, ask if they want to
				// take the badge
			} else if (userHas(Items.BACKPACK) && !userHas(Items.BADGE)) {

				askQuestion("You seem to find a badge on a dead body. Do you want to carry the badge with you?",
						"yes:no");
				if (userInput.equals("yes")) {
					unlockedItems.add(Items.BADGE);
					showMessage("You now have a badge in your backpack.");
				} else {
					showMessage("Okay, no problem.");
				}

				// If user has the badge but no backpack, ask if they want to
				// take the backpack
			} else if (userHas(Items.BADGE) && !userHas(Items.BACKPACK)) {
				askQuestion("You have found a backpack... Do you want to take it?", "yes:no");
				if (userInput.equals("yes")) {
					unlockedItems.add(Items.BACKPACK);
					showMessage("You sling the backpack on to your back. A slight and subtle chill runs down your spine. You are unsure wether you feel safer or not.");
				} else {
					showMessage("Okay, no problem.");
				}
				// If the user has already collected items
			} else {
				showMessage("You do not seem to find anything in the room...");
			}

			// Get user out of the room now...
			if (userHasMetSteven) {
				showMessage("You now slowly make your way outside the hallway...");
			} else {
				showMessage("You hear a noise and run outside.");
			}

			// Update user's location to the hallway
			stateLocation(Location.HALLWAY);

			break;

		case BASEMENT:

			showMessage("The basement is a dark and cold place.");

			// If the user has a flashlight, turn it on
			if (userHas(Items.FLASHLIGHT)) {
				showMessage("Luckily, you have a flashlight in your backpack so you use it.");
			} else {
				// Since the user has no flashlight... ask if they want to turn
				// switch on
				showMessage("You feel something moving in the dark. You cannot see anything but you feel a light switch on the right side of the wall.");
				askQuestion("Do you want to turn the switch on?", "yes:no");

				// User dies after turning on the light
				if (userInput.equals("yes")) {
					showMessage("You feel massive amounts of electricity surging through your body. The loose wiring in the light switch got you electrocuted. You are killed.");
					userHasDied();
					break;
				}
			}

			showMessage(
					"There does not seem to be anything in the basement. You want to get out and you see 2 sets of doors. The first set of doors was painted a dull gray fashioned with a cold, pair of steel handles, while the second has a dried red liquid splattered across.");
			askQuestion("Do you want to go through from the first or the second doors?", "door 1:door 2");

			// Door 1 leads to the cafeteria
			if (userInput.equals("door 1")) {
				locationHistory.add(Location.CAFETERIA);

				// Door 2 leads to the parking lot
			} else {
				locationHistory.add(Location.PARKING_LOT);
				stateLocation(Location.PARKING_LOT);
			}

			break;

		case CAFETERIA:

			// If the last location is basement, show them another message
			if (lastLocation() == Location.BASEMENT) {
				showMessage("You thought it was an exit, but it seems to be an entrance to the cafeteria.");
				showMessage("Before you could go back, the doors behind you are locked.");
			}
			showMessage("You find yourself in a vast room. You see rows of tables stretching form wall to wall.");
			showMessage("You smell food somewhere, your stomach grumbles in response.");
			showMessage("You go towards the scent and you notice a warm and inviting plate of dinner.");

			askQuestion("Do you eat the food?", "yes:no");

			// Eat food?
			if (userInput.equals("yes")) {
				showMessage("Yum... It seems pretty tasty...");
				showMessage("After finishing the meal, you sit up from the table. You're tounge sits uncomfortably in your mouth, an unpleasant taste is lingering there. You collapse to the floor.");
				showMessage("The food was laced with rat poison. You died");
				userHasDied();
				break;

			} else if (userInput.equals("no")) {
				showMessage("You leave the food on the table. You continue exploring.");
			}

			// Take the phone?
			showMessage("You notice a phone on the floor, it has a bright red LED on the front of the phone.");
			askQuestion("Do you want to take the phone?", "yes:no");

			if (userInput.equals("yes")) {

				// Check if user has space to store the phone
				if (userHas(Items.BACKPACK)) {
					unlockedItems.add(Items.PHONE);
					showMessage("Congratulations, you have the phone!");
				} else {
					showMessage("Sorry, you have nowhere to store the phone.");
				}

				// The user does not want the phone
			} else {
				showMessage("You leave the phone on the ground and you continue exploring the cafeteria.");
			}

			showMessage("You come across a stairway and a white double door set with paint peeling off.");
			showMessage("The stairway is labelled: BASEMENT and the door is labelled: EXIT");

			// parking lot or basement?
			askQuestion("Do you go down to the basement or through the exit?", "basement:exit");

			if (userInput.equals("basement")) {

				if (lastLocation() == Location.BASEMENT) {
					showMessage("You are now on your way back to the basement...");
				}
				stateLocation(Location.BASEMENT);
				break;

			} else if (userInput.equals("exit")) {

				if (hasSteven == true) {
					showMessage(
							"You try to open the door but it is locked. You notice that there is a number pad to the left of the door. Luckily, Steven reassures you that he knows the code, he goes towards the number pad and enters the pin.");
					showMessage("The door clicks. You push it open and a gust of cold wind greets you. ");
					unlockedPlaces.add(Location.PARKING_LOT);
					stateLocation(Location.PARKING_LOT);
					break;
				}

				// If the user doesn't have Steven, they have to guess the code
				else {
					showMessage(
							"You try to open the door but it is locked. You notice that there is a number pad to the left of the door. The 3 spaces displayed on the top of the number pad suggests that there is a 3 number pin.");

					// If the cafeteria doors to the parking lot are permanently
					// locked
					if (caffParkingLotDoorsPermanentlyLocked) {
						showMessage(
								"... However, you have already guessed too many times which caused the doors to be permanently locked.");
					} else {

						// Generate a random number
						int parkingLotDoorPin = generateRandomNumberBetween(100, 999);

						// Validate the pin
						boolean doorPinCorrect = validatePin(parkingLotDoorPin, 3, "A buzzer buzzes twice. The pin you entered is wrong.");

						// If the user guessed the pin correctly
						if (doorPinCorrect) {
							showMessage(
									"You guessed the pin correctly! The door clicks. You push it open and a gust of cold wind greets you.");
							unlockedPlaces.add(Location.PARKING_LOT);
							stateLocation(Location.PARKING_LOT);
							break;
						} else {
							// Permanently lock the parking lot doors doors
							caffParkingLotDoorsPermanentlyLocked = true;
							showMessage(
									"A long buzz erupts from the number pad. The door shivered as internal locking mechanisms permanently locked the door.");
						}
					}

				} // end if: user does not have Steven

				// Since the user can not exit the cafeteria upon guessing the
				// code incorrectly, they must now go to the basement.
				showMessage("You have no option so you take the door to the basement.");
				locationHistory.add(Location.BASEMENT);
				stateLocation(Location.BASEMENT);
				break;

			} // end if: user input equals exit

			break;

		case PARKING_LOT:

			showMessage("It's pretty dark out here. You see cars. It seems to be a parking lot.");

			// Show message saying user has an interest in cars
			showMessage(hasSteven ? "Steven seems" : "You seem" + " to have an interest in cars so you look around.");

			showMessage("You see a truck, Mercedes Benz and a school bus.");
			askQuestion("Which one do you want?", "truck:mercedes:school bus");

			String vehicleInfo;

			// set the vehicle text as whatever vehicle they chose
			if (userInput.equals("truck")) {

				userVehicle = Vehicles.TRUCK;
				vehicleInfo = "old, rusty, big truck.";

			} else if (userInput.equals("mercedes")) {

				userVehicle = Vehicles.MERCEDES_BENZ;
				vehicleInfo = "Mercedes Benz S Class 2017 Model.";

			} else {
				userVehicle = Vehicles.SCHOOL_BUS;
				vehicleInfo = "School bus.";
			}

			showMessage("Okay, you now have a " + vehicleInfo);
			showMessage("You start driving away to escape. But you see that the door is locked with a key");

			// If user picked up Steven, they can escape without any problem
			if (hasSteven) {
				showMessage("Thankfully, Steven has the key to the gate so he unlocks it.");
				userHasEscaped();
			} else {

				askQuestion(
						"Since you don't know the pin, you can either break through the gate by driving or try guessing the pin. Which one do you want?",
						"guess:drive through");

				// if user wants to drive through
				if (userInput.equals("drive through")) {

					showMessage("You start driving in full speed.");
					showMessage("As you get closer to the gate, you get nervous.");
					
					// generate a random number
					
					int randomNumber = generateRandomNumberBetween(0, 100);
					System.out.println(randomNumber);
					System.out.println(userVehicle.getSuccessRate());
					
					// randomly check if the random number is less than the success rate.
					// the higher the success rate of vehicle, the more chances it will have of going through
					if (userVehicle.getSuccessRate() >= randomNumber) {
						// user can break through
					
						showMessage("The front of the car slams the gate, breaking it open.");
						userHasEscaped();
						break;
				
					} else {
						//user cannot break through
						showMessage("Your vehicle slams the door at a high speed, killing you.");
						userHasDied();
						break;
					}
					
					// if user wants to guess pin
				} else {

					showMessage(
							"You have to guess the pin to get out of the parking lot. You see a box where you enter the pin.");
					showMessage("The pin is 3 numbers and you have 3 guesses before it permanently locks.");

					int guessedPin = generateRandomNumberBetween(100, 999);
					boolean correctGuess = validatePin(guessedPin, 3, "That is not the right pin. Try again.");

					// Check if the user guessed the right pin
					if (correctGuess) {
						showMessage("That is the right pin!");
						// If the guess is correct, the user escapes
						// successfully and the game ends
						userHasEscaped();
						break;

						// If the guess is wrong
					} else {

						showMessage("You have guessed too many times. You have no option but to drive through.");
					}

				} // end: guess pin

			} // end: has steven

			break;

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

		unlockedPlaces = new ArrayList<Location>();
		locationHistory = new ArrayList<Location>();
		unlockedItems = new ArrayList<Items>();

		currentLocation = Location.OPERATING_ROOM;

		hasSteven = false;
		userHasMetSteven = false;
		saveSteven = false;
		userVehicle = null;

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

	/**
	 * Generate a random number between a range which includes the starting and
	 * end number
	 * 
	 * @param start
	 *            Start range
	 * @param end
	 *            End range
	 * @return
	 */
	private int generateRandomNumberBetween(int start, int end) {
		int size = end - start + 1; // include the end number
		return (int) (Math.random() * size) + start;
	}

	/**
	 * Validate user input against a pin that is asked to the user
	 * 
	 * @param pin
	 * @param maxTries
	 * @param errorMessage
	 * @return
	 */
	private boolean validatePin(int pin, int maxTries, String errorMessage) {
		System.out.println(pin);
		int totalTries = 0;
		// If user has not yet reached his max try limit
		while (totalTries < maxTries) {
			int userInputPin = scan.nextInt();
			// If user has entered the right pin, we simply return
			if (userInputPin == pin) {
				return true;
			} else {
				// If user has entered an incorrect pin, show error message
				showMessage(errorMessage);
			}
			totalTries++;
		}

		return false;
	}

	/**
	 * Check if user has an item
	 * 
	 * @param item
	 * @return
	 */
	private boolean userHas(Items item) {
		return unlockedItems.contains(item);
	}

	/**
	 * Check if user has been to a location
	 * 
	 * @param location
	 * @return
	 */
	private boolean userHasBeenTo(Location location) {
		return locationHistory.contains(location);
	}

	/**
	 * This method is called once the user dies. If the user dies, reset game
	 * and ask if they want to start over.
	 */
	private void userHasDied() {
		resetGame();
		askQuestion("Do you want to start over?", "yes:no");
		if (userInput.equals("yes")) {
			playGame();
		} else {
			showMessage("Guess you will never be able to escape this building.");
		}
	}

	/**
	 * User has successfully escaped
	 */
	private void userHasEscaped() {

		// Show them a message about user escaping
		if (hasSteven) {
			showMessage("You and Steven break free successfully.");
		} else {
			showMessage("You break free and drive as fast as you can to get away from this scary place.");
		}

		askQuestion("Since you have bravely escaped, do you want to try again?", "yes:nope");

		if (userInput.equals("yes")) {
			resetGame();
			playGame();
		} else {
			showMessage("Okay, thanks for playing.");
		}
	}

	/**
	 * Return the last location of the user
	 * 
	 * @return
	 */
	private Location lastLocation() {
		return locationHistory.get(locationHistory.size() - 1);
	}
}