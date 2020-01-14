/**
 *
 */
package fr.tse.ProjetInfo3.mvc.dto;

import java.io.Serializable;

import org.h2.engine.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ALAMI IDRISSI Taha
 * @author kamilcaglar
 * This class contains some tweets with with the same #
 */
public class Hashtag implements Serializable {
    private int id;
    private String hashtag;
    private Date lastSearchDate;

    private boolean allTweetsCollected; //true when hashtag have no more tweets. Not called in filter or stop.
    private boolean dateTweetsLimit; //true when there is no more tweets for the until date

    private boolean globalTweetsLimit; //true when date more than 7 days

    private List<Tweet> tweets;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    public boolean isGlobalTweetsLimit() {
        return globalTweetsLimit;
    }

    public void setGlobalTweetsLimit(boolean globalTweetsLimit) {
        this.globalTweetsLimit = globalTweetsLimit;
    }

    public boolean isAllTweetsCollected() {
        return allTweetsCollected;
    }

    public void setAllTweetsCollected(boolean allTweetsCollected) {
        this.allTweetsCollected = allTweetsCollected;
    }

    public boolean isDateTweetsLimit() {
        return dateTweetsLimit;
    }

    public void setDateTweetsLimit(boolean dateTweetsLimit) {
        this.dateTweetsLimit = dateTweetsLimit;
    }

    public Hashtag(String hashtag) {
        super();
        this.hashtag = hashtag;
        this.tweets = new ArrayList<>();
        this.dateTweetsLimit = false;
        this.allTweetsCollected = false;
        this.globalTweetsLimit = false;

    }

    public Hashtag(int id, String hashtag) {
        super();
        this.id = id;
        this.hashtag = hashtag;
        this.tweets = new ArrayList<>();
        this.dateTweetsLimit = false;
        this.allTweetsCollected = false;
        this.globalTweetsLimit = false;
    }

    public Hashtag() {

    }

    public String getHashtag() {
        return hashtag;
    }

    public Long getMaxId() {
        if (tweets.size() != 0) {
            return tweets.get(tweets.size() - 1).getId() - 1;
        } else {
            return null;
        }
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public List<Tweet> getTweets() {
        return tweets;
    }

    public Date getLastSearchDate() {
        return lastSearchDate;
    }

    public void setLastSearchDate(Date lastSearchDate) {
        this.lastSearchDate = lastSearchDate;
    }
}
