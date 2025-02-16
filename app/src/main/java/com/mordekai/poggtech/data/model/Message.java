package com.mordekai.poggtech.data.model;

public class Message {

    // ToDo: Trocar o nome das variáveis, pois estão todas erradas e não vão funcionar com o retrofit

    private int idMessage;
    private int ownerId;
    private int senderId;
    private String senderName;
    private String senderLastName;
    private int receiverId;
    private String receiverName;
    private String receiverLastName;
    private int productId;
    private String productTitle;
    private String message;
    private String timestamp;
    private String timestampFormat;

    // Construtor vazio
    public Message() {}

    // Construtor com parâmetros
    public Message(int idMessage, int ownerId, int senderId, String senderName, String senderLastName,
                   int receiverId, String receiverName, String receiverLastName, int productId,
                   String productTitle, String message, String timestamp, String timestampFormat) {
        this.idMessage = idMessage;
        this.ownerId = ownerId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderLastName = senderLastName;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.receiverLastName = receiverLastName;
        this.productId = productId;
        this.productTitle = productTitle;
        this.message = message;
        this.timestamp = timestamp;
        this.timestampFormat = timestampFormat;
    }

    // Getters e Setters
    public int getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(int idMessage) {
        this.idMessage = idMessage;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderLastName() {
        return senderLastName;
    }

    public void setSenderLastName(String senderLastName) {
        this.senderLastName = senderLastName;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverLastName() {
        return receiverLastName;
    }

    public void setReceiverLastName(String receiverLastName) {
        this.receiverLastName = receiverLastName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
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

    public String getTimestampFormat() {
        return timestampFormat;
    }

    public void setTimestampFormat(String timestampFormat) {
        this.timestampFormat = timestampFormat;
    }

    // Métdo toString() para facilitar o debug
    @Override
    public String toString() {
        return "Message{" +
                "idMessage=" + idMessage +
                ", ownerId=" + ownerId +
                ", senderId=" + senderId +
                ", senderName='" + senderName + '\'' +
                ", senderLastName='" + senderLastName + '\'' +
                ", receiverId=" + receiverId +
                ", receiverName='" + receiverName + '\'' +
                ", receiverLastName='" + receiverLastName + '\'' +
                ", productId=" + productId +
                ", productTitle='" + productTitle + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", timestampFormat='" + timestampFormat + '\'' +
                '}';
    }
}
