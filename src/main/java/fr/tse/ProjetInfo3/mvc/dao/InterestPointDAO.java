package fr.tse.ProjetInfo3.mvc.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.repository.SingletonDBConnection;

/**
 * @author ALAMI IDRISSI Taha
 *	in this class we will perform all the actions for the PIs that have a direct relations with the DB 
 *	like Saving , reading , updating PI in the DB
 */
public class InterestPointDAO {
	
	public InterestPoint saveInterestPoint(InterestPoint interestPoint) {
		Connection connection = SingletonDBConnection.getInstance();
		try { 
			PreparedStatement ps = connection.prepareStatement("INSERT INTO interestpoint(interestpoint_id,NAME,DESCRIPTION,CREATED_AT) "
															 + "VALUES (?,?,?,?)");
			ps.setInt(1, interestPoint.getId());
			ps.setString(2, interestPoint.getName());
			ps.setString(3, interestPoint.getDescription());
			ps.setDate(4, (Date) interestPoint.getDateOfCreation());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return interestPoint;
	}
	
	public InterestPoint saveHashtag(InterestPoint interestPoint) {
		Connection connection = SingletonDBConnection.getInstance();
		try { 
			PreparedStatement ps = connection.prepareStatement("INSERT INTO hashtag(hashtag_id,hashtag,interestpoint_id) "
															 + "VALUES (?,?,?)");
			
			for(Hashtag hash:interestPoint.getHashtags()) {
				ps.setInt(1, hash.getId());
				ps.setString(2, hash.getHashtag());
				ps.setInt(3, interestPoint.getId());
			}
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return interestPoint;
	}
	
	public List<InterestPoint> getAllInterestPoints(){
		Connection connection = SingletonDBConnection.getInstance();
		List<InterestPoint> interestPoints = new ArrayList<>();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM interestpoint");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				InterestPoint interestPoint = new InterestPoint();
				interestPoint.setId(rs.getInt("interestpoint_id"));
				interestPoint.setName(rs.getString("NAME"));
				interestPoint.setDescription(rs.getString("DESCRIPTION"));
				interestPoint.setDateOfCreation(rs.getDate("CREATED_AT"));
				interestPoints.add(interestPoint);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return interestPoints;
	}
	
	public InterestPoint getSelectedInterestPoint(int id) {
		Connection connection = SingletonDBConnection.getInstance();
		InterestPoint interestPoint = new InterestPoint();
		try {
			PreparedStatement ps = connection.prepareStatement("SELECT * FROM interestpoint WHERE id= ?");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				interestPoint.setId(rs.getInt("interestpoint_id"));
				interestPoint.setName(rs.getString("NAME"));
				interestPoint.setDescription(rs.getString("DESCRIPTION"));
				interestPoint.setDateOfCreation(rs.getDate("CREATED_AT"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return interestPoint;
	}

}
