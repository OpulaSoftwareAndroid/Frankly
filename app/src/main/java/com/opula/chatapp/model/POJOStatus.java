package com.opula.chatapp.model;

import com.opula.chatapp.constant.WsConstant;

import java.util.ArrayList;

public class POJOStatus {

    private String id;
    private String to;
    private String senderID;

    private String receiver;
//    private ArrayList<String> broadcast_receiver;
    private String message;
    private boolean issend;
    private boolean isseen;
    private boolean isimage;
    private boolean iscontact;
    private String imageURL=WsConstant.STATUS_IMAGE_URL;
    private String senderDisplayName=WsConstant.STATUS_SENDER_DISPLAY_NAME;

    private String time;
    private String sender_username;
    private String sender_image;
    private String table_id;
    private String doc_uri;
    private String contact_name;
    private String contact_number;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }




//    public void setImageUrl(String imageURL) {
//        this.imageURL = imageURL;
//    }
//
//    public String getImageUrl() {
//        return imageURL;
//    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

//    public ArrayList<String> getBroadcast_receiver() {
//        return broadcast_receiver;
//    }
//
//    public void setBroadcast_receiver(ArrayList<String> broadcast_receiver) {
//        this.broadcast_receiver = broadcast_receiver;
//    }

    public String getMessage() {
        return message;
    }

    public void setImageUrl(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageUrl() {
        return imageURL;
    }
    public String getSenderDisplayName() {
        return senderDisplayName;
    }

    public void setSenderDisplayName(String senderDisplayName) {
        this.senderDisplayName = senderDisplayName;
    }

    public void setMessage(String message) {
        this.message = WsConstant.STATUS_IMAGE_URL;
    }

    public boolean isIssend() {
        return issend;
    }

    public void setIssend(boolean issend) {
        this.issend = issend;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public boolean isIsimage() {
        return isimage;
    }

    public void setIsimage(boolean isimage) {
        this.isimage = isimage;
    }

    public boolean isIscontact() {
        return iscontact;
    }

    public void setIscontact(boolean iscontact) {
        this.iscontact = iscontact;
    }

    public String getImage() {
        return imageURL;
    }

    public void setImage(String imageUrl) {
        this.imageURL = imageURL;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSender_username() {
        return sender_username;
    }

    public void setSender_username(String sender_username) {
        this.sender_username = sender_username;
    }

    public String getSender_image() {
        return sender_image;
    }

    public void setSender_image(String sender_image) {
        this.sender_image = sender_image;
    }

    public String getTable_id() {
        return table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getDoc_uri() {
        return doc_uri;
    }

    public void setDoc_uri(String doc_uri) {
        this.doc_uri = doc_uri;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }
}
