package fr.tse.ProjetInfo3.mvc.viewer;

import java.util.*;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXProgressBar;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;

public class HastagViewer {

    private RequestManager requestManager;
    private Hashtag hashtag;

    private List<Tweet> tweets = new ArrayList<>();
    private List<User> users = new ArrayList<User>();
    private List<String> hashtags = new ArrayList<>();

    public HastagViewer() {
        requestManager = new RequestManager();
        hashtag = new Hashtag();
    }

    public void setHashtag(String hashtag) throws Exception {
        this.hashtag.setHashtagName(hashtag);
    }

    public void search(String hashtag, JFXProgressBar progressBar) throws Exception {
        tweets = requestManager.searchTweets(hashtag, 4500, progressBar);
    }

    public List<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}

	public List<Tweet> getTweetList() {
        return tweets;
    }

    public Integer getNumberOfTweets() {
        return tweets.size();
    }

    public Integer getNumberOfUniqueAccounts() {
        users = getUsersFromHashtag();
        return users.size();
    }

    public List<String> getHashtagsLinked() {
        hashtags = getHashtagLinked();
        return hashtags;
    }

    public Hashtag getHashtag() {
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

    /**
     * @return the list of unique users who have used the #
     * @author Laïla
     **/
    public List<User> getUsersFromHashtag() {
        List<User> users = new ArrayList<>();
        tweets.forEach(tweet -> users.add(tweet.getUser()));
        List<User> listWithoutDuplicates = new ArrayList<>(
                new HashSet<>(users));
        return listWithoutDuplicates;
    }

    /**
     * @return the list of hashtags linked to that #
     * @author Laïla
     **/
    public List<String> getHashtagLinked() {
        List<String> hashtags = new ArrayList<>();
        List<String> result = null;
        tweets.forEach(tweet -> tweet.getEntities().getHashtags().forEach(hashtag -> hashtags.add(hashtag.getText())));
        //We lowercase every # so we can get only linked ones
        result = hashtags.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        //We remove the # that we're looking from the list
        while (result.contains(hashtag.getHashtagName().toLowerCase())) {
            result.remove(hashtag.getHashtagName().toLowerCase());
        }
        return result;
    }

    public Map<String, Integer> sortByValue(final Map<String, Integer> hashtagCounts) {

        return hashtagCounts.entrySet()

                .stream()

                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))

                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        //.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

    }

    /**
     * Gets the number of tweets received by the request manager during search process
     */
    public int getSearchProgression() {
        return requestManager.getSizeOfList();
    }
}
