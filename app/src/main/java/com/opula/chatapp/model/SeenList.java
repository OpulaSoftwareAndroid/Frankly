package com.opula.chatapp.model;

public class SeenList
{
    private String textViewUserName;
    private String strProfileImageUrl;

    public SeenList() {
    }

    public SeenList(String textViewUserName, String strProfileImageUrl)
    {
            this.textViewUserName = textViewUserName;
            this.strProfileImageUrl = strProfileImageUrl;
    }
    public String getTextViewUserName()
    {
            return textViewUserName;
    }

    public void setTextViewUserName(String textViewUserName) {
            this.textViewUserName = textViewUserName;
    }

    public String getProfileImageUrl() {
            return strProfileImageUrl;
    }

    public void setProfileImageUrl(String strProfileImageUrl) {
            this.strProfileImageUrl = strProfileImageUrl;
    }

}
