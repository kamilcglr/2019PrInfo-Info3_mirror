package fr.tse.ProjetInfo3.mvc.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;

public class HastagViewer {
    private String hashtag;
    private List<Tweet> tweets= new ArrayList<>();
    private List<User> users =new ArrayList<User>();
    private List<String> hashtags=new ArrayList<>();
    private RequestManager requestManager;

    public HastagViewer() {
        requestManager = new RequestManager();
    }

    public List<Tweet> searchHashtag(String hashtag) throws Exception {
       tweets=requestManager.searchTweets(hashtag);
     //  tweets.forEach(tweet->System.out.println(tweet));
   		//System.out.println(tweets.size());
       return tweets;

    }
    
    public Integer getNumberOfTweets(String hashtag) {
    	tweets=requestManager.searchTweets(hashtag);
    	return tweets.size();
    }
    
    public Integer getNumberOfUniqueAccounts(String hashtag) {
    	users=requestManager.getUsersFromHashtag(hashtag);
    	return users.size();
    }
    
    public List<String> getHashtagsLinked(String hashtag){
    	hashtags=requestManager.getHashtagLinked(hashtag);
		return hashtags;
    	
    }
    public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}

	public String getHashtag() {
        return hashtag;
    }

	public Map<String, Integer> topHashtag(List<String> hashtags) {
        Map<String, Integer> hashtagUsedSorted;
        Map<String, Integer> hashtagUsed = new HashMap<String, Integer>();

       
        for (String hashtag : hashtags) {
            Integer occurence = hashtagUsed.get(hashtag);
            hashtagUsed.put(hashtag, (occurence == null) ? 1 : occurence + 1);
        }

        hashtagUsedSorted = sortByValue(hashtagUsed);

        return hashtagUsedSorted;
    }
	  public Map<String, Integer> sortByValue(final Map<String, Integer> hashtagCounts) {

	        return hashtagCounts.entrySet()

	                .stream()

	                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))

	                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	        //.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

	    }
}
