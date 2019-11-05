package fr.tse.ProjetInfo3.mvc.viewer;

import fr.tse.ProjetInfo3.mvc.dao.Hashtag;
import fr.tse.ProjetInfo3.mvc.dao.Tweet;
import fr.tse.ProjetInfo3.mvc.services.RequestManager;

import java.util.List;

public class HastagViewer {
    private Hashtag hashtag;

    private RequestManager requestManager;

    public HastagViewer() {
        requestManager = new RequestManager();
    }

    public void searchHashtag(String hashtag) throws Exception {
        /*Need Hastag object to feed*/
    }

    public Hashtag getHashtag() {
        return hashtag;
    }

}
