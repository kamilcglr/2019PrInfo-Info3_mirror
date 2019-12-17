package fr.tse.ProjetInfo3.mvc.viewer;


import com.jfoenix.controls.JFXProgressBar;
import fr.tse.ProjetInfo3.mvc.dao.InterestPointDAO;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.util.Pair;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * This class contains the list of PIs of the user
 * it transfers DTOs from DATABASE (repository) to Controllers
 */
public class PIViewer {
    private static List<InterestPoint> listOfInterestPoint = new ArrayList<>();
    private static InterestPointDAO interestPointDAO = new InterestPointDAO();
    private InterestPoint selectedInterestPoint;
    private UserViewer userViewer;
    private HastagViewer hashtagViewer;

    public PIViewer() {
        userViewer = new UserViewer();
        hashtagViewer = new HastagViewer();
    }

    /**
     * At the moment, this function calls generatePIs, but in the futur, it will get the list from database
     */
    public List<InterestPoint> getlistOfInterestPoint() {
        listOfInterestPoint = getListOfInterestPointFromDataBase();
        return listOfInterestPoint;
    }

    public void setSelectedInterestPoint(int index) {
        this.selectedInterestPoint = listOfInterestPoint.get(index);
        System.out.println("Affichage : " + selectedInterestPoint.getId());
    }

    /**
     * Return the selected interest Point which was selected in MyPIsController
     * This function is used inside PICreate
     */
    public InterestPoint getSelectedInterestPoint() {
        return this.selectedInterestPoint;
    }

    /**
     *
     */
    public void addInterestPointToDatabase(InterestPoint interestPoint) {
        //listOfInterestPoint.add(interestPoint);
        interestPointDAO.saveInterestPoint(interestPoint);
    }

    public void deleteInterestPointFromDatabaseById(int id) {
        interestPointDAO.deleteSelectedInterestPointById(id);
    }

    /**
     * In the futur, this function will return the created Interest Point into the database
     */
    public List<InterestPoint> getListOfInterestPointFromDataBase() {
        return interestPointDAO.getAllInterestPoints();
    }


    /*
     * New function designed for US53
     * This function gives a coherent list of tweets from Users and Hashtags
     *  */
    public List<Tweet> getTweets(Label progressLabel) throws Exception {
        List<Tweet> tweetsToReturn;

        List<User> usersOfIP = selectedInterestPoint.getUsers();
        List<Hashtag> hashtagsOfIP = selectedInterestPoint.getHashtags();

        int maxRequestPerTour = 10;

        Date dateToSearch = null;
        int totalNumberOfRequest = 0;

        while (totalNumberOfRequest < 40 && !limitsReached(hashtagsOfIP, usersOfIP)) {
            //For each hashtag and user, get tweets until oldestTweet
            //In each request, increase the number of request
            for (Hashtag hashtag : hashtagsOfIP) {
                Platform.runLater(() -> {
                    progressLabel.setText("Récupération des tweets de " + hashtag.getHashtag());
                });
                int nbRequestDone;
                nbRequestDone = getTweetsFromHashtag(hashtag, maxRequestPerTour, dateToSearch, progressLabel);
                totalNumberOfRequest += nbRequestDone;
            }

            //Get the tweets from user
            for (User user : usersOfIP) {
                Platform.runLater(() -> {
                    progressLabel.setText("Récupération des tweets de " + user.getScreen_name());
                });
                int nbRequestDone;
                nbRequestDone = getTweetsFromUser(user, maxRequestPerTour, dateToSearch, progressLabel);
                totalNumberOfRequest += nbRequestDone;
            }

            dateToSearch = findSecondMostRecentDate(hashtagsOfIP, usersOfIP);

            logProgress(totalNumberOfRequest, hashtagsOfIP, usersOfIP);

        }
        Platform.runLater(() -> {
            progressLabel.setText("Filtrage des résultats");
        });
        tweetsToReturn = filterResult(hashtagsOfIP, usersOfIP);

        logProgress(totalNumberOfRequest, hashtagsOfIP, usersOfIP);

        return tweetsToReturn;
    }

    private void logProgress(int totalNumberOfRequest, List<Hashtag> hashtagsOfIP, List<User> usersOfIP) {
        System.out.println("Nb request : " + totalNumberOfRequest);
        for (Hashtag hashtag : hashtagsOfIP) {
            if (hashtag.getTweets().size() > 0) {
                System.out.println("#" + hashtag.getHashtag() + " Nb tweets=" + hashtag.getTweets().size()
                        + " Date last " + hashtag.getTweets().get(hashtag.getTweets().size() - 1).getCreated_at());

            } else {
                System.out.println("#" + hashtag.getHashtag() + " Nb tweets=0");
            }
        }
        for (User user : usersOfIP) {
            if (user.getListoftweets().size() > 0) {
                System.out.println("@" + user.getScreen_name() + " Nb tweets=" + user.getListoftweets().size()
                        + " Date last " + user.getListoftweets().get(user.getListoftweets().size() - 1).getCreated_at());
            } else {
                System.out.println("@" + user.getScreen_name() + " Nb tweets=0");
            }
        }
    }

    /**
     * Stop searching to have coherent result
     *
     * @return boolean when true, stop searching
     */
    private boolean limitsReached(List<Hashtag> hashtags, List<User> users) {
        //At first verify if the globalTweetLimit is reached for at least one user
        //Stop if we got the max tweets for an user (3200)
        for (User user : users) {
            if (user.isGlobalTweetsLimit()) {
                return true;
            }
        }

        //Then, we can continue searching while there are at least one object that has not reach his limit
        //It stops only there is no tweets, e.g. all collected
        for (Hashtag hashtag : hashtags) {
            if (!hashtag.isAllTweetsCollected()) {
                return false;
            }
        }
        for (User user : users) {
            if (!user.isAllTweetsCollected()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Filter the result in a coherent way.
     */
    private List<Tweet> filterResult(List<Hashtag> hashtags, List<User> users) {
        List<Tweet> tweets = new ArrayList<>();
        Date filterFromDate = findMostOldInAny(hashtags, users);

        for (Hashtag hashtag : hashtags) {
            if (hashtag.getTweets().size() > 0) {
                hashtag.getTweets()
                        .removeIf(tweet -> tweet.getCreated_at().before(filterFromDate));
                tweets.addAll(hashtag.getTweets());
            }
        }
        for (User user : users) {
            if (user.getListoftweets().size() > 0) {
                user.getListoftweets()
                        .removeIf(tweet -> tweet.getCreated_at().before(filterFromDate));
                tweets.addAll(user.getListoftweets());
            }
        }
        return tweets;
    }

    //Find the object with the most recent date
    private Date findSecondMostRecentDate(List<Hashtag> hashtags, List<User> users) {
        List<Date> datesOfLast = new ArrayList<>();

        for (Hashtag hashtag : hashtags) {
            if (hashtag.getTweets().size() > 0) {
                datesOfLast.add(hashtag.getTweets().get(hashtag.getTweets().size() - 1).getCreated_at());
            }
        }
        for (User user : users) {
            if (user.getListoftweets().size() > 0) {
                datesOfLast.add(user.getListoftweets().get(user.getListoftweets().size() - 1).getCreated_at());
            }
        }
        Collections.sort(datesOfLast);
        Collections.reverse(datesOfLast);

        if (datesOfLast.size() < 2) {
            //only one item has tweets, then we can push the date +1 day
            LocalDateTime localDateTime = datesOfLast.get(0).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            localDateTime = localDateTime.minusDays(1);
            Date newDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            return newDate;


            //return datesOfLast.get(0); //If only one item in PI
        } else {
            return datesOfLast.get(1);
        }
    }

    private Date findMostOldInAny(List<Hashtag> hashtags, List<User> users) {
        List<Date> datesOfMostOldInEach = new ArrayList<>();

        //Find if a user have a global limit
        boolean userHaveGlobalLimit = false;
        for (User user : users) {
            if (user.isGlobalTweetsLimit()) {
                userHaveGlobalLimit = true;
                datesOfMostOldInEach.add(user.getListoftweets().get(user.getListoftweets().size() - 1).getCreated_at());
                break;
            }
        }

        //Find if a user have a global limit
        boolean hashTagHaveGlobalLimit = false;
        for (Hashtag hashtag : hashtags) {
            if (hashtag.isGlobalTweetsLimit()) {
                hashTagHaveGlobalLimit = true;
                datesOfMostOldInEach.add(hashtag.getTweets().get(hashtag.getTweets().size() - 1).getCreated_at());
                break;
            }
        }

        //If there is a user with tweetGlobalLimit (3200), it will be the older
        if (userHaveGlobalLimit || hashTagHaveGlobalLimit) {
            Collections.sort(datesOfMostOldInEach);
            Collections.reverse(datesOfMostOldInEach);
        } else {
            // The date is taken in account only if all the tweets are not collected. (a user or hashtag could not have tweets)
            for (Hashtag hashtag : hashtags) {
                if (hashtag.getTweets().size() > 0 && !hashtag.isAllTweetsCollected()) {
                    datesOfMostOldInEach.add(hashtag.getTweets().get(hashtag.getTweets().size() - 1).getCreated_at());
                }
            }
            for (User user : users) {
                if (user.getListoftweets().size() > 0 && !user.isAllTweetsCollected()) {
                    datesOfMostOldInEach.add(user.getListoftweets().get(user.getListoftweets().size() - 1).getCreated_at());
                }
            }

            //All the hashtags or users tweets have been collected
            if (datesOfMostOldInEach.size() == 0) {
                for (Hashtag hashtag : hashtags) {
                    if (hashtag.getTweets().size() > 0) {
                        datesOfMostOldInEach.add(hashtag.getTweets().get(hashtag.getTweets().size() - 1).getCreated_at());
                    }
                }
                for (User user : users) {
                    if (user.getListoftweets().size() > 0) {
                        datesOfMostOldInEach.add(user.getListoftweets().get(user.getListoftweets().size() - 1).getCreated_at());
                    }
                }
            }
            Collections.sort(datesOfMostOldInEach);
            Collections.reverse(datesOfMostOldInEach);
        }

        return datesOfMostOldInEach.get(0);
    }


    private Integer getTweetsFromUser(User user, int nbRequestMax, Date untilDate, Label progressLabel) throws Exception {
        int NbRequestDone = 0;
        List<Tweet> tweetList = new ArrayList<>();
        //Get the user timeline only if it is possible
        if (user.getStatuses_count() > 0) {
            //search without date
            if (untilDate == null) {
                System.out.println("search Tweets by count for " + user.getScreen_name());
                tweetList = userViewer.getTweetsByCount(user.getScreen_name(), 5, null);
                NbRequestDone++;
            }
            //else SEARCH ONLY IF DATE IS OK
            else {
                if (user.getListoftweets().size() > 0) {
                    if (user.getListoftweets().size() < 3194) {
                        if (user.getListoftweets().get(user.getListoftweets().size() - 1).getCreated_at().after(untilDate)) {
                            System.out.println("search Tweets by date for " + user.getScreen_name());
                            Pair<List<Tweet>, Integer> pair = userViewer.getTweetsByDate(user, nbRequestMax, untilDate, user.getMaxId(), user.getListoftweets().size());
                            NbRequestDone = pair.getValue();
                            tweetList = pair.getKey();
                        } else {
                            System.out.println("skip search for " + user.getScreen_name() + " : NO TWEETS FOR THIS DATE");
                            user.setDateTweetsLimit(true);
                        }
                    } else {
                        user.setGlobalTweetsLimit(true); //More than 3200 tweets have been collected
                        System.out.println("skip search for " + user.getScreen_name() + " : GLOBAL 3200 days LIMIT");
                        NbRequestDone = 0;
                    }

                }
            }
            //Add tweets to list of tweets inside USer
            user.getListoftweets().addAll(tweetList);
        } else {
            user.setAllTweetsCollected(true); //user have no tweets
            System.out.println("skip search for " + user.getScreen_name() + " : ALL TWEETS COLLECTED");
        }
        return NbRequestDone;
    }

    //Gets tweets for hashtag and sets them inside the object.
    Integer getTweetsFromHashtag(Hashtag hashtag, int nbRequestMax, Date maxDate, Label progressLabel) throws Exception {
        int NbRequestDone = 0;
        List<Tweet> tweetList = new ArrayList<>();

        //EXPLORATION only 5
        if (maxDate == null) {
            System.out.println("search Tweets by count for " + hashtag.getHashtag());
            tweetList = hashtagViewer.searchByCount(hashtag.getHashtag(), null, 5, hashtag.getMaxId());
            if (tweetList.size() == 0) {//this hashtag will not have any tweets
                hashtag.setAllTweetsCollected(true);
            }
            NbRequestDone++;
        } else {
            if (!hashtag.isAllTweetsCollected()) {
                LocalDate now = LocalDate.now();
                LocalDate dateOfLast = new java.sql.Date(hashtag.getTweets().get(hashtag.getTweets().size() - 1).getCreated_at().getTime()).toLocalDate();

                Period period = Period.between(now, dateOfLast);
                int diff = period.getDays();

                if (diff < 9) { //TODO Handle this in the request
                    //else SEARCH ONLY IF DATE IS OK
                    if (hashtag.getTweets().get(hashtag.getTweets().size() - 1).getCreated_at().after(maxDate)) {
                        System.out.println("search Tweets by date for " + hashtag.getHashtag());
                        Pair<List<Tweet>, Integer> pair = hashtagViewer.searchTweetsByDate(hashtag.getHashtag(), 30, maxDate, hashtag.getMaxId());
                        tweetList = pair.getKey();
                        NbRequestDone = pair.getValue();
                    } else {
                        System.out.println("skip search for " + hashtag.getHashtag() + " : NO TWEETS FOR THIS DATE");
                        hashtag.setDateTweetsLimit(true);
                    }
                } else {
                    System.out.println("skip search for " + hashtag.getHashtag() + " : GLOBAL 7 DAYS LIMIT");
                    hashtag.setGlobalTweetsLimit(true);
                }
            } else {
                System.out.println("skip search for " + hashtag.getHashtag() + " : ALL TWEETS COLLECTED");
            }
        }
        //Add tweets to list of tweets inside Hashtag
        hashtag.getTweets().addAll(tweetList);
        return NbRequestDone;
    }

    /**
     * From a big list of tweets, return the top users by number of followers
     * Excludes users that are already in interest Point
     */
    public List<User> getTopFiveUsers(List<Tweet> tweetList) {
        List<User> usersToReturn = new ArrayList<>();
        for (Tweet tweet : tweetList) {
            //First we get the id of all users involved
            User usertoAdd = tweet.getUser();
            if (!usersToReturn.contains(usertoAdd)) {
                usersToReturn.add(usertoAdd);
            }

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

    public Map<String, Integer> sortByValue(final Map<String, Integer> hashtagCounts) {

        return hashtagCounts.entrySet()

                .stream()

                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))

                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

    }

    /*
     * This method will create a restricted PI in the DB just to test some of the methods of insertion and creation
     * in the db , the Interest Point does not contain the list of users , tweets , and hastags for the moment
     * */
    public void createRestrictedPIinDatabase() {
        Date date = new Date();
        InterestPoint ip1 = new InterestPoint("Politique", "Suivi des personnalites politiques", date);
        // TO-DO
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


    //Controller of PITabController
    //Called by Sergiy PITabController
    private void saveInterestPoint() {
        //function to save in DAO
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
}