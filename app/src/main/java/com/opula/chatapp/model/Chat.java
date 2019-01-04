package com.opula.chatapp.model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private boolean isseen;
    private boolean isimage;
    private String image;

    public Chat(String sender, String receiver, String message, boolean isseen, boolean isimage, String image) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.isimage = isimage;
        this.image = image;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getisIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public boolean getisIsimage() {
        return isimage;
    }

    public void setIsimage(boolean isimage) {
        this.isimage = isimage;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
