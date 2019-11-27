package fr.tse.ProjetInfo3.mvc.repository;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jfoenix.controls.JFXProgressBar;
import com.mgiorda.oauth.HttpMethod;
import com.mgiorda.oauth.OAuthConfig;
import com.mgiorda.oauth.OAuthConfigBuilder;
import com.mgiorda.oauth.OAuthSignature;
import fr.tse.ProjetInfo3.mvc.dto.Statuses;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.viewer.TwitterDateParser;
import javafx.application.Platform;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sergiy
 * @author kamilcaglar
 * @author TahaAlami
 * @author Kamil CAGLAR
 * @author Laïla
 * This class regroups all the methods used to interact with Twitter using TwitterAPI
 * It contains methods that will return POJO like user, tweet...
 */
public class RequestManager {

    //We get this bearer only once, we will use it to make the calls
    private RequestManager.Bearer bearer;
    private OAuthManager oAuthManager;

    // one instance, reuse
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
    private final String consumer = "PahWHDFSZ02bTaqFUVamZ0iBI";
    private final String consumerSecret = "mGDqU2cwWrw85cMvj7YOBSczI8qZQM0IKKymdbRL82sXqtyhhr";
    private final String accessToken = "4664421557-y8N6WL3BVrhBTIfuzZcHqRmNZeGDkt0TbAFoz9g";
    private final String accessTokensecret = "riGJEs4QhZWjOgQyQJY4jQJM8nCRnsfHisU1Vnq1VpDiv";

    //This key is generated by transforming to base 64 consumer:consumerSecret key
    //We could make a function in the future and add RFC 1738 (even it is not necessary at the moment)
    private final String consumerAndconsumerSecret = "Basic UGFoV0hERlNaMDJiVGFxRlVWYW1aMGlCSTptR0RxVTJjd1dydzg1Y012ajdZT0JTY3pJOHFaUU0wSUtLeW1kYlJMODJzWHF0eWhocg==";

    //Requested by hashtag viewer to know where we are in search process
    private int sizeOfList;

    /**
     * Constructor, identify the client, and get bearer token
     * Code 200 is OK
     */
    public RequestManager() {

        //Parameters of the body : https://developer.twitter.com/en/docs/basics/authentication/overview/application-only
        HttpRequest.BodyPublisher requestBody = HttpRequest.BodyPublishers
                .ofString("grant_type=client_credentials");

        //Post request builder to get the access_token
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://api.twitter.com/oauth2/token")))
                .header("Authorization", consumerAndconsumerSecret)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .POST(requestBody)
                .build();

        try {
            var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            //The response body is in JSON format, we will need gson to parse the result in the class barrier
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting() //human-readable json
                    .create();

            //add the access_token value to bearer, we will use it for other request
            bearer = gson.fromJson(response.body(), RequestManager.Bearer.class);

            //assignation of oAuth
            oAuthManager = new OAuthManager();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*  ****************************************************************************************************************
     *  Functions with Users
     *  ***************************************************************************************************************/

    /**
     * Return User object completed by the result of search
     * https://developer.twitter.com/en/docs/accounts-and-users/follow-search-get-users/api-reference/get-users-show
     *
     * @param screen_name the name of the profile, e.g. realdonaldtrump
     * @return User
     */
    public User getUser(String screen_name) throws RequestManagerException, IOException, InterruptedException {
        // We cannot pass the parameters as arguments of get method
        // We add the parameters of id directly in the link
        String url = "https://api.twitter.com/1.1/users/show.json?screen_name=" + screen_name;

        //Building of the request, we use the header Authorization", "Bearer <bearer_code>"
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .setHeader("Authorization", "Bearer " + bearer.getAccess_token())
                .build();

        return parseUsers(request, true).get(0);
    }

    /**
     * Search a list of users by name, or screen_name
     *
     * @param userProposition name of a user, or at least the beginning of a name
     * @return Map of Names and screen_names of user
     */
    public Map<String, String> getUsersbyName(String userProposition) throws IOException, InterruptedException {
        String url = "https://api.twitter.com/1.1/users/search.json?q=" + userProposition + "&count=20&include_entities=false";

        //if the proposition contains spaces we will remove them
        //WARNING ! we have to keep userProposition as it is because oAuthManager need spaces
        String urlSpaceRemoved = url.replace(" ", "%20");

        HttpRequest httpRequest = HttpRequest.newBuilder().GET().uri(URI.create(urlSpaceRemoved))
                .setHeader("Authorization", oAuthManager.getheader(url)).build();

        List<User> users = new ArrayList<>(parseUsers(httpRequest, false));
        Map<String, String> userNamesANDScreenName = new HashMap<>();
        users.forEach(user -> userNamesANDScreenName.put(user.getName(), user.getScreen_name()));
        return userNamesANDScreenName;
    }

    public List<Tweet> getTweetsFromUserByDate(String screen_name, Date date) throws RequestManagerException {
        //sometimes twitter api sends a response with a body "[]", we test 10 times, because user can have no tweets
        int tentatives = 0;
        List<Tweet> tweets = new ArrayList<Tweet>();
        HttpResponse<String> response = null;
        HttpRequest request;
        long max_id = 0L;
        try {
            while (tweets.size() < 3200 && (tentatives < 10)) {
                if (tweets.size() > 0 && (tweets.get(tweets.size() - 1).getCreated_at().before(date))) {
                    break;
                }
                request = buildUserTweetsRequest(screen_name, "200", max_id);
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                //Sometimes twitter API gives bad result then we increment tentatives
                if (response.body().equals("[]")) {
                    tentatives++;
                    continue;
                }

                if (response.body().contains("code\":50")) {
                    throw new RequestManagerException("Unknown user");
                }
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting() //human-readable json
                        .setDateFormat(TwitterDateParser.twitterFormat)
                        .create();

                Type tweetListType = new TypeToken<ArrayList<Tweet>>() {
                }.getType();
                //gson will complete the attributes of object if it finds elements that have the same name
                List<Tweet> tempList = gson.fromJson(response.body(), tweetListType);
                System.out.println(tempList);
                /* ! -1 gets the id f the tweet just before the oldest tweet of this query
                 *https://developer.twitter.com/en/docs/tweets/timelines/guides/working-with-timelines
                 */
                max_id = tempList.get(tempList.size() - 1).getId() - 1;

                tweets.addAll(tempList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(response.body());
            System.out.println(tweets);
            if (response.body().contains("code\":50")) {
                throw new RequestManagerException("Unknown user");
            }
        }

        //Filtering
        tweets = tweets.stream().filter(tweet -> tweet.getCreated_at().after(date)).collect(Collectors.toList());
        return tweets;
    }

    /**
     * Return a list of user already parsed with Gson
     *
     * @param httpRequest the request already built and SIGNED (if needed)
     * @param unique      if true, there is only one user to get
     * @return a List of users
     */
    private List<User> parseUsers(HttpRequest httpRequest, boolean unique) throws IOException, InterruptedException {
        HttpResponse<String> response = null;
        // Create a list of users
        List<User> users = new ArrayList<>();
        response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.body().contains("code\":50")) {
            throw new RequestManagerException("Unknown user");
        }
        Gson gson = new GsonBuilder().setPrettyPrinting() // human-readable json
                .setLenient().create();

        if (unique) {
            users.add(gson.fromJson(response.body(), User.class));
        } else {
            Type userListType = new TypeToken<ArrayList<User>>() {
            }.getType();
            users = gson.fromJson(response.body(), userListType);
        }
        return users;
    }

    /*  ****************************************************************************************************************
     *  Functions with Tweets
     *  ***************************************************************************************************************/

    /**
     * @param screen_name the name of the profile, e.g. realdonaldtrump
     * @param count       number of tweets (max 3200)
     * @return User
     * @author kamilcglr
     * Return List Of tweets of some user
     * https://developer.twitter.com/en/docs/tweets/timelines/api-reference/get-statuses-user_timeline
     * TODO optimize the List format
     */
    public List<Tweet> getTweetsFromUser(String screen_name, int count) throws RequestManagerException {
        //sometimes twitter api sends a response with a body "[]", we test 100 times
        int tentatives = 0;
        //Manouche methods TODO
        boolean oldFailed = false;
        int successiveFails = 0;

        List<Tweet> tweets = new ArrayList<Tweet>();
        HttpResponse<String> response = null;
        HttpRequest request;
        long max_id = 0L;
        try {
            while (tweets.size() < count && (tentatives < 100) && successiveFails < 5) {
                request = buildUserTweetsRequest(screen_name, "200", max_id);
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.body().contains("code\":50")) {
                    throw new RequestManagerException("Unknown user");
                }

                //Sometimes twitter API gives bad result then we increment tentatives and wait 1 second
                if (response.body().equals("[]")) {
                    tentatives++;
                    if (oldFailed) {
                        successiveFails++;
                    }
                    oldFailed = true;
                    Thread.sleep(1000);
                    System.out.println("successive fails :" + successiveFails);
                    System.out.println("tentatives :" + tentatives);
                    continue;
                } else {
                    oldFailed = false;
                    successiveFails = 0;
                }

                Gson gson = new GsonBuilder()
                        .setPrettyPrinting() //human-readable json
                        .setDateFormat(TwitterDateParser.twitterFormat)
                        .create();

                Type tweetListType = new TypeToken<ArrayList<Tweet>>() {
                }.getType();
                //gson will complete the attributes of object if it finds elements that have the same name
                List<Tweet> tempList = gson.fromJson(response.body(), tweetListType);

                /* ! -1 gets the id f the tweet just before the oldest tweet of this query
                 *https://developer.twitter.com/en/docs/tweets/timelines/guides/working-with-timelines
                 */
                max_id = tempList.get(tempList.size() - 1).getId() - 1;

                tweets.addAll(tempList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (response.body().contains("code\":50")) {
                throw new RequestManagerException("Unknown user");
            }
        }
        return tweets;
    }

    /*  ****************************************************************************************************************
     *  Functions with Hashtags
     *  ***************************************************************************************************************/

    /**
     * @param hashtagName the hashtag that we search
     * @return list of tweets that contain this hashtag
     * @author Laïla
     * Method to get tweets that contains #. Uses the bearer token method.
     * We limit the number of request to 45 for the moment, 45*100 = 4500 tweets
     * We also stop making requests when we have 0 result 5 times TODO : change to test with date
     */
    public List<Tweet> searchTweets(String hashtagName, int count, JFXProgressBar progressBar) {
        sizeOfList = 0;
        int tentatives = 0;
        List<Tweet> tweets = new ArrayList<Tweet>();
        HttpResponse<String> response = null;
        HttpRequest request;
        long max_id = 0L;

        try {
            while (tweets.size() < count && tentatives < 5) {
                //100 is the max for this type f research !
                request = buildHashtagTweetRequest(hashtagName, "100", max_id);

                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                //If there is 0 tweets about this, we test 5 times then we stop.
                if (response.body().equals("[]")) {
                    tentatives++;
                }

                Gson gson = new GsonBuilder()
                        .setPrettyPrinting() //human-readable json
                        //.setLenient()
                        .setDateFormat(TwitterDateParser.twitterFormat)
                        .create();

                //gson will complete the attributes of object if it finds elements that have the same name
                //we use statuses because twitter sends statuses in this search {statuses : [...
                Statuses tempStatuses = gson.fromJson(response.body(), Statuses.class);
                List<Tweet> tempList = tempStatuses.getTweets();

                /* ! -1 gets the id f the tweet just before the oldest tweet of this query
                 *https://developer.twitter.com/en/docs/tweets/timelines/guides/working-with-timelines
                 */
                if (tempList.size() > 1) {
                    max_id = tempList.get(tempList.size() - 1).getId() - 1;
                    tweets.addAll(tempList);
                } else {
                    tentatives++; //it is possible that we reach the end of avaible tweets
                }

                sendProgress(progressBar, tweets.size(), count);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (response.body().contains("code\":50")) {
                throw new RequestManagerException("Unknown user");
            }
        }
        return tweets;
    }

    /**
     * @author kamil CAGLAR
     * Porvides a request for getting the tweets from user timeline
     * @apiNote It is very important to have FULL TWEET. We add the tweet_mode=extended header
     */
    private HttpRequest buildUserTweetsRequest(String screen_name, String count, Long max_id) {
        String url = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + screen_name + "&count=" + count + "&tweet_mode=extended";

        if (max_id > 0) {
            url = url + "&max_id=" + max_id.toString();
        }

        //Building of the request, we use the header Authorization", "Bearer <bearer_code>"
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .setHeader("Authorization", "Bearer " + bearer.getAccess_token())
                .build();

        return request;
    }

    /**
     * @author kamil CAGLAR
     * Porvides a request for getting the tweets that contains a specifi #.
     * It is the mix of recent and popular.
     * https://developer.twitter.com/en/docs/tweets/search/api-reference/get-search-tweets
     * @apiNote It is very important to have FULL TWEET. We add the tweet_mode=extended header
     */
    private HttpRequest buildHashtagTweetRequest(String hashtagName, String count, Long max_id) {
        String url = "https://api.twitter.com/1.1/search/tweets.json?q=%23" + hashtagName + "&count=" + count + "&tweet_mode=extended";

        if (max_id > 0) {
            url = url + "&max_id=" + max_id.toString();
        }

        //Building of the request, we use the header Authorization", "Bearer <bearer_code>"
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .setHeader("Authorization", "Bearer " + bearer.getAccess_token())
                .build();

        return request;
    }

    public int getSizeOfList() {
        return sizeOfList;
    }

    public void sendProgress(JFXProgressBar progressBar, int progress, int count) {
        Platform.runLater(() -> {
            progressBar.setProgress((double) progress / (double) count);
        });
    }

    /*  ****************************************************************************************************************
     *  Sub classes to identify
     *  ***************************************************************************************************************/

    /**
     * @author kamilcaglar
     * Simple Class that contains the access token
     * It is easier to complete from gson
     */
    public static class Bearer {
        // ! Same name as the key in the body. "access_token":"this value"
        private String access_token;

        public String getAccess_token() {
            return access_token;
        }
    }

    public class RequestManagerException extends RuntimeException {
        public RequestManagerException(String errorMessage, Throwable err) {
            super(errorMessage, err);
        }

        public RequestManagerException(String errorMessage) {
            super(errorMessage);
        }
    }

    /**
     * @author Laïla Class for OAuth
     */
    public class OAuthManager {
        private OAuthConfig oauthConfig;
        private OAuthSignature signature;

        public String getheader(String url) {
            oauthConfig = new OAuthConfigBuilder(consumer, consumerSecret)
                    .setTokenKeys(accessToken, accessTokensecret)
                    .build();
            signature = oauthConfig.buildSignature(HttpMethod.GET, url).create();
            return signature.getAsHeader().replace(signature.getSignature(),
                    URLEncoder.encode(signature.getSignature(), StandardCharsets.UTF_8));
        }
    }

}
