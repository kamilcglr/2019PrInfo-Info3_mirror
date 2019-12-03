/**
 *
 */
package fr.tse.ProjetInfo3.mvc.dto;

import java.io.Serializable;

import org.h2.engine.User;

import java.util.List;

/**
 * @author ALAMI IDRISSI Taha
 * @author kamilcaglar
 * This class contains some tweets with with the same #
 */
public class Hashtag implements Serializable {
    private int id;
    private String hashtag;

    private List<Tweet> tweets;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Hashtag(int id, String hashtag) {
        super();
        this.id = id;
        this.hashtag = hashtag;
    }

    public Hashtag(String hashtag) {
        super();
        this.hashtag = hashtag;
    }

    public Hashtag() {

    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public List<Tweet> getTweets() {
        return tweets;
    }

}
