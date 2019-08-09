package com.grobo.notifications.admin.clubevents;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClubEventItem {

    public class ClubEventSuper {

        @SerializedName("events")
        @Expose
        private List<ClubEventItem> events = null;

        public List<ClubEventItem> getEvents() {
            return events;
        }

        public void setEvents(List<ClubEventItem> events) {
            this.events = events;
        }

    }

    public class RelatedClub {

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("bio")
        @Expose
        private String bio;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }
    }

    @SerializedName("coordinators")
    @Expose
    private List<Object> coordinators = null;
    @SerializedName("postLinks")
    @Expose
    private List<Object> postLinks = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("relatedClub")
    @Expose
    private RelatedClub relatedClub;
    @SerializedName("venue")
    @Expose
    private String venue;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("date")
    @Expose
    private long date;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    public List<Object> getCoordinators() {
        return coordinators;
    }

    public void setCoordinators(List<Object> coordinators) {
        this.coordinators = coordinators;
    }

    public List<Object> getPostLinks() {
        return postLinks;
    }

    public void setPostLinks(List<Object> postLinks) {
        this.postLinks = postLinks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RelatedClub getRelatedClub() {
        return relatedClub;
    }

    public void setRelatedClub(RelatedClub relatedClub) {
        this.relatedClub = relatedClub;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
