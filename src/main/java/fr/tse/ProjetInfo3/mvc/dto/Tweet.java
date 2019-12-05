package fr.tse.ProjetInfo3.mvc.dto;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author kamilcaglar
 * This class is fed by the request manager
 * THE NAME OF ATTRIBUTES MUST BE THE SAME WIT TWITTER RESPONSE
 * PLEASE EXPLAIN IF IT IS NECESSARY WHY WE NEED THEM
 * https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/tweet-object
 */
public class Tweet {

    private Date created_at;
    private long id;
    //don't change this,
    // you have to add tweet_mode=extended if there is not this entry in your result
    private String full_text;
    private Boolean truncated;
    private User user;

    private long quoted_status_id;
    private Tweet quoted_status;
    private Tweet retweeted_status;
    private long quote_count;
    private long reply_count;
    private long retweet_count;
    private long favorite_count;

    //contains hashtags and other object of tweet (urls...)
    private entities entities;

    public void setQuote_count(long quote_count) {
        this.quote_count = quote_count;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "created_at=" + created_at +
                ", id=" + id +
                ", text='" + full_text + '\'' +
                ", truncated=" + truncated +
                ", user=" + user +
                ", quoted_status_id=" + quoted_status_id +
                ", quoted_status=" + quoted_status +
                ", retweeted_status=" + retweeted_status +
                ", quote_count=" + quote_count +
                ", reply_count=" + reply_count +
                ", retweet_count=" + retweet_count +
                ", favorite_count=" + favorite_count +
                ", entities=" + entities +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tweet tweet = (Tweet) o;

        if (id != tweet.id) return false;
        if (quoted_status_id != tweet.quoted_status_id) return false;
        if (quote_count != tweet.quote_count) return false;
        if (reply_count != tweet.reply_count) return false;
        if (retweet_count != tweet.retweet_count) return false;
        if (favorite_count != tweet.favorite_count) return false;
        if (created_at != null ? !created_at.equals(tweet.created_at) : tweet.created_at != null) return false;
        if (full_text != null ? !full_text.equals(tweet.full_text) : tweet.full_text != null) return false;
        if (truncated != null ? !truncated.equals(tweet.truncated) : tweet.truncated != null) return false;
        if (user != null ? !user.equals(tweet.user) : tweet.user != null) return false;
        if (quoted_status != null ? !quoted_status.equals(tweet.quoted_status) : tweet.quoted_status != null)
            return false;
        if (retweeted_status != null ? !retweeted_status.equals(tweet.retweeted_status) : tweet.retweeted_status != null)
            return false;
        return entities != null ? entities.equals(tweet.entities) : tweet.entities == null;
    }

    @Override
    public int hashCode() {
        int result = created_at != null ? created_at.hashCode() : 0;
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (full_text != null ? full_text.hashCode() : 0);
        result = 31 * result + (truncated != null ? truncated.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (int) (quoted_status_id ^ (quoted_status_id >>> 32));
        result = 31 * result + (quoted_status != null ? quoted_status.hashCode() : 0);
        result = 31 * result + (retweeted_status != null ? retweeted_status.hashCode() : 0);
        result = 31 * result + (int) (quote_count ^ (quote_count >>> 32));
        result = 31 * result + (int) (reply_count ^ (reply_count >>> 32));
        result = 31 * result + (int) (retweet_count ^ (retweet_count >>> 32));
        result = 31 * result + (int) (favorite_count ^ (favorite_count >>> 32));
        result = 31 * result + (entities != null ? entities.hashCode() : 0);
        return result;
    }

    public Tweet.entities getEntities() {
        return entities;
    }

    public void setEntities(Tweet.entities entities) {
        this.entities = entities;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getFull_text() {
        return full_text;
    }

    public void setFull_text(String full_text) {
        this.full_text = full_text;
    }

    public Boolean getTruncated() {
        return truncated;
    }

    public void setTruncated(Boolean truncated) {
        this.truncated = truncated;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getQuoted_status_id() {
        return quoted_status_id;
    }

    public void setQuoted_status_id(long quoted_status_id) {
        this.quoted_status_id = quoted_status_id;
    }

    public Tweet getQuoted_status() {
        return quoted_status;
    }

    public void setQuoted_status(Tweet quoted_status) {
        this.quoted_status = quoted_status;
    }

    public Tweet getRetweeted_status() {
        return retweeted_status;
    }

    public void setRetweeted_status(Tweet retweeted_status) {
        this.retweeted_status = retweeted_status;
    }

    public long getQuote_count() {
        return quote_count;
    }

    public void setQuote_count(Integer quote_count) {
        this.quote_count = quote_count;
    }

    public long getReply_count() {
        return reply_count;
    }

    public void setReply_count(long reply_count) {
        this.reply_count = reply_count;
    }

    public long getRetweet_count() {
        return retweet_count;
    }

    public void setRetweet_count(long retweet_count) {
        this.retweet_count = retweet_count;
    }

    public long getFavorite_count() {
        return favorite_count;
    }

    public void setFavorite_count(long favorite_count) {
        this.favorite_count = favorite_count;
    }

    /*
     * Entities object from twitter
     * It contains the list of hashtags for each tweets and user_mentions
     * https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/entities-object
     * We keep only user_mentions and hashtags for the moment*/
    public class entities {
        private List<hashtags> hashtags;
        private List<user_mentions> user_mentions;

        public List<Tweet.hashtags> getHashtags() {
            return hashtags;
        }

        public void setHashtags(List<Tweet.hashtags> hashtags) {
            this.hashtags = hashtags;
        }

        public List<Tweet.user_mentions> getUser_mentions() {
            return user_mentions;
        }

        public void setUser_mentions(List<Tweet.user_mentions> user_mentions) {
            this.user_mentions = user_mentions;
        }

        @Override
        public String toString() {
            return "entities{" +
                    "hashtags=" + hashtags +
                    ", user_mentions=" + user_mentions +
                    '}';
        }
    }

    /*
     * hashtag object from twitter
     * TODO delete indices if it is not necessary
     * */
    public class hashtags {
        private int[] indices;
        private String text;

        public int[] getIndices() {
            return indices;
        }

        public void setIndices(int[] indices) {
            this.indices = indices;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "hashtags{" +
                    "indices=" + Arrays.toString(indices) +
                    ", text='" + text + '\'' +
                    '}';
        }
    }

    /*
     * user object from twitter entities
     * */
    public class user_mentions {
        private long id;
        private String name;
        private String screen_name;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getScreen_name() {
            return screen_name;
        }

        public void setScreen_name(String screen_name) {
            this.screen_name = screen_name;
        }
    }
}
