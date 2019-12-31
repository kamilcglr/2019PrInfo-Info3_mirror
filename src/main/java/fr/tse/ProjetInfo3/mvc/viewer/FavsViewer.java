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

	public int addUserToFavourites(User user) {
		return favsDAO.checkFavUser(user);
	}
	public int addHashtagToFavourites(Hashtag hashtag) {
		return favsDAO.checkFavHash(hashtag);
	}
	public Favourite getlistOfFavourites() {
		favourites= new ArrayList<>();
		favouriteUsers=favsDAO.getFavouriteUsers();
		favouriteHashtags=favsDAO.getFavouriteHashtags();
		Favourite favourite=new Favourite();
		favourite.setUsers(favouriteUsers);
		favourite.setHashtags(favouriteHashtags);
		
	return favourite;
	}
}
