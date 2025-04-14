package com.mordekai.poggtech.data.model;

public class Chat {
    private int owner_id;
    private int chat_id;
    private int id_message;
    private int user_id;
    private int chat_with;
    private String chat_with_name;
    private String chat_with_last_name;
    private int product_id;
    private String product_title;
    private Float product_price;
    private String last_message;
    private String image_product;
    private String last_message_time;
    private String last_message_time_format;
    private int unread_count;

    // Construtor vazio
    public Chat() {}

    // Construtor com parâmetros

    public Chat(int chat_id, int product_id) {
        this.chat_id = chat_id;
        this.product_id = product_id;
    }

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

    public int getowner_id() {
        return owner_id;
    }

    public void setowner_id(int owner_id) {
        this.owner_id = owner_id;
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

    public Float getProduct_price() {
        return product_price;
    }

    public void setProduct_price(Float product_price) {
        this.product_price = product_price;
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

    public int getUnread_count() { return unread_count; }

    public void setUnread_count(int unread_count) { this.unread_count = unread_count; }

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
                ", ownerId=" + owner_id +
                ", chatId=" + chat_id +
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
