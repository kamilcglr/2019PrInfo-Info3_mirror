/**
 * 
 */
package fr.tse.ProjetInfo3.mvc.dto;

import java.util.List;

/**
 * @author ALAMI IDRISSI Taha
 * @author kamilcaglar
 * This class contains some tweets with with the same #
 */
public class Hashtag {
	private String text;

	private List<String> indices;


	public Hashtag(String hashtag) {
		super();
		this.text = hashtag;
	}

	public String getHashtag() {
		return text;
	}

	public void setHashtag(String hashtag) {
		this.text = hashtag;
	}

	

}
