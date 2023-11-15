package org.nonames;

import javax.json.Json;
import javax.json.JsonArray;

public class Main {
    public static void main(String[] args) {
        try {
            MessageConsumer messageConsumer = new MessageConsumer();
            messageConsumer.listen();
        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
}