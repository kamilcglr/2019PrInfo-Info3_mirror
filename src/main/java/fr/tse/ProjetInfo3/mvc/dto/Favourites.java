/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Laïla
 *
 */
public class Favourites {
	private int favouritesID;
	private int user_id;
    private List<Hashtag> hashtags;
    private List<User> users;

    public Favourites(){
    	hashtags= new ArrayList<>();
    	users = new ArrayList<>();
	}

    public void addUser(User user){
    	this.users.add(user);
	}

	public void addHashtag(Hashtag hashtag){
		this.hashtags.add(hashtag);
	}

	public int getFavouritesID() {
		return favouritesID;
	}

	public void setFavouritesID(int favouritesID) {
		this.favouritesID = favouritesID;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Hashtag> getHashtags() {
		return hashtags;
	}

	public void setHashtags(List<Hashtag> hashtags) {
		this.hashtags = hashtags;
	}

	public boolean containsUser(String userScreenName){
    	return users.stream().map(User::getScreen_name).collect(Collectors.toList()).contains(userScreenName);
	}

	public boolean containsHashtag(String hashtagName){
		return hashtags.stream().map(Hashtag::getHashtag).collect(Collectors.toList()).contains(hashtagName);
	}

	public void removeUser(String userScreenName){
    	users.removeIf(user -> user.getScreen_name().equals(userScreenName));
	}

	public void removeHashtag(String hashtagName){
    	hashtags.removeIf(hashtag -> hashtag.getHashtag().equals(hashtagName));
	}
}
