package fr.tse.ProjetInfo3.mwp.dao;

import java.net.URL;

public class UserTest {
    private int id;
    private String name;
    private String screen_name;
    private String location;
    private String description;
    private URL url;
    private int statuses_count;
    private Boolean verified;

    @Override
    public String toString() {
        return "UserTest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", screen_name='" + screen_name + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", url=" + url +
                ", statuses_count=" + statuses_count +
                ", verified=" + verified +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public URL getUrl() {
        return url;
    }

    public int getStatuses_count() {
        return statuses_count;
    }

    public Boolean getVerified() {
        return verified;
    }
}
