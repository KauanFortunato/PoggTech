package com.mordekai.poggtech.data.model;

public class Message {
    private int id_message;
    private int sender_id;
    private int receiver_id;
    private String message;
    private String timestamp;

    // Getters e Setters
    public int getId_message() { return id_message; }
    public void setId_message(int id_message) { this.id_message = id_message; }

    public int getSender_id() { return sender_id; }
    public void setSender_id(int sender_id) { this.sender_id = sender_id; }

    public int getReceiver_id() { return receiver_id; }
    public void setReceiver_id(int receiver_id) { this.receiver_id = receiver_id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String timestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
