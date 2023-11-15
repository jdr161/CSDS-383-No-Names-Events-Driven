package org.example;

import com.github.javafaker.DateAndTime;
import com.github.javafaker.Faker;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class EventGenerator {
    static Faker faker = new Faker();
    static Random random = new Random();
    public static UUID getId(){
        return UUID.randomUUID();
    }
    public static String getDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(faker.date().birthday());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        return String.format("%4d", year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
    }
    public static String getTime(){
        int hours = random.nextInt(1, 13);
        int minutes = random.nextInt(0, 61);
        boolean isMorning = random.nextBoolean();
        String thing;
        if(isMorning){
            thing = "AM";
        } else {
            thing = "PM";
        }
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + " " + thing;
    }
    public static String getTitle(){
        int which = random.nextInt(1, 5);
        String string;
        switch (which){
            case 1:
                string = faker.university().name() + " event";
                break;
            case 2:
                string = faker.esports().team() + " vs. " + faker.esports().team();
                break;
            case 3:
                string = faker.beer().name() + " tasting event";
                break;
            case 4:
                string = faker.chuckNorris().fact();
                break;
            default:
                string = faker.backToTheFuture().quote();
        }
        return string;
    }
    public static String getDescription(){
        return "Event located at: " + faker.address().fullAddress();
    }
    public static String getHostEmail(){
        return faker.name().firstName() + "." + faker.name().lastName() + "@gmail.com";
    }
}
