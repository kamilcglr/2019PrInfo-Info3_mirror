package fr.tse.ProjetInfo3.mvc.viewer;

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

    public PIViewer() {
    }

    /**
     * At the moment, this function calls generatePIs, but in the futur, it we get the list from database
     */
    public List<InterestPoint> getlistOfInterestPoint() {
        try {
            return generatePIs();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addInterestPointToDatabase(InterestPoint interestPoint){
        listOfInterestPoint.add(interestPoint);
    }

    public List<InterestPoint> getListOfInterestPointFromDataBase(){
        return listOfInterestPoint;
    }

    /**
     * This function generate a list of PIs for the tests
     */
    private List<InterestPoint> generatePIs() throws IOException, InterruptedException {
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

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

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


        List<InterestPoint> interestPoints = new ArrayList<InterestPoint>();
        interestPoints.add(ip1);
        interestPoints.add(ip2);

        return interestPoints;
    }
}
