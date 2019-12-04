/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.viewer;

import java.util.*;

import com.jfoenix.controls.JFXProgressBar;

import fr.tse.ProjetInfo3.mvc.controller.PiTabController;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;

import static java.util.stream.Collectors.toMap;

/**
 * @author Laïla
 *
 */
public class PITabViewer {
    private RequestManager requestManager;
    private UserViewer userViewer;
    Map<String,Integer> TophashtagsOfHashtags;
    Map<String,Integer> TophashtagsOfUsers;
    List<String> hashtagsOfHashtags=new ArrayList<>();
    List<String>hashtagOfUsers=new ArrayList<>();
    List<String>ListOfUsers=new ArrayList<>();

    public List<String> getHashtagsOfHashtags() {
		return hashtagsOfHashtags;
	}

	public void setHashtagsOfHashtags(List<String> hashtagsOfHashtags) {
		this.hashtagsOfHashtags = hashtagsOfHashtags;
	}

	public List<String> getHashtagOfUsers() {
		return hashtagOfUsers;
	}

	public void setHashtagOfUsers(List<String> hashtagOfUsers) {
		this.hashtagOfUsers = hashtagOfUsers;
	}

	public List<String> getMyHashtags() {
		return myHashtags;
	}

	public void setMyHashtags(List<String> myHashtags) {
		this.myHashtags = myHashtags;
	}

	public List<String> getListOfUsers() { return ListOfUsers; }

	private HastagViewer hastagViewer;

    List<String> myHashtags=new ArrayList<>();

    public PITabViewer(){
    	requestManager=new RequestManager();
    	hastagViewer=new HastagViewer();
    	userViewer= new UserViewer();
    	myHashtags.add("blackfriday");
    	myHashtags.add("mardi");
    }
    
    public Map<String,Integer> getListOfHashtagsforPI(JFXProgressBar progressBar,List<String> myhashtags) {
    	

    	for(String hashtag: myHashtags) {
    		try {
				//hastagViewer.setHashtag(hashtag);
				hastagViewer.search(hashtag, progressBar);	
				hastagViewer.setHashtag(hashtag);
	    		hashtagsOfHashtags.addAll(hastagViewer.getHashtagLinked());

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	TophashtagsOfHashtags=hastagViewer.topHashtag(hashtagsOfHashtags);
    	return TophashtagsOfHashtags;
    }

    public List<Tweet> getListOfTweets (List<String> ListOfUsers,int count, JFXProgressBar progressBar){
    	List<Tweet> tweetList = new ArrayList<>();
    	for (int i =0;i<ListOfUsers.size();i++){
			tweetList.addAll(requestManager.getTweetsFromUser(ListOfUsers.get(i),count,progressBar));
		}
		return tweetList;
	}

	public Map<Tweet, Integer> topTweetsFromUsers(List<Tweet> tweetList, JFXProgressBar progressBar) {
		return userViewer.topTweets(tweetList);
	}

}
