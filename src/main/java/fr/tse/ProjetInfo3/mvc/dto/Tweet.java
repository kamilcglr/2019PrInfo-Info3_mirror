package fr.tse.ProjetInfo3.mvc.dto;

/**
 * @author kamilcaglar
 * This class is fed by the request manager
 * THE NAME OF ATTRIBUTES MUST BE THE SAME WIT TWITTER RESPONSE
 * https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/tweet-object
 * We can add more attributes or delete if they are unnecessary
 * */
public class Tweet {
    private String created_at;
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
                '}';
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
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
}
