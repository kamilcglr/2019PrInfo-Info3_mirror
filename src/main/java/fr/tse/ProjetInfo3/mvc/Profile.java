package fr.tse.ProjetInfo3.mvc;

import java.awt.Image;

/**
 * 
 * @author Sergiy
 * This class groups all the information needed to show a Twitter profile
 */
public class Profile {
	private String name;
	private String description;
	private int followersCount;
	private int friendsCount;
	private int favouritesCount;
	private Image profileImage;

	public Profile(String name, String description, int followersCount, int friendsCount, int favouritesCount,
			Image profileImage) {
		super();
		this.name = name;
		this.description = description;
		this.followersCount = followersCount;
		this.friendsCount = friendsCount;
		this.favouritesCount = favouritesCount;
		this.profileImage = profileImage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}

	public int getFavouritesCount() {
		return favouritesCount;
	}

	public void setFavouritesCount(int favouritesCount) {
		this.favouritesCount = favouritesCount;
	}

	public Image getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(Image profileImage) {
		this.profileImage = profileImage;
	}

	@Override
	public String toString() {
		return "Profile [name=" + name + ", description=" + description + ", followersCount=" + followersCount
				+ ", friendsCount=" + friendsCount + ", favouritesCount=" + favouritesCount + ", profileImage="
				+ profileImage + "]";
	}
}
