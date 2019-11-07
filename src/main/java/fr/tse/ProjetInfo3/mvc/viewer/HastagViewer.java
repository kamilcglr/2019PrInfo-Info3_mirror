package fr.tse.ProjetInfo3.mvc.viewer;

import fr.tse.ProjetInfo3.mvc.dto.Hashtag;
import fr.tse.ProjetInfo3.mvc.repository.RequestManager;

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
