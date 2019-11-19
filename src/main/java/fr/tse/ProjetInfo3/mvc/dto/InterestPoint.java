package fr.tse.ProjetInfo3.mvc.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ALAMI IDRISSI Taha
 * In this class we define what an interest point is and what we can do with it by implementing the
 * InterestPointInterface multiple method with the same name but with different
 * parameters are defined to have a variety of definition for an InterestPoint
 */

public class InterestPoint {
    private String name;
    private String description;
    private Date dateOfCreation;

    private List<Hashtag> hashtags;
    private List<User> users;
    private List<Tweet> tweets;
    private String title;
    private List<InterestPoint> interestPoints;

    public InterestPoint(String name, String description, Date dateOfCreation) {
        this.name = name;
        this.description = description;
        this.dateOfCreation = dateOfCreation;
        this.hashtags = new ArrayList<Hashtag>();
        this.users = new ArrayList<User>();
        this.interestPoints = new ArrayList<InterestPoint>();
        this.tweets = new ArrayList<Tweet>();
    }

    public InterestPoint(List<Hashtag> hashtags, List<User> users, String title) {
        this.hashtags = hashtags;
        this.users = users;
        this.title = title;
    }

    public InterestPoint(String name, String description, Date dateOfCreation, List<Hashtag> hashtags, List<User> users) {
        super();
        this.name = name;
        this.description = description;
        this.dateOfCreation = dateOfCreation;
        this.hashtags = hashtags;
        this.users = users;
        this.title = title;
    }

    public InterestPoint(List<Hashtag> hashtags, String title) {
        super();
        this.hashtags = hashtags;
        this.title = title;
    }

    public InterestPoint(String name, String description, Date dateOfCreation, List<Hashtag> hashtags, List<User> users, List<InterestPoint> interestPoints) {
        super();
        this.name = name;
        this.description = description;
        this.dateOfCreation = dateOfCreation;
        this.hashtags = hashtags;
        this.users = users;
        this.interestPoints = interestPoints;
    }

    /*
     * every interest point that going to call this method is going to add to his
     * current list a brand new hashtag and a new user
     * (non-Javadoc)
     * @see fr.tse.ProjetInfo3.mwp.dao.InterestPointInterface#retreiveAllInterestPoints()
     */
    public void addToInterestPoint(Hashtag hashtag, User user) {
        this.hashtags.add(hashtag);
        this.users.add(user);
    }

    /*
     * every interest point that going to call this method is going to add to his
     * current list a brand new user
     * (non-Javadoc)
     * @see fr.tse.ProjetInfo3.mwp.dao.InterestPointInterface#retreiveAllInterestPoints()
     */
    public void addToInterestPoint(User user) {
        // TODO Auto-generated method stub
        this.users.add(user);
    }

    /*
     * every interest point that going to call this method is going to add to his
     * current list a brand new hashtag
     * (non-Javadoc)
     * @see fr.tse.ProjetInfo3.mwp.dao.InterestPointInterface#retreiveAllInterestPoints()
     */
    public void addToInterestPoint(Hashtag hashtag) {
        // TODO Auto-generated method stub
        this.hashtags.add(hashtag);
    }

    /*
     * Method not FINISHED YET !
     * (non-Javadoc)
     * @see fr.tse.ProjetInfo3.mwp.dao.InterestPointInterface#retreiveAllInterestPoints()
     */
    public boolean modifyInterestPoint(InterestPoint interestPoint, Hashtag hashtag, User user) {

        for (InterestPoint ip : interestPoints) {
            if (ip.equals(interestPoint)) {
                // for the moment we're just adding not modifying i'll solve problem really soon and quick
                interestPoint.addToInterestPoint(hashtag, user);
                return true;
            }
        }
        return false;
    }

    /*
     * creating a new Interest point
     * (non-Javadoc)
     * @see fr.tse.ProjetInfo3.mwp.dao.InterestPointInterface#retreiveAllInterestPoints()
     */


    public boolean createInterestPoint(Hashtag hashtag, User user, String name, String description, Date dateOfCreation) {
        InterestPoint interestPoint = new InterestPoint(name, description, dateOfCreation);
        // create a list of Interest of point to check (after)  if the interest point already exist
        for (InterestPoint ip : interestPoints) {
            if (ip.equals(interestPoint))
                return false;
        }

        interestPoint.hashtags.add(hashtag);
        interestPoint.users.add(user);
        interestPoint.setTitle(title);
        interestPoint.interestPoints.add(interestPoint);
        return true;
    }

    /*
     * removing an interest point from the list of interests points
     * (non-Javadoc)
     * @see fr.tse.ProjetInfo3.mwp.dao.InterestPointInterface#retreiveAllInterestPoints()
     */
    public boolean removeInterestPoint(InterestPoint interestPoint) {
        // TODO Auto-generated method stub
        for (InterestPoint ip : interestPoints) {
            if (ip.equals(interestPoints))
                return interestPoints.remove(interestPoint);
        }
        return false;
    }

    public List<Hashtag> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hashtags == null) ? 0 : hashtags.hashCode());
        result = prime * result + ((users == null) ? 0 : users.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InterestPoint other = (InterestPoint) obj;
        if (hashtags == null) {
            if (other.hashtags != null)
                return false;
        } else if (!hashtags.equals(other.hashtags))
            return false;
        if (users == null) {
            return other.users == null;
        } else return users.equals(other.users);
    }

    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setInterestPoints(List<InterestPoint> interestPoints) {
        this.interestPoints = interestPoints;
    }

    public List<InterestPoint> getInterestPoints() {
        return interestPoints;
    }

    @Override
    public String toString() {
        return "InterestPoint [name=" + name + ", description=" + description + ", dateOfCreation=" + dateOfCreation
                + "]";
    }

}
