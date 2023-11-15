package org.nonames;

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