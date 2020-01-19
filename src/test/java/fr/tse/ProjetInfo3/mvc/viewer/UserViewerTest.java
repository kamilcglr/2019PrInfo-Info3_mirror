package fr.tse.ProjetInfo3.mvc.viewer;

import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test if request manager sends correct information
 */
class UserViewerTest {
    private User user;
    private RequestManager requestManager;
    private UserViewer userViewer;

    public UserViewerTest() {
        requestManager = new RequestManager();
    }

    /**
     */
    @Test
    void searchScreenName() throws IOException, InterruptedException {
        user = requestManager.getUser("realDonaldTrump");
        assertEquals(user.getScreen_name(), "realDonaldTrump");

        user = requestManager.getUser("unittest");
        assertEquals(user.getScreen_name(), "unittest");
        assertEquals(user.getFollowers_count(), 34);
        assertEquals(user.getCreated_at(), "Fri Apr 13 01:45:08 +0000 2007");
        assertEquals(user.getFriends_count(), 99);
    }
}