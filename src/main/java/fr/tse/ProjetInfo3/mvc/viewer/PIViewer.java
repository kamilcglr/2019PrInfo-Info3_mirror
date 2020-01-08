package fr.tse.ProjetInfo3.mvc.viewer;


import com.jfoenix.controls.JFXProgressBar;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.DatabaseManager;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.util.Pair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static fr.tse.ProjetInfo3.mvc.utils.DateFormats.hoursAndDateFormat;
import static java.util.stream.Collectors.toMap;

/**
 * This class contains the list of PIs of the user
 * it transfers DTOs from DATABASE (repository) to Controllers
 */
public class PIViewer {
    private static List<InterestPoint> listOfInterestPoint = new ArrayList<>();
    private static DatabaseManager databaseManager = new DatabaseManager();

    private InterestPoint selectedInterestPoint;
    private UserViewer userViewer;
    private HashtagViewer hashtagViewer;

    public PIViewer() {
        userViewer = new UserViewer();
        hashtagViewer = new HashtagViewer();
    }

    /**
     * At the moment, this function calls generatePIs, but in the futur, it will get the list from database
     */
    public List<InterestPoint> getlistOfInterestPoint(int userID) {
        listOfInterestPoint = getListOfInterestPointFromDataBase(userID);
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
    public long addInterestPointToDatabase(InterestPoint interestPoint) {
        return databaseManager.saveInterestPointToDataBase(interestPoint);
    }


    public void deleteInterestPointFromDatabaseById(long id) {
        databaseManager.deleteSelectedInterestPointById(id);
    }

    /**
     *
     */
    public List<InterestPoint> getListOfInterestPointFromDataBase(int userID) {
        return databaseManager.getAllInterestPointFromDataBase(userID);
    }

    public void deleteTweetsFromInterestPoint() {
        databaseManager.deleteTweetsFromInterestPoint(selectedInterestPoint.getId());
    }

    /**
     * Look first in DB
     */
    public List<Tweet> getTweetsWrapper(Label progressLabel, Label lastSearchLabel) throws Exception {
        List<Tweet> tweetsToReturn;
        //Search in the database if the IP has tweets
        //Yes, load them
        Platform.runLater(() -> progressLabel.setText("Recherche de résultats dans le cache"));
        tweetsToReturn = databaseManager.getTweetsFromInterestPoint(selectedInterestPoint.getId());
        if (tweetsToReturn != null && tweetsToReturn.size() > 0) {
            System.out.println(tweetsToReturn.size() + " loaded from db");
        } else {
            //No, do the research
            tweetsToReturn = this.getTweets(progressLabel);
            //save this new result to database
            Platform.runLater(() -> progressLabel.setText("Sauvegarde des résultats dans le cache"));
            databaseManager.setTweetsToInterestPoint(selectedInterestPoint.getId(), tweetsToReturn);
            selectedInterestPoint.setLastSearchDate(new Date());
        }
        Platform.runLater(() -> {
            if (selectedInterestPoint.getLastSearchDate() != null) {
                lastSearchLabel.setText("Dernière recherche effectuée le " + hoursAndDateFormat.format(selectedInterestPoint.getLastSearchDate()));
            }
        });
        return tweetsToReturn;
    }

    /*
     * New function designed for US53
     * This function gives a coherent list of tweets from Users and Hashtags
     *  */
    public List<Tweet> getTweets(Label progressLabel) throws Exception {
        List<Tweet> tweetsToReturn;
        //clean cache if refresh
        List<User> usersOfIP = selectedInterestPoint.getUsers();
        List<Hashtag> hashtagsOfIP = selectedInterestPoint.getHashtags();

        int maxRequestPerTour = 10;

        Date dateToSearch = null;
        int totalNumberOfRequest = 0;

        //Default 40, 10 for tests
        while (totalNumberOfRequest < 10 && !limitsReached(hashtagsOfIP, usersOfIP)) {
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

            logProgress(totalNumberOfRequest, hashtagsOfIP, usersOfIP, dateToSearch);

        }
        Platform.runLater(() -> {
            progressLabel.setText("Filtrage des résultats");
        });
        tweetsToReturn = filterResult(hashtagsOfIP, usersOfIP);

        logProgress(totalNumberOfRequest, hashtagsOfIP, usersOfIP, null);

        return tweetsToReturn;
    }

    private void logProgress(int totalNumberOfRequest, List<Hashtag> hashtagsOfIP, List<User> usersOfIP, Date date) {
        System.out.println("");
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
            if (user.getTweets().size() > 0) {
                System.out.println("@" + user.getScreen_name() + " Nb tweets=" + user.getTweets().size()
                        + " Date last " + user.getTweets().get(user.getTweets().size() - 1).getCreated_at());
            } else {
                System.out.println("@" + user.getScreen_name() + " Nb tweets=0");
            }
        }
        if (date != null) {
            System.out.println("Next date " + date + "\n");
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
                //List<Tweet> tempList = new ArrayList<>();
                //tempList = hashtag.getTweets()
                //        .stream()
                //        .filter(tweet -> tweet.getCreated_at().after(filterFromDate))
                //        .collect(Collectors.toList());
                hashtag.getTweets()
                        .removeIf(tweet -> tweet.getCreated_at().before(filterFromDate));
                tweets.addAll(hashtag.getTweets());
            }
        }
        for (User user : users) {
            if (user.getTweets().size() > 0) {
                //List<Tweet> tempList = new ArrayList<>();
                //tempList = user.getTweets()
                //        .stream()
                //        .filter(tweet -> tweet.getCreated_at().after(filterFromDate))
                //        .collect(Collectors.toList());
                user.getTweets()
                        .removeIf(tweet -> tweet.getCreated_at().before(filterFromDate));
                tweets.addAll(user.getTweets());
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
            if (user.getTweets().size() > 0) {
                datesOfLast.add(user.getTweets().get(user.getTweets().size() - 1).getCreated_at());
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
            //Fix when same dates on high frequency tweets
            datesOfLast = datesOfLast.stream().distinct().collect(Collectors.toList());
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
                datesOfMostOldInEach.add(user.getTweets().get(user.getTweets().size() - 1).getCreated_at());
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
                if (user.getTweets().size() > 0 && !user.isAllTweetsCollected()) {
                    datesOfMostOldInEach.add(user.getTweets().get(user.getTweets().size() - 1).getCreated_at());
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
                    if (user.getTweets().size() > 0) {
                        datesOfMostOldInEach.add(user.getTweets().get(user.getTweets().size() - 1).getCreated_at());
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
            //EXPLORATION Do this only if maxDate is null and user does not contain any tweet.
            if (untilDate == null) {
                if (user.getTweets().size() == 0) {
                    System.out.println("search Tweets by count for " + user.getScreen_name());
                    tweetList = userViewer.getTweetsByCount(user.getScreen_name(), 5, null);
                    NbRequestDone++;
                } else {
                    System.out.println(user.getScreen_name() + " has already tweets");
                }
            }
            //else SEARCH ONLY IF DATE IS OK
            else {
                if (user.getTweets().size() > 0) {
                    if (user.getTweets().size() < 3194) {
                        if (user.getTweets().get(user.getTweets().size() - 1).getCreated_at().after(untilDate)) {
                            System.out.println("search Tweets by date for " + user.getScreen_name());
                            Pair<List<Tweet>, Integer> pair = userViewer.getTweetsByDate(user, nbRequestMax, untilDate, user.getMaxId(), user.getTweets().size());
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
            user.getTweets().addAll(tweetList);
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

        //EXPLORATION Do this only if maxDate is null and hashtag does not contain any tweet.
        if (maxDate == null) {
            if (hashtag.getTweets().size() == 0) {
                System.out.println("search Tweets by count for " + hashtag.getHashtag());
                tweetList = hashtagViewer.searchByCount(hashtag.getHashtag(), null, 5, hashtag.getMaxId());
                if (tweetList.size() == 0) {//this hashtag will not have any tweets
                    hashtag.setAllTweetsCollected(true);
                }
                NbRequestDone++;
            } /*else {   //TODO DELETE THIS AFTER END OF SPRINT 5
                System.out.println(hashtag.getHashtag() + " has already tweets");
            }*/
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
                    } /*else {   //TODO DELETE THIS AFTER END OF SPRINT 5
                        System.out.println("skip search for " + hashtag.getHashtag() + " : NO TWEETS FOR THIS DATE");
                        hashtag.setDateTweetsLimit(true);
                    }*/
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