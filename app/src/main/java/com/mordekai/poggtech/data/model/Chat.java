package com.mordekai.poggtech.data.model;

import com.google.gson.annotations.SerializedName;

public class Chat {

    @SerializedName("owner_id")
    private int ownerId;

    @SerializedName("chat_id")
    private int chatId;

    @SerializedName("id_message")
    private int idMessage;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("chat_with")
    private int chatWith;

    @SerializedName("chat_with_name")
    private String chatWithName;

    @SerializedName("product_id")
    private int productId;

    @SerializedName("product_title")
    private String productTitle;

    @SerializedName("product_price")
    private Float productPrice;

    @SerializedName("last_message")
    private String lastMessage;

    @SerializedName("cover_product")
    private String coverProduct;

    @SerializedName("last_message_time")
    private String lastMessageTime;

    @SerializedName("last_message_time_format")
    private String lastMessageTimeFormat;

    @SerializedName("unread_count")
    private int unreadCount;

    // Getters and Setters

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public int getChatId() { return chatId; }
    public void setChatId(int chatId) { this.chatId = chatId; }

    public int getIdMessage() { return idMessage; }
    public void setIdMessage(int idMessage) { this.idMessage = idMessage; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getChatWith() { return chatWith; }
    public void setChatWith(int chatWith) { this.chatWith = chatWith; }

    public String getChatWithName() { return chatWithName; }
    public void setChatWithName(String chatWithName) { this.chatWithName = chatWithName; }


    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }

    public Float getProductPrice() { return productPrice; }
    public void setProductPrice(Float productPrice) { this.productPrice = productPrice; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public String getCoverProduct() { return coverProduct; }
    public void setCoverProduct(String coverProduct) { this.coverProduct = coverProduct; }

    public String getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(String lastMessageTime) { this.lastMessageTime = lastMessageTime; }

    public String getLastMessageTimeFormat() { return lastMessageTimeFormat; }
    public void setLastMessageTimeFormat(String lastMessageTimeFormat) { this.lastMessageTimeFormat = lastMessageTimeFormat; }

    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
}
