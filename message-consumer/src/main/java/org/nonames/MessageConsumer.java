package org.nonames;

import com.rabbitmq.client.*;

import javax.json.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


public class MessageConsumer {

    private final static String QUEUE_NAME = "hello";

    public void listen() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> onDelivery(consumerTag, delivery);
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }

    private void onDelivery(String consumerTag, Delivery delivery){
        JsonObject jsonObject = parseJsonObject(delivery.getBody());
        // if is a create-event message
        if(jsonObject.isNull("participantID")){
            createEvent(parseEventData(jsonObject));
        } else { // if is a register-participant message
            Participant p = parseParticipantData(jsonObject);
            createParticipant(p);
            UUID eventId = UUID.fromString(jsonObject.getString("eventID"));
            registerParticipant(eventId, p.getParticipantId());
        }
    }

    private JsonObject parseJsonObject(byte[] body){
        InputStream messageBodyStream = new ByteArrayInputStream(body);
        try(JsonReader reader = Json.createReader(messageBodyStream)){
            JsonObject jsonObject = reader.readObject();
            return jsonObject;
        } catch (Exception e) {
            System.out.println("Could not parse JSON object: " + new String(body, StandardCharsets.UTF_8));
        }
        return null;
    }

    private Event parseEventData(JsonObject jsonObject){
        UUID uuid = UUID.fromString(jsonObject.getString("id"));
        String date = jsonObject.getString("date");
        String time = jsonObject.getString("time");
        String title = jsonObject.getString("title");
        String description = jsonObject.getString("description");
        String hostEmail = jsonObject.getString("hostEmail");
        return new Event(uuid,date,time,title,description,hostEmail);
    }


    private Participant parseParticipantData(JsonObject jsonObject){
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
                // TODO: mark message as dead-letter (idk what that means)
                System.out.println("Failed to create an event");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private String createEventJson(Event event){
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

    private void createParticipant(Participant participant){
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
                // TODO: mark message as dead-letter (idk what that means)
                System.out.println("Failed to create a participant");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private String createParticipantJson(Participant participant){
        JsonObject value = Json.createObjectBuilder()
                .add("id", participant.getParticipantId().toString())
                .add("name", participant.getParticipantName())
                .add("email", participant.getParticipantEmail())
                .build();
        return value.toString();
    }

    private void registerParticipant(UUID eventID, UUID participantID){
        try {
            String endpoint = "http://localhost:3001/api/register-participant?participantId="
                    + URLEncoder.encode(participantID.toString(), "UTF-8")
                    + "&eventId=" + URLEncoder.encode(eventID.toString(), "UTF-8");

            HttpURLConnection connection = connectToApi(endpoint, "PUT");
            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                // TODO: mark message as dead-letter (idk what that means)
            } else {
                System.out.println("Successfully registered a participant.");
            }
        } catch (IOException e) {
            e.printStackTrace();
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

