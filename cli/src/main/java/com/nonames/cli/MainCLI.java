package com.nonames.cli;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nonames.cli.utils.PrintUtils;

public class MainCLI {
    private static final Scanner scanner = new Scanner(System.in);

    private static void printMenuOptions(String[] options) {
        for (String option : options) {
            System.out.println(option);
        }
        System.out.println("---------------------------");
        System.out.print("Select an option: ");
    }

    private static void clearConsole() {
        try {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } catch (Exception ignored) {
        }
    }

    public static CLICode mainMenu() {
        try {
            String[] options = { "\n --- MainCLI Menu ---",
                    "[1] View All Events",
                    "[2] Create an Event",
                    "[3] Create Participants",
                    "[4] View All Participants",
                    "[5] Register a Participant to an Event",
                    "[6] Exit Program"
            };
            printMenuOptions(options);

            int input = 0;
            try {
                input = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                throw new RuntimeException("Incorrect input given");
            }

            clearConsole();

            switch (input) {
                // View all events
                case 1 -> {
                    return handleViewAllEventsRequest();
                }
                // Create an event
                case 2 -> {
                    return handleCreateEventRequest();
                }
                // Create participants
                case 3 -> {
                    return handleCreateParticipantRequest();
                }
                // View all participants
                case 4 -> {
                    return handleViewAllParticipantsRequest();
                }
                // Register a participant to event
                case 5 -> {
                    return handleRegisterParticipantRequest();
                }

                // Exit program
                case 6 -> {
                    scanner.close();
                    return CLICode.NO_ERROR_NO_REPEAT_OP;
                }

                // Invalid integer input
                default -> {
                    throw new InputMismatchException("Incorrect input given");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return CLICode.MAIN_MENU;
        }
    }

    // Option 1: View all events
    // DONE
    private static CLICode handleViewAllEventsRequest() {
        try {
            System.out.println("-----------------------");
            System.out.println("Events and Participants");
            String apiEndpoint = "http://localhost:3001/api/view-events";

            URL url = new URI(apiEndpoint).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String responseBody = response.toString();
                JSONArray json = new JSONArray(responseBody);
                String[][] table = PrintUtils.convertGetEventsResponseToTable(json);
                PrintUtils.prettyPrintTable(table);

                System.out.println("[*] Retrieved all events");
            } else {
                System.out.println("Failed to fetch requested data");
            }
        } catch (IOException e) {
            System.out.println("No available events. Returning to main menu");
            System.out.println("--------------");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return CLICode.MAIN_MENU;
    }

    // Option 2: handles the creation of a new event as well as the validation of
    // the input
    private static CLICode handleCreateEventRequest() throws SQLException {
        try {
            System.out.println("--- New event ---");
            System.out.println("[*] Press 'C' or 'c' and then ENTER at any input prompt to cancel");
            String apiEndpoint = "http://localhost:3001/api/create-event";

            URL url = new URI(apiEndpoint).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            /* Get data from user: */
            // -------------------------------------------------------
            String[] results = new String[6];
            String result = CliInputHandlerUtils
                    .handleUuidCreationInput("Set a UUID for the event, or press ENTER for an auto-generated one: ");
            if (result == null)
                return CLICode.MAIN_MENU;
            results[0] = result;

            result = CliInputHandlerUtils.handleEventDateInput();
            if (result == null)
                return CLICode.MAIN_MENU;
            results[1] = result;

            result = CliInputHandlerUtils.handleEventTimeInput();
            if (result == null)
                return CLICode.MAIN_MENU;
            results[2] = result;

            result = CliInputHandlerUtils.handleEventTitleInput();
            if (result == null)
                return CLICode.MAIN_MENU;
            results[3] = result;

            result = CliInputHandlerUtils.handleEventDescriptionInput();
            if (result == null)
                return CLICode.MAIN_MENU;
            results[4] = result;

            result = CliInputHandlerUtils.handleEmailInput("Enter the email of the event host: ");
            if (result == null)
                return CLICode.MAIN_MENU;
            results[5] = result;
            // -------------------------------------------------------

            // JSON data for the request body
            String eventData = "{ " +
                    "\"id\": \"" + results[0] + "\", " +
                    "\"date\": \"" + results[1] + "\", " +
                    "\"time\": \"" + results[2] + "\", " +
                    "\"title\": \"" + results[3] + "\", " +
                    "\"description\": \"" + results[4] + "\", " +
                    "\"hostEmail\": \"" + results[5] + "\"" +
                    "}";

            // Write the JSON data to the request body
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(eventData);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Successfully created an event.");
            } else {
                System.out.println("Failed to create an event");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return CLICode.MAIN_MENU;
    }

    // Option 4: handles viewing all participants
    // DONE
    private static CLICode handleViewAllParticipantsRequest() {
        try {
            System.out.println("-----------------------");
            System.out.println("Participants");
            String apiEndpoint = "http://localhost:3001/api/view-participants";

            URL url = new URI(apiEndpoint).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String responseBody = response.toString();
                JSONArray json = new JSONArray(responseBody);
                String[][] table = PrintUtils.convertGetParticipantsResponseToTable(json);
                PrintUtils.prettyPrintTable(table);

                System.out.println("[*] Retrieved all participants");
            } else {
                System.out.println("Failed to fetch requested data");
            }
        } catch (IOException e) {
            System.out.println("No available participants. Returning to main menu");
            System.out.println("--------------");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return CLICode.MAIN_MENU;
    }

    // Option 3: Create participants
    private static CLICode handleCreateParticipantRequest() throws SQLException {
        try {
            System.out.println("--- New participant ---");
            System.out.println("[*] Press 'C' or 'c' and then ENTER at any input prompt to cancel");
            String apiEndpoint = "http://localhost:3001/api/create-participant";

            URL url = new URI(apiEndpoint).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            /* Get data from user: */
            // -------------------------------------------------------
            String[] results = new String[3];
            String result = CliInputHandlerUtils.handleUuidCreationInput(
                    "Set a UUID for the participant, or press ENTER for an auto-generated one: ");
            if (result == null)
                return CLICode.MAIN_MENU;
            results[0] = result;

            result = CliInputHandlerUtils.handleParticipantNameInput();
            if (result == null)
                return CLICode.MAIN_MENU;
            results[1] = result;

            result = CliInputHandlerUtils.handleEmailInput("Enter email of participant: ");
            if (result == null)
                return CLICode.MAIN_MENU;
            results[2] = result;
            // -------------------------------------------------------

            // JSON data for the request body
            String participantData = "{ " +
                    "\"id\": \"" + results[0] + "\", " +
                    "\"name\": \"" + results[1] + "\", " +
                    "\"email\": \"" + results[2] + "\"" +
                    "}";

            // Write the JSON data to the request body
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(participantData);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Successfully created a participant.");
            } else {
                System.out.println("Failed to create a participant");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return CLICode.MAIN_MENU;
    }

    // Option 5: handles the registration of participants
    private static CLICode handleRegisterParticipantRequest() {
        boolean validInput = false;

        System.out.println("[*] Press 'C' or 'c' and then ENTER at any input prompt to cancel");

        String participantUuidInput = null;
        while (!validInput) {
            try {
                System.out.print("Enter the UUID for the participant to register: ");
                participantUuidInput = scanner.nextLine();

                if (isCancelRequest(participantUuidInput)) {
                    return CLICode.MAIN_MENU;
                }
                if (participantUuidInput == null || participantUuidInput.isBlank()) {
                    System.out.println("Input must be a valid UUID. Try again");
                    continue;
                }
                
                UUID.fromString(participantUuidInput);

                if (!isParticipantPresent(participantUuidInput)) {
                    System.out.println("Participant UUID doesn't exist. Try again");
                    continue;
                }

                validInput = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Input must be a valid UUID. Try again");
            }
        }

        String eventUuidInput = null;
        validInput = false;
        while (!validInput) {
            try {
                System.out.print("Enter the UUID for the event to register to: ");
                eventUuidInput = scanner.nextLine();

                if (isCancelRequest(eventUuidInput)) {
                    return CLICode.MAIN_MENU;
                }
                if (eventUuidInput == null || eventUuidInput.isBlank()) {
                    System.out.println("Input must be a valid UUID. Try again");
                    continue;
                }

                UUID.fromString(eventUuidInput);

                if (!isEventPresent(eventUuidInput)) {
                    System.out.println("Event UUID doesn't exist.");
                    continue;
                }

                validInput = true;
            } catch (IllegalArgumentException e) {
                System.out.println("Input must be a valid UUID. Try again");
            }
        }

        try {
            String endpoint = "http://localhost:3001/api/register-participant?participantId="
                    + URLEncoder.encode(participantUuidInput, "UTF-8")
                    + "&eventId=" + URLEncoder.encode(eventUuidInput, "UTF-8");
            HttpURLConnection connection = connectToApi(endpoint, "PUT");
            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String responseBody = response.toString();
                JSONObject error = new JSONObject(responseBody);
                throw new RuntimeException(error.getString("message"));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("[*] Successfully registered participant to event. Returning to main menu");
        return CLICode.MAIN_MENU;
    }

    // Check if the participant exists
    private static boolean isParticipantPresent(String participantId) {
        try {
            HttpURLConnection connection = connectToApi("http://localhost:3001/api/view-participants", "GET");

            assert connection != null;
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String responseBody = response.toString();
                // System.out.println(responseBody);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check if the event exists
    private static boolean isEventPresent(String eventId) {
        try {
            HttpURLConnection connection = connectToApi("http://localhost:3001/api/view-events", "GET");

            assert connection != null;
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String responseBody = response.toString();
                // System.out.println(responseBody);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to make HTTP connections
    private static HttpURLConnection connectToApi(String endpoint, String requestMethod) throws IOException {
        try {
            URL url = new URI(endpoint).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestMethod);
            return connection;
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if user requested to cancel the creation of an event or participant
     *
     * @param input input received from user
     * @return true if input is equal to 'C' or 'c'; false otherwise
     */
    public static boolean isCancelRequest(String input) {
        if (input != null && input.equalsIgnoreCase("c")) {
            System.out.println("[*] Cancel request successful");
            return true;
        }
        return false;
    }
}
