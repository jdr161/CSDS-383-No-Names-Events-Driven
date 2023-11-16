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
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Main {
    static Connection conn;
    static Channel channel;

    static String queueName;

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

        System.out.println("Press enter to create 50-100 events, where each event has 5 to 10 event participants.");
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            int eventsCount = 50 + random.nextInt(51);
            for (int i = 0; i < eventsCount; i++){
                sendRandomEventAndParticipants();
            }
            System.out.println("Sent " + eventsCount + " create events messages.");
        }

        scanner.close();
        channel.close();
        conn.close();
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