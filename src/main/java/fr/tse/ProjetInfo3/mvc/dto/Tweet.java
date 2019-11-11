package fr.tse.ProjetInfo3.mvc.dto;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author kamilcaglar
 * This class is fed by the request manager
 * THE NAME OF ATTRIBUTES MUST BE THE SAME WIT TWITTER RESPONSE
 * https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/tweet-object
 * We can add more attributes or delete if they are unnecessary
 */
public class Tweet {
    private Date created_at;
    //private String created_at;
    private long id;
    private String id_str;
    private String text;
    private Boolean truncated;
    private long in_reply_to_status_id;
    private String in_reply_to_status_id_str;
    private long in_reply_to_user_id;
    private String in_reply_to_user_id_str;
    private String in_reply_to_screen_name;
    private User user;
    private long quoted_status_id;
    private String quoted_status_id_str;
    private Boolean is_quote_status;
    private Tweet quoted_status;
    private Tweet retweeted_status;
    private Integer quote_count;
    private long reply_count;
    private long retweet_count;
    private long favorite_count;
    private Boolean favorited;
    private Boolean retweeted;
    private entities entities;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tweet tweet = (Tweet) o;

        if (id != tweet.id) return false;
        if (in_reply_to_status_id != tweet.in_reply_to_status_id) return false;
        if (in_reply_to_user_id != tweet.in_reply_to_user_id) return false;
        if (quoted_status_id != tweet.quoted_status_id) return false;
        if (reply_count != tweet.reply_count) return false;
        if (retweet_count != tweet.retweet_count) return false;
        if (favorite_count != tweet.favorite_count) return false;
        if (created_at != null ? !created_at.equals(tweet.created_at) : tweet.created_at != null) return false;
        if (id_str != null ? !id_str.equals(tweet.id_str) : tweet.id_str != null) return false;
        if (text != null ? !text.equals(tweet.text) : tweet.text != null) return false;
        if (truncated != null ? !truncated.equals(tweet.truncated) : tweet.truncated != null) return false;
        if (in_reply_to_status_id_str != null ? !in_reply_to_status_id_str.equals(tweet.in_reply_to_status_id_str) : tweet.in_reply_to_status_id_str != null)
            return false;
        if (in_reply_to_user_id_str != null ? !in_reply_to_user_id_str.equals(tweet.in_reply_to_user_id_str) : tweet.in_reply_to_user_id_str != null)
            return false;
        if (in_reply_to_screen_name != null ? !in_reply_to_screen_name.equals(tweet.in_reply_to_screen_name) : tweet.in_reply_to_screen_name != null)
            return false;
        if (user != null ? !user.equals(tweet.user) : tweet.user != null) return false;
        if (quoted_status_id_str != null ? !quoted_status_id_str.equals(tweet.quoted_status_id_str) : tweet.quoted_status_id_str != null)
            return false;
        if (is_quote_status != null ? !is_quote_status.equals(tweet.is_quote_status) : tweet.is_quote_status != null)
            return false;
        if (quoted_status != null ? !quoted_status.equals(tweet.quoted_status) : tweet.quoted_status != null)
            return false;
        if (retweeted_status != null ? !retweeted_status.equals(tweet.retweeted_status) : tweet.retweeted_status != null)
            return false;
        if (quote_count != null ? !quote_count.equals(tweet.quote_count) : tweet.quote_count != null) return false;
        if (favorited != null ? !favorited.equals(tweet.favorited) : tweet.favorited != null) return false;
        if (retweeted != null ? !retweeted.equals(tweet.retweeted) : tweet.retweeted != null) return false;
        return entities != null ? entities.equals(tweet.entities) : tweet.entities == null;
    }

    @Override
    public int hashCode() {
        int result = created_at != null ? created_at.hashCode() : 0;
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (id_str != null ? id_str.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (truncated != null ? truncated.hashCode() : 0);
        result = 31 * result + (int) (in_reply_to_status_id ^ (in_reply_to_status_id >>> 32));
        result = 31 * result + (in_reply_to_status_id_str != null ? in_reply_to_status_id_str.hashCode() : 0);
        result = 31 * result + (int) (in_reply_to_user_id ^ (in_reply_to_user_id >>> 32));
        result = 31 * result + (in_reply_to_user_id_str != null ? in_reply_to_user_id_str.hashCode() : 0);
        result = 31 * result + (in_reply_to_screen_name != null ? in_reply_to_screen_name.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (int) (quoted_status_id ^ (quoted_status_id >>> 32));
        result = 31 * result + (quoted_status_id_str != null ? quoted_status_id_str.hashCode() : 0);
        result = 31 * result + (is_quote_status != null ? is_quote_status.hashCode() : 0);
        result = 31 * result + (quoted_status != null ? quoted_status.hashCode() : 0);
        result = 31 * result + (retweeted_status != null ? retweeted_status.hashCode() : 0);
        result = 31 * result + (quote_count != null ? quote_count.hashCode() : 0);
        result = 31 * result + (int) (reply_count ^ (reply_count >>> 32));
        result = 31 * result + (int) (retweet_count ^ (retweet_count >>> 32));
        result = 31 * result + (int) (favorite_count ^ (favorite_count >>> 32));
        result = 31 * result + (favorited != null ? favorited.hashCode() : 0);
        result = 31 * result + (retweeted != null ? retweeted.hashCode() : 0);
        result = 31 * result + (entities != null ? entities.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "created_at='" + created_at + '\'' +
                ", id=" + id +
                ", id_str='" + id_str + '\'' +
                ", text='" + text + '\'' +
                ", truncated=" + truncated +
                ", in_reply_to_status_id=" + in_reply_to_status_id +
                ", in_reply_to_status_id_str='" + in_reply_to_status_id_str + '\'' +
                ", in_reply_to_user_id=" + in_reply_to_user_id +
                ", in_reply_to_user_id_str='" + in_reply_to_user_id_str + '\'' +
                ", in_reply_to_screen_name='" + in_reply_to_screen_name + '\'' +
                ", user=" + user +
                ", quoted_status_id=" + quoted_status_id +
                ", quoted_status_id_str='" + quoted_status_id_str + '\'' +
                ", is_quote_status=" + is_quote_status +
                ", quoted_status=" + quoted_status +
                ", retweeted_status=" + retweeted_status +
                ", quote_count=" + quote_count +
                ", reply_count=" + reply_count +
                ", retweet_count=" + retweet_count +
                ", favorite_count=" + favorite_count +
                ", favorited=" + favorited +
                ", retweeted=" + retweeted +
                ", entities=" + entities +
                '}';
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

    public String getId_str() {
        return id_str;
    }

    public void setId_str(String id_str) {
        this.id_str = id_str;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getTruncated() {
        return truncated;
    }

    public void setTruncated(Boolean truncated) {
        this.truncated = truncated;
    }

    public long getIn_reply_to_status_id() {
        return in_reply_to_status_id;
    }

    public void setIn_reply_to_status_id(long in_reply_to_status_id) {
        this.in_reply_to_status_id = in_reply_to_status_id;
    }

    public String getIn_reply_to_status_id_str() {
        return in_reply_to_status_id_str;
    }

    public void setIn_reply_to_status_id_str(String in_reply_to_status_id_str) {
        this.in_reply_to_status_id_str = in_reply_to_status_id_str;
    }

    public long getIn_reply_to_user_id() {
        return in_reply_to_user_id;
    }

    public void setIn_reply_to_user_id(long in_reply_to_user_id) {
        this.in_reply_to_user_id = in_reply_to_user_id;
    }

    public String getIn_reply_to_user_id_str() {
        return in_reply_to_user_id_str;
    }

    public void setIn_reply_to_user_id_str(String in_reply_to_user_id_str) {
        this.in_reply_to_user_id_str = in_reply_to_user_id_str;
    }

    public String getIn_reply_to_screen_name() {
        return in_reply_to_screen_name;
    }

    public void setIn_reply_to_screen_name(String in_reply_to_screen_name) {
        this.in_reply_to_screen_name = in_reply_to_screen_name;
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

    public String getQuoted_status_id_str() {
        return quoted_status_id_str;
    }

    public void setQuoted_status_id_str(String quoted_status_id_str) {
        this.quoted_status_id_str = quoted_status_id_str;
    }

    public Boolean getIs_quote_status() {
        return is_quote_status;
    }

    public void setIs_quote_status(Boolean is_quote_status) {
        this.is_quote_status = is_quote_status;
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

    public Integer getQuote_count() {
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

    public Boolean getFavorited() {
        return favorited;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }

    public Boolean getRetweeted() {
        return retweeted;
    }

    public void setRetweeted(Boolean retweeted) {
        this.retweeted = retweeted;
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
