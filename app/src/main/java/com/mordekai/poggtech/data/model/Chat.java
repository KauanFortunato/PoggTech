package com.mordekai.poggtech.data.model;

public class Chat {
    private int id_message;
    private int user_id;
    private int chat_with;
    private String chat_with_name;
    private String chat_with_last_name;
    private int product_id;
    private String product_title;
    private String last_message;
    private String image_product;
    private String last_message_time;
    private String last_message_time_format;

    // Construtor vazio
    public Chat() {}

    // Construtor com parâmetros
    public Chat(int id_message, int user_id, int chat_with, String chat_with_name, String chat_with_last_name,
                int product_id, String product_title, String last_message, String last_message_time,
                String last_message_time_format) {
        this.id_message = id_message;
        this.user_id = user_id;
        this.chat_with = chat_with;
        this.chat_with_name = chat_with_name;
        this.chat_with_last_name = chat_with_last_name;
        this.product_id = product_id;
        this.product_title = product_title;
        this.last_message = last_message;
        this.last_message_time = last_message_time;
        this.last_message_time_format = last_message_time_format;
    }

    // Getters e Setters
    public int getId_message() {
        return id_message;
    }

    public void setId_message(int id_message) {
        this.id_message = id_message;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getChat_with() {
        return chat_with;
    }

    public void setChat_with(int chat_with) {
        this.chat_with = chat_with;
    }

    public String getChat_with_name() {
        return chat_with_name;
    }

    public void setChat_with_name(String chat_with_name) {
        this.chat_with_name = chat_with_name;
    }

    public String getChat_with_last_name() {
        return chat_with_last_name;
    }

    public void setChat_with_last_name(String chat_with_last_name) {
        this.chat_with_last_name = chat_with_last_name;
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

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getImage_product() {
        return image_product;
    }

    public String getLast_message_time() {
        return last_message_time;
    }

    public void setLast_message_time(String last_message_time) {
        this.last_message_time = last_message_time;
    }

    public String getLast_message_time_format() {
        return last_message_time_format;
    }

    public void setLast_message_time_format(String last_message_time_format) {
        this.last_message_time_format = last_message_time_format;
    }

    // Método toString() para facilitar o debug
    @Override
    public String toString() {
        return "Chat{" +
                "idMessage=" + id_message +
                ", userId=" + user_id +
                ", chatWith=" + chat_with +
                ", chatWithName='" + chat_with_name + '\'' +
                ", chatWithLastName='" + chat_with_last_name + '\'' +
                ", productId=" + product_id +
                ", productTitle='" + product_title + '\'' +
                ", lastMessage='" + last_message + '\'' +
                ", lastMessageTime='" + last_message_time + '\'' +
                ", lastMessageTimeFormat='" + last_message_time_format + '\'' +
                '}';
    }
}
