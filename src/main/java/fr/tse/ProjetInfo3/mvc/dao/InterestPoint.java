package fr.tse.ProjetInfo3.mvc.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ALAMI IDRISSI Taha
 *	In this class we define what an interest point is and what we can do with it by implementing the 
 *  InterestPointInterface multiple method with the same name but with different
 *   parameters are defined to have a variety of definition for an InterestPoint
 */

public class InterestPoint implements InterestPointInterface {
	
	private List<Hashtag> hashtags;
	private List<User> users;
	// a voir 
	private List<InterestPoint> interestPoints;
	
	public InterestPoint() {
		this.hashtags = new ArrayList<Hashtag>();
		this.users = new ArrayList<User>();
		this.interestPoints = new ArrayList<InterestPoint>();
	}

	public InterestPoint(List<Hashtag> hashtags, List<User> users) {
		super();
		this.hashtags = hashtags;
		this.users = users;
	}
	

	public InterestPoint(List<Hashtag> hashtags, List<User> users, List<InterestPoint> interestPoints) {
		super();
		this.hashtags = hashtags;
		this.users = users;
		this.interestPoints = interestPoints;
	}
	/*
	 * this method is going to get us all the interest point 
	 * (non-Javadoc)
	 * @see fr.tse.ProjetInfo3.mwp.dao.InterestPointInterface#retreiveAllInterestPoints()
	 */
	@Override
	public List<InterestPoint> retreiveAllInterestPoints() {
		return this.interestPoints;
	}
	/*
	 * every interest point that going to call this method is going to add to his 
	 * current list a brand new hashtag and a new user
	 * (non-Javadoc)
	 * @see fr.tse.ProjetInfo3.mwp.dao.InterestPointInterface#retreiveAllInterestPoints()
	 */
	@Override
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
	@Override
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
	@Override
	public void addToInterestPoint(Hashtag hashtag) {
		// TODO Auto-generated method stub
		this.hashtags.add(hashtag);
	}

	/*
	 * Method not FINISHED YET !
	 * (non-Javadoc)
	 * @see fr.tse.ProjetInfo3.mwp.dao.InterestPointInterface#retreiveAllInterestPoints()
	 */
	@Override
	public boolean modifyInterestPoint(InterestPoint interestPoint,Hashtag hashtag, User user) {
		
		for(InterestPoint ip : interestPoints) {
			if(ip.equals(interestPoint)) {
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

	@Override
	public boolean createInterestPoint(Hashtag hashtag, User user) {
		InterestPoint interestPoint = new InterestPoint();
		// create a list of Interest of point to check (after)  if the interest point already exist
		for(InterestPoint ip : interestPoints) {
			if(ip.equals(interestPoint)) 
				return false;
		}
		
		interestPoint.hashtags.add(hashtag);
		interestPoint.users.add(user);
		
		interestPoint.interestPoints.add(interestPoint);
		return true;
	}
	/*
	 * creating a new Interest point
	 * (non-Javadoc)
	 * @see fr.tse.ProjetInfo3.mwp.dao.InterestPointInterface#retreiveAllInterestPoints()
	 */
	@Override
	public boolean createInterestPoint(User user) {
		// TODO Auto-generated method stub
		return false;
	}
	/*
	 * creating a new Interest point
	 * (non-Javadoc)
	 * @see fr.tse.ProjetInfo3.mwp.dao.InterestPointInterface#retreiveAllInterestPoints()
	 */
	@Override
	public boolean createInterestPoint(Hashtag hashtag) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * removing an interest point from the list of interests points 
	 * (non-Javadoc)
	 * @see fr.tse.ProjetInfo3.mwp.dao.InterestPointInterface#retreiveAllInterestPoints()
	 */
	@Override
	public boolean removeInterestPoint(InterestPoint interestPoint) {
		// TODO Auto-generated method stub
		for(InterestPoint ip : interestPoints) {
			if(ip.equals(interestPoints))
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hashtags == null) ? 0 : hashtags.hashCode());
		result = prime * result + ((users == null) ? 0 : users.hashCode());
		return result;
	}

	@Override
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
			if (other.users != null)
				return false;
		} else if (!users.equals(other.users))
			return false;
		return true;
	}	
	
}
