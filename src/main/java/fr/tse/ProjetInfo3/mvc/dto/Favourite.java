/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.dto;

import java.util.List;

/**
 * @author Laïla
 *
 */
public class Favourite {
	
    private List<Hashtag> hashtags;
    private List<User> users;
	public List<Hashtag> getHashtags() {
		return hashtags;
	}
	public void setHashtags(List<Hashtag> hashtags) {
		this.hashtags = hashtags;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
    

}
