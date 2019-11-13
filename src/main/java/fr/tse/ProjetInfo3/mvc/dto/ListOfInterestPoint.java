package fr.tse.ProjetInfo3.mvc.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ALAMI IDRISSI Taha
 *	In this class we define a list of interest point 
 */
public class ListOfInterestPoint {

	private List<InterestPoint> interestPoints;

	public ListOfInterestPoint() {
		this.interestPoints = new ArrayList<>();
	}

	public List<InterestPoint> getInterestPoints() {
		return interestPoints;
	}

	public void setInterestPoints(List<InterestPoint> interestPoints) {
		this.interestPoints = interestPoints;
	}
	
	
}
