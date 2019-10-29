package fr.tse.ProjetInfo3.mwp.dao;

import java.util.List;
/**
 * @author ALAMI IDRISSI Taha
 *	this interface is a contract that's going to be implemented by the InterestPoint interface 
 *  every method is going to be overwritten to avoid creating 
 *  Object after that are going to be created like this : InterestPointInterface ip = new InterestPoint();
 */
public interface InterestPointInterface {
	// for adding a single hashtag and a single user to the lists of users and hashtags of a certain interestPoint
	public void addToInterestPoint(Hashtag hashtag, User user);
	// to modify an InterestPoint (not finished yet !)
	public boolean modifyInterestPoint(InterestPoint interestPoint,Hashtag hashtag, User user);
	// creating a brand new interest point on its own
	public boolean createInterestPoint(Hashtag hashtag,User user);
	// removing an interest point from the list of interests points
	public boolean removeInterestPoint(InterestPoint interestPoint);
	// getting all the interest points
	public List<InterestPoint> retreiveAllInterestPoints();
}
