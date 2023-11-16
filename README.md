# CSDS-383-No-Names-Events-Driven

## Versions
Web UI: Node v18.12.0 (https://nodejs.org/en/blog/release/v18.12.0) \
CLI: JDK version 17 (https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) \
Event Broker: RabbitMQ version 3.12.8 (WINDOWS - https://www.rabbitmq.com/install-windows.html#chocolatey) \
Databases: PostgresSQL in the cloud via https://aws.amazon.com/rds/

## Explanation of Project
The goal of this project was to develop an application using a microservices architecture. The application had the following requirements:

- Create an application that will act as the event producer.
  - This application should randomly send messages to a message queue in the message-broker platform.
    - Create small batches of 50 to 100 events, where each event has 5 to 10 event participants.
    - This application should directly interface with your project's message-broker.
  - These events should represent events as described as follow, your team can use any strategy to represent and transfer this date:
    - Event Name: Create Event
      - Each event should have the following attributes:
        - Event Id: A UUIDLinks to an external site.. This should be always generated by the producer.
        - Event Date
          - The event date must follow the format: YYYY-MM-DD
        - Event Time: Date of the event.
          - The event time must follow the format: HH:MM AM/PM 
        - Event Title: Title of the event.
          - The event title should not be longer than 255 characters.
        - Event Description: Description of the event.
          - The event title should not be longer than 600 characters.
        - Event Host Email: The email of the host of the event.
          - The event host's email should be valid.
          - Invalid emails should be rejected.
        - If the event "Create Event" is rejected, the message should be marked as a dead-letter for later retrieval.
    - Event Name: Add Participant to Event
      - Each event participant should have the following attributes:
        - Event Participant Id:  A UUID. This should be generated by the producer.
        - Event Id: Id of the event to which the participant is registered to. This should be generated by the producer.
        - Event participant name: Name of the event participant.
          - The name of the event participant should not be longer than 600 characters.
        - Event participant email: Email of the event participant.
          - The event participant's email should be valid.
          - Invalid emails should be rejected.
        - If the event "Add Participant to Event" is rejected, the message should be marked as a dead-letter for later retrieval.
  - The Microservices architecture developed in the previous assignment must be used by the message consumer to process the events.
  - The events should be routed to the corresponding micro-services. This can be configured at the message broker or at the consumer(s) level, depending on the team's design.
  - After the events are processed by the micro-services the data should be made available in the microservices database(s).
  - After the events are processed the events and the event participants should be made available in web application and the CLI developed in the previous assignment.

## How to run the project
Make sure you have the relevant version of Node, Java Development Kit, and RabbitMQ installed (see [Versions Section](#versions))
1. Run the "RabbitMQ Service - Start" app on your computer
2. Event Producer: build the ```./event-producer``` project with Maven and run the ```Main.java``` file or run the jar file via ```java -jar event-producer.jar```
3. Message Consumer: build the ```./message-consumer``` project with Maven and run the ```Main.java``` file or run the jar file via ```java -jar message-consumer.jar```
4. Web User Interface: run the following commands in order at the root of the ```./cli``` directory:
    - ```npm install```
    - ```npm run start```
5. Command Line Interface: build the ```./cli``` project with Maven and run the ```Main.java``` file, or run the jar file via ```java -jar cli.jar```
6. API Gateway: run the following commands in order at the root of the ```./API Gateway``` directory:
    - ```npm install```
    - ```npm run start```
7. Events Microservice: build the ```./events-ms``` project with Maven and run the ```EventsMsApplication.java``` file or run the jar file via ```java -jar events-ms.jar```
8. Participants Microservice: build the ```./participants-ms``` project with Maven and run the ```ParticipantsMsApplication.java``` file or run the jar file via ```java -jar participants-ms.jar```
9. Navigate to ```http://localhost:3000``` or use the command line interface
