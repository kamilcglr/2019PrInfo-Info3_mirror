/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Laïla
 *
 */
public class Statuses {
	private List<Tweet> statuses=new ArrayList<>();
	
	
	public List<Tweet> getTweets() {
		return statuses;
	}
	
	public Statuses() {
		// TODO Auto-generated constructor stub
	}
	
	public void setTweets(List<Tweet> tweets) {
		this.statuses = tweets;
	}
	
	

}
