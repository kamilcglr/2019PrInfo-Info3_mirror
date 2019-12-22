package fr.tse.ProjetInfo3.mvc.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.tse.ProjetInfo3.mvc.dao.InterestPointDAO;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.utils.TwitterDateParser;

public class DatabaseManager {

	private Gson gson;
	private InterestPointDAO dao;

	public DatabaseManager() {
		super();
	}
	
	public void cachingUserData(User user,Gson gson,InterestPointDAO dao) {
		String userJson = gson.toJson(user);
        dao.saveUser(user, userJson);
		
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
