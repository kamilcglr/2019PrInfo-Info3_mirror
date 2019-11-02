package fr.tse.ProjetInfo3.mvc.viewer;

import fr.tse.ProjetInfo3.mvc.dao.User;
import fr.tse.ProjetInfo3.mvc.services.RequestManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test if request manager sends correct information
 * */
class UserViewerTest {
    private User user;
    private RequestManager requestManager;

    public UserViewerTest() {
        requestManager = new RequestManager();
    }

    /**
     * TODO We have to add more unit test*/
    @Test
    void searchScreenName() {
        user = requestManager.getUser("realDonaldTrump");
        assertEquals(user.getScreen_name(), "realDonaldTrump");

        user = requestManager.getUser("unittest");
        assertEquals(user.getScreen_name(), "unittest");
        assertEquals(user.getFollowers_count(), 35);
        assertEquals(user.getCreated_at(), "Fri Apr 13 01:45:08 +0000 2007");
        assertEquals(user.getFriends_count(), 84);
    }
}