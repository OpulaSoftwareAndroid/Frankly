package com.opula.chatapp.model;

import java.util.ArrayList;

public class GroupUser {

    private String groupName;
    private String groupId;
    private String memberCount;
    private String imageURL;
    private String groupAdmin;
    private ArrayList<String> memberList;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(String memberCount) {
        this.memberCount = memberCount;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public ArrayList<String> getMemberList() {
        return memberList;
    }

    public void setMemberList(ArrayList<String> memberList) {
        this.memberList = memberList;
    }

    public String getGroupAdmin() {
        return groupAdmin;
    }

    public void setGroupAdmin(String groupAdmin) {
        this.groupAdmin = groupAdmin;
    }
}
