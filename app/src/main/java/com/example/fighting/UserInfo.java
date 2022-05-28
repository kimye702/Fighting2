package com.example.fighting;

public class UserInfo {
    private String name;
    private String photoUri;

    public UserInfo(String name, String photoUri){
        this.name=name;
        this.photoUri=photoUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
