/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.viewer;

import java.util.ArrayList;
import java.util.List;

import fr.tse.ProjetInfo3.mvc.dao.FavsDAO;
import fr.tse.ProjetInfo3.mvc.dto.Favourite;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.User;

/**
 * @author Laïla
 *
 */
public class FavsViewer {
    private static FavsDAO favsDAO = new FavsDAO();
    private List<Favourite> favourites;
    private List<User> favouriteUsers=new ArrayList<>();
    private List<Hashtag> favouriteHashtags=new ArrayList<>();

    
	public void addUserToFavourites(User user,int userID) {
		 favsDAO.addFavUser(user,userID);
	}
	public int checkUserInFav(User user,int userID) {
		return favsDAO.checkFavUser(user,userID);
	}
	public void addHashtagToFavourites(Hashtag hashtag,int userID) {
		 favsDAO.addFavHash(hashtag,userID);
	}
	public int checkHashInFav(Hashtag hashtag,int userID) {
		return favsDAO.checkFavHashtag(hashtag,userID);
	}
	public Favourite getlistOfFavourites(int userID) throws Exception {
		favourites= new ArrayList<>();
		favouriteUsers=favsDAO.getFavouriteUsers(userID);
		favouriteHashtags=favsDAO.getFavouriteHashtags(userID);
		Favourite favourite=new Favourite();
		favourite.setUsers(favouriteUsers);
		favourite.setHashtags(favouriteHashtags);
		
	return favourite;
	}
}
