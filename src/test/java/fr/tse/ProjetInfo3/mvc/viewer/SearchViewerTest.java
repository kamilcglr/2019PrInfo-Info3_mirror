package fr.tse.ProjetInfo3.mvc.viewer;


import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchViewerTest {

    SearchViewer searchViewer = new SearchViewer();
    RequestManager requestManager = new RequestManager();
    @Test
    void getListPropositions() {
    }

    @Test
    void getUser() {
//        User  inexistentUser = null;
//        try {
//            inexistentUser = requestManager.getUser("sobun'");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        assertEquals(null,inexistentUser);
        // User{id=2243547677, name='bubun 🇰🇭', screen_name='SobunUng', location='Paris, France', description='', url='null', verified=false, followers_count=153, friends_count=311, listed_count=0, favourites_count=413, statuses_count=1617, created_at='Wed Dec 25 17:43:06 +0000 2013', profile_banner_url='https://pbs.twimg.com/profile_banners/2243547677/1418336098', profile_image_url_https='https://pbs.twimg.com/profile_images/1185302129227059200/wMsHmFoo_normal.jpg', listoftweets=null}

        User sobun = new User(2243547677L, "bubun \uD83C\uDDF0\uD83C\uDDED", "SobunUng", "Paris, France", "", "null",false,153,311,0,413,1617, "Wed Dec 25 17:43:06 +0000 2013", "https://pbs.twimg.com/profile_banners/2243547677/1418336098", "https://pbs.twimg.com/profile_images/1185302129227059200/wMsHmFoo_normal.jpg",null);

        try {
            User user = requestManager.getUser("sobunung");
            assertEquals(sobun, user);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        }
    }

    @Test(expected= RequestManager.RequestManagerException.class)
    void getUser() {
        User  inexistentUser = null;
        try {
            inexistentUser = requestManager.getUser("sobun'");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(null,inexistentUser);
    }