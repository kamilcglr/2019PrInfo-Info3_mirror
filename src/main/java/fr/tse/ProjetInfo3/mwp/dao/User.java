package fr.tse.ProjetInfo3.mwp.dao;

import java.util.List;

/**
 * @author kamilcaglar
 * This class is fed by the request manager
 * THE NAME OF ATTRIBUTES MUST BE THE SAME WIT TWITTER RESPONSE
 * https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/user-object
 * We can add more attributes or delete if they are unnecessary
 */
public class User {
    //Twitter attributes (same name)
    private long id;
    private String name;
    private String screen_name;
    private String location;
    private String description;
    private String url;
    private Boolean verified;
    private long followers_count;
    private long friends_count;
    private long listed_count;
    private long favourites_count;
    private long statuses_count;
    private String created_at;
    private String profile_banner_url;
    private String profile_image_url_https;


    /**
     * Our own attributes, they are not alimented by the API
     */
    private List<Tweet> listoftweets;

    public List<Tweet> getListoftweets() {
        return listoftweets;
    }
    /* ************************************************** */

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", screen_name='" + screen_name + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", verified=" + verified +
                ", followers_count=" + followers_count +
                ", friends_count=" + friends_count +
                ", listed_count=" + listed_count +
                ", favourites_count=" + favourites_count +
                ", statuses_count=" + statuses_count +
                ", created_at='" + created_at + '\'' +
                ", profile_banner_url='" + profile_banner_url + '\'' +
                ", profile_image_url_https='" + profile_image_url_https + '\'' +
                ", listoftweets=" + listoftweets +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (followers_count != user.followers_count) return false;
        if (friends_count != user.friends_count) return false;
        if (listed_count != user.listed_count) return false;
        if (favourites_count != user.favourites_count) return false;
        if (statuses_count != user.statuses_count) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (screen_name != null ? !screen_name.equals(user.screen_name) : user.screen_name != null) return false;
        if (location != null ? !location.equals(user.location) : user.location != null) return false;
        if (description != null ? !description.equals(user.description) : user.description != null) return false;
        if (url != null ? !url.equals(user.url) : user.url != null) return false;
        if (verified != null ? !verified.equals(user.verified) : user.verified != null) return false;
        if (created_at != null ? !created_at.equals(user.created_at) : user.created_at != null) return false;
        if (profile_banner_url != null ? !profile_banner_url.equals(user.profile_banner_url) : user.profile_banner_url != null)
            return false;
        if (profile_image_url_https != null ? !profile_image_url_https.equals(user.profile_image_url_https) : user.profile_image_url_https != null)
            return false;
        return listoftweets != null ? listoftweets.equals(user.listoftweets) : user.listoftweets == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (screen_name != null ? screen_name.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (verified != null ? verified.hashCode() : 0);
        result = 31 * result + (int) (followers_count ^ (followers_count >>> 32));
        result = 31 * result + (int) (friends_count ^ (friends_count >>> 32));
        result = 31 * result + (int) (listed_count ^ (listed_count >>> 32));
        result = 31 * result + (int) (favourites_count ^ (favourites_count >>> 32));
        result = 31 * result + (int) (statuses_count ^ (statuses_count >>> 32));
        result = 31 * result + (created_at != null ? created_at.hashCode() : 0);
        result = 31 * result + (profile_banner_url != null ? profile_banner_url.hashCode() : 0);
        result = 31 * result + (profile_image_url_https != null ? profile_image_url_https.hashCode() : 0);
        result = 31 * result + (listoftweets != null ? listoftweets.hashCode() : 0);
        return result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public long getFollowers_count() {
        return followers_count;
    }

    public void setFollowers_count(long followers_count) {
        this.followers_count = followers_count;
    }

    public long getFriends_count() {
        return friends_count;
    }

    public void setFriends_count(long friends_count) {
        this.friends_count = friends_count;
    }

    public long getListed_count() {
        return listed_count;
    }

    public void setListed_count(long listed_count) {
        this.listed_count = listed_count;
    }

    public long getFavourites_count() {
        return favourites_count;
    }

    public void setFavourites_count(long favourites_count) {
        this.favourites_count = favourites_count;
    }

    public long getStatuses_count() {
        return statuses_count;
    }

    public void setStatuses_count(long statuses_count) {
        this.statuses_count = statuses_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getProfile_banner_url() {
        return profile_banner_url;
    }

    public void setProfile_banner_url(String profile_banner_url) {
        this.profile_banner_url = profile_banner_url;
    }

    public String getProfile_image_url_https() {
        return profile_image_url_https;
    }

    public void setProfile_image_url_https(String profile_image_url_https) {
        this.profile_image_url_https = profile_image_url_https;
    }

    public void setListoftweets(List<Tweet> listoftweets) {
        this.listoftweets = listoftweets;
    }
}
