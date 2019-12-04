/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    Map<String,Integer> Tophashtags=new HashMap<String, Integer>();
    List<String> hashtagsOfHashtags=new ArrayList<>();
    List<String>hashtagOfUsers=new ArrayList<>();
	private HastagViewer hastagViewer;
    private UserViewer userViewer;
    List<String> myHashtags=new ArrayList<>();
    //List<User> myUsers=new ArrayList<>();


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



    public PITabViewer(){
    	requestManager=new RequestManager();
    	hastagViewer=new HastagViewer();
    	userViewer= new UserViewer();


    	
    }

    public Map<String,Integer> getListOfHashtagsforPI(JFXProgressBar progressBar,List<String> myhashtags,List<User> myUsers) {
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
    	for(User user: myUsers) {
    		try {
    		userViewer.setUser(user);
    		List<Tweet> tweets=userViewer.getTweetsByCount(user.getScreen_name(), 3400, progressBar);
    		TophashtagsOfUsers=userViewer.topHashtag(tweets);
    		//TophashtagsOfUsers.putAll(userViewer.topHashtag(tweets));
    		TophashtagsOfUsers.forEach((k,v)->System.out.println(k+" "+v));
        	//Tophashtags.putAll(TophashtagsOfUsers);

    		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	TophashtagsOfHashtags=hastagViewer.topHashtag(hashtagsOfHashtags);

    	Tophashtags=TophashtagsOfHashtags;
    	Tophashtags.putAll(TophashtagsOfUsers);


    	return Tophashtags;
    }

}
