package com.driver;

public class Group {
    private String name;
    private int numberOfParticipants;
    private int numberOfMessages;

    public Group() {
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    public void setNumberOfMessages(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }

    public Group(String name, int numberOfParticipants, int numberOfMessages) {
        this.name = name;
        this.numberOfParticipants = numberOfParticipants;
        this.numberOfMessages=numberOfMessages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfParticipants() {
        return numberOfParticipants;
    }

    public void setNumberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }
}
