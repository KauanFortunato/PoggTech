package com.mordekai.poggtech.data.model;

public class MessageDateSeparator extends Message {
    private final String dateLabel;

    public MessageDateSeparator(String dateLabel) {
        this.dateLabel = dateLabel;
    }

    public String getDateLabel() {
        return dateLabel;
    }
}
