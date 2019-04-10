package com.opula.chatapp.model;

import java.util.ArrayList;

public class Chat {

    private String id;
    private String to;
    private String sender;
    private String receiver;
    private ArrayList<String> broadcast_receiver;
    private String message;
    private String isseenby;
    private String isseentime;

    private boolean issend;
    private String isstatus;

    private boolean isseen;
    private boolean isreceived;
    private boolean isrepliedmessage;


    private boolean isimage;
    private boolean iscontact;
    private boolean issecure;

    private boolean isaudio;
    private String image;
    private String isrepliedmessageid;
    private String isrepliedmessageby;

    private String time;
    private String sender_username;
    private String sender_image;
    private String table_id;
    private String doc_uri;
    private String audio_uri;

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

    public ArrayList<String> getBroadcast_receiver() {
        return broadcast_receiver;
    }

    public void setBroadcast_receiver(ArrayList<String> broadcast_receiver) {
        this.broadcast_receiver = broadcast_receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIsseenby() {
        return isseenby;
    }

    public void setIsseenby(String isseenby) {
        this.isseenby = isseenby;
    }

    public String getIsseentime() {
        return isseentime;
    }

    public void setIsseentime(String isseentime) {
        this.isseentime = isseentime;
    }

    public boolean isIssend() {
        return issend;
    }

    public void setIssend(boolean issend) {
        this.issend = issend;
    }



    public String getIsstatus() {
        return isstatus;
    }

    public void setIsstatus(String isstatus) {
        this.isstatus = isstatus;
    }



    public boolean isIsreceived() {
        return isreceived;
    }

    public void setIsreceived(boolean isreceived) {
        this.isreceived = isreceived;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public boolean isIsrepliedmessage() {
        return isrepliedmessage;
    }

    public void setIsrepliedmessage(boolean isrepliedmessage) {
        this.isrepliedmessage = isrepliedmessage;
    }

    public String isIsrepliedmessageid() {
        return isrepliedmessageid;
    }

    public void setIsrepliedmessageid(String isrepliedmessageid) {
        this.isrepliedmessageid = isrepliedmessageid;
    }

    public String isIsrepliedmessageby() {
        return isrepliedmessageby;
    }

    public void setIsrepliedmessageby(String isrepliedmessageby) {
        this.isrepliedmessageby = isrepliedmessageby;
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

    public void setIssecure(boolean issecure) {
        this.issecure = issecure;
    }

    public boolean getIssecure() {
        return issecure;
    }

    public void setIscontact(boolean iscontact) {
        this.iscontact = iscontact;
    }

    public boolean isIsaudio() {
        return isaudio;
    }

    public void setIsaudio(boolean isaudio) {
        this.isaudio = isaudio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getAudio_uri() {
        return audio_uri;
    }

    public void setAudio_uri(String audio_uri) {
        this.audio_uri = audio_uri;
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
