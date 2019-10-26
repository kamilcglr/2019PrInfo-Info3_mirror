package fr.tse.ProjetInfo3.mwp.services;

import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import fr.tse.ProjetInfo3.mwp.Profile;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * 
 * @author Sergiy
 * This class regroups all the methods used to interact with Twitter using TwitterAPI
 */
public class RequestManager {
	private Twitter twitter;

	public RequestManager(Twitter twitter) {
		this.twitter = twitter;
	}

	/**
	 * 
	 * @param id - identifier of a Twitter user
	 * @return A Profile object
	 * @throws IOException
	 */
	public Profile getProfile(String id) throws IOException {
		Profile profile = null;

		String name;
		String description;
		int followersCount;
		int friendsCount;
		int favouritesCount;
		Image profileImage;

		User user = null;

		try {
			user = twitter.showUser(id);

			name = user.getName();
			description = user.getDescription();
			followersCount = user.getFollowersCount();
			friendsCount = user.getFriendsCount();
			favouritesCount = user.getFavouritesCount();

			URL url = new URL(user.get400x400ProfileImageURL());
			profileImage = ImageIO.read(url);

			profile = new Profile(name, description, followersCount, friendsCount, favouritesCount, profileImage);

		} catch (TwitterException te) {
			te.printStackTrace();
		}

		return profile;
	}

	/**
	 * 
	 * @param request - a String corresponding to the hashtag to find
	 * @return A List of Tweets
	 */
	public List<Status> getTweets(String request) {
		Query query = new Query(request);
		List<Status> tweets = new ArrayList<>();

		try {
			QueryResult qr = twitter.search(query);
			tweets = qr.getTweets();

		} catch (TwitterException e) {
			e.printStackTrace();
		}

		for (Status s : tweets) {
			System.out.println(s.getText());
		}
		System.out.println(tweets.size());
		return tweets;
	}

	// Causes rate limit exceeding
	void getAllTweets(String request) {
		Query query = new Query(request);
		List<Status> tweets = new ArrayList<>();

		while (true) {
			try {
				QueryResult qr = twitter.search(query);
				List<Status> tempTweets = qr.getTweets();

				if (tempTweets == null || tempTweets.isEmpty()) {
					break;
				} else {
					tweets.addAll(tempTweets);
					System.out.println(tweets.size());

					for (Status s : tempTweets) {
						System.out.println(s.getId());
						System.out.println(s.getUser().getName());
					}

				}

			} catch (TwitterException e) {
				e.printStackTrace();
			}

		}
		System.out.println(tweets.size());
	}
}
