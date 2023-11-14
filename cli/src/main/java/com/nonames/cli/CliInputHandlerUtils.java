package com.nonames.cli;

import com.nonames.cli.utils.FormatUtils;

import java.text.ParseException;
import java.util.Scanner;
import java.util.UUID;

public class CliInputHandlerUtils {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Prompts and validates user input for a UUID upon Event or Participant creation
     * @param promptMessage message to prompt user for input
     * @return {@link CLICode#MAIN_MENU} if user cancels. Otherwise, {@link CLICode#CONTINUE} for successful input
     */
    public static String handleUuidCreationInput(String promptMessage) {
        while (true) {
            try {
                System.out.print(promptMessage);
                String uuidInput = scanner.nextLine();
                if (MainCLI.isCancelRequest(uuidInput)) {
                    return null;
                }
                if (uuidInput == null || uuidInput.isBlank()) {
                    return UUID.randomUUID().toString();
                } else {
                    UUID.fromString(uuidInput);
                    return uuidInput;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Input must be a valid UUID. Try again");
            }
        }
    }

    /**
     * Prompts and validates user input for the date of a new event
     * @return {@link CLICode#MAIN_MENU} if user cancels. Otherwise, {@link CLICode#CONTINUE} for successful input
     */
    public static String handleEventDateInput() {
        while (true) {
            try {
                System.out.print("Set a date (YYYY-MM-DD): ");
                String dateInput = scanner.nextLine();
                if (MainCLI.isCancelRequest(dateInput)) {
                    return null;
                }
                return FormatUtils.formatDate(dateInput);
            } catch (IllegalArgumentException | ParseException e) {
                System.out.println("Input must be a valid date and match the format YYYY-MM-DD or be parsable into the specified format. Try again");
            }
        }
    }

    /**
     * Prompts and validates user input for the time of a new event
     * @return {@link CLICode#MAIN_MENU} if user cancels. Otherwise, {@link CLICode#CONTINUE} for successful input
     */
    public static String handleEventTimeInput() {
        while (true) {
            try {
                System.out.print("Set a time (HH:MM AM/PM): ");
                String timeInput = scanner.nextLine();
                if (MainCLI.isCancelRequest(timeInput)) {
                    return null;
                }
                return FormatUtils.formatTime(timeInput);
            } catch (IllegalArgumentException | ParseException e) {
                System.out.println("Input must match the format HH:MM AM/PM or be parsable into the specified format. Try again");
            }
        }
    }

    /**
     * Prompts and validates user input for the title of a new event
     * @return {@link CLICode#MAIN_MENU} if user cancels. Otherwise, {@link CLICode#CONTINUE} for successful input
     */
    public static String handleEventTitleInput() {
        while (true) {
            try {
                System.out.print("Set a title: ");
                String title = scanner.nextLine();
                if (MainCLI.isCancelRequest(title)) {
                    return null;
                }

                if (title == null || title.isEmpty() || title.length() > 255) {
                    throw new IllegalArgumentException("Title should be between 1 and 255 characters, inclusive. Try again");
                } else {
                    return title;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Prompts and validates user input for the description of a new event
     * @return {@link CLICode#MAIN_MENU} if user cancels. Otherwise, {@link CLICode#CONTINUE} for successful input
     */
    public static String handleEventDescriptionInput() {
        while (true) {
            try {
                System.out.print("Set a description: ");
                String description = scanner.nextLine();
                if (MainCLI.isCancelRequest(description)) {
                    return null;
                }

                if (description == null || description.isEmpty() || description.length() > 600) {
                    throw new IllegalArgumentException("Title should be between 1 and 600 characters, inclusive. Try again");
                } else {
                    return description;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Prompts and validates user input for an email upon Event or Participant creation
     * @param promptMessage message to prompt user for input
     * @return {@link CLICode#MAIN_MENU} if user cancels. Otherwise, {@link CLICode#CONTINUE} for successful input
     */
    public static String handleEmailInput(String promptMessage) {
        while (true) {
            try {
                System.out.print(promptMessage);
                String input = scanner.nextLine();
                if (MainCLI.isCancelRequest(input)) {
                    return null;
                }

                if (input.isBlank() || !FormatUtils.isValidEmail(input)) {
                    throw new IllegalArgumentException("Invalid email. Try again");
                } else {
                    return input;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Prompts and validates user input for the name of a new participant
     * @return {@link CLICode#MAIN_MENU} if user cancels. Otherwise, {@link CLICode#CONTINUE} for successful input
     */
    public static String handleParticipantNameInput() {
        while (true) {
            try {
                System.out.print("Enter Participant Name: ");
                String input = scanner.nextLine();
                if (MainCLI.isCancelRequest(input)) {
                    return null;
                }

                if (input == null || input.isEmpty() || input.length() > 600) {
                    throw new IllegalArgumentException("Participant name should be between 1 and 600 characters, inclusive. Try again");
                } else {
                    return input;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
