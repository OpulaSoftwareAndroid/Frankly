package com.opula.chatapp.model;

import android.net.Uri;

public class ContactVO {
    private Uri ContactImage;
    private String ContactName;
    private String ContactNumber;

    public Uri getContactImage() {
        return ContactImage;
    }

    public void setContactImage(Uri contactImage) {
        ContactImage = contactImage;
    }

    public String getContactName() {
        return ContactName;
    }

    public void setContactName(String contactName) {
        ContactName = contactName;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }
}