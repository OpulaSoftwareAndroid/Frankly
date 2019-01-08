package com.opula.chatapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Chatlist implements Serializable {
    public String id;
    public boolean istyping;
    public ArrayList<String> group;

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

    public ArrayList<String> getGroup() {
        return group;
    }

    public void setGroup(ArrayList<String> group) {
        this.group = group;
    }
}
