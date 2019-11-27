/**
 *
 */
package fr.tse.ProjetInfo3.mvc.dto;

import org.h2.engine.User;

import java.util.List;

/**
 * @author ALAMI IDRISSI Taha
 * @author kamilcaglar
 * This class contains some tweets with with the same #
 */
public class Hashtag {
    private String nameOfHashtag;

    public Hashtag(String hashtag) {
        super();
        this.nameOfHashtag = hashtag;
    }

    public Hashtag() {

    }

    public String getHashtagName() {
        return nameOfHashtag;
    }

    public void setHashtagName(String hashtag) {
        this.nameOfHashtag = hashtag;
    }

}
