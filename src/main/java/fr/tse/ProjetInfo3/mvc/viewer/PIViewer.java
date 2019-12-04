package fr.tse.ProjetInfo3.mvc.viewer;

import com.jfoenix.controls.JFXProgressBar;
import fr.tse.ProjetInfo3.mvc.dto.*;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * This class contains the list of PIs of the user
 * it transfers DTOs from DATABASE (repository) to Controllers
 */
public class PIViewer {
    private static List<InterestPoint> listOfInterestPoint = new ArrayList<>();

    private InterestPoint selectedInterestPoint;

    public PIViewer() {
        try {
            generatePIsDemo();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * At the moment, this function calls generatePIs, but in the futur, it will get the list from database
     */
    public List<InterestPoint> getlistOfInterestPoint() {
        return listOfInterestPoint;
    }

    public void setSelectedInterestPoint(int index) {
        this.selectedInterestPoint = listOfInterestPoint.get(index);
    }

    /**
     * Return the selected interest Point which was selected in MyPIsController
     * This function is used inside PICreate
     */
    public InterestPoint getSelectedInterestPoint() {
        return this.selectedInterestPoint;
    }

    /**
     * In the futur, this function will add the created Interest Point into the database
     */
    public void addInterestPointToDatabase(InterestPoint interestPoint) {
        listOfInterestPoint.add(interestPoint);
    }

    /**
     * In the futur, this function will return the created Interest Point into the database
     */
    public List<InterestPoint> getListOfInterestPointFromDataBase() {
        return listOfInterestPoint;
    }

    /**
     * This function generate a list of PIs for the tests
     */
    public void generatePIsDemo() throws IOException, InterruptedException {
        //First IP
        List<Hashtag> hashtags = new ArrayList<>();
        Hashtag president = new Hashtag("#president");
        Hashtag congres = new Hashtag("#congrés");
        Hashtag meetup = new Hashtag("#meetup");

        hashtags.add(president);
        hashtags.add(congres);
        hashtags.add(meetup);

        List<User> users = new ArrayList<>();
        RequestManager requestManager = new RequestManager();
        User trump = requestManager.getUser("realdonaldtrump");
        User macron = requestManager.getUser("EmmanuelMacron");

        users.add(trump);
        users.add(macron);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Date date = new Date();
        InterestPoint ip1 = new InterestPoint("Politique", "Suivi des personnalités politiques", date);
        ip1.setHashtags(hashtags);
        ip1.setUsers(users);

        //Second IP
        List<Hashtag> hashtags1 = new ArrayList<>();
        Hashtag example = new Hashtag("#Telecom");
        Hashtag city = new Hashtag("#Saint-Etienne");
        Hashtag dep = new Hashtag("#42");

        hashtags1.add(example);
        hashtags1.add(city);
        hashtags1.add(dep);

        List<User> users2 = new ArrayList<>();
        User sobun = requestManager.getUser("sobunung");
        User kamil = requestManager.getUser("kamilcglr");

        users2.add(sobun);
        users2.add(kamil);

        Date date2 = new Date();
        InterestPoint ip2 = new InterestPoint("Mes amis", "Suivi des amis", date2);
        ip2.setHashtags(hashtags1);
        ip2.setUsers(users2);

        listOfInterestPoint.add(ip1);
        listOfInterestPoint.add(ip2);
    }

    /*Return a list of tweets provided by user and hashtag
     * TODO replace with function provided by laila and sobun*/
    public List<Tweet> getTweets(JFXProgressBar progressBar) throws Exception {
        List<Tweet> tweetsToRetrun = new ArrayList<>();

        for (User userInIP : selectedInterestPoint.getUsers()) {
            long numberOfRequest = userInIP.getStatuses_count();
            if (numberOfRequest > 3194) {
                numberOfRequest = 3194;
            }
            //TODO test to delete
            //hardcode numberofrequest for tests
            numberOfRequest = 600;
            UserViewer userViewer = new UserViewer();
            userViewer.searchScreenName(userInIP.getScreen_name());
            tweetsToRetrun.addAll(userViewer.getTweetsByCount(userInIP.getScreen_name(), (int) numberOfRequest, progressBar));
            System.out.println("tweets from " + userInIP.getName() + " received, number of tweets : " + tweetsToRetrun.size());
        }
        for (Hashtag hashtag : selectedInterestPoint.getHashtags()) {
            HastagViewer hastagViewer = new HastagViewer();
            hastagViewer.setHashtag(hashtag.getHashtagName().substring(1));
            hastagViewer.search(hashtag.getHashtagName().substring(1), progressBar);
            tweetsToRetrun.addAll(hastagViewer.getTweetList());
            System.out.println("tweets from " + hashtag.getHashtagName() + " received, number of tweets : " + tweetsToRetrun.size());
        }
        return tweetsToRetrun;
    }


    public Map<Tweet, Integer> topTweets(List<Tweet> tweetList, JFXProgressBar progressBar) {
        Map<Tweet, Integer> TweetsSorted;
        Map<Tweet, Integer> Tweeted = new HashMap<Tweet, Integer>();

        for (Tweet tweet : tweetList) {
            if (!Tweeted.containsKey(tweet) && tweet.getRetweeted_status() == null) { //On prend en compte les retweets pour l'instant
                int PopularCount = (int) tweet.getRetweet_count() + (int) tweet.getFavorite_count();
                Tweeted.put(tweet, PopularCount);
            }
        }

        TweetsSorted = Tweeted
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        return TweetsSorted;
    }
}