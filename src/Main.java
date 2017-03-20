import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import hsa_new.Console;


/**
 * 
 * Main.java
 * This program is a console game. It is a state machine where a user can go to the original location from different locations.
 * March 01, 2017
 * @author Rishab Bhatt, Ian Ayuso, Ainslie Forbes
 *
 */
public class Main {

	// If debug mode, show debug messages such as randomly generated number, etc
	public static final boolean DEBUG_MODE = false;

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

		MERCEDES_BENZ(30), TRUCK(65), SCHOOL_BUS(85);

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

	// If the user has died
	private boolean userHasDied = false;

	// If the user has driving through
	private boolean userHasDrivenThrough = false;

	// If the user has escaped
	private boolean userHasEscaped = false;

	// Variable to store which vehicle the user chose
	String vehicle;

	// All images should have the same width/height for the best results
	private static final int IMAGE_HEIGHT = 676;
	private static final int IMAGE_WIDTH = 1200;

	private Console c = new Console(40, 150, "Exitus");


	// ALL IMAGES IN THE GAME
	private BufferedImage cafeteriaImage, cafeteriaBlurryImage, cafeteriaSecondImage, cafeteriaFoodImage,
			cafeteriaExitDoorImage, cafeteriaParkingLotDoor,

			hallwayImage, operatingRoomImage, basementImage, basementWithFlashlightImage,

			parkingLotImage, parkingLotCarsImage, parkingLotMercedesImage, parkingLotTruckImage,
			parkingLotSchoolBusImage, parkingLotGateImage, parkingLotRoadImage,

			leftHallwayImage, rightHallwayImage, stevenHallwayImage,

			currentBackgroundImage, startImage;

	// ALL AUDIO IN THE GAME
	Clip backgroundMusic, carDrivingAwayAudio, carCrashAudio, operatingRoomAudio, carEngineStartAudio, doorOpeningAudio,
			eatingFoodAudio, footstepsAudio, buzzAudio, doorSqueak1Audio, doorSqueak2Audio, doorShutAudio,
			footstepsRunAudio, lightSwitchAudio, electricalNoiseAudio, carIdleAudio, stevenCryingAudio;

	/**
	 * The main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Main consoleGame = new Main();
		consoleGame.loadImages();
		consoleGame.loadAudio();
		consoleGame.resetGame();
		consoleGame.playGame();
	}

	/**
	 * Load audio in game
	 */
	private void loadAudio() {
		try {
			backgroundMusic = AudioSystem.getClip();
			backgroundMusic.open(AudioSystem.getAudioInputStream(new File("audio/background-music.wav")));

			carDrivingAwayAudio = AudioSystem.getClip();
			carDrivingAwayAudio.open(AudioSystem.getAudioInputStream(new File("audio/car-driving-away.wav")));

			carCrashAudio = AudioSystem.getClip();
			carCrashAudio.open(AudioSystem.getAudioInputStream(new File("audio/car-crash.wav")));

			operatingRoomAudio = AudioSystem.getClip();
			operatingRoomAudio.open(AudioSystem.getAudioInputStream(new File("audio/operating-room.wav")));

			carIdleAudio = AudioSystem.getClip();
			carIdleAudio.open(AudioSystem.getAudioInputStream(new File("audio/car-idle.wav")));

			stevenCryingAudio = AudioSystem.getClip();
			stevenCryingAudio.open(AudioSystem.getAudioInputStream(new File("audio/steven-crying.wav")));

			carEngineStartAudio = AudioSystem.getClip();
			carEngineStartAudio.open(AudioSystem.getAudioInputStream(new File("audio/car-engine-start.wav")));

			doorOpeningAudio = AudioSystem.getClip();
			doorOpeningAudio.open(AudioSystem.getAudioInputStream(new File("audio/door-opening.wav")));

			eatingFoodAudio = AudioSystem.getClip();
			eatingFoodAudio.open(AudioSystem.getAudioInputStream(new File("audio/eating-food.wav")));

			footstepsAudio = AudioSystem.getClip();
			footstepsAudio.open(AudioSystem.getAudioInputStream(new File("audio/footsteps.wav")));

			buzzAudio = AudioSystem.getClip();
			buzzAudio.open(AudioSystem.getAudioInputStream(new File("audio/buzz.wav")));

			doorSqueak1Audio = AudioSystem.getClip();
			doorSqueak1Audio.open(AudioSystem.getAudioInputStream(new File("audio/door-squeak-1.wav")));

			doorSqueak2Audio = AudioSystem.getClip();
			doorSqueak2Audio.open(AudioSystem.getAudioInputStream(new File("audio/door-squeak-2.wav")));

			doorShutAudio = AudioSystem.getClip();
			doorShutAudio.open(AudioSystem.getAudioInputStream(new File("audio/door-shut.wav")));

			footstepsRunAudio = AudioSystem.getClip();
			footstepsRunAudio.open(AudioSystem.getAudioInputStream(new File("audio/footsteps-run.wav")));

			lightSwitchAudio = AudioSystem.getClip();
			lightSwitchAudio.open(AudioSystem.getAudioInputStream(new File("audio/light-switch.wav")));

			electricalNoiseAudio = AudioSystem.getClip();
			electricalNoiseAudio.open(AudioSystem.getAudioInputStream(new File("audio/electrical-noise.wav")));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load images and store in variables
	 */
	private void loadImages() {

		try {
			basementImage = ImageIO.read(new File("images/basement.jpg"));
			basementWithFlashlightImage = ImageIO.read(new File("images/basement-with-flashlight.jpg"));

			cafeteriaImage = ImageIO.read(new File("images/cafeteria.jpg"));
			cafeteriaBlurryImage = ImageIO.read(new File("images/cafeteria-blurry.jpg"));
			cafeteriaSecondImage = ImageIO.read(new File("images/cafeteria-second.jpg"));
			cafeteriaFoodImage = ImageIO.read(new File("images/cafeteria-food.jpg"));
			cafeteriaExitDoorImage = ImageIO.read(new File("images/cafeteria-exit-door.jpg"));
			cafeteriaParkingLotDoor = ImageIO.read(new File("images/cafeteria-parking-lot-door.jpg"));

			hallwayImage = ImageIO.read(new File("images/hallway.png"));
			operatingRoomImage = ImageIO.read(new File("images/operatingroom.jpg"));

			parkingLotImage = ImageIO.read(new File("images/parkinglot.jpg"));
			parkingLotCarsImage = ImageIO.read(new File("images/parkinglot-cars.jpg"));
			parkingLotMercedesImage = ImageIO.read(new File("images/parkinglot-mercedes.jpg"));
			parkingLotTruckImage = ImageIO.read(new File("images/parkinglot-truck.jpg"));
			parkingLotSchoolBusImage = ImageIO.read(new File("images/parkinglot-school-bus.jpg"));
			parkingLotRoadImage = ImageIO.read(new File("images/parkinglot-road.jpg"));
			parkingLotGateImage = ImageIO.read(new File("images/parkinglot-gate.jpg"));

			startImage = ImageIO.read(new File("images/start.jpg"));
			leftHallwayImage = ImageIO.read(new File("images/hallway-left.jpg"));
			rightHallwayImage = ImageIO.read(new File("images/hallway-right.jpg"));

			stevenHallwayImage = ImageIO.read(new File("images/hallway-steven.jpg"));

		} catch (IOException e) {
			System.err.println("Error loading images.");
			e.printStackTrace();
		}
	}

	/**
	 * Start the game
	 */
	private void playGame() {

		showBackgroundImage(startImage);

		backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);

		c.getChar();

		currentLocation = Location.OPERATING_ROOM;
		stateLocation();

	}

	/**
	 * Main method in the game. This method handles all the different states in
	 * the game
	 */
	private void stateLocation() {

		boolean continueLooping = true;

		while (continueLooping) {

			switch (currentLocation) {
			case HALLWAY:

				showBackgroundImage(hallwayImage);

				// If the user has never been to a hallway, show them the
				// message
				if (!userHasBeenTo(Location.HALLWAY)) {
					showMessage("The hallway seems to be a dark and scary place.");
				}

				// This loop is going to keep happening unless the user has keys
				// to go left
				while (true) {

					// Ask user which direction they want to go to
					askQuestion("Do you want to go left or right? Choose wisely...", "left:right");

					// If user wants to go left...
					if (userInput.equals("left")) {

						showBackgroundImage(leftHallwayImage);

						showMessage("You see a dark door in front of you.");

						// Firstly, lets check if the user has keys (in the
						// backpack) to go to the cafeteria
						if (userHas(Items.KEYS)) {

							playAudio(doorSqueak1Audio);
							showMessage("The door is locked... so you unlock the door with the keys in your backpack.");
							unlockedPlaces.add(Location.CAFETERIA);

							currentLocation = Location.CAFETERIA;

							break;

						} else {

							showBackgroundImage(leftHallwayImage);

							playAudio(footstepsAudio);
							// Nope, they don't have the keys...
							showMessage("It seems to be locked. You are now on your way back.");

							showMessage("There you are... right outside of the operating room again...");

							footstepsAudio.stop();

							showBackgroundImage(hallwayImage);
						}

					} else {
						break;
					}

				}

				// If they want to go right
				if (userInput.equals("right")) {

					showBackgroundImage(rightHallwayImage);

					playAudio(footstepsAudio);

					// If user has not met the guy before...
					if (!userHasMetSteven) {

						stevenCryingAudio.loop(Clip.LOOP_CONTINUOUSLY);

						showMessage(
								"You are now turning right and walking slowly as you hear noises of someone crying getting closer");
						showMessage("And there is a wounded guy right in front of you.");

						pauseGame(2);
						footstepsAudio.stop();

						showBackgroundImage(stevenHallwayImage);

						askQuestion("Do you want to save the guy, or rob him and take all his stuff?", "rob:save");

						stevenCryingAudio.stop();

						// If user want to rob the guy...
						if (userInput.equals("rob")) {

							// If the user has backpack to store things in
							if (userHas(Items.BACKPACK)) {

								// now the user has met the guy
								userHasMetSteven = true;

								unlockedItems.add(Items.FLASHLIGHT);
								unlockedItems.add(Items.KEYS);

								showMessage("You have taken keys and flashlight from this man and left him to die.");
								showMessage("You then start walking straight slowly...");

								// if user has no backpack, send them to
								// basement
							} else {
								showMessage(
										"This guy has things you could've taken... But you have nowhere to store it.");
								showMessage("You continue walking straight and see an old door. You slowly open it.");
							}

							currentLocation = Location.BASEMENT;
							break;

							// User wants to save this guy
						} else {

							userHasMetSteven = true;
							saveSteven = true;
							hasSteven = true;

							// Send them back to the operating room to save them
							currentLocation = Location.OPERATING_ROOM;
							break;
						}

						// User has met the guy before already
					} else {

						footstepsAudio.stop();
						showMessage("There is a dark, old door in front of you.");

						playAudio(doorSqueak2Audio);

						showMessage("You open it slowly, after a shrilling creak you go through it...");

						// update user location to the basement
						currentLocation = Location.BASEMENT;
					}

				}

				break;

			case OPERATING_ROOM:

				showBackgroundImage(operatingRoomImage);
				operatingRoomAudio.loop(Clip.LOOP_CONTINUOUSLY);

				// If the user has never been to the operating room
				if (!userHasBeenTo(Location.OPERATING_ROOM)) {
					showMessage(
							"You wake up in an operating room. A bright white light emits from a lamp at the far right corner of the room.");
					locationHistory.add(Location.OPERATING_ROOM);
				}

				// If the user is in the operating room because user choose to
				// save the guy
				if (saveSteven) {
					showMessage("You are now in the operating room...");
					showMessage(
							"You seem to have saved him using medication located around the room. He seems pretty stable. He introduces himself as Steven.");
					showMessage("You are now looking around in the room...");
				}

				// If user does not have backpack or the badge, ask them which
				// they want to pick
				if (!userHas(Items.BACKPACK) && !userHas(Items.BADGE)) {

					showMessage("You see a dead body and a back pack.");
					askQuestion("Do you search the body or take the backpack?", "body:backpack");

					// If the user wants to search the body...
					if (userInput.equals("body")) {
						// Add item to the user inventory
						unlockedItems.add(Items.BADGE);

						showMessage(
								"The body reeks of sweat and death, but after searching for 2 agonizing minutes you find a badge on the dead body.");

					} else if (userInput.equals("backpack")) {
						unlockedItems.add(Items.BACKPACK);

						showMessage("Okay, you now have the backpack. You can use this to store more stuff in...");
					}

					// If user has the backpack but no badge, ask if they want
					// to take the badge
				} else if (userHas(Items.BACKPACK) && !userHas(Items.BADGE)) {

					askQuestion("You seem to find a badge on a dead body. Do you want to carry the badge with you?",
							"yes:no");

					if (userInput.equals("yes")) {

						unlockedItems.add(Items.BADGE);

						showMessage("You now have a badge in your backpack.");
					} else {
						showMessage("Okay, no problem.");
					}

					// If user has the badge but no backpack, ask if they want
					// to take the backpack
				} else if (userHas(Items.BADGE) && !userHas(Items.BACKPACK)) {
					askQuestion("You have found a backpack... Do you want to take it?", "yes:no");
					if (userInput.equals("yes")) {

						unlockedItems.add(Items.BACKPACK);
						showMessage(
								"You sling the backpack on to your back. A slight and subtle chill runs down your spine. You are unsure whether you feel safer or not.");
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

					operatingRoomAudio.stop();
					playAudio(footstepsRunAudio);

					showMessage("You hear a noise and run outside.");

					footstepsRunAudio.stop();
				}

				operatingRoomAudio.stop();

				// Update user's location to the hallway
				currentLocation = Location.HALLWAY;
				break;

			case BASEMENT:

				showBackgroundImage(basementImage);

				showMessage("The basement is a dark and cold place.");

				// If the user has a flashlight, turn it on
				if (userHas(Items.FLASHLIGHT)) {

					showBackgroundImage(basementWithFlashlightImage);

					playAudio(lightSwitchAudio);
					showMessage("Luckily, you have a flashlight in your backpack so you use it.");

				} else {
					// Since the user has no flashlight... ask if they want to
					// turn switch on
					showMessage(
							"You feel something moving in the dark. You cannot see anything but you feel a light switch on the right side of the wall.");
					askQuestion("Do you want to turn the switch on?", "yes:no");

					// User dies after turning on the light
					if (userInput.equals("yes")) {
						playAudio(lightSwitchAudio);
						playAudio(electricalNoiseAudio);
						showMessage(
								"You feel massive amounts of electricity surging through your body. The loose wiring in the light switch got you electrocuted.");
						showMessage(" You are killed.");

						// USER IS DEAD
						userHasDied = true;
						continueLooping = false;

						break;
					}
				} // end: user has flashlight

				showMessage(
						"There does not seem to be anything in the basement. You want to get out and you see 2 sets of doors.");
				showMessage(
						"The first set of doors was painted a dull gray fashioned with a cold, pair of steel handles...");
				showMessage("The second door has a dried red liquid splattered across.");

				askQuestion("Do you want to go through from the first or the second door?", "first:second");

				// Door 1 leads to the cafeteria
				if (userInput.equals("first")) {

					playAudio(doorSqueak1Audio);
					locationHistory.add(Location.CAFETERIA);
					currentLocation = Location.CAFETERIA;

					// Door 2 leads to the parking lot
				} else {

					showMessage("The door is locked with a 3 digit pin. The number is between 100 and 120.");
					showMessage("You have 3 tries. Please enter the pin.");
					
					// Not so easy. They have to guess the pin.
					int parkingLotDoorPin = generateRandomNumberBetween(100, 120);

					// Validate the pin
					boolean doorPinCorrect = validatePin(parkingLotDoorPin, 3,
							"A buzzer buzzes twice. The pin you entered is wrong.");

					if (doorPinCorrect) {
						playAudio(doorSqueak2Audio);
						locationHistory.add(Location.PARKING_LOT);
						currentLocation = Location.PARKING_LOT;

					} else {

						showMessage("You guessed too many times, so you are forced to go through the first door.");

						locationHistory.add(Location.CAFETERIA);
						currentLocation = Location.CAFETERIA;
					}

				} // end: user goes through the second door

				break;

			case CAFETERIA:
				showBackgroundImage(cafeteriaImage);

				// If the last location is basement, show them another message
				if (lastLocation() == Location.BASEMENT) {
					showMessage("You thought it was an exit, but it seems to be an entrance to the cafeteria.");

					playAudio(doorShutAudio);
					showMessage("Before you could go back, the doors behind you are locked.");
				}
				showMessage("You find yourself in a vast room. You see rows of tables stretching form wall to wall.");
				showMessage("You smell food somewhere, your stomach grumbles in response.");

				pauseGame(2);
				showBackgroundImage(cafeteriaFoodImage);

				showMessage("You go towards the scent and you notice a warm and inviting plate of dinner.");

				askQuestion("Do you eat the food?", "yes:no");

				// Eat food?
				if (userInput.equals("yes")) {

					playAudio(eatingFoodAudio);

					showMessage("Yum... It seems pretty tasty...");
					showBackgroundImage(cafeteriaImage);

					showMessage("After finishing the meal, you sit up from the table. You're tonge sits uncomfortably in your mouth, an unpleasant taste is lingering there.");

					pauseGame(1);

					showBackgroundImage(cafeteriaBlurryImage);

					showMessage("The whole world seems dizzy to you.");
					showMessage("The food was laced with rat poison. You painfully die.");

					userHasDied = true;
					continueLooping = false;
					break;

				} else if (userInput.equals("no")) {
					showMessage("You leave the food on the table. You continue exploring.");
				}

				showBackgroundImage(cafeteriaSecondImage);

				// Take the phone?
				showMessage("You notice a phone on the floor, it has a bright red LED on the front of the phone.");
				askQuestion("Do you want to take the phone?", "yes:no");

				if (userInput.equals("yes")) {

					// Check if user has space to store the phone
					if (userHas(Items.BACKPACK)) {
						unlockedItems.add(Items.PHONE);
						showMessage("You now have the cell phone. You feel its weight being added to your backpack.");
					} else {
						showMessage("You pick it up, and put it back down since you have nowhere to store it.");
					}

					// The user does not want the phone
				} else {
					showMessage("You leave the phone on the ground and you continue exploring the cafeteria.");
				}

				pauseGame(1);

				showBackgroundImage(cafeteriaExitDoorImage);

				showMessage("You come across a stairway and a white door set with paint peeling off.");
				showMessage("The stairway is labelled: BASEMENT and the door is labelled: EXIT");

				// parking lot or basement?
				askQuestion("Do you go down to the basement or through the exit?", "basement:exit");

				if (userInput.equals("basement")) {

					playAudio(footstepsAudio);

					if (lastLocation() == Location.BASEMENT) {
						showMessage("You are now on your way back to the basement...");
					}

					footstepsAudio.stop();
					currentLocation = Location.BASEMENT;
					break;

				} else if (userInput.equals("exit")) {

					playAudio(doorOpeningAudio);

					showBackgroundImage(cafeteriaParkingLotDoor);

					if (hasSteven == true) {
						showMessage(
								"You try to open the door but it is locked. You notice that there is a number pad to the left of the door.");

						playAudio(doorOpeningAudio);
						showMessage(
								"Luckily, Steven reassures you that he knows the code, he goes towards the number pad and enters the pin.");
						showMessage("The door clicks. You push it open and a gust of cold wind greets you. ");
						unlockedPlaces.add(Location.PARKING_LOT);
						currentLocation = Location.PARKING_LOT;
						break;
					}

					// If the user doesn't have Steven, they have to guess the
					// code
					else {
						showMessage(
								"You try to open the door but it is locked. You notice that there is a number pad to the left of the door. ");
						showMessage(
								"The 3 spaces displayed on the top of the number pad suggests that there is a 3 number pin.");

						showMessage("The pin is between 500 and 510. Enter the pin...");
						// If the cafeteria doors to the parking lot are
						// permanently locked
						if (caffParkingLotDoorsPermanentlyLocked) {
							showMessage(
									"... However, you have already guessed too many times which caused the doors to be permanently locked.");
						} else {

							// Generate a random number
							int parkingLotDoorPin = generateRandomNumberBetween(500, 510);

							// Validate the pin
							boolean doorPinCorrect = validatePin(parkingLotDoorPin, 3,
									"A buzzer buzzes twice. The pin you entered is wrong.");

							// If the user guessed the pin correctly
							if (doorPinCorrect) {

								showMessage(
										"You guessed the pin correctly! The door clicks. You push it open and a gust of cold wind greets you.");
								unlockedPlaces.add(Location.PARKING_LOT);
								currentLocation = Location.PARKING_LOT;
								break;
							} else {
								// Permanently lock the parking lot doors doors
								caffParkingLotDoorsPermanentlyLocked = true;
								showMessage(
										"A long buzz erupts from the number pad. The door shivered as internal locking mechanisms permanently locked the door.");
							}
						}

					} // end if: user does not have Steven

					// Since the user can not exit the cafeteria upon guessing
					// the code incorrectly, they must now go to the basement.
					showBackgroundImage(cafeteriaExitDoorImage);

					playAudio(doorOpeningAudio);

					showMessage("You have no option so you take the door to the basement.");
					locationHistory.add(Location.BASEMENT);
					currentLocation = Location.BASEMENT;
					break;

				} // end if: user input equals exit

				break;

			case PARKING_LOT:

				showBackgroundImage(parkingLotImage);

				showMessage("It's pretty dark out here. It seems to be a parking lot. An empty parking lot.");

				// Show message saying user has an interest in cars
				showMessage(hasSteven ? "Steven seems to love cars so you look around to find cars together."
						: "You seem to have an interest in cars so you look around to find cars.");

				showBackgroundImage(parkingLotCarsImage);

				showMessage("You see a truck, Mercedes Benz and a school bus.");
				askQuestion("Which one do you want?", "truck:mercedes:school bus");

				String vehicleInfo;

				// set the vehicle text as whatever vehicle they chose
				if (userInput.equals("truck")) {

					showBackgroundImage(parkingLotTruckImage);

					userVehicle = Vehicles.TRUCK;
					vehicleInfo = "old, rusty, big truck.";

				} else if (userInput.equals("mercedes")) {

					showBackgroundImage(parkingLotMercedesImage);

					userVehicle = Vehicles.MERCEDES_BENZ;
					vehicleInfo = "Mercedes Benz S Class 2017 Model.";

				} else {

					showBackgroundImage(parkingLotSchoolBusImage);

					userVehicle = Vehicles.SCHOOL_BUS;
					vehicleInfo = "School bus.";
				}

				showMessage("Okay, you now have a " + vehicleInfo);

				// If user has a cell phone it randomly explodes killing them
				if (userHas(Items.PHONE)) {

					int randomCellNumber = generateRandomNumberBetween(1, 100);

					// Only explode if the random number is even
					if (randomCellNumber % 2 == 0) {
						// Cell explodes, killing them
						playAudio(electricalNoiseAudio);
						showMessage(
								"The cell phone you picked up receives random flow of electricity, electrocuting and killing you...");

						userHasDied = true;
						continueLooping = false;
						break;
					}
				}

				playAudio(carEngineStartAudio);
				playAudio(carDrivingAwayAudio);

				showMessage("You start driving away to escape. But you see that the door is locked with a key");

				carDrivingAwayAudio.stop();
				carIdleAudio.loop(Clip.LOOP_CONTINUOUSLY);

				showBackgroundImage(parkingLotGateImage);

				// If user picked up Steven, they can escape without any problem
				if (hasSteven) {
					showMessage("Thankfully, Steven has the key to the gate so he unlocks it.");

					userHasEscaped = true;
					continueLooping = false;
					break;

				} else {

					showMessage(
							"Since you don't know the pin, you can either break through the gate by driving or try guessing the pin.");
					askQuestion("Which one do you want?", "guess:drive through");

					// if user wants to drive through
					if (userInput.equals("drive through")) {

						userHasDrivenThrough = true;
						continueLooping = false;
						break;

						// if user wants to guess pin
					} else {

						showMessage(
								"You have to guess the pin to get out of the parking lot. You see a box where you enter the pin.");
						showMessage("The pin is 3 numbers and you have 3 guesses before it permanently locks.");

						// Clear old text by showing current background image
						// which clears the text and everything
						showBackgroundImage(currentBackgroundImage);

						showMessage("Please enter a pin. Remember, you have 3 guesses.");
						int guessedPin = generateRandomNumberBetween(100, 999);
						boolean correctGuess = validatePin(guessedPin, 3, "That is not the right pin.");

						// Check if the user guessed the right pin
						if (correctGuess) {
							showMessage("That is the right pin!");
							// If the guess is correct, the user escapes
							// successfully and the game ends

							userHasEscaped = true;
							continueLooping = false;
							break;

							// If the guess is wrong
						} else {

							showMessage("You have guessed too many times. You have no option but to drive through.");

							userHasDrivenThrough = true;
							continueLooping = false;
							break;

						}

					} // end: guess pin

				} // end: has steven

			} // end: switch statement

		} // end: while loop

		// The user only reaches here if they exit the while loop, which means
		// that they are either dead or have escaped

		// User is dead
		if (userHasDied) {
			userHasDied();
		}

		// User has driven through
		if (userHasDrivenThrough) {
			driveThrough();
		}

		// User has escaped
		if (userHasEscaped) {
			userHasEscaped();
		}

	}

	/**
	 * This method is called if the user wants to drive through the gate
	 */
	private void driveThrough() {

		showBackgroundImage(parkingLotGateImage);

		carIdleAudio.stop();
		playAudio(carDrivingAwayAudio);

		showMessage("You start driving in full speed.");
		showMessage("As you get closer to the gate, you get nervous.");

		// generate a random number
		int randomNumber = generateRandomNumberBetween(0, 100);

		// Debug stuff
		if (DEBUG_MODE) {
			System.out.println("Random number for vehicle success rate: " + randomNumber);
			System.out.println("Vehicle success rate: " + userVehicle.getSuccessRate());
		}

		playAudio(carCrashAudio);

		// randomly check if the random number is less than the
		// success rate.
		// the higher the success rate of vehicle, the more chances
		// it will have of going through
		if (userVehicle.getSuccessRate() >= randomNumber) {
			// user can break through

			playAudio(carCrashAudio);
			showMessage("The front of the car slams the gate, breaking it open.");
			userHasEscaped();

		} else {
			// user cannot break through
			playAudio(carCrashAudio);
			carDrivingAwayAudio.stop();
			showMessage("Your vehicle slams the door at a high speed, killing you.");
			userHasDied();
		}
	}

	/**
	 * Show a message by animating it
	 * 
	 * @param message
	 */
	private void showMessage(String message) {
		animateString(message, 35, 1000, false);
	}

	/**
	 * Print a message to console
	 * 
	 * @param message
	 */
	private void printToConsole(String message) {
		c.println(message);
	}

	/**
	 * Print a character to console
	 * 
	 * @param character
	 * @param sameLine
	 */
	private void printToConsole(char character, boolean sameLine) {
		if (sameLine) {
			c.print(character);
		} else {
			c.println(character);
		}
	}

	/**
	 * Animate string so it prints character by character
	 * 
	 * @param text
	 * @param speed
	 * @param pauseTimer
	 */
	private void animateString(String text, int speed, int pauseTimer, boolean sameLine) {

		if (DEBUG_MODE) {
			speed = 0;
		}

		try {

			for (int i = 0; i < text.length(); i++) {

				Thread.sleep(speed);

				printToConsole(text.charAt(i), true);

				// if text has a period and text after it, it probably means
				// that there are more than one sentences in the text
				// Pause for a moment after the period before showing another sentence
				if ((text.charAt(i) == '.' || text.charAt(i) == '!')) {
					// this if statement ensures that we don't get an
					// ArrayOutOfBounds exception
					if (text.length() != i + 1 && text.charAt(i + 1) == ' ') {
						Thread.sleep(500);
					}
				}

			}

			if (!sameLine) {
				// Print a new line after
				printToConsole("");
			}

			if (pauseTimer > 0) {
				Thread.sleep(pauseTimer);
			} else {
				Thread.sleep(1000);
			}

		} catch (InterruptedException ie) {
			// if something goes wrong just print it normally
			printToConsole(text);
		}
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
		userHasDied = false;
		userHasEscaped = false;
		userHasDrivenThrough = false;

		// Stop all audio
		backgroundMusic.stop();
		carDrivingAwayAudio.stop();
		carIdleAudio.stop();
		carDrivingAwayAudio.stop();
		carCrashAudio.stop();
		stevenCryingAudio.stop();
		operatingRoomAudio.stop();
		carEngineStartAudio.stop();
		doorOpeningAudio.stop();
		eatingFoodAudio.stop();
		footstepsAudio.stop();
		buzzAudio.stop();
		doorSqueak1Audio.stop();
		doorSqueak2Audio.stop();
		doorShutAudio.stop();
		footstepsRunAudio.stop();
		lightSwitchAudio.stop();
		electricalNoiseAudio.stop();

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

		printToConsole("");

		String input;
		String parsedAcceptedAnswers = " ( " + acceptedAnswers.replaceAll(":", " / ") + " ) ";
		while (true) {

			animateString(question, 30, 100, true);

			c.setTextColor(Color.yellow);

			animateString(parsedAcceptedAnswers, 20, 100, true);

			c.setTextColor(Color.green);

			// Convert user input to lower case so it's easier to parse
			input = c.readLine().toLowerCase();

			c.setTextColor(Color.white);

			// Validate user's input
			String message = validateInput(input, acceptedAnswers);
			if (message.equals("")) {
				break;
			}
			animateString(message, 20, 0, false);
		}

		// Set the current user input to the input
		this.userInput = input;

		showBackgroundImage(currentBackgroundImage);

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
			message = "Oops! You must either type \"" + acceptedInput.replace(":", "\" or \"")
					+ "\". Please try again.";
		}

		return message;
	}

	/**
	 * Generate a random number between a range which includes the starting and
	 * end number
	 * 
	 * @param start
	 * @param end
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
		if (DEBUG_MODE) {
			System.out.println("PIN: " + pin);
		}
		int totalTries = 0;
		// If user has not yet reached his max try limit
		while (totalTries < maxTries) {
			// Get string because if it's a number and the user types a string
			// it will throw an exception
			String userInputPin = c.readString();

			// If user has entered the right pin, we simply return
			if (userInputPin.equals(pin)) {
				return true;
			} else {
				playAudio(buzzAudio);
				// If user has entered an incorrect pin, show error message
				// If the user has less than 3 tries add a "try again" after the
				// error message.
				// If it's the last try, we don't add any try again messages
				showMessage(totalTries < 2 ? errorMessage + " Try again." : errorMessage);

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
		askQuestion("Do you want to start over?", "yes:no");
		if (userInput.equals("yes")) {
			resetGame();
			playGame();
		} else {
			showMessage("Guess you will never be able to escape this building.");
		}
	}

	/**
	 * User has successfully escaped
	 */
	private void userHasEscaped() {

		playAudio(carDrivingAwayAudio);
		showBackgroundImage(parkingLotRoadImage);

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

	/**
	 * Show an image as a background image
	 * 
	 * @param image
	 */
	private void showBackgroundImage(BufferedImage image) {
		c.clear();

		currentBackgroundImage = image;

		c.setTextBackgroundColour(Color.BLACK);
		c.setTextColor(Color.WHITE);
		c.setBackground(Color.BLACK);
		c.drawImage(image, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, null);
	}

	/**
	 * Play audio by first setting the frame to 0
	 * 
	 * @param audio
	 */
	private void playAudio(Clip audio) {
		audio.setFramePosition(0);
		audio.start();
	}

	/**
	 * Pause game for a specified number of seconds
	 * 
	 * @param seconds
	 */
	private void pauseGame(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}