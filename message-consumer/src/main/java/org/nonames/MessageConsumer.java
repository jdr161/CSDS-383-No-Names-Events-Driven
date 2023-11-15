package org.nonames;

import com.rabbitmq.client.*;

import javax.json.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
        JsonArray jsonArray = parseJsonArray(delivery.getBody());
        for (JsonValue jsonValue : jsonArray) {
            if(jsonValue.getValueType() != JsonValue.ValueType.OBJECT){
                System.out.println("Expected JsonValue.ValueType.OBJECT, received " + jsonValue.getValueType());
            }
            JsonObject jsonObject = (JsonObject) jsonValue;

            Event e = parseEventData(jsonObject);
            createEvent(e);

            ArrayList<Participant> participantsList = parseParticipantsData(jsonObject);
            for(Participant p : participantsList) {
                createParticipant(p);
                registerParticipant(e.getId(), p.getParticipantId());
            }
        }

    }

    private JsonArray parseJsonArray(byte[] body){
        InputStream messageBodyStream = new ByteArrayInputStream(body);
        try(JsonReader reader = Json.createReader(messageBodyStream)){
            JsonArray jsonArray = reader.readArray();
            return jsonArray;
        } catch (Exception e) {
            System.out.println("Could not parse JSON array: " + new String(body, StandardCharsets.UTF_8));
            System.exit(-1);
        }
        return null;
    }

    private Event parseEventData(JsonObject jsonObject){
        return null;
    }

    private ArrayList<Participant> parseParticipantsData(JsonObject jsonObject){
        return null;
    }

    private void createEvent(Event event){

    }

    private void createParticipant(Participant participant){

    }

    private void registerParticipant(UUID eventID, UUID participantID){

    }
}

