package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Main {
    static Connection conn;
    static Channel channel;

    static String queueName;
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
    public static void main(String[] args) throws IOException, TimeoutException, ConfigurationException {
        PropertiesConfiguration config = new PropertiesConfiguration();
        config.load("application.properties");

        queueName = config.getString("QUEUE_NAME");

        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(config.getString("HOST"));
        factory.setUsername(config.getString("USERNAME"));
        factory.setPassword(config.getString("PASSWORD"));
        conn = factory.newConnection();
        channel = conn.createChannel();
        channel.queueDeclare(queueName, false, false, false, null);
        channel.queuePurge("hello");

        String[] options = { "\n --- MainCLI Menu ---",
                "[1] Create 50-100 Events (Each With 5-10 Participants)",
                "[2] Exit Program"
        };

        int input = 0;
        while(input != 2){
            printMenuOptions(options);
            try {
                input = Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                throw new RuntimeException("Incorrect input given");
            }
            clearConsole();

            switch (input) {
                // Create new events and participants
                case 1 -> {
                    int eventsCount = 5;//50 + random.nextInt(51);
                    for (int i = 0; i < eventsCount; i++){
                        sendRandomEventAndParticipants();
                    }
                    System.out.println("Sent " + eventsCount + " create events messages.");
                }
                // Exit program
                case 2 -> {
                    scanner.close();
                    channel.close();
                    conn.close();
                    System.exit(1);
                }

                // Invalid integer input
                default -> {
                    throw new InputMismatchException("Incorrect input given");
                }
            }
        }
    }

    private static byte[] jsonObjToBytes(JsonObject jsonObj){
        return jsonObj.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static Random random = new Random();

    private static void sendRandomEventAndParticipants() throws IOException {
        String eventId = EventGenerator.getId().toString();
        String date = EventGenerator.getDate();
        String time = EventGenerator.getTime();
        String title = EventGenerator.getTitle();
        String description = EventGenerator.getDescription();
        String hostEmail = EventGenerator.getHostEmail();

        JsonObject createEventJson = Json.createObjectBuilder()
                .add("id", eventId)
                .add("date", date)
                .add("time", time)
                .add("title", title)
                .add("description", description)
                .add("hostEmail", hostEmail)
                .build();
        channel.basicPublish("", queueName, null, jsonObjToBytes(createEventJson));

        int participantsCount = 5 + random.nextInt(6);
        for (int i = 0; i < participantsCount; i++){
            sendAddParticipant(eventId);
        }
    }

    private static void sendAddParticipant(String eventId) throws IOException {
        JsonObject addParticipantJson = Json.createObjectBuilder()
                .add("eventID", eventId)
                .add("participantID", ParticipantGenerator.getId().toString())
                .add("name", ParticipantGenerator.getName())
                .add("email", ParticipantGenerator.getEmail())
                .build();

        channel.basicPublish("", queueName, null, jsonObjToBytes(addParticipantJson));
    }
}