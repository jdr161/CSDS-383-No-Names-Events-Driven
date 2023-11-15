package org.example;

import com.github.javafaker.Faker;
import java.util.UUID;

public class ParticipantGenerator {
    static Faker faker = new Faker();
    public static UUID getId(){
        return UUID.randomUUID();
    }
    public static String getName(){
        return faker.name().fullName();
    }
    public static String getEmail(){
        return faker.name().firstName() + "." + faker.name().lastName() + "@" + faker.company().name() + ".com";
    }
}
