package org.example;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public class Main {
    public static void main(String[] args) throws IOException, TimeoutException, ConfigurationException {
        System.out.println("Hello world!");

        PropertiesConfiguration config = new PropertiesConfiguration();
        config.load("application.properties");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(config.getString("USERNAME"));
        factory.setPassword(config.getString("PASSWORD"));
        factory.setVirtualHost(config.getString("VIRTUAL_HOST"));
        factory.setHost(config.getString("HOST"));
        factory.setPort(config.getInt("PORT"));

        Connection conn = factory.newConnection();
    }
}