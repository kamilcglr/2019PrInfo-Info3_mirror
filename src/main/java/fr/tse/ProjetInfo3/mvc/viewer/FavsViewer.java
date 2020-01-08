package fr.tse.ProjetInfo3.mvc.viewer;

import fr.tse.ProjetInfo3.mvc.dto.Favourites;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.DatabaseManager;

/**
 * @author Laïla
 */
public class FavsViewer {
    private Favourites favourites;
    private DatabaseManager databaseManager;


    public void setUserID(int user_id) {
        favourites.setUser_id(user_id);
        getFavoritesFromDatabase();
    }

    public FavsViewer(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
        favourites = new Favourites();
    }

    public void addUserToFavourites(User user) {
        favourites.addUser(user);
        updateFavoritesFromDatabase();
    }

    public void removeUserFromFavourites(User user) {
        favourites.removeUser(user.getScreen_name());
    }

    public void removeHashtagFromFavourites(Hashtag hashtag) {
        favourites.removeHashtag(hashtag.getHashtag());
    }

    public void addHashtagToFavourites(Hashtag hashtag) {
        favourites.addHashtag(hashtag);
        updateFavoritesFromDatabase();
    }

    public boolean checkUserInFav(User user) {
        getFavoritesFromDatabase();
        return favourites.containsUser(user.getScreen_name());
    }

    public boolean checkHashInFav(Hashtag hashtag) {
        return favourites.containsHashtag(hashtag.getHashtag());
    }

    public void getFavoritesFromDatabase() {
        favourites = databaseManager.getFavourite(favourites.getUser_id());
    }

    public void updateFavoritesFromDatabase() {
        databaseManager.updateFavouritesInDatabase(favourites);
    }



    public Favourites getFavourites() {
        return favourites;
    }


}
