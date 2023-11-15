package org.nonames;

import com.rabbitmq.client.*;

import javax.json.*;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
            for (Participant p: parseParticipantData(jsonObject)) {
                createParticipant(p);
                UUID eventId = UUID.fromString(jsonObject.getString("eventID"));
                registerParticipant(eventId, p.getParticipantId());
            }
        }
    }

    private JsonObject parseJsonObject(byte[] body){
        InputStream messageBodyStream = new ByteArrayInputStream(body);
        try(JsonReader reader = Json.createReader(messageBodyStream)){
            JsonObject jsonObject = reader.readObject();
            return jsonObject;
        } catch (Exception e) {
            System.out.println("Could not parse JSON object: " + new String(body, StandardCharsets.UTF_8));
            System.exit(-1);
        }
        return null;
    }

    public static Event parseEventData(JsonObject jsonObject){
        UUID uuid = UUID.fromString(jsonObject.getString("id"));
        String date = jsonObject.getString("date");
        String time = jsonObject.getString("time");
        String title = jsonObject.getString("title");
        String description = jsonObject.getString("description");
        String hostEmail = jsonObject.getString("hostEmail");
        return new Event(uuid,date,time,title,description,hostEmail);
    }

    private ArrayList<Participant> parseParticipantData(JsonObject jsonObject){
        return null;
    }

    private void createEvent(Event event) {
//        try {
//            String apiEndpoint = "http://localhost:3001/api/create-event";
//
//            URL url = new URI(apiEndpoint).toURL();
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type", "application/json");
//            connection.setDoOutput(true);
//            // Write the JSON data to the request body
//            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
//                wr.writeBytes(eventData);
//            }
//
//            int responseCode = connection.getResponseCode();
//
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                System.out.println("Successfully created an event.");
//            } else {
//                System.out.println("Failed to create an event");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (URISyntaxException e) {
//
//
//        }
    }

    private void createParticipant(Participant participant){

    }

    private void registerParticipant(UUID eventID, UUID participantID){

    }
}

