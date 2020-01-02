package fr.tse.ProjetInfo3.mvc.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.tse.ProjetInfo3.mvc.dao.InterestPointDAO;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.utils.TwitterDateParser;


/**
 * in this class we will perform all the actions that has a relation with the DB and will be
 * cached 
 */
public class DatabaseManager {

	private Gson gson;
	private InterestPointDAO dao;

	public DatabaseManager() {
		super();
	}
	
	/**
     * cache a User on the db
     * @param the user that we want to save (RequestManager.getUser())
     * @param gonna be passed like for this ex : DatabaseManager db = new Data ..() ; --> db.getGsonInstance()
     * @param samething for the interestpoint --> db.getInterestPointDao()
     */
	public void cachingUserData(User user,Gson gson,InterestPointDAO dao) {
		String userJson = gson.toJson(user);
        dao.saveUser(user, userJson);
		
	}
	/**
     * cache a tweet on the db
     * @param the tweet that we want to save 
     * @param gonna be passed like for this ex : DatabaseManager db = new Data ..() ; --> db.getGsonInstance()
     * @param samething for the interestpoint --> db.getInterestPointDao()
     */
	public void cachingTweetData(Tweet tweet , Gson gson , InterestPointDAO dao) {
		String tweetJson = gson.toJson(tweet);
		dao.saveTweetInCache(tweet, tweetJson);
	}
	
	/**
     * cache a hashtag on the db
     * @param the hashtag that we want to save 
     * @param gonna be passed like for this ex : DatabaseManager db = new Data ..() ; --> db.getGsonInstance()
     * @param samething for the interestpoint --> db.getInterestPointDao()
     */
	public void cachingHashtagData(Hashtag hashtag , Gson gson , InterestPointDAO dao) {
		String hashtagJson = gson.toJson(hashtag);
		dao.saveHashtagInCache(hashtag,hashtagJson);
	}
	
	
	public User getCachedUser(String screen_name,InterestPointDAO dao,Gson gson) {
		return dao.getUserFromDatabase(screen_name, gson);
	}
	public Gson getGsonInstance() {
		return new GsonBuilder()
                .setPrettyPrinting() //human-readable json
                .setDateFormat(TwitterDateParser.twitterFormat)
                .create();
	}
	public InterestPointDAO getInterestPointDao() {
		return new InterestPointDAO();
	}
}
