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
import java.util.List;

/**
 * This class contains the list of PIs of the user
 * it transfers DTOs from DATABASE (repository) to Controllers
 * */
public class PIViewer {
    private List<InterestPoint> listOfInterestPoint;

    public PIViewer(){
    }

    /**
     * At the moment, this function calls generatePIs, but in the futur, it we get the list from database
     */
    public List<InterestPoint> getlistOfInterestPoint(){
        try {
            return generatePIs();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This function generate a list of PIs for the tests
     * */
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

        InterestPoint ip1 = new InterestPoint(hashtags, users, "Politique");

        //Second IP
        List<Hashtag> hashtags1 = new ArrayList<>();
        Hashtag example = new Hashtag("#Telecom");
        Hashtag city = new Hashtag("#Saint-Etienne");
        Hashtag dep = new Hashtag("#42");

        hashtags.add(example);
        hashtags.add(city);
        hashtags.add(dep);

        List<User> users2 = new ArrayList<>();
        User sobun = requestManager.getUser("sobunung");
        User kamil = requestManager.getUser("kamilcglr");

        users.add(sobun);
        users.add(kamil);

        InterestPoint ip2 = new InterestPoint(hashtags, users, "Mes amis");



        List<InterestPoint> interestPoints = new ArrayList<InterestPoint>();
        interestPoints.add(ip1);
        interestPoints.add(ip2);

        return interestPoints;
    }
}
