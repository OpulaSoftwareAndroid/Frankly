package com.opula.chatapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Chatlist implements Serializable {
    public String id;
    public boolean istyping;
    public boolean isnotification;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getIstyping() {
        return istyping;
    }

    public void setIstyping(boolean istyping) {
        this.istyping = istyping;
    }

    public boolean getIsnotification() {
        return isnotification;
    }

    public void setIsnotification(boolean isnotification) {
        this.isnotification = isnotification;
    }
}
