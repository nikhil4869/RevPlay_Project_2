package com.example.demo.dto.music;


public class UserProfileDTO {

    private String name;
    private String email;
    private String bio;
    private String profileImage;

    public UserProfileDTO() {}

    public UserProfileDTO(String name, String email, String bio, String profileImage) {
        this.name = name;
        this.email = email;
        this.bio = bio;
        this.profileImage = profileImage;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
}
