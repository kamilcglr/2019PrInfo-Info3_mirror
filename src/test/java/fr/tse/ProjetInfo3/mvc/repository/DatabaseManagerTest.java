package fr.tse.ProjetInfo3.mvc.repository;

import fr.tse.ProjetInfo3.mvc.dto.*;
import fr.tse.ProjetInfo3.mvc.viewer.FavsViewer;
import fr.tse.ProjetInfo3.mvc.viewer.HashtagViewer;
import fr.tse.ProjetInfo3.mvc.viewer.PIViewer;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import org.junit.jupiter.api.*;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseManagerTest {
    static DatabaseManager databaseManager;
    static UserViewer userViewer;
    static HashtagViewer hashtagViewer;
    static UserApp userApp;

    @BeforeAll
    static void initialize() {
        databaseManager = new DatabaseManager();
        userViewer = new UserViewer();
        hashtagViewer = new HashtagViewer();
        userApp = new UserApp();
    }

    @Test
    @Order(1)
    void createUserApp() {
        databaseManager.addNewUserToDataBase(userApp);
        userApp = databaseManager.getUserFromDataBase("", "");
        assertTrue(userApp.getId() != 0);
    }

    @Test
    @Order(2)
    void cacheAndGetUserFromCache() throws Exception {
        userViewer.searchScreenName("realdonaldtrump");
        User user1 = userViewer.getUser();
        user1.setTweets(userViewer.getTweetsByCount(user1.getScreen_name(), 3194, null));

        userViewer.searchScreenName("emmanuelmacron");
        User user2 = userViewer.getUser();
        user2.setTweets(userViewer.getTweetsByCount(user2.getScreen_name(), 3194, null));

        databaseManager.cacheUserToDataBase(user1);
        databaseManager.cacheUserToDataBase(user2);

        User userFromCache1 = databaseManager.getCachedUserFromDatabase(user1.getScreen_name());
        User userFromCache2 = databaseManager.getCachedUserFromDatabase(user2.getScreen_name());

        assertEquals(user1.getScreen_name(), userFromCache1.getScreen_name());
        assertEquals(user2.getScreen_name(), userFromCache2.getScreen_name());
        assertEquals(user1.getTweets().size(), userFromCache1.getTweets().size());
        assertEquals(user2.getTweets().size(), userFromCache2.getTweets().size());
    }

    @Test
    @Order(2)
    void testUserNotInDB() {
        assertFalse(databaseManager.cachedUserInDataBase("azerty"));
    }

    /**
     * Add then verify added, then delete and verify deleted.
     */
    @Test
    @Order(3)
    void testUserDelete() throws Exception {
        userViewer.searchScreenName("sobunung");
        User user2 = userViewer.getUser();
        user2.setTweets(userViewer.getTweetsByCount(user2.getScreen_name(), 3194, null));
        databaseManager.cacheUserToDataBase(user2);

        assertTrue(databaseManager.cachedUserInDataBase("sobunung"));
        databaseManager.deleteCachedUserFromDataBase("sobunung");
        assertFalse(databaseManager.cachedUserInDataBase("sobunung"));
    }

    @Test
    @Order(4)
    void cacheAndGetHashtagFromCache() throws Exception {
        hashtagViewer.setHashtag("test");
        Hashtag hashtag1 = new Hashtag(hashtagViewer.getHashtag().getHashtag());
        hashtag1.setTweets(hashtagViewer.searchByCount(hashtag1.getHashtag(), null, 1000, null));

        hashtagViewer.setHashtag("trump");
        Hashtag hashtag2 = new Hashtag(hashtagViewer.getHashtag().getHashtag());
        hashtag2.setTweets(hashtagViewer.searchByCount(hashtag2.getHashtag(), null, 4500, null));

        databaseManager.cacheHashtagToDataBase(hashtag1);
        databaseManager.cacheHashtagToDataBase(hashtag2);

        Hashtag hashtagFromCache1 = databaseManager.getCachedHashtagFromDatabase(hashtag1.getHashtag());
        Hashtag hashtagFromCache2 = databaseManager.getCachedHashtagFromDatabase(hashtag2.getHashtag());

        assertEquals(hashtag1.getHashtag(), hashtagFromCache1.getHashtag());
        assertEquals(hashtag2.getHashtag(), hashtagFromCache2.getHashtag());
        assertEquals(hashtag1.getTweets().size(), hashtag1.getTweets().size());
        assertEquals(hashtag2.getTweets().size(), hashtag2.getTweets().size());
    }

    @Test
    @Order(5)
    void testHashtagNotInDB() {
        assertFalse(databaseManager.cachedHashtagInDataBase("azerty"));
    }

    /**
     * Add then verify added, then delete and verify deleted.
     */
    @Test
    @Order(6)
    void testHashtagDelete() throws Exception {
        hashtagViewer.setHashtag("test");
        Hashtag hashtag1 = new Hashtag(hashtagViewer.getHashtag().getHashtag());
        hashtag1.setTweets(hashtagViewer.searchByCount(hashtag1.getHashtag(), null, 1000, null));

        assertTrue(databaseManager.cachedHashtagInDataBase("test"));
        databaseManager.deleteCachedHashtagFromDatabase("test");
        assertFalse(databaseManager.cachedHashtagInDataBase("test"));
    }

    /**
     * Test interest point saving. User must exists first.
     */
    @Test
    @Order(7)
    void testSaveInterestPointToDataBase() throws Exception {
        PIViewer piViewer = new PIViewer();
        InterestPoint interestPoint = new InterestPoint("test", "description interest point", new Date());
        interestPoint.setUserID(userApp.getId());
        piViewer.addInterestPointToDatabase(interestPoint);

        List<InterestPoint> interestPoints = piViewer.getlistOfInterestPoint(userApp.getId());

        int before = interestPoints.size();
        assertTrue(before > 0);
        piViewer.deleteInterestPointFromDatabaseById(interestPoint.getId());

        interestPoints = piViewer.getlistOfInterestPoint(userApp.getId());
        int after = interestPoints.size();
        assertTrue(after < before);
    }

    /**
     * You have to create an user first
     */
    @Test
    @Order(8)
    void testCreateFavorites() throws Exception {
        userViewer.searchScreenName("realdonaldtrump");
        User user1 = userViewer.getUser();

        userViewer.searchScreenName("emmanuelmacron");
        User user2 = userViewer.getUser();


        hashtagViewer.setHashtag("test");
        Hashtag hashtag1 = new Hashtag(hashtagViewer.getHashtag().getHashtag());

        hashtagViewer.setHashtag("trump");
        Hashtag hashtag2 = new Hashtag(hashtagViewer.getHashtag().getHashtag());

        FavsViewer favsViewer = new FavsViewer(databaseManager);
        favsViewer.setUserID(userApp.getId());

        favsViewer.getFavoritesFromDatabase();
        favsViewer.addUserToFavourites(user1);
        favsViewer.addUserToFavourites(user2);
        favsViewer.addHashtagToFavourites(hashtag1);
        favsViewer.addHashtagToFavourites(hashtag2);

        favsViewer.getFavoritesFromDatabase();
        Favourites favouritesFromDB = favsViewer.getFavourites();

        assertTrue(favouritesFromDB.containsUser(user1.getScreen_name()));
        assertTrue(favouritesFromDB.containsUser(user2.getScreen_name()));
        assertTrue(favouritesFromDB.containsHashtag(hashtag1.getHashtag()));
        assertTrue(favouritesFromDB.containsHashtag(hashtag2.getHashtag()));
    }


}