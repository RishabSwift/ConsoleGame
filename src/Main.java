import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

	private boolean isDead;

	enum Items {
		KEYS, BACKPACK, BADGE, GUN, FLASHLIGHT
	}

	private ArrayList<Items> unlockedItems;
	// Item list
	private boolean hasGuy;
	private boolean choice; // if they rob or save the guy
	boolean backpack = false; // backpack
	private boolean left = false; // if guy went left in the hallway
	private Scanner scan = new Scanner(System.in);

	//
	enum Location {
		OPERATING_ROOM, HALLWAY, BASEMENT, PARKING_LOT, CAF
	}

	private Location currentLocation;
	private List<Location> unlockedPlaces;
	private List<Location> locationHistory;
	private Location unlockedLocations[] = new Location[Location.values().length];
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
		// in the hospital room
		// either get badge or back pack
		if (userInput.equals("body")) {
			unlockedItems.add(Items.BADGE);
			showMessage("Congradulations, you unlock the badge");
		} else if (userInput.equals("backpack")) {
			unlockedItems.add(Items.BACKPACK);
			showMessage("Congradulations, you unlock the backpack");
			backpack = true;
		}

		while (true) {
			// in hallway with guy
			if (hasGuy && left == false) {
				showMessage("The guy tells you that you need to leave.");
				showMessage("You run into the hallway");
			} else if (left == false) {
				unlockedPlaces.add(Location.HALLWAY);
				showMessage("You hear a noice and run into the hallway.");
			}
			currentLocation = Location.HALLWAY;
			// in the hallway
			askQuestion("Do you go left or right?", "right:left");
			if (userInput.equals("right") || left) {
				// determine if character has already met the guy
				if (choice) {
					// user has already robed or killed the guy
					showMessage("You see a staircase and go down them and arrive in the basemnt");
					unlockedPlaces.add(Location.BASEMENT);
					currentLocation = Location.BASEMENT;
					// now you are in the basement
					break;
				} else {
					showMessage("You hear someone crying and continue down the hall");
					showMessage("You come across a guy who is hurt");
					askQuestion("Do you save him?", "yes:no");
					choice = true;
					// decide to save or rob the guy
					if (userInput.equals("yes")) {
						// save guy
						showMessage("You carry the guy to the operating room");
						currentLocation = Location.OPERATING_ROOM;
						showMessage(
								"Congratulations, you save the guy. He happends to know his way arround the hospital");
						hasGuy = true;
						showMessage("You are back in the operating room.");
						String n; // determine if they have back pack or badge
						// ask them if they want the backpack or badge
						if (backpack) {
							n = "badge";
						} else {
							n = "backpack";
						}
						askQuestion("Do you want to take the " + n + " ", "yes:no");
						if (n.equals("badge") && userInput.equals("yes")) {
							unlockedItems.add(Items.BADGE);
							showMessage("Congradulations you have unlocked the badge");
							// unlocked badge
						} else if (n.equals("backpack") && userInput.equals("yes")) {
							unlockedItems.add(Items.BACKPACK);
							showMessage("Congradulation you have unlocked the backpack");
							// unlocked backpack
						} else {
							showMessage("Ok, you didn't unlock anything");
						}
					}

					else {
						// rob guy

						if (backpack) {
							showMessage("You found keys and a flashlight.");
							showMessage("Let's put them in your backpack, they might be important.");
							unlockedItems.add(Items.KEYS);
							unlockedItems.add(Items.FLASHLIGHT);
							// unlocked flashlight and keys, stored in backpack
						} else {
							showMessage("You don't have the room to store any more items");
						}

						break;
					}
				}

			}

			else {
				// go left
				left = true;
				if (backpack) {
					// in caf
					break;
				} else {
					showMessage("You don't have the keys to the cafateria. You have to go back");
				}

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
//		this.unlockedLocations = ;
		this.locationHistory = null;
		this.hasGuy = false;
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
