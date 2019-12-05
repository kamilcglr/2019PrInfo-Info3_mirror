package fr.tse.ProjetInfo3.mvc.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    public User(long id, String name, String screen_name, String location, String description, String url,
			Boolean verified, long followers_count, long friends_count, long listed_count, long favourites_count,
			long statuses_count, String created_at, String profile_banner_url, String profile_image_url_https,
			List<Tweet> listoftweets) {
		super();
		this.id = id;
		this.name = name;
		this.screen_name = screen_name;
		this.location = location;
		this.description = description;
		this.url = url;
		this.verified = verified;
		this.followers_count = followers_count;
		this.friends_count = friends_count;
		this.listed_count = listed_count;
		this.favourites_count = favourites_count;
		this.statuses_count = statuses_count;
		this.created_at = created_at;
		this.profile_banner_url = profile_banner_url;
		this.profile_image_url_https = profile_image_url_https;
		this.listoftweets = listoftweets;
	}
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

    //Id is sufficient and necessary because twitter ensure us that it is unique
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
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
    
    public  Date parseTwitterUTC() 
    		throws ParseException {
    	
    	 	String twitterFormat="EEE MMM dd HH:mm:ss ZZZZZ yyyy";

    	 	// Important note. Only ENGLISH Locale works.
    		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
    		sf.setLenient(true);

    	  	return sf.parse(this.getCreated_at());
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
        //We want the original picture, so we susbtract the _normal.jpg
        return profile_image_url_https.replace("_normal", "");
    }

    public void setProfile_image_url_https(String profile_image_url_https) {
        this.profile_image_url_https = profile_image_url_https;
    }

    public void setListoftweets(List<Tweet> listoftweets) {
        this.listoftweets = listoftweets;
    }
}
