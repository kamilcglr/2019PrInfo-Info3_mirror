/**
 * 
 */
package fr.tse.ProjetInfo3.mwp.dao;

import java.util.List;

/**
 * @author ALAMI IDRISSI Taha
 * @author kamilcaglar
 * This class contains some tweets with with the same #
 */
public class Hashtag {
	private String hashtag;
	private List<Hashtag> tweets;


	public String getHashtag() {
		return hashtag;
	}

	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}

	public List<Hashtag> getTweets() {
		return tweets;
	}

	public void setTweets(List<Hashtag> tweets) {
		this.tweets = tweets;
	}

	public Hashtag(String hashtag, List<Hashtag> tweets) {
		this.hashtag = hashtag;
		this.tweets = tweets;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Hashtag hashtag1 = (Hashtag) o;

		if (hashtag != null ? !hashtag.equals(hashtag1.hashtag) : hashtag1.hashtag != null) return false;
		return tweets != null ? tweets.equals(hashtag1.tweets) : hashtag1.tweets == null;
	}

	@Override
	public int hashCode() {
		int result = hashtag != null ? hashtag.hashCode() : 0;
		result = 31 * result + (tweets != null ? tweets.hashCode() : 0);
		return result;
	}
}
