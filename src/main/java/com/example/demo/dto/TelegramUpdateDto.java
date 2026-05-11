package com.example.demo.dto;

public class TelegramUpdateDto {

    private Long update_id;
    private Message message;

    public Long getUpdate_id() {
        return update_id;
    }

    public void setUpdate_id(Long update_id) {
        this.update_id = update_id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public static class Message {
        private Long message_id;
        private Chat chat;
        private From from;
        private String text;

        public Long getMessage_id() {
            return message_id;
        }

        public void setMessage_id(Long message_id) {
            this.message_id = message_id;
        }

        public Chat getChat() {
            return chat;
        }

        public void setChat(Chat chat) {
            this.chat = chat;
        }

        public From getFrom() {
            return from;
        }

        public void setFrom(From from) {
            this.from = from;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class Chat {
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    public static class From {
        private Long id;
        private String username;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}