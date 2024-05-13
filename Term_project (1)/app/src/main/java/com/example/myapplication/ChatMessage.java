package com.example.myapplication;

import java.util.Date;

public class ChatMessage {
    private String senderId;
    private String recipientId;
    private String text;
    private Date timestamp;
    private String senderName;
    private String senderPhotoUrl;
    private String receiverPhotoUrl;

    String messageId;

    public ChatMessage() {
    }

    public ChatMessage(String senderId, String recipientId, String text, Date timestamp, String senderName, String senderPhotoUrl, String messageId) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.text = text;
        this.timestamp = timestamp;
        this.senderName = senderName;
        this.senderPhotoUrl = senderPhotoUrl;
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhotoUrl() {
        return senderPhotoUrl;
    }

    public void setSenderPhotoUrl(String senderPhotoUrl) {
        this.senderPhotoUrl = senderPhotoUrl;
    }

    public String getReceiverPhotoUrl() {
        return receiverPhotoUrl;
    }

    public void setReceiverPhotoUrl(String receiverPhotoUrl) {
        this.receiverPhotoUrl = receiverPhotoUrl;
    }

    public boolean isSender(String currentUserId) {
        return senderId.equals(currentUserId);
    }

    public String getMessageId() {
        return messageId;

    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}

