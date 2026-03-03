package com.example.demo.dto.playlist;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaylistDTO {

    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("isPublic")
    private Boolean isPublic;
    
    @JsonProperty("ownerName")
    private String ownerName;

    @JsonProperty("followedStatus")
    private Boolean followedStatus;

    public PlaylistDTO() {}

    public PlaylistDTO(Long id, String name, String description, Boolean isPublic, String ownerName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.ownerName = ownerName;
    }

    public PlaylistDTO(Long id, String name, String description, Boolean isPublic, String ownerName, Boolean followedStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.ownerName = ownerName;
        this.followedStatus = followedStatus;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public Boolean getFollowedStatus() { return followedStatus; }
    public void setFollowedStatus(Boolean followedStatus) { this.followedStatus = followedStatus; }
}