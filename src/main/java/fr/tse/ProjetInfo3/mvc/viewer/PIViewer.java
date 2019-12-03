package fr.tse.ProjetInfo3.mvc.viewer;

import fr.tse.ProjetInfo3.mvc.dao.InterestPointDAO;
import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.dto.InterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.ListOfInterestPoint;
import fr.tse.ProjetInfo3.mvc.dto.User;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class contains the list of PIs of the user
 * it transfers DTOs from DATABASE (repository) to Controllers
 */
public class PIViewer {
    private static List<InterestPoint> listOfInterestPoint = new ArrayList<>();
    private static InterestPointDAO interestPointDAO = new InterestPointDAO();
    private InterestPoint selectedInterestPoint;

    public PIViewer() {
        try {
            generatePIsDemo();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * At the moment, this function calls generatePIs, but in the futur, it will get the list from database
     */
    public List<InterestPoint> getlistOfInterestPoint() {
        return listOfInterestPoint;
    }

    public void setSelectedInterestPoint(int index) {
        this.selectedInterestPoint = listOfInterestPoint.get(index);
    }

    /**
     * Return the selected interest Point which was selected in MyPIsController
     * This function is used inside PICreate
     */
    public InterestPoint getSelectedInterestPoint() {
        return this.selectedInterestPoint;
    }

    /**
     * In the futur, this function will add the created Interest Point into the database
     */
    public void addInterestPointToDatabase(InterestPoint interestPoint) {
        //listOfInterestPoint.add(interestPoint);
    	interestPointDAO.saveInterestPoint(interestPoint);
    }

    /**
     * In the futur, this function will return the created Interest Point into the database
     */
    public List<InterestPoint> getListOfInterestPointFromDataBase() {
        //return listOfInterestPoint;
    	return interestPointDAO.getAllInterestPoints();
    }

    /**
     * This function generate a list of PIs for the tests
     */
    public void generatePIsDemo() throws IOException, InterruptedException {
        //First IP
        List<Hashtag> hashtags = new ArrayList<>();
        Hashtag president = new Hashtag("#president");
        Hashtag congres = new Hashtag("#congrés");
        Hashtag meetup = new Hashtag("#meetup");

        hashtags.add(president);
        hashtags.add(congres);
        hashtags.add(meetup);

        List<User> users = new ArrayList<>();
        RequestManager requestManager = new RequestManager();
        User trump = requestManager.getUser("realdonaldtrump");
        User macron = requestManager.getUser("EmmanuelMacron");

        users.add(trump);
        users.add(macron);


        Date date = new Date();
        InterestPoint ip1 = new InterestPoint("Politique", "Suivi des personnalités politiques", date);
        ip1.setHashtags(hashtags);
        ip1.setUsers(users);

        //Second IP
        List<Hashtag> hashtags1 = new ArrayList<>();
        Hashtag example = new Hashtag("#Telecom");
        Hashtag city = new Hashtag("#Saint-Etienne");
        Hashtag dep = new Hashtag("#42");

        hashtags1.add(example);
        hashtags1.add(city);
        hashtags1.add(dep);

        List<User> users2 = new ArrayList<>();
        User sobun = requestManager.getUser("sobunung");
        User kamil = requestManager.getUser("kamilcglr");

        users2.add(sobun);
        users2.add(kamil);

        Date date2 = new Date();
        InterestPoint ip2 = new InterestPoint("Mes amis", "Suivi des amis", date2);
        ip2.setHashtags(hashtags1);
        ip2.setUsers(users2);

        listOfInterestPoint.add(ip1);
        listOfInterestPoint.add(ip2);
    }

    
    /*
     * This method will create a restricted PI in the DB just to test some of the methods of insertion and creation
     * in the db , the Interest Point does not contain the list of users , tweets , and hastags for the moment
     * */
    public void createRestrictedPIinDatabase() {
    	Date date = new Date();
        InterestPoint ip1 = new InterestPoint("Politique", "Suivi des personnalites politiques", date);
        // TO-DO
    }
}
