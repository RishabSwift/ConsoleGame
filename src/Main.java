import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    private boolean isDead;
    private String items[] = new String[]{"keys", "backpack", "badge", "gun"};
    private boolean hasGuy;
    private Scanner scan = new Scanner(System.in);

    enum Location {OPERATING_ROOM, HALLWAY, BASEMENT, PARKING_LOT}

    private Location currentLocation;
    private ArrayList<Location> unlockedPlaces;
    private ArrayList<Location> locationHistory;
    private Location unlockedLocations[] = new Location[Location.values().length];
    // The current user input is whatever the user entered last in console
    private String userInput;

    public static void main(String[] args) {
        Main consoleGame =new Main();
        consoleGame.resetGame();
        consoleGame.playGame();
    }

    private void playGame() {
        askQuestion("Are you gay?", "yes:yeah");
        if (userInput.equals("yes")) {
            showMessage("No problem. Everyone is...");
        } else {
            showMessage("Ah. ok.");
        }

        askQuestion("Where do you wanna go?", "china:africa:fucking");
        if (userInput.equals("africa")) {
            showMessage("Fuck, every one loves africa!");
        } else {
            showMessage("Shit! I love china. fucking chinese condoms is teh reason i have a baby");
        }
    }

    /**
     * Show user a message. Instead of
     * @param message
     */
    private void showMessage(String message) {
        System.out.println(message);
    }

//    private void showMessage(String message, Image image) {
//
//    }

    private void resetGame() {
        this.currentLocation = Location.OPERATING_ROOM;
        this.unlockedLocations = null;
        this.locationHistory = null;
        this.hasGuy = false;
    }

    private void setUnlockedItems(String itemName) {

    }

    /**
     * Used to ask question to user.
     * Keep asking until user has entered an acceptable answer
     *
     * @param question        the question to ask user
     * @param acceptedAnswers list of accepted answers separated by colon ":"
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
     * @param userInput
     * @param acceptedInput
     * @return
     */
    private String validateInput(String userInput, String acceptedInput) {

        String message = "";

        // split the accept answer into an acceptable array
        String[] acceptedInputArray = acceptedInput.split(":");

        // Check if the user's input equals to the accepted input by converting it to a List first
        List acceptedList = Arrays.asList(acceptedInputArray);
         if (!acceptedList.contains(userInput)) {
             message = "Opps! You must either type \"" + acceptedInput.replace(":", "\" or \"") + "\". Please try again.";
        }

        return message;
    }
}
