package com.mordekai.poggtech.utils;

public class MessageNotifier {
    private static MessageListener  listener;

    public static void setListener(MessageListener newListener) {
        listener = newListener;
    }

    public static void notifyMessage() {
        if (listener != null) {
            listener.onNewMessage();
        }
    }

    public static void clearListener() {
        listener = null;
    }
}
