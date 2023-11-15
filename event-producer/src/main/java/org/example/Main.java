package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;

public class Main {
    static Connection conn;
    static Channel channel;

    static String queueName;

    public static void main(String[] args) throws IOException, TimeoutException, ConfigurationException {
        System.out.println("Hello world!");

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


        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            int eventsCount = 50 + random.nextInt(50);
            for (int i = 0; i < eventsCount; i++){
                sendRandomEventAndParticipants();
            }
            System.out.println("Sent " + eventsCount + " create events messages.");
        }

        scanner.close();
        channel.close();
        conn.close();
    }

    private static int nextEventId = 0;
    private static int nextParticipantId = 0;

    private static byte[] jsonObjToBytes(JsonObject jsonObj){
        return jsonObj.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static Random random = new Random();

    private static String[] generateUser(){
        String[] names = new String[] { "Jeff", "Bob", "John", "Penjamin" };
        String name = names[random.nextInt(names.length)];

        String[] emails = new String[] { "jeff@gmail.com", "bob@gmail.com", "john@gmail.com", "penjamin@gmail.com" };
        String email = emails[random.nextInt(emails.length)];

        return new String[] { name, email };
    }

    private static void sendRandomEventAndParticipants() throws IOException {
        int eventId = nextEventId++;

        long startSeconds = Instant.now().plus(1, ChronoUnit.DAYS).getEpochSecond();
        long endSeconds = Instant.now().plus(180, ChronoUnit.DAYS).getEpochSecond();
        long randomUnixTime = ThreadLocalRandom
                .current()
                .nextLong(startSeconds, endSeconds);
        Instant dateTime = Instant.ofEpochSecond(randomUnixTime);

        JsonObject createEventJson = Json.createObjectBuilder()
                .add("id", eventId)
                .add("date", DateTimeFormatter.ofPattern("YYYY MM dd").format(dateTime.atZone(ZoneOffset.UTC).withFixedOffsetZone()))
                .add("time", DateTimeFormatter.ofPattern("H:mm a").format(dateTime.atZone(ZoneOffset.UTC).withFixedOffsetZone()))
                .add("title", "Cool event " + eventId)
                .add("description", "Lots of cool activities will occur during this event.")
                .add("hostEmail", generateUser()[1])
                .build();

        channel.basicPublish("", queueName, null, jsonObjToBytes(createEventJson));

        int participantsCount = 5 + random.nextInt(6);
        for (int i = 0; i < participantsCount; i++){
            sendAddParticipant(eventId);
        }
    }

    private static void sendAddParticipant(int eventId) throws IOException {
        String[] user = generateUser();
        JsonObject addParticipantJson = Json.createObjectBuilder()
                .add("eventID", eventId)
                .add("participantID", nextParticipantId++)
                .add("name", user[0])
                .add("email", user[1])
                .build();

        channel.basicPublish("", queueName, null, jsonObjToBytes(addParticipantJson));
    }
}