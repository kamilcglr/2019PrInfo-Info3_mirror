package fr.tse.ProjetInfo3.mvc.repository;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.viewer.HashtagViewer;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseManagerTest {
    static DatabaseManager databaseManager;
    static UserViewer userViewer;
    static HashtagViewer hashtagViewer;

    @BeforeAll
    static void initialize() {
        databaseManager = new DatabaseManager();
        userViewer = new UserViewer();
        hashtagViewer = new HashtagViewer();
    }

    @Test
    @Order(1)
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
     * To be passed the database has to contain realdonaldtrump
     */
    @Test
    @Order(3)
    void testUserDelete() {
        assertTrue(databaseManager.cachedUserInDataBase("realdonaldtrump"));
        databaseManager.deleteCachedUserFromDataBase("realdonaldtrump");
        assertFalse(databaseManager.cachedUserInDataBase("realdonaldtrump"));
    }

    @Test
    @Order(4)
    void cacheAndGetHashtagFromCache() throws Exception {
        hashtagViewer.setHashtag("test");
        Hashtag hashtag1 = new Hashtag(hashtagViewer.getHashtag().getHashtag());
        hashtag1.setTweets(hashtagViewer.searchByCount(hashtag1.getHashtag(), null, 1000, null));

        hashtagViewer.setHashtag("trump");
        Hashtag hashtag2 = new Hashtag(hashtagViewer.getHashtag().getHashtag());
        hashtag2.setTweets(hashtagViewer.searchByCount(hashtag2.getHashtag(), null, 10000, null));

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
     * To be passed the database has to contain realdonaldtrump
     */
    @Test
    @Order(6)
    void testHashtagDelete() {
        assertTrue(databaseManager.cachedHashtagInDataBase("trump"));
        databaseManager.deleteCachedHashtagFromDatabase("trump");
        assertFalse(databaseManager.cachedHashtagInDataBase("trump"));
    }

}