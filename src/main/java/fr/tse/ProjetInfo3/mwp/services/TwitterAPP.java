package fr.tse.ProjetInfo3.mwp.services;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterAPP {
    private Twitter twitter;

    public TwitterAPP() {
        String consumer = "PahWHDFSZ02bTaqFUVamZ0iBI";
        String consumerSecret = "mGDqU2cwWrw85cMvj7YOBSczI8qZQM0IKKymdbRL82sXqtyhhr";
        String accessToken = "4664421557-y8N6WL3BVrhBTIfuzZcHqRmNZeGDkt0TbAFoz9g";
        String accessTokensecret = "riGJEs4QhZWjOgQyQJY4jQJM8nCRnsfHisU1Vnq1VpDiv";

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumer)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokensecret);
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    }

    public User getUserFromId(String id) {
        User user = null;
        try {
            user = twitter.showUser(id);
        } catch (TwitterException te) {
            te.printStackTrace();
        }
        return user;
    }
}
