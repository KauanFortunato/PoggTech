package com.mordekai.poggtech.utils;

import android.content.Context;

public class NotificationFlagHelper {
    private static final String PREFS_NAME = "message_flags";
    private static final String KEY_NEW_MESSAGE = "has_new_message";

    public static void setNewMessageFlag(Context context, boolean value) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_NEW_MESSAGE, value)
                .apply();
    }

    public static boolean hasNewMessage(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_NEW_MESSAGE, false);
    }

    public static void clearNewMessageFlag(Context context) {
        setNewMessageFlag(context, false);
    }
}
