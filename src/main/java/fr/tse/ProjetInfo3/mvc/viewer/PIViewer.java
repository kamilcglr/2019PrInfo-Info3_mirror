package fr.tse.ProjetInfo3.mvc.viewer;


import fr.tse.ProjetInfo3.mvc.dao.InterestPointDAO;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.ListOfInterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class contains the list of PIs of the user
 * it transfers DTOs from DATABASE (repository) to Controllers
 */
public class PIViewer {
    private static List<InterestPoint> listOfInterestPoint = new ArrayList<>();
    private static InterestPointDAO interestPointDAO = new InterestPointDAO();
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
        //listOfInterestPoint.add(interestPoint);
    	interestPointDAO.saveInterestPoint(interestPoint);
    }

    /**
     * In the futur, this function will return the created Interest Point into the database
     */
    public List<InterestPoint> getListOfInterestPointFromDataBase() {
        //return listOfInterestPoint;
    	return interestPointDAO.getAllInterestPoints();
    }

    /**
     * This function generate a list of PIs for the tests
     */
    public void generatePIsDemo() throws IOException, InterruptedException {
        //First IP
        List<Hashtag> hashtags = new ArrayList<>();
        Hashtag h1 = new Hashtag("#blackfriday");
        Hashtag h2 = new Hashtag("#amazon");
        Hashtag h3 = new Hashtag("#darty");

        hashtags.add(h1);
        //hashtags.add(h2);
        //hashtags.add(h3);

        List<User> users = new ArrayList<>();
        RequestManager requestManager = new RequestManager();
        User u1 = requestManager.getUser("twandroid");
        User u2 = requestManager.getUser("Dealabs");

        users.add(u1);
        //users.add(u2);


        Date date = new Date();
        InterestPoint ip1 = new InterestPoint("Black friday", "Suivi des promotions", date);
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

    /**
     * From a big list of tweets, return the top users by number of followers
     * Excludes users that are already in interest Point
     */
    public List<User> getTopFiveUsers(List<Tweet> tweetList, List<User> usersToExclude) {
        List<User> usersToReturn = new ArrayList<>();
        for (Tweet tweet : tweetList) {
            //ps : if PO asks us to retrieve the number of tweets we will use a map

            //First we get the id of all users involved
            User usertoAdd = tweet.getUser();
            if (!usersToReturn.contains(usertoAdd)) {
                usersToReturn.add(usertoAdd);
            }
            usersToReturn.stream().filter((user -> !usersToExclude.contains(user))).collect(Collectors.toList());

        }
        //Sort by Followers Count
        usersToReturn.sort(new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                return (int) (u1.getFollowers_count() - u2.getFollowers_count());
            }
        });
        Collections.reverse(usersToReturn);
        return usersToReturn;
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
    
    /*
     * This method will create a restricted PI in the DB just to test some of the methods of insertion and creation
     * in the db , the Interest Point does not contain the list of users , tweets , and hastags for the moment
     * */
    public void createRestrictedPIinDatabase() {
    	Date date = new Date();
        InterestPoint ip1 = new InterestPoint("Politique", "Suivi des personnalites politiques", date);
        // TO-DO
    }
}
