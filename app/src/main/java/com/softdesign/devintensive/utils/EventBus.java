package com.softdesign.devintensive.utils;

/**
 * Класс отправки сообщений через Otto
 */
public class EventBus {

    public static class EventAuth {

        private String mMessage;

        public EventAuth(String message) {
            mMessage = message;
        }

        public String getMessage() {
            return mMessage;
        }
    }

    public static class EventLoadToken {
        private String mMessage;

        public EventLoadToken(String message) {
            mMessage = message;
        }

        public String getMessage() {
            return mMessage;
        }
    }

    public static class EventSaveInDbBus {

        private String mMessage;

        public EventSaveInDbBus(String message) {
            mMessage = message;
        }

        public String getMessage() {
            return mMessage;
        }
    }



}
