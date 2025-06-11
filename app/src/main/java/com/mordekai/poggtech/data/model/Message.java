package com.mordekai.poggtech.data.model;

public class Message {

    private int id_message;
    private int chat_id;
    private int owner_id;
    private int sender_id;
    private String sender_name;
    private int receiver_id;
    private String receiver_name;
    private int product_id;
    private String product_title;
    private String message;
    private String timestamp;
    private String timestamp_format;
    private int is_read;

    // Construtor vazio
    public Message() {
    }

    // Construtor com parâmetros
    public Message(int id_message, int owner_id, int sender_id, String sender_name,
                   int receiver_id, String receiver_name, int product_id,
                   String product_title, String message, String timestamp, String timestamp_format) {
        this.id_message = id_message;
        this.owner_id = owner_id;
        this.sender_id = sender_id;
        this.sender_name = sender_name;
        this.receiver_id = receiver_id;
        this.receiver_name = receiver_name;
        this.product_id = product_id;
        this.product_title = product_title;
        this.message = message;
        this.timestamp = timestamp;
        this.timestamp_format = timestamp_format;
    }

    // Getters e Setters

    public int getChat_id() {
        return chat_id;
    }

    public void setChat_id(int chat_id) {
        this.chat_id = chat_id;
    }

    public int getId_message() {
        return id_message;
    }

    public void setId_message(int id_message) {
        this.id_message = id_message;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp_format() {
        return timestamp_format;
    }

    public void setTimestamp_format(String timestamp_format) {
        this.timestamp_format = timestamp_format;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    // Método toString() para facilitar o debug
    @Override
    public String toString() {
        return "Message{" +
                "id_message=" + id_message +
                ", owner_id=" + owner_id +
                ", sender_id=" + sender_id +
                ", sender_name='" + sender_name + '\'' +
                ", receiver_id=" + receiver_id +
                ", receiver_name='" + receiver_name + '\'' +
                ", product_id=" + product_id +
                ", product_title='" + product_title + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", timestamp_format='" + timestamp_format + '\'' +
                '}';
    }
}
