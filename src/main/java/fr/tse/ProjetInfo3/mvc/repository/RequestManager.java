package fr.tse.ProjetInfo3.mvc.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.tse.ProjetInfo3.mvc.dto.Tweet;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.viewer.TwitterDateParser;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sergiy
 * @author Kamil CAGLAR
 * @author Taha
 * This class regroups all the methods used to interact with Twitter using TwitterAPI
 * It contains methods that will return POJO like user, tweet...
 */
public class RequestManager {
    //We get this bearer only once, we will use it to make the calls
    private RequestManager.Bearer bearer;

    // one instance, reuse
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();
    private String consumer = "PahWHDFSZ02bTaqFUVamZ0iBI";
    private String consumerSecret = "mGDqU2cwWrw85cMvj7YOBSczI8qZQM0IKKymdbRL82sXqtyhhr";
    private String accessToken = "4664421557-y8N6WL3BVrhBTIfuzZcHqRmNZeGDkt0TbAFoz9g";
    private String accessTokensecret = "riGJEs4QhZWjOgQyQJY4jQJM8nCRnsfHisU1Vnq1VpDiv";

    /**
     * Constructor, identify the client, and get bearer token
     * Code 200 is OK
     */
    public RequestManager() {
        //This key is generated by transforming to base 64 consumer:consumerSecret key
        //We could make a function in the future and add RFC 1738 (even it is not necessary at the moment)
        String consumerAndconsumerSecret = "Basic UGFoV0hERlNaMDJiVGFxRlVWYW1aMGlCSTptR0RxVTJjd1dydzg1Y012ajdZT0JTY3pJOHFaUU0wSUtLeW1kYlJMODJzWHF0eWhocg==";

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return User object completed by the result of search
     * https://developer.twitter.com/en/docs/accounts-and-users/follow-search-get-users/api-reference/get-users-show
     *
     * @param screen_name the name of the profile, e.g. realdonaldtrump
     * @return User
     */
    public User getUser(String screen_name) throws RequestManagerException {
        // We cannot pass the parameters as arguments of get method
        // We add the parameters of id directly in the link
        String url = "https://api.twitter.com/1.1/users/show.json?screen_name=" + screen_name;

        //Building of the request, we use the header Authorization", "Bearer <bearer_code>"
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .setHeader("Authorization", "Bearer " + bearer.getAccess_token())
                .build();

        HttpResponse<String> response = null;
        User userReturned = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.body().contains("code\":50")) {
                throw new RequestManagerException("Unknown user");
            }
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting() //human-readable json
                    .create();

            //gson will complete the attributes of object if it finds elements that have the same name
            userReturned = gson.fromJson(response.body(), User.class);

        } catch (Exception e) {
            e.printStackTrace();
            if (response.body().contains("code\":50")) {
                throw new RequestManagerException("Unknown user");
            }
        }
        return userReturned;
    }

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
        List<Tweet> tweets = new ArrayList<Tweet>();
        HttpResponse<String> response = null;
        HttpRequest request;
        long max_id = 0L;
        try {
            while (tweets.size() < count) {
                request = buildUserTweetsRequest(screen_name, "200", max_id);
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.body().contains("code\":50")) {
                    throw new RequestManagerException("Unknown user");
                }

                //Sometimes twitter API gives bad result
                if (response.body().equals("[]")){
                    continue;
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

    public List<Tweet> getTweetsFromUserByDate(String screen_name, Date date) throws RequestManagerException {
        List<Tweet> tweets = new ArrayList<Tweet>();
        HttpResponse<String> response = null;
        HttpRequest request;
        long max_id = 0L;
        try {
            while (tweets.size() < 3200) {
                if (tweets.size() > 0 && (tweets.get(tweets.size() - 1).getCreated_at().before(date))) {
                    break;
                }
                request = buildUserTweetsRequest(screen_name, "200", max_id);
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

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

    public List<Tweet> searchTweets(String label) {

        String url = "https://api.twitter.com/1.1/search/tweets.json?q=%23" + label;

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .setHeader("Authorization", "Bearer " + bearer.getAccess_token())
                .build();
        HttpResponse<String> response = null;
        // Create a tweet Object
        ArrayList<Tweet> hundredRetweets = new ArrayList<>();
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());


            if (response.body().contains("code\":50")) {
                throw new RequestManagerException("Unknown user");
            }
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting() //human-readable json
                    .setLenient()
                    .create();
            // assign the line below to the tweet object
            /*String newBody = removeLastChar(response.body());
            String tableOfTweets = newBody.replace("{\"statuses\":","")+"]";
            System.out.println(tableOfTweets);*/
            Map jsonJavaRootObject = new Gson().fromJson(response.body(), Map.class);
            System.out.println(jsonJavaRootObject.get("statuses"));
            // take just the table
            Type listType = new TypeToken<ArrayList<Tweet>>() {
            }.getType();
            //hundredRetweets = gson.fromJson(jsonJavaRootObject.get("statuses").toString(),listType);

        } catch (Exception e) {
            e.printStackTrace();
            if (response.body().contains("code\":50")) {
                throw new RequestManagerException("Unknown user");
            }
        }
        return hundredRetweets;
    }

    /**
     * @author kamil CAGLAR
     * Porvides a request for getting the tweets from user timeline
     * @apiNote It is very important to have FULL TWEET. We add the tweet_mode=extended header
     */
    private HttpRequest buildUserTweetsRequest(String screen_name, String count, Long max_id) {
        String link = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + screen_name + "&count=" + count+"&tweet_mode=extended";

        if (max_id > 0) {
            link = link + "&max_id=" + max_id.toString();
        }

        //Building of the request, we use the header Authorization", "Bearer <bearer_code>"
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(link))
                .setHeader("Authorization", "Bearer " + bearer.getAccess_token())
                .build();

        return request;
    }

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

}
