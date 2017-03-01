import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

	private boolean isDead;

	enum Items {
		KEYS, BACKPACK, BADGE, GUN
	}

	private ArrayList<Items> unlockedItems;
	// Item list
	private boolean hasGuy;
	private Scanner scan = new Scanner(System.in);

	//
	enum Location {
		OPERATING_ROOM, HALLWAY, BASEMENT, PARKING_LOT
	}

	private Location currentLocation;
	private ArrayList<Location> unlockedPlaces;
	private ArrayList<Location> locationHistory;
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

		if (userInput.equals("body")) {
			unlockedItems.add(Items.BADGE);
			showMessage("Congradulations, you unlock the badge");
		} else if (userInput.equals("backpack")) {
			unlockedItems.add(Items.BACKPACK);
			showMessage("Congradulations, you unlock the backpack");

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
		} else if (userInput.equals("left")) {

		}

		// askQuestion("Where do you wanna go?", "china:africa:");
		// if (userInput.equals("africa")) {
		// showMessage("Every one loves africa!");
		// } else {
		// showMessage("I love china. Chinese food is the
		// reason I live");
		// }
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
		this.unlockedLocations = null;
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
