/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.viewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jfoenix.controls.JFXProgressBar;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;

/**
 * @author Laïla
 *
 */
public class PITabViewer {
    private RequestManager requestManager;
    Map<String,Integer> TophashtagsOfHashtags;
    Map<String,Integer> TophashtagsOfUsers;
    List<String> hashtagsOfHashtags=new ArrayList<>();
    List<String>hashtagOfUsers=new ArrayList<>();
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

	private HastagViewer hastagViewer;
    private UserViewer userViewer;
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

}
