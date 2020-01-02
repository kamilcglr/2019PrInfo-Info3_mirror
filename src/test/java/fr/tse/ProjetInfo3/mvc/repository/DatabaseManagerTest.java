package fr.tse.ProjetInfo3.mvc.repository;

import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.viewer.UserViewer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {
    static DatabaseManager databaseManager;
    static UserViewer userViewer;

    @BeforeAll
    static void initialize() {
        databaseManager = new DatabaseManager();
        userViewer = new UserViewer();
    }

    @Test
    void cacheAndGetUserFromCache() throws Exception {
        userViewer.searchScreenName("realdonaldtrump");
        User user1 = userViewer.getUser();
        //user1.setListoftweets(userViewer.getTweetsByCount(user1.getScreen_name(), 3194, null));
//
        userViewer.searchScreenName("emmanuelmacron");
        User user2 = userViewer.getUser();
        //user2.setListoftweets(userViewer.getTweetsByCount(user2.getScreen_name(), 3194, null));

        //databaseManager.cacheUserToDataBase(user1);
        //databaseManager.cacheUserToDataBase(user2);

        User userFromCache1 = databaseManager.getCachedUserFromDatabase(user1.getScreen_name());
        User userFromCache2 = databaseManager.getCachedUserFromDatabase(user2.getScreen_name());

        //assertEquals(user1.getScreen_name(), userFromCache1.getScreen_name());
        assertEquals(user2.getScreen_name(), userFromCache2.getScreen_name());
        //assertEquals(user1.getListoftweets().size(), userFromCache1.getListoftweets().size());
        assertEquals(user2.getListoftweets().size(), userFromCache2.getListoftweets().size());
    }

    @Test
    void testUserNotInDB() {
        assertNull(databaseManager.getCachedUserFromDatabase("azerty"));
    }

    /**
     * To be passed the database has to contain realdonaldtrump
     */
    @Test
    void testDelete() {
        User user = databaseManager.getCachedUserFromDatabase("realdonaldtrump");
        assertNotNull(user);
        databaseManager.deleteCachedUserFromDataBase("realdonaldtrump");
        assertNull(databaseManager.getCachedUserFromDatabase("realdonaldtrump"));
    }

}