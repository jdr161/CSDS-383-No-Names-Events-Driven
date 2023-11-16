package org.nonames;

import com.rabbitmq.client.*;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.DataFormatException;


public class MessageConsumer {

    private final static String QUEUE_NAME = "hello";
    private final static String DLX_NAME = "hello_dlx_exchange";
    private final static String DLX_KEY = "hello_dlx_key";
    private final static String DLQ_NAME = "hello_dlq";

    public void listen() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // DLQ config
        channel.exchangeDeclare(DLX_NAME, "direct");
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_NAME);
        args.put("x-dead-letter-routing-key", DLX_KEY);
        channel.queueDeclare(DLQ_NAME, false, false, false, null);
        channel.queueBind(DLQ_NAME, DLX_NAME, DLX_KEY);

        channel.queueDeclare(QUEUE_NAME, false, false, false, args);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> onDelivery(consumerTag, delivery, channel);
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
    }

    private void onDelivery(String consumerTag, Delivery delivery, Channel channel) throws IOException {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] Received '" + message + "'");
        JsonObject jsonObject = parseJsonObject(delivery.getBody());
        long deliveryTag = delivery.getEnvelope().getDeliveryTag();

        try {
            // if is a create-event message
            if (jsonObject.size() == 6) {
                createEvent(parseEventData(jsonObject));
            } else if (jsonObject.size() == 4) { // if is a register-participant message
                Participant p = parseParticipantData(jsonObject);
                createParticipant(p);
                UUID eventId = UUID.fromString(jsonObject.getString("eventID"));
                registerParticipant(eventId, p.getParticipantId());
            } else {
                throw new DataFormatException("Received improperly sized JSON.");
            }
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) { // other processing errors -> send to DLQ
            System.out.println("Exception occurred while processing message '" + message + "'");
            e.printStackTrace();
            channel.basicNack(deliveryTag, false, false);
        }
    }

    private JsonObject parseJsonObject(byte[] body) {
        InputStream messageBodyStream = new ByteArrayInputStream(body);
        try (JsonReader reader = Json.createReader(messageBodyStream)) {
            JsonObject jsonObject = reader.readObject();
            return jsonObject;
        } catch (Exception e) {
            throw new RuntimeException("Could not parse JSON object: " + new String(body, StandardCharsets.UTF_8), e);
        }
    }

    private Event parseEventData(JsonObject jsonObject) {
        UUID uuid = UUID.fromString(jsonObject.getString("id"));
        String date = jsonObject.getString("date");
        String time = jsonObject.getString("time");
        String title = jsonObject.getString("title");
        String description = jsonObject.getString("description");
        String hostEmail = jsonObject.getString("hostEmail");
        return new Event(uuid, date, time, title, description, hostEmail);
    }

    private Participant parseParticipantData(JsonObject jsonObject) {
        UUID uuid = UUID.fromString(jsonObject.getString("participantID"));
        String name = jsonObject.getString("name");
        String email = jsonObject.getString("email");
        return new Participant(uuid, name, email);
    }

    private void createEvent(Event event) {
        try {
            String apiEndpoint = "http://localhost:3001/api/create-event";

            URL url = new URI(apiEndpoint).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String eventData = createEventJson(event);
            // Write the JSON data to the request body
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(eventData);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Successfully created an event.");
            } else {
                throw new RuntimeException("Failed to create an event");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while processing event", e);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not parse String as URI", e);
        }
    }

    private String createEventJson(Event event) {
        JsonObject value = Json.createObjectBuilder()
                .add("id", event.getId().toString())
                .add("date", event.getDate())
                .add("time", event.getTime())
                .add("title", event.getTitle())
                .add("description", event.getDescription())
                .add("hostEmail", event.getHostEmail())
                .build();
        return value.toString();
    }

    private void createParticipant(Participant participant) {
        try {
            String apiEndpoint = "http://localhost:3001/api/create-participant";

            URL url = new URI(apiEndpoint).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String participantData = createParticipantJson(participant);
            // Write the JSON data to the request body
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(participantData);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Successfully created a participant.");
            } else {
                throw new RuntimeException("Failed to create a participant");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while creating participant", e);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not parse String as URI", e);
        }
    }

    private String createParticipantJson(Participant participant) {
        JsonObject value = Json.createObjectBuilder()
                .add("id", participant.getParticipantId().toString())
                .add("name", participant.getParticipantName())
                .add("email", participant.getParticipantEmail())
                .build();
        return value.toString();
    }

    private void registerParticipant(UUID eventID, UUID participantID) {
        try {
            String endpoint = "http://localhost:3001/api/register-participant?participantId="
                    + URLEncoder.encode(participantID.toString(), "UTF-8")
                    + "&eventId=" + URLEncoder.encode(eventID.toString(), "UTF-8");

            HttpURLConnection connection = connectToApi(endpoint, "PUT");
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Successfully registered a participant.");
            } else {
                throw new RuntimeException("Failed to register a participant with ID " + participantID + " to event with ID " + eventID);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
}
