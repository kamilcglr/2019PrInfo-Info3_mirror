package fr.tse.ProjetInfo3.mvc.services;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fr.tse.ProjetInfo3.mvc.dao.Tweet;
import fr.tse.ProjetInfo3.mvc.dao.User;

/**
 * @author Sergiy
 * @author kamilcaglar
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

            System.out.println(response.body().toString());

            //add the access_token value to bearer, we will use it for other request
            bearer = gson.fromJson(response.body().toString(), RequestManager.Bearer.class);

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
        String link = "https://api.twitter.com/1.1/users/show.json?screen_name=" + screen_name;

        //Building of the request, we use the header Authorization", "Bearer <bearer_code>"
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(link))
                .setHeader("Authorization", "Bearer " + bearer.getAccess_token())
                .build();

        HttpResponse<String> response = null;
        User userReturned = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.body().toString().contains("code\":50")) {
                throw new RequestManagerException("Unknown user");
            }
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting() //human-readable json
                    .create();

            //gson will complete the attributes of object if it finds elements that have the same name
            userReturned = gson.fromJson(response.body().toString(), User.class);

            System.out.println(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            if (response.body().toString().contains("code\":50")) {
                throw new RequestManagerException("Unknown user");
            }
        }
        return userReturned;
    }

    /**
     * Return List Of tweets of some user
     * https://developer.twitter.com/en/docs/tweets/timelines/api-reference/get-statuses-user_timeline
     *
     * @param screen_name the name of the profile, e.g. realdonaldtrump
     * @param count number of tweets (max 200 per request)
     * @return User
     */
    public List<Tweet> getTweetsFromUSer(String screen_name, int count) throws RequestManagerException {
        // We cannot pass the parameters as arguments of get method
        // We add the parameters of id directly in the link
        String link = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=" + screen_name + "&count=" + String.valueOf(count);
        System.out.println(link);
        //Building of the request, we use the header Authorization", "Bearer <bearer_code>"
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(link))
                .setHeader("Authorization", "Bearer " + bearer.getAccess_token())
                .build();

        HttpResponse<String> response = null;
        Tweet[] tweetList = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.body().toString().contains("code\":50")) {
                throw new RequestManagerException("Unknown user");
            }
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting() //human-readable json
                    .create();

            //gson will complete the attributes of object if it finds elements that have the same name
            
            tweetList = gson.fromJson(response.body().toString(), Tweet[].class);
            

            System.out.println(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            if (response.body().toString().contains("code\":50")) {
                throw new RequestManagerException("Unknown user");
            }
        }
        List<Tweet> tweets = new ArrayList<Tweet>(Arrays.asList(tweetList));
        return tweets;
    }
    
    /**
     * Return a single tweet by id
     * https://developer.twitter.com/en/docs/tweets/post-and-engage/api-reference/get-statuses-show-id
     * @author ALAMI IDRISSI Taha
     *	this method is gonna be useful when trying to get number of retweets of a hashtag
     */
    public Tweet getSingleTweet(String name , int id) throws RequestManagerException{
    	
    	String url = "https://api.twitter.com/1.1/statuses/show.json?id="+id;
    	
    	HttpRequest httpRequest = HttpRequest.newBuilder()
    										 .GET()
    										 .uri(URI.create(url))
    										 .setHeader("Authorization", "Bearer " + bearer.getAccess_token())
    							             .build();
    	HttpResponse<String> response = null;
        Tweet returnedTweet = null;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.body().toString().contains("code\":50")) {
                throw new RequestManagerException("Unknown user");
            }
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting() 
                    .create();

            
            returnedTweet = gson.fromJson(response.body().toString(), Tweet.class);

            System.out.println(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            if (response.body().toString().contains("code\":50")) {
                throw new RequestManagerException("Unknown user");
            }
        }
    	
    	return returnedTweet;
    }
    
    /**
     * Returns a collection of the 100 most recent retweets of the Tweet specified by the id parameter.
     * https://developer.twitter.com/en/docs/tweets/post-and-engage/api-reference/get-statuses-retweets-id
     * @author ALAMI IDRISSI Taha
     *	we're going to get for the moment just the 100 most recent retweets after that we're going to figure out how we can 
     * 	the number of retweets
     */
    
    	public List<Tweet> getHundredRecentRetweets(String name,int id) throws RequestManagerException {
    		
    		String url = "https://api.twitter.com/1.1/statuses/retweets/"+id+".json";
    		
    		HttpRequest httpRequest = HttpRequest.newBuilder()
    											 .GET()
    											 .uri(URI.create(url))
    											 .setHeader("Authorization", "Bearer " + bearer.getAccess_token())
    											 .build();
    		HttpResponse<String> response = null;
            Tweet[] hundredRetweets = null;
            try {
                response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                if (response.body().toString().contains("code\":50")) {
                    throw new RequestManagerException("Unknown user");
                }
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting() //human-readable json
                        .create();

                //gson will complete the attributes of object if it finds elements that have the same name
                hundredRetweets = gson.fromJson(response.body().toString(), Tweet[].class);

                System.out.println(response.body());
            } catch (Exception e) {
                e.printStackTrace();
                if (response.body().toString().contains("code\":50")) {
                    throw new RequestManagerException("Unknown user");
                }
            }
            
            List<Tweet> tweets = new ArrayList<Tweet>(Arrays.asList(hundredRetweets));
            return tweets;
    	}
    	
    	/**
         * @author ALAMI IDRISSI Taha
         * Simple Class that contains the access token
         * It is easier to complete from gson
         */
    	public String removeLastChar(String str) {
    	    if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == '}') {
    	        str = str.substring(0, str.length() - 1);
    	    }
    	    return str;
    	}
    	
    	public List<Tweet> searchTweets(String label){
    		
    		String url = "https://api.twitter.com/1.1/search/tweets.json?q=%23"+label;
    		
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
                

                if (response.body().toString().contains("code\":50")) {
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
                Type listType = new TypeToken<ArrayList<Tweet>>(){}.getType();
                
                hundredRetweets = gson.fromJson(jsonJavaRootObject.get("statuses").toString(),listType);
                
            } catch (Exception e) {
                e.printStackTrace();
                if (response.body().toString().contains("code\":50")) {
                    throw new RequestManagerException("Unknown user");
                }
            }
            
            return hundredRetweets;
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

    //ALL THIS PART WILL BE DELETED, WE WILL NOT USER TWITTER4J anymore
    ///*
    //* */
    //private Twitter twitter;
//
    //public RequestManager(Twitter twitter) {
    //	this.twitter = twitter;
    //}
//
    ///**
    // *
    // * @param id - identifier of a Twitter user
    // * @return A Profile object
    // * @throws IOException
    // */
    //public Profile getProfile(String id) throws IOException, TwitterException {
    //	Profile profile = null;
//
    //	String name;
    //	String description;
    //	int followersCount;
    //	int friendsCount;
    //	int favouritesCount;
    //	Image profileImage;
    //
    //	User user = null;
    //
    //	try {
    //		user = twitter.showUser(id);
    //
    //		name = user.getName();
    //		description = user.getDescription();
    //		followersCount = user.getFollowersCount();
    //		friendsCount = user.getFriendsCount();
    //		favouritesCount = user.getFavouritesCount();
    //
    //		URL url = new URL(user.get400x400ProfileImageURL());
    //		profileImage = ImageIO.read(url);
    //
    //		profile = new Profile(name, description, followersCount, friendsCount, favouritesCount, profileImage);
    //
    //	} catch (TwitterException e) {
    //		e.printStackTrace();
    //		//added by Kamil : permits to catch type of error when calling during search
    //		throw new TwitterException(e);
    //	}
    //
    //	return profile;
    //}
    //
    ///**
    // *
    // * @param request - a String corresponding to the hashtag to find
    // * @return A List of Tweets
    // */
    //public List<Status> getTweets(String request) throws TwitterException {
    //	Query query = new Query(request);
    //	List<Status> tweets = new ArrayList<>();
    //
    //	try {
    //		QueryResult qr = twitter.search(query);
    //		tweets = qr.getTweets();
    //
    //	} catch (TwitterException e) {
    //		e.printStackTrace();
    //		//added by Kamil : permits to catch type of error when calling during search
    //		throw new TwitterException(e);
    //	}
    //
    //	for (Status s : tweets) {
    //		System.out.println(s.getText());
    //	}
    //	System.out.println(tweets.size());
    //	return tweets;
    //}
    //
    //// Causes rate limit exceeding
    //void getAllTweets(String request) {
    //	Query query = new Query(request);
    //	List<Status> tweets = new ArrayList<>();
    //
    //	while (true) {
    //		try {
    //			QueryResult qr = twitter.search(query);
    //			List<Status> tempTweets = qr.getTweets();
    //
    //			if (tempTweets == null || tempTweets.isEmpty()) {
    //				break;
    //			} else {
    //				tweets.addAll(tempTweets);
    //				System.out.println(tweets.size());
    //
    //				for (Status s : tempTweets) {
    //					System.out.println(s.getId());
    //					System.out.println(s.getUser().getName());
    //				}
    //
    //			}
    //
    //		} catch (TwitterException e) {
    //			e.printStackTrace();
    //		}
    //
    //	}
    //	System.out.println(tweets.size());
    //}
}
