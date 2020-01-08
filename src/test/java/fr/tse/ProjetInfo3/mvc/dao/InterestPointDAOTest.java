package fr.tse.ProjetInfo3.mvc.dao;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager.RequestManagerException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterestPointDAOTest {
    InterestPoint interestpoint;

    @BeforeAll
    public void initializeTests() {
        //First IP
        List<Hashtag> hashtags = new ArrayList<>();
        Hashtag h1 = new Hashtag("#blackfriday");
        Hashtag h2 = new Hashtag("#amazon");
        Hashtag h3 = new Hashtag("#darty");

        hashtags.add(h1);
        hashtags.add(h2);
        hashtags.add(h3);

        List<User> users = new ArrayList<>();
        RequestManager requestManager = new RequestManager();
        try {
            User u1 = requestManager.getUser("twandroid");
            User u2 = requestManager.getUser("Dealabs");
            users.add(u1);
            users.add(u2);
        } catch (RequestManagerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Date date = new Date();
        interestpoint = new InterestPoint("Black friday", "Suivi des promotions", date);
        interestpoint.setHashtags(hashtags);
        interestpoint.setUsers(users);
    }

    @Test
    public void testSaveInterestPoint() {
        //interestPointDAO.saveHashtag(interestpoint, interestpoint.getId());
        //assertEquals(interestpoint, interestPointDAO.getSelectedInterestPoint(interestpoint.getId()));
    }

    @Test
    public void testSaveHashtag() {

    }

    @Test
    public void testSaveUsers() {

    }

    @Test
    public void testGetAllInterestPoints() {

    }

    @Test
    public void testGetAllHashtagOfAnInterestPoint() {

    }

    @Test
    public void testGetAllUsersfAnInterestPoint() {

    }

    @Test
    public void testGetSelectedInterestPoint() {

    }

    @Test
    public void testDeleteSelectedInterestPointById() {

    }

    @Test
    public void testUpdateSelectedInterestPoint() {

    }

}
