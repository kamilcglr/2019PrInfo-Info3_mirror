package fr.tse.ProjetInfo3.mvc.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ALAMI IDRISSI Taha
 * In this class we define what an interest point is and what we can do with it by implementing the
 * InterestPointInterface multiple method with the same name but with different
 * parameters are defined to have a variety of definition for an InterestPoint
 */

public class InterestPoint implements Serializable {
    private int id;
    private String name;
    private String description;
    private Date dateOfCreation;

    private List<Hashtag> hashtags;
    private List<User> users;
    private List<Tweet> tweets;
    private List<InterestPoint> interestPoints;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public InterestPoint() {
    }

    public InterestPoint(InterestPoint interestPoint) {
        super();
        this.id = interestPoint.getId();
        this.name = interestPoint.getName();
        this.description = interestPoint.getDescription();
        this.dateOfCreation = interestPoint.getDateOfCreation();
        this.hashtags = interestPoint.getHashtags();
        this.users = interestPoint.getUsers();
        this.interestPoints = interestPoint.getInterestPoints();
        this.tweets = interestPoint.getTweets();
    }

    public InterestPoint(int id, String name, String description, Date dateOfCreation) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateOfCreation = dateOfCreation;
    }

    public InterestPoint(String name, String description, Date dateOfCreation) {
        this.name = name;
        this.description = description;
        this.dateOfCreation = dateOfCreation;
        this.hashtags = new ArrayList<Hashtag>();
        this.users = new ArrayList<User>();
        this.interestPoints = new ArrayList<InterestPoint>();
        this.tweets = new ArrayList<Tweet>();
    }

    public InterestPoint(String name, String description, Date dateOfCreation, List<Hashtag> hashtags, List<User> users) {
        this.name = name;
        this.description = description;
        this.dateOfCreation = dateOfCreation;
        this.hashtags = hashtags;
        this.users = users;
        this.interestPoints = new ArrayList<InterestPoint>();
        this.tweets = new ArrayList<Tweet>();
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

    public boolean containsUser(User user) {
        return this.getUsers().contains(user);
        //System.out.println(user);
        //System.out.println(user.getName());
        //List<String> screenNames = this.getUsers().stream().map(User::getName).collect(Collectors.toList());
        //System.out.println(screenNames);
        //return this.getUsers().stream().map(User::getName).collect(Collectors.toList()).contains(user.getName());
    }

    public boolean containsHashtag(String hashtagName) {
        System.out.println(this.getHashtags().stream()
                .map(Hashtag::getHashtag)
                .collect(Collectors.toList())
                .contains(hashtagName));
        return this.getHashtags().stream()
                .map(Hashtag::getHashtag)
                .collect(Collectors.toList())
                .contains(hashtagName);
    }

    public Hashtag getHashtagFromName(String hashtag) {
        List<Hashtag> hashtags = this.getHashtags();
        Hashtag found = null;

        for (int i = 0; i < hashtags.size(); i++) {
            System.out.println(hashtags.get(i).getHashtag());
            Hashtag candidate = hashtags.get(i);
            if (candidate.getHashtag() == hashtag) {
                found = candidate;
                System.out.println("Found " + found.getHashtag());
                return found;
            }
        }

        return found;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public List<Hashtag> getHashtags() {
        return hashtags;
    }

    public List<String> getHashtagNames() {
        List<String> hashtagNames = new ArrayList<String>();
        for (int i = 0; i < hashtags.size(); i++) {
            hashtagNames.add(hashtags.get(i).getHashtag());
        }

        return hashtagNames;
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
