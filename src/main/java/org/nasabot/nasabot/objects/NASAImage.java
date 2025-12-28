package org.nasabot.nasabot.objects;

public class NASAImage {

    private final String title;
    private final String nasaId;
    private final String description;
    private final String date;
    private final String location;
    private final String imageLink;

    public NASAImage(String title, String nasaId, String description, String date, String location, String imageLink) {
        this.title = title;
        this.nasaId = nasaId;
        this.description = description;
        this.date = date;
        this.location = location;
        this.imageLink = imageLink;
    }

    public String getTitle() {
        return title;
    }

    public String getNasaId() {
        return nasaId;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getLocation() {
        return location;
    }
}
